package com.example.datn.DTO.banhangtaiquay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamBanHangTaiQuayDTO {
    private String maSKU;
    private String tenSanPham;
    private String maSKUPhuKien; // ✅ THÊM: SKU phụ kiện
    private String tenPhuKien; // ✅ THÊM: Tên phụ kiện
    private BigDecimal gia;
    private String thuocTinh;
    private Integer soLuong;
    private String loai; // "Sản phẩm chính" hoặc "Phụ kiện"
    private Integer trangThai; // Trạng thái sản phẩm
    private Integer soIMEI; // ✅ THÊM: Số lượng IMEI
    private List<ImeiBanHangTaiQuayDTO> imeiList; // Danh sách IMEI của sản phẩm
}