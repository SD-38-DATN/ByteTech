package com.example.datn.Service;



import com.example.datn.DTO.TrangMuaHang.MessageDTO;
import com.example.datn.DTO.TrangMuaHang.WebSocketMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendMessageToUser(Integer userId, MessageDTO message) {
        WebSocketMessage wsMessage = new WebSocketMessage();
        wsMessage.setType("CHAT");
        wsMessage.setSenderId(message.getSenderId());
        wsMessage.setReceiverId(message.getReceiverId());
        wsMessage.setContent(message.getContent());
        wsMessage.setTimestamp(message.getTimestamp());
        wsMessage.setSenderName(message.getSenderName());

        String destination = "/topic/user/" + userId;
        messagingTemplate.convertAndSend(destination, wsMessage);

        log.info("WebSocket message sent to user {} at destination {}", userId, destination);
    }

    public void sendNotificationToUser(Integer userId, String notification) {
        WebSocketMessage wsMessage = new WebSocketMessage();
        wsMessage.setType("NOTIFICATION");
        wsMessage.setContent(notification);

        String destination = "/topic/user/" + userId;
        messagingTemplate.convertAndSend(destination, wsMessage);

        log.info("Notification sent to user {}: {}", userId, notification);
    }

    public void broadcastToAdmins(WebSocketMessage message) {
        messagingTemplate.convertAndSend("/topic/admins", message);
        log.info("Message broadcasted to all admins");
    }
}