package com.example.datn.DTO.TrangMuaHang;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GioHangChiTietDTO {
    private Integer id;
    private String maSKU;
    private String tenSanPham;
    private Integer soLuong;
    private BigDecimal gia;
    private Integer soLuongTon; // mới: số lượng tồn kho
    private Map<String, String> thuocTinh;
}
