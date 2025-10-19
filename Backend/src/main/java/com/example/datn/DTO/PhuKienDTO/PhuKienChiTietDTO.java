package com.example.datn.DTO.PhuKienDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
//public class PhuKienChiTietDTO {
//    private Integer maPhuKien;
//    private String tenPhuKien;
//    private String moTa;
//    private String tenDanhMucPhuKien;
//    private List<BienThePhuKienDTO> bienThePhuKienList;
//}
public class PhuKienChiTietDTO {

    // --- Thông tin cơ bản của Phụ kiện ---
    private Integer maPhuKien;
    private String tenPhuKien;
    private String moTa;
    private Integer maDanhMucPhuKien; // ✅ Bổ sung
    private String tenDanhMucPhuKien;

    // --- Danh sách các biến thể của phụ kiện ---
    private List<BienThePhuKienDTO> bienTheList;

    // --- Danh sách các loại thông số và thông số kỹ thuật chi tiết ---
    private List<LoaiThongSoDTO> loaiThongSoList; // ✅ Bổ sung

    // --- Danh sách feedback của người dùng cho phụ kiện này ---
    private List<FeedbackDTO> feedbackList; // ✅ Bổ sung


    // ================== CÁC DTO CON LỒNG NHAU ==================

    /**
     * DTO cho Biến Thể của Phụ Kiện
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BienThePhuKienDTO {
        private String maSKUPhuKien;
        private BigDecimal gia;
        private Integer soLuong;
        private Integer trangThai;
        // Sửa lại từ Map sang List<DTO> cho nhất quán và linh hoạt hơn
        private List<ThuocTinhPhuKienDTO> thuocTinhList;
    }

    /**
     * DTO cho Thuộc Tính của Biến Thể Phụ Kiện
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ThuocTinhPhuKienDTO {
        private String tenThuocTinh;
        private String giaTriThuocTinh;
    }

    /**
     * DTO cho Loại Thông Số Kỹ Thuật (VD: "Kết nối", "Thiết kế")
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoaiThongSoDTO {
        private String tenLoaiThongSo;
        private List<ThongSoKyThuatDTO> thongSoList;
    }

    /**
     * DTO cho Thông Số Kỹ Thuật chi tiết (VD: "Cổng sạc" - "USB-C")
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ThongSoKyThuatDTO {
        private String tenThongSo;
        private String giaTriThongSo;
        private Integer trangThai;
    }

    /**
     * DTO cho Phản Hồi (Feedback) từ người dùng
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FeedbackDTO {
        private Integer userId; // Hoặc có thể là tên người dùng
        private String noiDung;
        private int danhGia; // Sao (1-5)
        private LocalDateTime ngayDanhGia;
        // Bạn có thể thêm các trường khác như tenNguoiDung nếu cần
    }
}