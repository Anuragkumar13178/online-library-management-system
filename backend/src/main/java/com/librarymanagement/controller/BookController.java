package com.librarymanagement.controller;

import com.librarymanagement.dto.ApiDtos.BookInput;
import com.librarymanagement.entity.Book;
import com.librarymanagement.entity.Role;
import com.librarymanagement.entity.SearchHistory;
import com.librarymanagement.entity.User;
import com.librarymanagement.repository.BookRepository;
import com.librarymanagement.repository.SearchHistoryRepository;
import com.librarymanagement.service.LibraryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository books;
    private final LibraryService service;
    private final SearchHistoryRepository history;

    public BookController(BookRepository b, LibraryService s, SearchHistoryRepository h) {
        books = b;
        service = s;
        history = h;
    }

    @GetMapping
    public Map<String, Object> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String genre,
            @RequestParam(defaultValue = "false") boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "title") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            Authentication authentication) {

        String field = Set.of("title", "publicationYear", "availableCopies", "createdAt")
                .contains(sort) ? sort : "title";

        if (authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())
                && page == 0
                && ((search != null && !search.isBlank()) || (genre != null && !genre.isBlank()))) {
            try {
                User member = service.user(authentication.getName());
                if (member.getRole() == Role.MEMBER) {
                    SearchHistory h = new SearchHistory();
                    h.setMember(member);
                    h.setSearchText(search);
                    h.setGenre(genre);
                    history.save(h);
                }
            } catch (Exception ignored) {
                // Search history must never break the actual book search.
            }
        }

        Sort s = Sort.by(
                direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                field
        );

        Page<Map<String, Object>> p = books.search(
                search == null || search.isBlank() ? null : search,
                genre == null || genre.isBlank() ? null : genre,
                available,
                PageRequest.of(Math.max(0, page), Math.min(100, Math.max(1, size)), s)
        ).map(service::bookView);

        return Map.of(
                "content", p.getContent(),
                "page", p.getNumber(),
                "size", p.getSize(),
                "totalElements", p.getTotalElements(),
                "totalPages", p.getTotalPages()
        );
    }

    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable Long id) {
        return service.bookView(service.book(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public Map<String, Object> create(@Valid @RequestBody BookInput in) {
        return service.createBookWithNotification(in);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public Map<String, Object> update(@PathVariable Long id,
                                      @Valid @RequestBody BookInput in) {
        return service.bookView(service.updateBook(id, in));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public void delete(@PathVariable Long id) {
        books.delete(service.book(id));
    }
}
