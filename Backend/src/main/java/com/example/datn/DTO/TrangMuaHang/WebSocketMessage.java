package com.example.datn.DTO.TrangMuaHang;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {
    private String type; // "CHAT", "NOTIFICATION", "STATUS"
    private Integer senderId;
    private Integer receiverId;
    private String content;
    private LocalDateTime timestamp;
    private String senderName;
}