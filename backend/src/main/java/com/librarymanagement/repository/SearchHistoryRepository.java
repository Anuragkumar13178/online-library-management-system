package com.librarymanagement.repository;

import com.librarymanagement.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    List<SearchHistory> findTop20ByMemberIdOrderBySearchedAtDesc(Long memberId);
}
