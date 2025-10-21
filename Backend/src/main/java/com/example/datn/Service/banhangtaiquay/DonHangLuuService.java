package com.example.datn.Service.BanHangTaiQuay;

import com.example.datn.DTO.banhangtaiquay.DonHangLuuDTO;
import com.example.datn.Model.DonHang;
import com.example.datn.Repository.banhangtaiquay.DonHangLuuRepository;
import com.example.datn.Service.banhangtaiquay.ImeiBanHangTaiQuayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DonHangLuuService {

    private final DonHangLuuRepository donHangLuuRepository;
    private final ImeiBanHangTaiQuayService imeiBanHangTaiQuayService;

    /**
     * Lấy danh sách đơn hàng đã lưu
     *
     * @param userId - ID của user (null để lấy tất cả)
     * @return List<DonHangLuuDTO> - Danh sách đơn hàng đã lưu
     */
    public List<DonHangLuuDTO> getDonHangLuu(Integer userId) {

        List<DonHang> donHangs = donHangLuuRepository.findDonHangLuuWithDetails(userId);
        return donHangs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public DonHangLuuDTO getDonHangLuuByMaDonHang(Integer maDonHang) {
        log.info("Lấy chi tiết đơn hàng đã lưu: {}", maDonHang);

        DonHang donHang = donHangLuuRepository.findDonHangLuuByMaDonHang(maDonHang);

        if (donHang == null) {
            return null;
        }

        return convertToDTO(donHang);
    }

    /**
     * Đếm số lượng đơn hàng đã lưu
     *
     * @param userId - ID của user (null để đếm tất cả)
     * @return Long - Số lượng đơn hàng
     */
    public Long countDonHangLuu(Integer userId) {
        log.info("Đếm số lượng đơn hàng đã lưu cho user: {}", userId);
        return donHangLuuRepository.countDonHangLuuByUser(userId);
    }

    /**
     * Chuyển đổi từ Entity sang DTO
     *
     * @param donHang - Entity DonHang
     * @return DonHangLuuDTO - DTO
     */
    private DonHangLuuDTO convertToDTO(DonHang donHang) {
        log.info("Converting DonHang to DTO: maDonHang={}, user={}, tenHienThi={}",
                donHang.getMaDonHang(),
                donHang.getUser() != null ? donHang.getUser().getUsername() : "NULL",
                donHang.getUser() != null ? donHang.getUser().getTenHienThi() : "NULL");

        return DonHangLuuDTO.builder()
                .maDonHang(donHang.getMaDonHang())
                .soDienThoai(donHang.getSoDienThoai())
                .diaChiGiaoHang(donHang.getDiaChiGiaoHang())
                .tongTien(donHang.getTongTien())
                .ngayDat(donHang.getNgayDat())
                .ghiChu(donHang.getGhiChu())
                .tenNhanVien(donHang.getUser() != null ? donHang.getUser().getTenHienThi() : null)
                .username(donHang.getUser() != null ? donHang.getUser().getUsername() : null)
                .chiTietDonHangs(convertChiTietDonHangsToDTO(donHang.getChiTietDonHangs()))
                .build();
    }


    /**
     * Chuyển đổi ChiTietDonHang Entity sang DTO
     */
    private List<DonHangLuuDTO.ChiTietDonHangLuuDTO> convertChiTietDonHangsToDTO(List<com.example.datn.Model.ChiTietDonHang> chiTietDonHangs) {
        if (chiTietDonHangs == null) return null;

        return chiTietDonHangs.stream()
                .map(this::convertChiTietDonHangToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Chuyển đổi ChiTietDonHang Entity sang DTO
     */
    private DonHangLuuDTO.ChiTietDonHangLuuDTO convertChiTietDonHangToDTO(com.example.datn.Model.ChiTietDonHang chiTiet) {
        return DonHangLuuDTO.ChiTietDonHangLuuDTO.builder()
                .id(chiTiet.getId())
                .soLuong(chiTiet.getSoLuong())
                .gia(chiTiet.getGia())
                .maSKU(chiTiet.getBienTheSanPham() != null ? chiTiet.getBienTheSanPham().getMaSKU() :
                        chiTiet.getBienThePhuKien() != null ? chiTiet.getBienThePhuKien().getMaSKUPhuKien() : null)
                .tenSanPham(chiTiet.getBienTheSanPham() != null ? chiTiet.getBienTheSanPham().getSanPham().getTenSanPham() :
                        chiTiet.getBienThePhuKien() != null ? chiTiet.getBienThePhuKien().getPhuKien().getTenPhuKien() : null)
                .imei(chiTiet.getImei() != null ? chiTiet.getImei().getImei() : null)
                .loaiSanPham(chiTiet.getBienTheSanPham() != null ? "sanpham" : "phukien")
                .thuocTinh(chiTiet.getBienTheSanPham() != null ?
                        chiTiet.getBienTheSanPham().getThuocTinhList() != null && !chiTiet.getBienTheSanPham().getThuocTinhList().isEmpty() ?
                                chiTiet.getBienTheSanPham().getThuocTinhList().stream()
                                        .map(tt -> tt.getTenThuocTinh() + ": " + tt.getTenThuocTinhBienThe())
                                        .reduce((a, b) -> a + ", " + b).orElse("") : "" :
                        chiTiet.getBienThePhuKien() != null ?
                                chiTiet.getBienThePhuKien().getThuocTinhPhuKienList() != null && !chiTiet.getBienThePhuKien().getThuocTinhPhuKienList().isEmpty() ?
                                        chiTiet.getBienThePhuKien().getThuocTinhPhuKienList().stream()
                                                .map(tt -> tt.getTenThuocTinh() + ": " + tt.getGiaTriThuocTinh())
                                                .reduce((a, b) -> a + ", " + b).orElse("") : "" : null)
                .giaTriThuocTinh(chiTiet.getBienTheSanPham() != null ?
                        chiTiet.getBienTheSanPham().getThuocTinhList() != null && !chiTiet.getBienTheSanPham().getThuocTinhList().isEmpty() ?
                                chiTiet.getBienTheSanPham().getThuocTinhList().stream()
                                        .map(tt -> tt.getTenThuocTinhBienThe())
                                        .reduce((a, b) -> a + ", " + b).orElse("") : "" :
                        chiTiet.getBienThePhuKien() != null ?
                                chiTiet.getBienThePhuKien().getThuocTinhPhuKienList() != null && !chiTiet.getBienThePhuKien().getThuocTinhPhuKienList().isEmpty() ?
                                        chiTiet.getBienThePhuKien().getThuocTinhPhuKienList().stream()
                                                .map(tt -> tt.getGiaTriThuocTinh())
                                                .reduce((a, b) -> a + ", " + b).orElse("") : "" : null)
                .build();
    }

    /**
     * Xóa đơn hàng đã lưu (chuyển trạng thái 5 → 3 và IMEI về 1)
     *
     * @param maDonHang - Mã đơn hàng
     */
    @Transactional
    public void xoaDonHangLuu(Integer maDonHang) {

        // Tìm đơn hàng theo mã
        DonHang donHang = donHangLuuRepository.findByMaDonHang(maDonHang);
        if (donHang == null) {
            throw new RuntimeException("Không tìm thấy đơn hàng với mã: " + maDonHang);
        }

        // Kiểm tra trạng thái hiện tại
        if (donHang.getTrangThai() != 5) {
            throw new RuntimeException("Đơn hàng không ở trạng thái đã lưu (5). Trạng thái hiện tại: " + donHang.getTrangThai());
        }

        // Thu thập tất cả IMEI từ chi tiết đơn hàng
        List<String> imeiList = new ArrayList<>();
        for (com.example.datn.Model.ChiTietDonHang chiTiet : donHang.getChiTietDonHangs()) {
            if (chiTiet.getImei() != null && chiTiet.getImei().getImei() != null) {
                imeiList.add(chiTiet.getImei().getImei());
            }
        }


        // Chuyển trạng thái IMEI về 1 (có sẵn)
        if (!imeiList.isEmpty()) {
            try {
                boolean success = imeiBanHangTaiQuayService.capNhatTrangThaiNhieuImeiVe1(imeiList);
                if (success) {
                    log.info("✅ Đã chuyển {} IMEI về trạng thái 1", imeiList.size());
                } else {
                    log.warn("⚠️ Không thể chuyển trạng thái IMEI");
                }
            } catch (Exception e) {
                log.error("Lỗi khi chuyển trạng thái IMEI: {}", e.getMessage());
                throw new RuntimeException("Lỗi khi chuyển trạng thái IMEI: " + e.getMessage());
            }
        }

        // Chuyển trạng thái đơn hàng thành 3 (đã hủy)
        donHang.setTrangThai(3);
        log.info("Đã chuyển trạng thái đơn hàng {} từ 5 thành 3 (đã hủy)", maDonHang);
    }

}
