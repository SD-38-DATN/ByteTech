package com.example.datn.DTO.DonHang;



import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class DonHangDTO {
    private Integer maDonHang;
    private LocalDateTime ngayDat;
    private String trangThai;
    private BigDecimal tongTien;
    private String diaChiGiaoHang;
    private String soDienThoai;
    private String tenKhachHang; // Thêm tên khách hàng để hiển thị
    private List<ChiTietDonHangTraCuuDTO> chiTietDonHang;
}