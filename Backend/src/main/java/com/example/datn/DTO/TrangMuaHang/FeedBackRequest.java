package com.example.datn.DTO.TrangMuaHang;



import lombok.Data;

@Data
public class FeedBackRequest {
    private Integer maSanPham;   // ID sản phẩm được đánh giá
    private String noiDung;      // Nội dung đánh giá
    private Integer danhGia;     // Điểm 1-5 sao
}
