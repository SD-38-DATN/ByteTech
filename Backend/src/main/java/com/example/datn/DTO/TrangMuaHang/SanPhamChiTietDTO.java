//
//
//package com.example.datn.DTO;
//
//import lombok.*;
//import java.math.BigDecimal;
//import java.util.List;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class SanPhamChiTietDTO {
//    // Thông tin cơ bản của sản phẩm chính
//    private Integer maSanPham;
//    private String tenSanPham;
//
//    private Integer maDanhMuc;   // ✅ thêm trường này để xuất theo mã danh mục
//
//    private String tenDanhMuc;
//    private String moTa;
//
//
//    // Danh sách biến thể của sản phẩm chính
//    private List<BienTheDTO> bienTheList;
//
//    // Danh sách thông số kỹ thuật của sản phẩm chính
//
//
//    // ================== PHẦN BỔ SUNG MỚI CHO PHỤ KIỆN ==================
//    // Danh sách các phụ kiện đi kèm của sản phẩm
//    private List<PhuKienDTO> phuKienList;
//    // ===================================================================
//
//    /**
//     * DTO con cho Thông số kỹ thuật
//     */
//
//    private List<LoaiThongSoDTO> loaiThongSoList;
//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class LoaiThongSoDTO {
//        private String tenLoaiThongSo; // Ví dụ: Kích thước, Trọng lượng
//        private List<ThongSoKyThuatDTO> thongSoList;
//    }
//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class ThongSoKyThuatDTO {
//        private String tenThongSo;
//        private String giaTriThongSo;
//
//        private Integer maSanPham;      // ✅ gắn trực tiếp với mã sản phẩm
//        private Integer trangThai; // ✅ thêm để frontend lọc
//    }
//
//    // ================== CÁC DTO CON MỚI CHO PHỤ KIỆN ==================
//
//    /**
//     * DTO con cho Phụ Kiện (Accessory)
//     */
//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class PhuKienDTO {
//        private Integer maPhuKien;
//        private String tenPhuKien;
//        private String moTa;
//        // Mỗi phụ kiện có một danh sách các biến thể (ví dụ: màu sắc)
//        private List<BienThePhuKienDTO> bienTheList;
//    }
//
//    /**
//     * DTO con cho Biến Thể Phụ Kiện (Accessory Variant)
//     */
//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class BienThePhuKienDTO {
//        private String maSKUPhuKien;
//        private BigDecimal gia;
//        private Integer soLuong;
//        // Mỗi biến thể phụ kiện có một danh sách các thuộc tính
//        private List<ThuocTinhPhuKienDTO> thuocTinhList;
//    }
//
//    /**
//     * DTO con cho Thuộc Tính Phụ Kiện (Accessory Attribute)
//     */
//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class ThuocTinhPhuKienDTO {
//        private String tenThuocTinh;
//        private String giaTriThuocTinh;
//    }
//    // =================================================================
//}
//
//


package com.example.datn.DTO.TrangMuaHang;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO tổng hợp, chứa toàn bộ thông tin chi tiết của một sản phẩm,
 * bao gồm các biến thể, thuộc tính, thông số kỹ thuật và các phụ kiện đi kèm.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SanPhamChiTietDTO {

    // --- Thông tin cơ bản của Sản phẩm (từ bảng SanPham) ---
    private Integer maSanPham;
    private String tenSanPham;

    private BigDecimal gia ;
    private String moTa;
    private Integer maDanhMuc;
    private String tenDanhMuc; // Lấy qua join với bảng DanhMuc

    // --- Danh sách các biến thể của sản phẩm chính (từ BienTheSanPham & ThuocTinh) ---
    private List<BienTheDTO> bienTheList;

    // --- Danh sách các loại thông số và thông số kỹ thuật chi tiết (từ LoaiThongSo & ThongSoKyThuat) ---
    private List<LoaiThongSoDTO> loaiThongSoList;

    // --- Danh sách các phụ kiện đi kèm của sản phẩm (từ PhuKien, BienThePhuKien, ThuocTinhPhuKien) ---
    private List<PhuKienDTO> phuKienList;


    // ================== CÁC DTO CON LỒNG NHAU ==================

    /**
     * DTO cho Biến Thể Sản Phẩm (tương ứng với BienTheSanPham)
     * Mỗi biến thể có giá, số lượng và các thuộc tính riêng (VD: màu Đỏ, size M)
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BienTheDTO {
        private String maSKU;
        private BigDecimal gia;

        private BigDecimal giaKhongKhuyenMai; // ✅ thêm giá gốc chưa giảm
        private Integer soLuong;
        private Integer trangThai;
        private Integer maKhuyenMai; // ID của khuyến mãi áp dụng cho biến thể này
        private List<ThuocTinhDTO> thuocTinhList; // Danh sách các thuộc tính của biến thể này
    }

    /**
     * DTO cho Thuộc Tính của Biến Thể (tương ứng với ThuocTinh)
     * Ví dụ: tenThuocTinh="Màu sắc", giaTriThuocTinh="Đỏ"
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ThuocTinhDTO {
        private String tenThuocTinh;
        // Đổi tên từ 'tenThuocTinhBienThe' trong DB thành 'giaTriThuocTinh' cho dễ hiểu
        private String giaTriThuocTinh;
    }

    /**
     * DTO cho Loại Thông Số Kỹ Thuật (tương ứng với LoaiThongSo)
     * Ví dụ: "Màn hình", "Hiệu năng", "Kết nối"
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoaiThongSoDTO {
        private String tenLoaiThongSo;
        private List<ThongSoKyThuatDTO> thongSoList; // Danh sách các thông số thuộc loại này
    }

    /**
     * DTO cho Thông Số Kỹ Thuật chi tiết (tương ứng với ThongSoKyThuat)
     * Ví dụ: tenThongSo="Độ phân giải", giaTriThongSo="Full HD+"
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ThongSoKyThuatDTO {
        private String tenThongSo;
        private String giaTriThongSo;
        private Integer maSanPham;
        private Integer trangThai;
    }

    /**
     * DTO cho Phụ Kiện (tương ứng với PhuKien)
     * Một sản phẩm có thể có nhiều phụ kiện đi kèm (VD: Ốp lưng, Sạc)
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PhuKienDTO {
        private Integer maPhuKien;
        private String tenPhuKien;
        private String moTa;
        // Mỗi phụ kiện lại có các biến thể riêng (VD: Ốp lưng màu Đen, Ốp lưng màu Trắng)
        private List<BienThePhuKienDTO> bienTheList;
    }

    /**
     * DTO cho Biến Thể của Phụ Kiện (tương ứng với BienThePhuKien)
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BienThePhuKienDTO {
        private String maSKUPhuKien;
        private BigDecimal gia;
        private Integer soLuong;
        // Mỗi biến thể phụ kiện có các thuộc tính (VD: Màu sắc = Đen)
        private List<ThuocTinhPhuKienDTO> thuocTinhList;
    }

    /**
     * DTO cho Thuộc Tính của Biến Thể Phụ Kiện (tương ứng với ThuocTinhPhuKien)
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ThuocTinhPhuKienDTO {
        private String tenThuocTinh;
        private String giaTriThuocTinh;
    }
}