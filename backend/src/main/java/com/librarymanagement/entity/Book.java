package com.librarymanagement.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="books", uniqueConstraints=@UniqueConstraint(columnNames="isbn"))
public class Book {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String title;
    @Column(nullable=false) private String author;
    @Column(nullable=false,length=40) private String isbn;
    private String genre; private String publisher; private Integer publicationYear;
    @Column(nullable=false) private Integer quantity=0;
    @Column(nullable=false) private Integer availableCopies=0;
    @Column(length=1000) private String coverImage;
    @Column(columnDefinition="TEXT") private String description;
    private Instant createdAt=Instant.now(); private Instant updatedAt=Instant.now();
    @PreUpdate void touch(){updatedAt=Instant.now();}
    public Long getId(){return id;} public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getAuthor(){return author;} public void setAuthor(String v){author=v;} public String getIsbn(){return isbn;} public void setIsbn(String v){isbn=v;}
    public String getGenre(){return genre;} public void setGenre(String v){genre=v;} public String getPublisher(){return publisher;} public void setPublisher(String v){publisher=v;}
    public Integer getPublicationYear(){return publicationYear;} public void setPublicationYear(Integer v){publicationYear=v;}
    public Integer getQuantity(){return quantity;} public Integer getAvailableCopies(){return availableCopies;}
    public void setQuantity(Integer v){int borrowed=quantity-availableCopies;if(v<borrowed)throw new IllegalArgumentException("Quantity cannot be lower than the number of borrowed copies.");quantity=v;availableCopies=v-borrowed;}
    public void initializeQuantity(Integer v){quantity=v;availableCopies=v;}
    public void decrementAvailable(){if(availableCopies<=0)throw new IllegalStateException("Book is currently unavailable.");availableCopies--;}
    public void incrementAvailable(){if(availableCopies>=quantity)throw new IllegalStateException("Available copies cannot exceed total quantity.");availableCopies++;}
    public String getCoverImage(){return coverImage;} public void setCoverImage(String v){coverImage=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;}
}
