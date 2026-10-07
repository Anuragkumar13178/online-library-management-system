package com.librarymanagement.controller;

import com.librarymanagement.entity.SearchHistory;
import com.librarymanagement.service.LibraryService;
import com.librarymanagement.repository.SearchHistoryRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search-history")
@PreAuthorize("hasRole('MEMBER')")
public class SearchHistoryController {
    private final SearchHistoryRepository history;
    private final LibraryService service;

    public SearchHistoryController(SearchHistoryRepository history, LibraryService service) {
        this.history = history;
        this.service = service;
    }

    @GetMapping
    public List<Map<String,Object>> list(Authentication authentication) {
        Long memberId = service.user(authentication.getName()).getId();
        return history.findTop20ByMemberIdOrderBySearchedAtDesc(memberId).stream()
                .map(h -> Map.<String,Object>of(
                        "id", h.getId(),
                        "searchText", h.getSearchText() == null ? "" : h.getSearchText(),
                        "genre", h.getGenre() == null ? "" : h.getGenre(),
                        "searchedAt", h.getSearchedAt()))
                .toList();
    }
}
