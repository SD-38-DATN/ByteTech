package com.example.datn.DTO.TrangMuaHang;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {
    private Integer userId;
    private String userDisplayName;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Integer unreadCount;
}