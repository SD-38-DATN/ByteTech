package com.example.datn.DTO.banhangtaiquay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImeiBanHangTaiQuayDTO {
    private Integer id; // ✅ SỬA: Thêm ID field
    private String imei;
    private Integer trangThai;
    private String maSKU;
    private String maSKUPhuKien; // Thêm field cho phụ kiện
    private String tenSanPham;
    private String tenPhuKien;
    private Object sanPham; // Thêm thông tin sản phẩm cho tìm kiếm IMEI chính xác
}