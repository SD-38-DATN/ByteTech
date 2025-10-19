package com.example.datn.Config;


import com.example.datn.DTO.TrangMuaHang.SendMessageRequest;
import com.example.datn.DTO.TrangMuaHang.WebSocketMessage;
import com.example.datn.Service.MessageService;

import com.example.datn.Service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketMessageController {

    private final MessageService messageService;
    private final WebSocketService webSocketService;
    private final AuthenticationService authService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload WebSocketMessage message, SimpMessageHeaderAccessor headerAccessor) {
        try {
            Authentication auth = (Authentication) headerAccessor.getUser();
            if (auth == null) {
                log.error("Unauthenticated WebSocket message attempt");
                return;
            }

            Integer senderId = authService.getCurrentUserId();

            log.info("WebSocket message received: from {} to {} - {}",
                    senderId, message.getReceiverId(), message.getContent());

            SendMessageRequest request = new SendMessageRequest();
            request.setReceiverId(message.getReceiverId());
            request.setContent(message.getContent());

            messageService.sendMessage(senderId, request);

        } catch (Exception e) {
            log.error("Error processing WebSocket message: ", e);
        }
    }

    @MessageMapping("/chat.addUser/{userId}")
    @SendTo("/topic/user/{userId}")
    public WebSocketMessage addUser(@DestinationVariable Integer userId,
                                    @Payload WebSocketMessage message,
                                    SimpMessageHeaderAccessor headerAccessor) {

        try {
            Integer currentUserId = authService.getCurrentUserId();
            if (!authService.isAdmin() && !currentUserId.equals(userId)) {
                log.error("User {} attempted to connect to channel for user {}", currentUserId, userId);
                return null;
            }

            log.info("User {} connected to chat", userId);

            WebSocketMessage notification = new WebSocketMessage();
            notification.setType("STATUS");
            notification.setSenderId(userId);
            notification.setContent("User connected");

            return notification;

        } catch (Exception e) {
            log.error("Error adding user to chat: ", e);
            return null;
        }
    }

    @SubscribeMapping("/topic/user/{userId}")
    public void subscribeToUserChannel(@DestinationVariable Integer userId,
                                       SimpMessageHeaderAccessor headerAccessor) {
        try {
            Integer currentUserId = authService.getCurrentUserId();
            if (!authService.isAdmin() && !currentUserId.equals(userId)) {
                log.error("Unauthorized subscription attempt by user {} to channel {}", currentUserId, userId);
                return;
            }

            log.info("User {} subscribed to their personal channel", userId);
        } catch (Exception e) {
            log.error("Error subscribing to user channel: ", e);
        }
    }

    @MessageMapping("/admin.broadcast")
    @SendTo("/topic/admins")
    public WebSocketMessage broadcastToAdmins(@Payload WebSocketMessage message,
                                              SimpMessageHeaderAccessor headerAccessor) {
        try {
            if (!authService.isAdmin()) {
                log.error("Non-admin user attempted to broadcast");
                return null;
            }

            log.info("Admin broadcast message: {}", message.getContent());
            return message;

        } catch (Exception e) {
            log.error("Error broadcasting to admins: ", e);
            return null;
        }
    }
}