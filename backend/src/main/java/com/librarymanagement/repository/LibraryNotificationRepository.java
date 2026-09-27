package com.librarymanagement.repository;
import com.librarymanagement.entity.LibraryNotification; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface LibraryNotificationRepository extends JpaRepository<LibraryNotification,Long>{ List<LibraryNotification> findByRecipientIdOrderByCreatedAtDesc(Long id); long countByRecipientIdAndIsReadFalse(Long id); Optional<LibraryNotification> findByIdAndRecipientId(Long id,Long recipientId); boolean existsByRecipientIdAndTypeAndMessage(Long recipientId,String type,String message); }
