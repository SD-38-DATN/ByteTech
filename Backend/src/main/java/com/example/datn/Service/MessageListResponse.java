package com.example.datn.Service;

import com.example.datn.DTO.TrangMuaHang.MessageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageListResponse {
    private List<MessageDTO> messages;
    private Integer totalPages;
    private Long totalElements;
    private Integer currentPage;
}