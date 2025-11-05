package com.example.datn.Service.banhangtaiquay;

import com.example.datn.DTO.banhangtaiquay.ChiTietDonHangBanHangTaiQuayDTO;
import com.example.datn.DTO.banhangtaiquay.ChotDonRequest;
import com.example.datn.DTO.banhangtaiquay.DonHangBanHangTaiQuayDTO;
import com.example.datn.DTO.banhangtaiquay.LuuDonRequest;
import com.example.datn.Model.*;
import com.example.datn.Repository.UserVoucherRepository;
import com.example.datn.Repository.banhangtaiquay.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ThanhToanBanHangTaiQuayService {

    private final DonHangBanHangTaiQuayRepository donHangRepository;
    private final DonHangChiTietBanHangTaiQuayRepository chiTietDonHangRepository;
    private final IMEIBanHangTaiQuayRepository imeiRepository;
    private final UsersBanHangTaiQuayRepository usersRepository;
    private final BienTheSanPhamBanHangTaiQuayRepository bienTheSanPhamRepository;
    private final BienThePhuKienBanHangTaiQuayRepository bienThePhuKienRepository;
    private final VoucherBanHangTaiQuayRepository voucherRepository;
    private final UserVoucherRepository userVoucherRepository;

    /**
     * Lưu đơn hàng - KHÔNG trừ kho, KHÔNG giảm voucher
     * Trạng thái: Chờ thanh toán
     * Logic: KIỂM TRA maDonHang - TẠO MỚI hoặc CẬP NHẬT
     */
    @Transactional
    public DonHangBanHangTaiQuayDTO luuDon(LuuDonRequest request) {
        try {

            Users user = usersRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + request.getUserId()));
            UserVoucher userVoucher = null;

            if (request.getUserVoucherId() != null) {
                userVoucher = new UserVoucher();
                userVoucher.setId(request.getUserVoucherId());
            }

            DonHang donHang;
            //  KIỂM TRA maDonHang - TẠO MỚI hoặc CẬP NHẬT
            if (request.getMaDonHang() != null) {
                // CẬP NHẬT ĐƠN HÀNG CŨ
                donHang = donHangRepository.findByMaDonHang(request.getMaDonHang());
                if (donHang == null) {
                    throw new RuntimeException("Không tìm thấy đơn hàng với mã: " + request.getMaDonHang());
                }
                // Xóa toàn bộ chi tiết đơn hàng cũ
                chiTietDonHangRepository.deleteByDonHangId(request.getMaDonHang());
            } else {
                // TẠO ĐƠN HÀNG MỚI
                donHang = new DonHang();
                donHang.setUser(user);
                donHang.setNgayDat(LocalDateTime.now());
                donHang.setUserVoucher(userVoucher);
            }

            // Cập nhật thông tin đơn hàng
            donHang.setTrangThai(5); // Trạng thái: 5 = Chờ thanh toán
            donHang.setTongTien(request.getTongTien());
            donHang.setDiaChiGiaoHang(request.getDiaChiGiaoHang());
            donHang.setTenNguoiNhan(request.getTenNguoiNhan()); // ✅ SỬA: Lưu tên người nhận
            donHang.setSoDienThoai(request.getSoDienThoai());
            donHang.setPhuongThucThanhToan(request.getPhuongThucThanhToan());
            donHang.setGhiChu(request.getGhiChu());

            // Cập nhật UserVoucher nếu có
            if (request.getUserVoucherId() != null) {
                UserVoucher userVoucherObj = new UserVoucher();
                userVoucherObj.setId(request.getUserVoucherId());
                donHang.setUserVoucher(userVoucherObj);
            }

            DonHang savedDonHang = donHangRepository.save(donHang);

            // Tạo chi tiết đơn hàng (KHÔNG trừ kho)
            for (ChiTietDonHangBanHangTaiQuayDTO chiTietDTO : request.getChiTietDonHangs()) {
                ChiTietDonHang chiTiet = new ChiTietDonHang();
                chiTiet.setDonHang(savedDonHang);
                chiTiet.setSoLuong(chiTietDTO.getSoLuong());
                chiTiet.setGia(chiTietDTO.getGia());

                // Xử lý sản phẩm chính hoặc phụ kiện
                if ("sanpham".equals(chiTietDTO.getLoaiSanPham())) {
                    // Sản phẩm chính - tìm từ database
                    BienTheSanPham bienTheSanPham = bienTheSanPhamRepository.findByMaSKU(chiTietDTO.getMaSKU())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể sản phẩm với SKU: " + chiTietDTO.getMaSKU()));
                    chiTiet.setBienTheSanPham(bienTheSanPham);
                } else if ("phukien".equals(chiTietDTO.getLoaiSanPham())) {
                    // Phụ kiện - tìm từ database
                    BienThePhuKien bienThePhuKien = bienThePhuKienRepository.findByMaSKUPhuKien(chiTietDTO.getMaSKUPhuKien())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể phụ kiện với SKU: " + chiTietDTO.getMaSKUPhuKien()));
                    chiTiet.setBienThePhuKien(bienThePhuKien);

                }
                // Xử lý IMEI nếu có - GIỮ NGUYÊN TRẠNG THÁI IMEI
                if (chiTietDTO.getImeiId() != null) {

                    //  KIỂM TRA: Nếu ID là string temp (từ đơn hàng lưu)
                    if (chiTietDTO.getImeiId().toString().startsWith("temp_")) {
                        //  TÌM IMEI THEO SỐ IMEI thay vì bỏ qua
                        if (chiTietDTO.getImei() != null && !chiTietDTO.getImei().trim().isEmpty()) {
                            //  SỬA LỖI: Xử lý trường hợp có duplicate IMEI
                            List<IMEI> imeiList = imeiRepository.findAllByImeiAndTrangThai(chiTietDTO.getImei());
                            if (!imeiList.isEmpty()) {
                                // Lấy IMEI đầu tiên có trạng thái = 1
                                chiTiet.setImei(imeiList.get(0));
                            } else {
                                // Nếu không có IMEI nào có trạng thái = 1, lấy IMEI đầu tiên
                                List<IMEI> allImeiList = imeiRepository.findAllByImei(chiTietDTO.getImei());
                                if (!allImeiList.isEmpty()) {
                                    chiTiet.setImei(allImeiList.get(0));
                                }
                            }
                        }
                    } else {
                        // Xử lý IMEI với ID thực tế
                        try {
                            Integer imeiId = Integer.parseInt(chiTietDTO.getImeiId().toString());
                            IMEI imei = imeiRepository.findById(imeiId).orElse(null);
                            if (imei != null) {
                                chiTiet.setImei(imei);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("⚠️ DEBUG: IMEI ID không phải số nguyên: " + chiTietDTO.getImeiId());
                        }
                    }
                }
                chiTietDonHangRepository.save(chiTiet);
            }
            return convertToDTO(savedDonHang);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu đơn hàng: " + e.getMessage(), e);
        }
    }

    /**
     * Thanh toán đơn hàng - Cập nhật trạng thái từ "Chờ thanh toán" sang "Đã thanh toán"
     * IMEI status = 0 (đã bán)
     * Logic: KIỂM TRA maDonHang - TẠO MỚI hoặc CẬP NHẬT
     */
    @Transactional
    public DonHangBanHangTaiQuayDTO thanhToan(ChotDonRequest request) {
        try {

            Users user = usersRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + request.getUserId()));

            UserVoucher userVoucher = null;
            if (request.getUserVoucherId() != null) {
                userVoucher = new UserVoucher();
                userVoucher.setId(request.getUserVoucherId());
            }
            DonHang donHang;

            //  KIỂM TRA maDonHang - TẠO MỚI hoặc CẬP NHẬT
            if (request.getMaDonHang() != null) {
                //  CẬP NHẬT ĐƠN HÀNG CŨ (ĐÃ LƯU)
                donHang = donHangRepository.findByMaDonHang(request.getMaDonHang());
                if (donHang == null) {
                    throw new RuntimeException("Không tìm thấy đơn hàng với mã: " + request.getMaDonHang());
                }

                //  QUAN TRỌNG: Giải phóng IMEI cũ về trạng thái 1 (available)
                // Vì sản phẩm có thể đã thay đổi, cần giải phóng IMEI cũ trước khi xóa chi tiết
                for (ChiTietDonHang chiTietCu : donHang.getChiTietDonHangs()) {
                    if (chiTietCu.getImei() != null) {
                        chiTietCu.getImei().setTrangThai(1); // Available
                        imeiRepository.save(chiTietCu.getImei());
                    }
                }

                //  QUAN TRỌNG: Xóa toàn bộ chi tiết đơn hàng cũ
                // Vì sản phẩm có thể đã thay đổi (thêm/bớt sản phẩm)
                chiTietDonHangRepository.deleteByDonHangId(request.getMaDonHang());
            } else {
                //  TẠO ĐƠN HÀNG MỚI
                donHang = new DonHang();
                donHang.setUser(user);
                donHang.setNgayDat(LocalDateTime.now());
                donHang.setUserVoucher(userVoucher);
            }

            // Cập nhật thông tin đơn hàng
            donHang.setTrangThai(2); // Trạng thái: 2 = Đã thanh toán
            donHang.setTongTien(request.getTongTien());
            donHang.setDiaChiGiaoHang(request.getDiaChiGiaoHang());
            //  DEBUG: Log tenNguoiNhan trước khi lưu
            donHang.setTenNguoiNhan(request.getTenNguoiNhan()); // ✅ SỬA: Lưu tên người nhận
            donHang.setSoDienThoai(request.getSoDienThoai());
            donHang.setPhuongThucThanhToan(request.getPhuongThucThanhToan());
            donHang.setGhiChu(request.getGhiChu());
            // Cập nhật UserVoucher nếu có
            if (request.getUserVoucherId() != null) {
                UserVoucher userVoucherObj = new UserVoucher();
                userVoucherObj.setId(request.getUserVoucherId());
                donHang.setUserVoucher(userVoucherObj);
            }

            DonHang savedDonHang = donHangRepository.save(donHang);

            // TẠO CHI TIẾT ĐƠN HÀNG MỚI (cho cả đơn mới và đơn đã lưu)
            // Với đơn đã lưu, chi tiết cũ đã được xóa ở trên, giờ tạo chi tiết mới

            int chiTietIndex = 0;
            for (ChiTietDonHangBanHangTaiQuayDTO chiTietDTO : request.getChiTietDonHangs()) {
                chiTietIndex++;
                ChiTietDonHang chiTiet = new ChiTietDonHang();
                chiTiet.setDonHang(savedDonHang);
                chiTiet.setSoLuong(chiTietDTO.getSoLuong());
                chiTiet.setGia(chiTietDTO.getGia());

                // Xử lý sản phẩm chính hoặc phụ kiện
                if ("sanpham".equals(chiTietDTO.getLoaiSanPham())) {
                    // Sản phẩm chính - tìm từ database

                    BienTheSanPham bienTheSanPham = bienTheSanPhamRepository.findByMaSKU(chiTietDTO.getMaSKU())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể sản phẩm với SKU: " + chiTietDTO.getMaSKU()));
                    chiTiet.setBienTheSanPham(bienTheSanPham);

                } else if ("phukien".equals(chiTietDTO.getLoaiSanPham())) {
                    // Phụ kiện - tìm từ database

                    BienThePhuKien bienThePhuKien = bienThePhuKienRepository.findByMaSKUPhuKien(chiTietDTO.getMaSKUPhuKien())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể phụ kiện với SKU: " + chiTietDTO.getMaSKUPhuKien()));
                    chiTiet.setBienThePhuKien(bienThePhuKien);

                }

                // Xử lý IMEI nếu có - CẬP NHẬT TRẠNG THÁI ĐÃ BÁN
                if (chiTietDTO.getImeiId() != null) {

                    //  KIỂM TRA: Nếu ID là string temp (từ đơn hàng lưu)
                    if (chiTietDTO.getImeiId().toString().startsWith("temp_")) {
                        // TÌM IMEI THEO SỐ IMEI thay vì bỏ qua
                        if (chiTietDTO.getImei() != null && !chiTietDTO.getImei().trim().isEmpty()) {
                            // SỬA LỖI: Xử lý trường hợp có duplicate IMEI (đặc biệt với phụ kiện)
                            // Ưu tiên lấy IMEI có trạng thái = 1 (available) để đánh dấu đã bán
                            List<IMEI> imeiList = imeiRepository.findAllByImeiAndTrangThai(chiTietDTO.getImei());
                            IMEI imei = null;
                            if (!imeiList.isEmpty()) {
                                // Lấy IMEI đầu tiên có trạng thái = 1
                                imei = imeiList.get(0);
                            } else {
                                // Nếu không có IMEI nào có trạng thái = 1, lấy IMEI đầu tiên
                                List<IMEI> allImeiList = imeiRepository.findAllByImei(chiTietDTO.getImei());
                                if (!allImeiList.isEmpty()) {
                                    imei = allImeiList.get(0);
                                }
                            }
                            if (imei != null) {
                                chiTiet.setImei(imei);
                                //  THANH TOÁN: Chuyển IMEI về trạng thái 0 (đã bán)
                                imei.setTrangThai(0);
                                imeiRepository.save(imei);

                            }
                        }
                    } else {
                        // Xử lý IMEI với ID thực tế
                        try {
                            Integer imeiId = Integer.parseInt(chiTietDTO.getImeiId().toString());
                            IMEI imei = imeiRepository.findById(imeiId).orElse(null);
                            if (imei != null) {
                                chiTiet.setImei(imei);
                                //  THANH TOÁN: Chuyển IMEI về trạng thái 0 (đã bán)
                                imei.setTrangThai(0);
                                imeiRepository.save(imei);

                            }
                        } catch (NumberFormatException e) {
                            System.out.println("⚠️ DEBUG: IMEI ID không phải số nguyên: " + chiTietDTO.getImeiId());
                        }
                    }
                }

                ChiTietDonHang savedChiTiet = chiTietDonHangRepository.save(chiTiet);

                //  THÊM LOGIC MỚI: Trừ số lượng sản phẩm/phụ kiện nếu được yêu cầu
                if (Boolean.TRUE.equals(request.getUpdateProductQuantities())) {

                    if ("sanpham".equals(chiTietDTO.getLoaiSanPham())) {
                        // Trừ số lượng sản phẩm chính

                        BienTheSanPham bienTheSanPham = bienTheSanPhamRepository.findByMaSKU(chiTietDTO.getMaSKU())
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể sản phẩm với SKU: " + chiTietDTO.getMaSKU()));

                        if (bienTheSanPham.getSoLuong() < chiTietDTO.getSoLuong()) {
                            throw new RuntimeException("Sản phẩm " + bienTheSanPham.getSanPham().getTenSanPham() + " không đủ số lượng tồn.");
                        }

                        bienTheSanPham.setSoLuong(bienTheSanPham.getSoLuong() - chiTietDTO.getSoLuong());
                        bienTheSanPhamRepository.save(bienTheSanPham);

                    } else if ("phukien".equals(chiTietDTO.getLoaiSanPham())) {
                        // Trừ số lượng phụ kiện
                        BienThePhuKien bienThePhuKien = bienThePhuKienRepository.findByMaSKUPhuKien(chiTietDTO.getMaSKUPhuKien())
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể phụ kiện với SKU: " + chiTietDTO.getMaSKUPhuKien()));

                        if (bienThePhuKien.getSoLuong() < chiTietDTO.getSoLuong()) {
                            throw new RuntimeException("Phụ kiện " + bienThePhuKien.getPhuKien().getTenPhuKien() + " không đủ số lượng tồn.");
                        }

                        bienThePhuKien.setSoLuong(bienThePhuKien.getSoLuong() - chiTietDTO.getSoLuong());
                        bienThePhuKienRepository.save(bienThePhuKien);
                    }
                }
            }

            // THÊM LOGIC MỚI: Trừ số lượng voucher nếu được yêu cầu
            if (Boolean.TRUE.equals(request.getUpdateVoucherQuantities()) && request.getUserVoucherId() != null) {
                try {

                    Voucher voucher = voucherRepository.findById(request.getUserVoucherId())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher với ID: " + request.getUserVoucherId()));

                    if (voucher.getSoLanSuDung() <= 0) {
                        throw new RuntimeException("Voucher đã hết số lần sử dụng");
                    }
                    voucher.setSoLanSuDung(voucher.getSoLanSuDung() - 1);
                    voucherRepository.save(voucher);

                    // Tìm UserVoucher theo userId và voucherId
                    List<UserVoucher> userVoucherList = userVoucherRepository.findByUserIdAndVoucherId(request.getUserId(), request.getUserVoucherId());

                    // Xử lý trường hợp có nhiều bản ghi (duplicate data)
                    if (userVoucherList.size() > 1) {
                        // Sử dụng bản ghi đầu tiên
                        UserVoucher userVoucherToUpdate = userVoucherList.get(0);
                        userVoucherToUpdate.setSoLanSuDung(userVoucherToUpdate.getSoLanSuDung() + 1);
                        userVoucherRepository.save(userVoucherToUpdate);

                        // Xóa các bản ghi duplicate (từ index 1 trở đi)
                        for (int i = 1; i < userVoucherList.size(); i++) {
                            userVoucherRepository.delete(userVoucherList.get(i));
                        }

                    } else if (userVoucherList.size() == 1) {
                        // Trường hợp bình thường: có đúng 1 bản ghi
                        UserVoucher userVoucherToUpdate = userVoucherList.get(0);
                        userVoucherToUpdate.setSoLanSuDung(userVoucherToUpdate.getSoLanSuDung() + 1);
                        userVoucherRepository.save(userVoucherToUpdate);

                    } else {
                        // Trường hợp không tìm thấy UserVoucher
                        // Tạo UserVoucher mới
                        UserVoucher newUserVoucher = new UserVoucher();
                        newUserVoucher.setUser(user);
                        newUserVoucher.setVoucher(voucher);
                        newUserVoucher.setSoLanSuDung(1);
                        newUserVoucher.setTrangThai(1); // Đã sử dụng
                        userVoucherRepository.save(newUserVoucher);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Lỗi khi xử lý voucher: " + e.getMessage(), e);
                }
            }
            return convertToDTO(savedDonHang);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi thanh toán đơn hàng: " + e.getMessage(), e);
        }
    }

    /**
     * Convert DonHang entity to DTO
     */
    private DonHangBanHangTaiQuayDTO convertToDTO(DonHang donHang) {
        return DonHangBanHangTaiQuayDTO.builder()
                .maDonHang(donHang.getMaDonHang())
                .tongTien(donHang.getTongTien())
                .diaChiGiaoHang(donHang.getDiaChiGiaoHang())
                .soDienThoai(donHang.getSoDienThoai())
                .phuongThucThanhToan(donHang.getPhuongThucThanhToan())
                .ghiChu(donHang.getGhiChu())
                .ngayDat(donHang.getNgayDat())
                .trangThai(donHang.getTrangThai())
                .chiTietDonHangs(donHang.getChiTietDonHangs() != null ?
                        donHang.getChiTietDonHangs().stream()
                                .map(this::convertChiTietToDTO)
                                .collect(Collectors.toList()) :
                        List.of())
                .build();
    }

    /**
     * Convert ChiTietDonHang entity to DTO
     */
    private ChiTietDonHangBanHangTaiQuayDTO convertChiTietToDTO(ChiTietDonHang chiTiet) {
        return ChiTietDonHangBanHangTaiQuayDTO.builder()
                .id(chiTiet.getId())
                .maDonHang(chiTiet.getDonHang().getMaDonHang())
                .maSKU(chiTiet.getBienTheSanPham() != null ? chiTiet.getBienTheSanPham().getMaSKU() : null)
                .maSKUPhuKien(chiTiet.getBienThePhuKien() != null ? chiTiet.getBienThePhuKien().getMaSKUPhuKien() : null)
                .imeiId(chiTiet.getImei() != null ? chiTiet.getImei().getId() : null)
                .imei(chiTiet.getImei() != null ? chiTiet.getImei().getImei() : null)
                .soLuong(chiTiet.getSoLuong())
                .gia(chiTiet.getGia())
                .loaiSanPham(chiTiet.getBienTheSanPham() != null ? "sanpham" : "phukien")
                .build();
    }
}
