package com.example.datn.DTO.TrangMuaHang;



import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FeedBackResponseDTO {
    private Integer id;
    private String noiDung;
    private Integer danhGia;
    private LocalDateTime ngayDanhGia;
    private String username;

    private String tenHienThi;
}
