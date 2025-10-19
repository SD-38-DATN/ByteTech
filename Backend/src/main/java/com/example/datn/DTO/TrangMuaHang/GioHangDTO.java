package com.example.datn.DTO.TrangMuaHang;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GioHangDTO {
    private Integer maGioHang;
    private Integer userId;
    private BigDecimal tongTien;
    private LocalDateTime ngayTao;
    private List<GioHangChiTietDTO> chiTietList;
}
