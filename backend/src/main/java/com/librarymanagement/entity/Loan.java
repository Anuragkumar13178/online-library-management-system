package com.librarymanagement.entity;

import jakarta.persistence.*;
import java.time.*;

@Entity @Table(name="transactions", indexes={@Index(columnList="member_id"),@Index(columnList="book_id"),@Index(columnList="due_date")})
public class Loan {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false,fetch=FetchType.EAGER) @JoinColumn(name="book_id") private Book book;
    @ManyToOne(optional=false,fetch=FetchType.EAGER) @JoinColumn(name="member_id") private User member;
    @Column(nullable=false) private LocalDate borrowDate;
    @Column(nullable=false) private LocalDate dueDate;
    private LocalDate returnDate;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private LoanStatus status=LoanStatus.BORROWED;
    private Instant createdAt=Instant.now();
    public Long getId(){return id;} public Book getBook(){return book;} public void setBook(Book v){book=v;} public User getMember(){return member;} public void setMember(User v){member=v;}
    public LocalDate getBorrowDate(){return borrowDate;} public void setBorrowDate(LocalDate v){borrowDate=v;} public LocalDate getDueDate(){return dueDate;} public void setDueDate(LocalDate v){dueDate=v;}
    public LocalDate getReturnDate(){return returnDate;} public void setReturnDate(LocalDate v){returnDate=v;} public LoanStatus getStatus(){return status;} public void setStatus(LoanStatus v){status=v;}
    public boolean isOverdue(){return returnDate==null&&LocalDate.now().isAfter(dueDate);}
}
