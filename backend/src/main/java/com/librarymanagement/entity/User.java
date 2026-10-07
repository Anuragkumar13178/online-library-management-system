package com.librarymanagement.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="users", uniqueConstraints={
        @UniqueConstraint(columnNames="email"),
        @UniqueConstraint(columnNames="membership_id")
})
public class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,length=120) private String name;
    @Column(nullable=false,length=190) private String email;
    @Column(name="membership_id",length=40,unique=true) private String membershipId;
    @Column(nullable=false) private String passwordHash;
    private String phone;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role=Role.MEMBER;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private AccountStatus accountStatus=AccountStatus.ACTIVE;
    private String profileImage;
    @Column(nullable=false,updatable=false) private Instant createdAt=Instant.now();
    @Column(nullable=false) private Instant updatedAt=Instant.now();

    @PrePersist
    void ensureMembershipId(){
        if(membershipId==null || membershipId.isBlank()){
            membershipId="MEM-"+UUID.randomUUID().toString().substring(0,8).toUpperCase();
        }
    }

    @PreUpdate void touch(){updatedAt=Instant.now();}

    public Long getId(){return id;}
    public String getName(){return name;}
    public void setName(String v){name=v;}
    public String getEmail(){return email;}
    public void setEmail(String v){email=v==null?null:v.toLowerCase().trim();}
    public String getMembershipId(){return membershipId;}
    public void setMembershipId(String v){membershipId=v;}
    public String getPasswordHash(){return passwordHash;}
    public void setPasswordHash(String v){passwordHash=v;}
    public String getPhone(){return phone;}
    public void setPhone(String v){phone=v;}
    public Role getRole(){return role;}
    public void setRole(Role v){role=v;}
    public AccountStatus getAccountStatus(){return accountStatus;}
    public void setAccountStatus(AccountStatus v){accountStatus=v;}
    public String getProfileImage(){return profileImage;}
    public void setProfileImage(String v){profileImage=v;}
    public Instant getCreatedAt(){return createdAt;}
}
