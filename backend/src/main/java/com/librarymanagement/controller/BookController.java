package com.librarymanagement.controller;

import com.librarymanagement.dto.ApiDtos.BookInput;
import com.librarymanagement.entity.Book;
import com.librarymanagement.repository.BookRepository;
import com.librarymanagement.service.LibraryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository books;
    private final LibraryService service;

    public BookController(BookRepository b, LibraryService s) {
        books = b;
        service = s;
    }

    @GetMapping
    public Map<String, Object> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String genre,
            @RequestParam(defaultValue = "false") boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "title") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        String field = Set.of("title", "publicationYear", "availableCopies", "createdAt")
                .contains(sort) ? sort : "title";

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
