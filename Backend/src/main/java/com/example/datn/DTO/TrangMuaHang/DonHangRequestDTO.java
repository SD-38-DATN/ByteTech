package com.example.datn.DTO.TrangMuaHang;

import lombok.Data;

import java.util.List;

@Data
public class DonHangRequestDTO {
    private String diaChiGiaoHang;
    private String soDienThoai;
    private String phuongThucThanhToan;
    private String ghiChu;

    private Integer userVoucherId;
//    private List<Integer> gioHangChiTietIds;

    // ✅ Thêm trường cho các ID chi tiết giỏ hàng của sản phẩm
    private List<Integer> gioHangChiTietSanPhamIds;


    private List<Integer> gioHangChiTietPhuKienIds; // ✅ Bổ sung cho phụ kiện


}
