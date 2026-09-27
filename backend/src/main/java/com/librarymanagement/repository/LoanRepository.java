package com.librarymanagement.repository;
import com.librarymanagement.entity.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.time.LocalDate; import java.util.*;
public interface LoanRepository extends JpaRepository<Loan,Long>{
 List<Loan> findByMemberIdOrderByBorrowDateDesc(Long id); List<Loan> findAllByOrderByBorrowDateDesc(); long countByMemberIdAndStatus(Long id,LoanStatus status); long countByStatus(LoanStatus status);
 @Query("select l from Loan l where l.member.id=:member and l.status=:status and l.id=:id") Optional<Loan> findOwned(@Param("member")Long member,@Param("status")LoanStatus status,@Param("id")Long id);
 @Query("select l from Loan l where l.returnDate is null and l.dueDate<:today") List<Loan> overdue(@Param("today")LocalDate today);
 @Query("select count(l) from Loan l where l.returnDate is null and l.dueDate<:today") long countOverdue(@Param("today")LocalDate today);
}
