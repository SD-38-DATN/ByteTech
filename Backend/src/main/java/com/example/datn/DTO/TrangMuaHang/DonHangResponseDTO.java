
package com.example.datn.DTO.TrangMuaHang;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DonHangResponseDTO {

    private String tenKhachHang;
    private Integer maDonHang;
    private Integer trangThai;
    private BigDecimal tongTien;
    private BigDecimal tongTienTruocGiam;
    private BigDecimal soTienGiam;
    private String diaChiGiaoHang;
    private String soDienThoai;
    private LocalDateTime ngayDat;

    private VoucherDTO voucher;

    private List<ChiTietDonHangDTO> chiTietList;

    @Data
    @Builder
    public static class ChiTietDonHangDTO {

        private Integer maChiTietDonHang;
        // Có thể là maSKU của sản phẩm hoặc maSKU của phụ kiện
        private String maSKU;

        // Cập nhật để có thể chứa tên sản phẩm hoặc tên phụ kiện
        private String tenSanPham;


        // Thêm các trường cho phụ kiện để phân biệt rõ ràng hơn
        private String maSKUPhuKien;
        private String tenPhuKien;
        private String thuocTinh;
        private Integer soLuong;
        private BigDecimal gia;


        private String imei;
    }

    @Data
    @Builder
    public static class VoucherDTO {
        private Integer voucherId;
        private String maVoucher;

        private String tenVoucher;       // tên gọi
        private String moTa;             // mô tả
        private Integer soLanSuDung;     // số lần sử dụng
        private String loaiGiam;
        private BigDecimal giaTriGiam;

        private BigDecimal dieuKienGiam; // điều kiện đơn tối thiểu

        private BigDecimal giamToiDa;    // giảm tối đa (khi giảm %)

        private LocalDateTime ngayBatDau;    // ngày bắt đầu
        private LocalDateTime ngayKetThuc;   // ngày kết thúc



    }
}