package com.librarymanagement.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="search_history", indexes=@Index(columnList="member_id,searched_at"))
public class SearchHistory {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="member_id") private User member;
    @Column(length=200) private String searchText;
    @Column(length=120) private String genre;
    @Column(nullable=false) private Instant searchedAt=Instant.now();

    public Long getId(){return id;}
    public User getMember(){return member;}
    public void setMember(User v){member=v;}
    public String getSearchText(){return searchText;}
    public void setSearchText(String v){searchText=v;}
    public String getGenre(){return genre;}
    public void setGenre(String v){genre=v;}
    public Instant getSearchedAt(){return searchedAt;}
}
