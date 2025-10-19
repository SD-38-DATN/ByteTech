package com.example.datn.DTO.TrangMuaHang;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {
    @NotNull(message = "Receiver ID is required")
    private Integer receiverId;

    @NotBlank(message = "Message content cannot be empty")
    private String content;
}