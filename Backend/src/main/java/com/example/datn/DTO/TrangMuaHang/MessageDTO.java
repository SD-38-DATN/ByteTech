package com.example.datn.DTO.TrangMuaHang;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long id;

    @NotNull(message = "Sender ID is required")
    private Integer senderId;

    @NotNull(message = "Receiver ID is required")
    private Integer receiverId;

    @NotBlank(message = "Message content cannot be empty")
    private String content;

    private LocalDateTime timestamp;
    private Boolean isRead;
    private String senderName;
    private String receiverName;
}