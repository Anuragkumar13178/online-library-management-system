package com.librarymanagement.service;

import com.librarymanagement.dto.ApiDtos.Register;
import com.librarymanagement.entity.*;
import com.librarymanagement.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LibraryServiceTest {
    UserRepository users; BookRepository books; LoanRepository loans; LibraryNotificationRepository notifications;
    NotificationPreferenceRepository preferences; PasswordEncoder encoder; LibraryService service;

    @BeforeEach
    void setup(){
        users=mock(UserRepository.class);
        books=mock(BookRepository.class);
        loans=mock(LoanRepository.class);
        notifications=mock(LibraryNotificationRepository.class);
        preferences=mock(NotificationPreferenceRepository.class);
        encoder=mock(PasswordEncoder.class);
        service=new LibraryService(users,books,loans,notifications,preferences,encoder,5,14);
    }

    @Test
    void borrowDecrementsInventoryAndCreatesDueDate(){
        User member=new User(); member.setEmail("reader@example.edu");
        Book book=new Book(); book.setTitle("Algorithms"); book.initializeQuantity(2);
        when(users.findByEmail("reader@example.edu")).thenReturn(Optional.of(member));
        when(loans.countByMemberIdAndStatus(any(),eq(LoanStatus.BORROWED))).thenReturn(0L);
        when(books.lockById(7L)).thenReturn(Optional.of(book));
        when(loans.save(any())).thenAnswer(i->i.getArgument(0));

        Loan loan=service.borrow("reader@example.edu",7L);

        assertEquals(1,book.getAvailableCopies());
        assertEquals(14,java.time.temporal.ChronoUnit.DAYS.between(loan.getBorrowDate(),loan.getDueDate()));
        verify(loans).save(loan);
    }

    @Test
    void unavailableBookCannotBeBorrowed(){
        User member=new User(); member.setEmail("reader@example.edu");
        Book book=new Book(); book.setTitle("Algorithms"); book.initializeQuantity(0);
        when(users.findByEmail(anyString())).thenReturn(Optional.of(member));
        when(loans.countByMemberIdAndStatus(any(),any())).thenReturn(0L);
        when(books.lockById(7L)).thenReturn(Optional.of(book));

        assertThrows(IllegalStateException.class,()->service.borrow("reader@example.edu",7L));
        verify(loans,never()).save(any());
    }

    @Test
    void borrowingLimitIsEnforcedBeforeInventoryChanges(){
        User member=new User(); member.setEmail("reader@example.edu");
        when(users.findByEmail(anyString())).thenReturn(Optional.of(member));
        when(loans.countByMemberIdAndStatus(any(),eq(LoanStatus.BORROWED))).thenReturn(5L);

        assertThrows(IllegalArgumentException.class,()->service.borrow("reader@example.edu",7L));
        verify(books,never()).lockById(any());
    }

    @Test
    void registrationStoresOnlyEncodedPassword(){
        when(users.existsByEmail(anyString())).thenReturn(false);
        when(encoder.encode("a-long-password")).thenReturn("bcrypt-hash");
        when(users.save(any())).thenAnswer(i->i.getArgument(0));

        service.register(new Register("Reader","reader@example.edu","a-long-password",null));

        verify(users).save(argThat(u->u.getPasswordHash().equals("bcrypt-hash")&&u.getRole()==Role.MEMBER));
        verify(preferences).save(any());
    }

    @Test
    void bookInventoryCannotExceedQuantityOrGoNegative(){
        Book book=new Book(); book.setTitle("Test"); book.initializeQuantity(2);
        book.decrementAvailable(); book.decrementAvailable();

        assertThrows(IllegalStateException.class,book::decrementAvailable);
        assertThrows(IllegalArgumentException.class,()->book.setQuantity(1));

        book.incrementAvailable(); book.incrementAvailable();
        assertThrows(IllegalStateException.class,book::incrementAvailable);
    }

    @Test
    void concurrentBorrowingOfLastCopyAllowsOnlyOneWinner() throws Exception {
        User first=new User(); first.setEmail("first@example.edu");
        User second=new User(); second.setEmail("second@example.edu");

        Book book=new Book(); book.setTitle("Concurrent Java"); book.initializeQuantity(1);

        when(users.findByEmail("first@example.edu")).thenReturn(Optional.of(first));
        when(users.findByEmail("second@example.edu")).thenReturn(Optional.of(second));
        when(loans.countByMemberIdAndStatus(any(),eq(LoanStatus.BORROWED))).thenReturn(0L);
        when(books.lockById(99L)).thenReturn(Optional.of(book));
        when(loans.save(any())).thenAnswer(i->i.getArgument(0));

        CountDownLatch ready=new CountDownLatch(2);
        CountDownLatch start=new CountDownLatch(1);
        List<Boolean> success=new ArrayList<>();
        List<Throwable> failures=new ArrayList<>();

        Runnable task=( ) -> {
            ready.countDown();
            try {
                start.await();
                String email=Thread.currentThread().getName();
                service.borrow(email,99L);
                synchronized(success){success.add(true);}
            } catch(Throwable t) {
                synchronized(failures){failures.add(t);}
            }
        };

        Thread t1=new Thread(task,"first@example.edu");
        Thread t2=new Thread(task,"second@example.edu");
        t1.start(); t2.start();
        ready.await(); start.countDown();
        t1.join(); t2.join();

        assertEquals(1,success.size());
        assertEquals(1,failures.size());
        assertEquals(0,book.getAvailableCopies());
    }
}
