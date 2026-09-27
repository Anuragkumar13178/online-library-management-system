package com.librarymanagement.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="notifications")
public class LibraryNotification {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="recipient_id") private User recipient;
    @Column(nullable=false) private String title;
    @Column(nullable=false,length=1500) private String message;
    @Column(nullable=false) private String type;
    @Column(nullable=false) private boolean isRead=false;
    private Instant createdAt=Instant.now();
    public Long getId(){return id;} public User getRecipient(){return recipient;} public void setRecipient(User v){recipient=v;} public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getMessage(){return message;} public void setMessage(String v){message=v;} public String getType(){return type;} public void setType(String v){type=v;}
    public boolean isRead(){return isRead;} public void setRead(boolean v){isRead=v;} public Instant getCreatedAt(){return createdAt;}
}
