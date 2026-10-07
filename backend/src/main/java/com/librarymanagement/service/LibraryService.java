package com.librarymanagement.service;

import com.librarymanagement.dto.ApiDtos.*;
import com.librarymanagement.entity.*;
import com.librarymanagement.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

/**
 * Core application service for the campus library.
 * Keeps business rules in one place so controllers remain thin.
 */
@Service
public class LibraryService {
    private final UserRepository users;
    private final BookRepository books;
    private final LoanRepository loans;
    private final LibraryNotificationRepository notifications;
    private final NotificationPreferenceRepository preferences;
    private final PasswordEncoder encoder;
    private final int limit;
    private final int days;

    public LibraryService(UserRepository u, BookRepository b, LoanRepository l,
                          LibraryNotificationRepository n, NotificationPreferenceRepository p,
                          PasswordEncoder e,
                          @Value("${app.borrowing.limit}") int limit,
                          @Value("${app.borrowing.days}") int days) {
        users = u;
        books = b;
        loans = l;
        notifications = n;
        preferences = p;
        encoder = e;
        this.limit = limit;
        this.days = days;
    }

    public User user(String email) {
        return users.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found."));
    }

    public Map<String, Object> publicUser(User u) {
        return Map.of(
                "id", u.getId(),
                "name", u.getName(),
                "email", u.getEmail(),
                "role", u.getRole().name(),
                "phone", u.getPhone() == null ? "" : u.getPhone(),
                "accountStatus", u.getAccountStatus().name(),
                "createdAt", u.getCreatedAt()
        );
    }

    @Transactional
    public User register(Register in) {
        String email = in.email().toLowerCase().trim();
        if (users.existsByEmail(email)) {
            throw new IllegalArgumentException("Email address is already registered.");
        }

        User u = new User();
        u.setName(in.name().trim());
        u.setEmail(email);
        u.setPhone(in.phone());
        u.setPasswordHash(encoder.encode(in.password()));
        u.setRole(Role.MEMBER);

        u = users.save(u);

        NotificationPreference pref = new NotificationPreference();
        pref.setMember(u);
        preferences.save(pref);

        return u;
    }

    public Book book(Long id) {
        return books.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book not found."));
    }

    public Map<String, Object> createBookWithNotification(BookInput in) {
        Book created = createBook(in);
        // Keep new-arrival behavior real and database-backed.
        for (User member : users.findByRole(Role.MEMBER)) {
            NotificationPreference pref = preferences.findByMemberId(member.getId()).orElse(null);
            if (pref == null || pref.isNewBookAlerts()) {
                createNotification(
                        member,
                        "New book added",
                        created.getTitle() + " is now available in the library.",
                        "NEW_ARRIVAL"
                );
            }
        }
        return bookView(created);
    }

    public Book createBook(BookInput in) {
        Book b = new Book();
        updateBookFields(b, in, true);
        return books.save(b);
    }

    public Book updateBook(Long id, BookInput in) {
        Book b = book(id);
        updateBookFields(b, in, false);
        return books.save(b);
    }

    private void updateBookFields(Book b, BookInput in, boolean create) {
        b.setTitle(in.title().trim());
        b.setAuthor(in.author().trim());
        b.setIsbn(in.isbn().trim());
        b.setGenre(in.genre());
        b.setPublisher(in.publisher());
        b.setPublicationYear(in.publicationYear());

        if (create) {
            b.initializeQuantity(in.quantity() == null ? 0 : in.quantity());
        } else if (in.quantity() != null) {
            b.setQuantity(in.quantity());
        }

        b.setCoverImage(in.coverImage());
        b.setDescription(in.description());
    }

    /**
     * Real concurrency-safe borrowing path.
     *
     * The database pessimistic lock is the authoritative protection across
     * multiple application instances/threads; the synchronized block makes
     * the critical section explicit for the Core Java Review-1 discussion.
     */
    @Transactional
    public Loan borrow(String email, Long bookId) {
        User member = user(email);

        if (member.getRole() != Role.MEMBER) {
            throw new IllegalArgumentException("Only members can borrow books.");
        }
        if (member.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException("Your account is inactive.");
        }
        if (loans.countByMemberIdAndStatus(member.getId(), LoanStatus.BORROWED) >= limit) {
            throw new IllegalArgumentException("Borrowing limit reached. Please return a book before borrowing another.");
        }

        Book b;
        synchronized (("BOOK-" + bookId).intern()) {
            b = books.lockById(bookId)
                    .orElseThrow(() -> new NoSuchElementException("Book not found."));
            b.decrementAvailable();

            Loan l = new Loan();
            l.setMember(member);
            l.setBook(b);
            l.setBorrowDate(LocalDate.now());
            l.setDueDate(LocalDate.now().plusDays(days));
            Loan saved = loans.save(l);

            NotificationPreference pref = preferences.findByMemberId(member.getId()).orElse(null);
            if (pref == null || pref.isDueDateAlerts()) {
                createNotification(
                        member,
                        "Book borrowed",
                        "You borrowed "" + b.getTitle() + "". Due date: " + l.getDueDate() + ".",
                        "DUE_DATE"
                );
            }

            return saved;
        }
    }

    /**
     * Members can return their own books, matching the official project
     * specification. Librarians may process any active return.
     */
    @Transactional
    public Loan returnLoan(String email, Long id) {
        User actor = user(email);

        Loan loan = loans.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found."));

        boolean librarian = actor.getRole() == Role.LIBRARIAN;
        boolean owner = loan.getMember().getId().equals(actor.getId());

        if (!librarian && !owner) {
            throw new AccessDeniedException("You can only return your own borrowed books.");
        }

        if (loan.getStatus() != LoanStatus.BORROWED) {
            throw new IllegalArgumentException("This book has already been returned.");
        }

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);
        loan.getBook().incrementAvailable();

        return loans.save(loan);
    }

    public Map<String, Object> loanView(Loan l) {
        return Map.of(
                "id", l.getId(),
                "book", bookView(l.getBook()),
                "member", Map.of(
                        "id", l.getMember().getId(),
                        "name", l.getMember().getName(),
                        "email", l.getMember().getEmail()
                ),
                "borrowDate", l.getBorrowDate(),
                "dueDate", l.getDueDate(),
                "returnDate", l.getReturnDate() == null ? "" : l.getReturnDate(),
                "status", l.isOverdue() ? "OVERDUE" : l.getStatus().name()
        );
    }

    public Map<String, Object> bookView(Book b) {
        return Map.ofEntries(
                Map.entry("id", b.getId()),
                Map.entry("title", b.getTitle()),
                Map.entry("author", b.getAuthor()),
                Map.entry("isbn", b.getIsbn()),
                Map.entry("genre", b.getGenre() == null ? "" : b.getGenre()),
                Map.entry("publisher", b.getPublisher() == null ? "" : b.getPublisher()),
                Map.entry("publicationYear", b.getPublicationYear() == null ? 0 : b.getPublicationYear()),
                Map.entry("quantity", b.getQuantity()),
                Map.entry("availableCopies", b.getAvailableCopies()),
                Map.entry("coverImage", b.getCoverImage() == null ? "" : b.getCoverImage()),
                Map.entry("description", b.getDescription() == null ? "" : b.getDescription())
        );
    }

    @Transactional
    public void createNotification(User recipient, String title, String message, String type) {
        LibraryNotification n = new LibraryNotification();
        n.setRecipient(recipient);
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        notifications.save(n);
    }

    public Map<String, Object> notificationView(LibraryNotification n) {
        return Map.of(
                "id", n.getId(),
                "title", n.getTitle(),
                "message", n.getMessage(),
                "type", n.getType(),
                "read", n.isRead(),
                "createdAt", n.getCreatedAt()
        );
    }

    public Map<String, Object> dashboard() {
        return Map.of(
                "totalBooks", books.count(),
                "availableCopies", books.findAll().stream().mapToLong(Book::getAvailableCopies).sum(),
                "totalMembers", users.findByRole(Role.MEMBER).size(),
                "borrowedBooks", loans.countByStatus(LoanStatus.BORROWED),
                "totalTransactions", loans.count(),
                "overdueBooks", loans.countOverdue(LocalDate.now())
        );
    }
}
