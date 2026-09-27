package com.librarymanagement.controller;
import com.librarymanagement.dto.ApiDtos.BorrowInput;import com.librarymanagement.entity.*;import com.librarymanagement.repository.LoanRepository;import com.librarymanagement.service.LibraryService;import jakarta.validation.Valid;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;import java.util.*;
@RestController @RequestMapping("/api/transactions") public class TransactionController{
 private final LoanRepository loans;private final LibraryService service;public TransactionController(LoanRepository l,LibraryService s){loans=l;service=s;}
 @PostMapping("/borrow") @PreAuthorize("hasRole('MEMBER')") public Map<String,Object> borrow(@Valid @RequestBody BorrowInput in,Authentication a){return service.loanView(service.borrow(a.getName(),in.bookId()));}
 @PutMapping("/{id}/return") @PreAuthorize("hasRole('LIBRARIAN')") public Map<String,Object> returnBook(@PathVariable Long id,Authentication a){return service.loanView(service.returnLoan(a.getName(),id));}
 @GetMapping("/my") public List<Map<String,Object>> mine(Authentication a){User u=service.user(a.getName());return loans.findByMemberIdOrderByBorrowDateDesc(u.getId()).stream().map(service::loanView).toList();}
 @GetMapping @PreAuthorize("hasRole('LIBRARIAN')") public List<Map<String,Object>> all(){return loans.findAllByOrderByBorrowDateDesc().stream().map(service::loanView).toList();}
}
