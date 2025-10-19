package com.example.datn.Repository;

import com.example.datn.Model.Message;
import com.example.datn.Model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Lấy cuộc hội thoại giữa user và admin
    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.id = :userId1 AND m.receiver.id = :userId2) OR " +
            "(m.sender.id = :userId2 AND m.receiver.id = :userId1) " +
            "ORDER BY m.timestamp DESC")
    Page<Message> findConversationBetweenUsers(
            @Param("userId1") Integer userId1,
            @Param("userId2") Integer userId2,
            Pageable pageable);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver.id = :receiverId AND m.isRead = false")
    Long countUnreadMessages(@Param("receiverId") Integer receiverId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.sender.id = :senderId AND m.receiver.id = :receiverId AND m.isRead = false")
    Long countUnreadMessagesBetweenUsers(@Param("senderId") Integer senderId, @Param("receiverId") Integer receiverId);

    @Query("SELECT m FROM Message m WHERE m.receiver.id = :receiverId AND m.isRead = false")
    List<Message> findUnreadMessages(@Param("receiverId") Integer receiverId);

    // Tìm cuộc hội thoại gần nhất với mỗi user (dành cho admin)
    @Query("SELECT DISTINCT u FROM Users u WHERE u.id IN " +
            "(SELECT DISTINCT CASE WHEN m.sender.id = :adminId THEN m.receiver.id ELSE m.sender.id END " +
            "FROM Message m WHERE m.sender.id = :adminId OR m.receiver.id = :adminId) " +
            "AND u.role.roleName = 'USER'")
    List<Users> findUsersWithConversations(@Param("adminId") Integer adminId);
}
