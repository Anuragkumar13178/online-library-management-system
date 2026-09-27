package com.librarymanagement.entity;

import jakarta.persistence.*;

@Entity @Table(name="notification_preferences")
public class NotificationPreference {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @OneToOne(optional=false) @JoinColumn(name="member_id",unique=true) private User member;
    private boolean dueDateAlerts=true, overdueAlerts=true, newBookAlerts=true, announcementAlerts=true;
    public Long getId(){return id;} public User getMember(){return member;} public void setMember(User v){member=v;}
    public boolean isDueDateAlerts(){return dueDateAlerts;} public void setDueDateAlerts(boolean v){dueDateAlerts=v;} public boolean isOverdueAlerts(){return overdueAlerts;} public void setOverdueAlerts(boolean v){overdueAlerts=v;}
    public boolean isNewBookAlerts(){return newBookAlerts;} public void setNewBookAlerts(boolean v){newBookAlerts=v;} public boolean isAnnouncementAlerts(){return announcementAlerts;} public void setAnnouncementAlerts(boolean v){announcementAlerts=v;}
}
