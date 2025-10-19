package com.example.datn.Config;



import com.example.datn.DTO.TrangMuaHang.ConversationDTO;
import com.example.datn.DTO.TrangMuaHang.MessageDTO;
import com.example.datn.DTO.TrangMuaHang.SendMessageRequest;
import com.example.datn.DTO.TrangMuaHang.UserDTO;
import com.example.datn.Service.ApiResponse;

import com.example.datn.Service.MessageListResponse;
import com.example.datn.Service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageService messageService;
    private final AuthenticationService authService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<MessageDTO>> sendMessage(
            @Valid @RequestBody SendMessageRequest request) {

        try {
            Integer senderId = authService.getCurrentUserId();
            MessageDTO message = messageService.sendMessage(senderId, request);

            log.info("Message sent from user {} to user {}", senderId, request.getReceiverId());
            return ResponseEntity.ok(ApiResponse.success("Message sent successfully", message));

        } catch (RuntimeException e) {
            log.error("Error sending message: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to send message: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error sending message: ", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Internal server error"));
        }
    }

    @GetMapping("/conversation")
    public ResponseEntity<ApiResponse<MessageListResponse>> getConversation(
            @RequestParam Integer otherUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            Integer currentUserId = authService.getCurrentUserId();

            MessageListResponse conversation = messageService.getConversation(
                    currentUserId, otherUserId, page, size);

            return ResponseEntity.ok(ApiResponse.success(conversation));

        } catch (RuntimeException e) {
            log.error("Error getting conversation: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get conversation: " + e.getMessage()));
        }
    }

    @PostMapping("/mark-read")
    public ResponseEntity<ApiResponse<String>> markMessagesAsRead(
            @RequestParam Integer fromUserId) {

        try {
            Integer currentUserId = authService.getCurrentUserId();
            messageService.markMessagesAsRead(fromUserId, currentUserId);

            return ResponseEntity.ok(ApiResponse.success("Messages marked as read"));

        } catch (RuntimeException e) {
            log.error("Error marking messages as read: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to mark messages as read: " + e.getMessage()));
        }
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadMessageCount() {
        try {
            Integer currentUserId = authService.getCurrentUserId();
            Long count = messageService.getUnreadMessageCount(currentUserId);

            return ResponseEntity.ok(ApiResponse.success(count));

        } catch (RuntimeException e) {
            log.error("Error getting unread message count: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get unread message count: " + e.getMessage()));
        }
    }

    @GetMapping("/admin/conversations")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<List<ConversationDTO>>> getAdminConversations() {

        try {
            Integer adminId = authService.getCurrentUserId();

            if (!authService.isAdmin()) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("Access denied: Admin role required"));
            }

            List<ConversationDTO> conversations = messageService.getAdminConversations(adminId);
            return ResponseEntity.ok(ApiResponse.success(conversations));

        } catch (RuntimeException e) {
            log.error("Error getting admin conversations: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get conversations: " + e.getMessage()));
        }
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        try {
            if (!authService.isAdmin()) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("Access denied: Admin role required"));
            }

            List<UserDTO> users = messageService.getAllUsers();
            return ResponseEntity.ok(ApiResponse.success(users));

        } catch (RuntimeException e) {
            log.error("Error getting users: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get users: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUserInfo() {
        try {
            UserDTO currentUser = messageService.getCurrentUserInfo(authService.getCurrentUserId());
            return ResponseEntity.ok(ApiResponse.success(currentUser));

        } catch (RuntimeException e) {
            log.error("Error getting current user info: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get user info: " + e.getMessage()));
        }
    }
}