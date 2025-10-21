package com.example.datn.Service.banhangtaiquay;

import com.example.datn.DTO.banhangtaiquay.ChiTietDonHangBanHangTaiQuayDTO;
import com.example.datn.DTO.banhangtaiquay.ChotDonRequest;
import com.example.datn.DTO.banhangtaiquay.DonHangBanHangTaiQuayDTO;
import com.example.datn.DTO.banhangtaiquay.LuuDonRequest;
import com.example.datn.Model.*;
import com.example.datn.Repository.banhangtaiquay.*;
import com.example.datn.Repository.UserVoucherRepository;
import com.example.datn.Model.Users;
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
            System.out.println("🔍 DEBUG: LuuDonRequest = " + request);
            System.out.println("🔍 DEBUG: UserId = " + request.getUserId());
            System.out.println("🔍 DEBUG: MaDonHang = " + request.getMaDonHang());
            System.out.println("🔍 DEBUG: ChiTietDonHangs = " + request.getChiTietDonHangs());

            Users user = usersRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + request.getUserId()));

            UserVoucher userVoucher = null;
            if (request.getUserVoucherId() != null) {
                userVoucher = new UserVoucher();
                userVoucher.setId(request.getUserVoucherId());
            }

            DonHang donHang;

            // ✅ KIỂM TRA maDonHang - TẠO MỚI hoặc CẬP NHẬT
            if (request.getMaDonHang() != null) {
                // ✅ CẬP NHẬT ĐƠN HÀNG CŨ
                System.out.println("🔄 Cập nhật đơn hàng cũ: " + request.getMaDonHang());
                donHang = donHangRepository.findByMaDonHang(request.getMaDonHang());
                if (donHang == null) {
                    throw new RuntimeException("Không tìm thấy đơn hàng với mã: " + request.getMaDonHang());
                }

                // Xóa toàn bộ chi tiết đơn hàng cũ
                chiTietDonHangRepository.deleteByDonHangId(request.getMaDonHang());
                System.out.println("🗑️ Đã xóa toàn bộ chi tiết đơn hàng cũ");

            } else {
                // ✅ TẠO ĐƠN HÀNG MỚI
                System.out.println("🆕 Tạo đơn hàng mới");
                donHang = new DonHang();
                donHang.setUser(user);
                donHang.setNgayDat(LocalDateTime.now());
                donHang.setUserVoucher(userVoucher);
            }

            // Cập nhật thông tin đơn hàng
            donHang.setTrangThai(5); // Trạng thái: 5 = Chờ thanh toán
            donHang.setTongTien(request.getTongTien());
            donHang.setDiaChiGiaoHang(request.getDiaChiGiaoHang());
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
                    System.out.println("🔍 DEBUG: Xử lý sản phẩm chính với SKU: " + chiTietDTO.getMaSKU());
                    BienTheSanPham bienTheSanPham = bienTheSanPhamRepository.findByMaSKU(chiTietDTO.getMaSKU())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể sản phẩm với SKU: " + chiTietDTO.getMaSKU()));
                    chiTiet.setBienTheSanPham(bienTheSanPham);
                    System.out.println("✅ DEBUG: Đã tìm thấy sản phẩm chính: " + bienTheSanPham.getMaSKU());
                } else if ("phukien".equals(chiTietDTO.getLoaiSanPham())) {
                    // Phụ kiện - tìm từ database
                    System.out.println("🔍 DEBUG: Xử lý phụ kiện với SKU: " + chiTietDTO.getMaSKUPhuKien());
                    BienThePhuKien bienThePhuKien = bienThePhuKienRepository.findByMaSKUPhuKien(chiTietDTO.getMaSKUPhuKien())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể phụ kiện với SKU: " + chiTietDTO.getMaSKUPhuKien()));
                    chiTiet.setBienThePhuKien(bienThePhuKien);
                    System.out.println("✅ DEBUG: Đã tìm thấy phụ kiện: " + bienThePhuKien.getMaSKUPhuKien());
                } else {
                    System.out.println("⚠️ DEBUG: Loại sản phẩm không xác định: " + chiTietDTO.getLoaiSanPham());
                }

                // Xử lý IMEI nếu có - GIỮ NGUYÊN TRẠNG THÁI IMEI
                if (chiTietDTO.getImeiId() != null) {
                    System.out.println("🔍 DEBUG: Xử lý IMEI với ID: " + chiTietDTO.getImeiId());

                    // ✅ KIỂM TRA: Nếu ID là string temp (từ đơn hàng lưu)
                    if (chiTietDTO.getImeiId().toString().startsWith("temp_")) {
                        System.out.println("ℹ️ DEBUG: IMEI ID là temp string, tìm IMEI theo số IMEI");
                        // ✅ TÌM IMEI THEO SỐ IMEI thay vì bỏ qua
                        if (chiTietDTO.getImei() != null && !chiTietDTO.getImei().trim().isEmpty()) {
                            IMEI imei = imeiRepository.findByImei(chiTietDTO.getImei()).orElse(null);
                            if (imei != null) {
                                chiTiet.setImei(imei);
                                // ✅ LƯU ĐƠN: GIỮ NGUYÊN trạng thái IMEI (không thay đổi)
                                System.out.println("✅ DEBUG: Giữ nguyên trạng thái IMEI: " + imei.getTrangThai());
                            } else {
                                System.out.println("⚠️ DEBUG: Không tìm thấy IMEI với số: " + chiTietDTO.getImei());
                            }
                        } else {
                            System.out.println("⚠️ DEBUG: Không có số IMEI để tìm");
                        }
                    } else {
                        // Xử lý IMEI với ID thực tế
                        try {
                            Integer imeiId = Integer.parseInt(chiTietDTO.getImeiId().toString());
                            IMEI imei = imeiRepository.findById(imeiId).orElse(null);
                            if (imei != null) {
                                chiTiet.setImei(imei);
                                // ✅ LƯU ĐƠN: GIỮ NGUYÊN trạng thái IMEI (không thay đổi)
                                System.out.println("✅ DEBUG: Giữ nguyên trạng thái IMEI: " + imei.getTrangThai());
                            } else {
                                System.out.println("⚠️ DEBUG: Không tìm thấy IMEI với ID: " + imeiId);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("⚠️ DEBUG: IMEI ID không phải số nguyên: " + chiTietDTO.getImeiId());
                        }
                    }
                } else {
                    System.out.println("ℹ️ DEBUG: Không có IMEI cho sản phẩm này");
                }

                chiTietDonHangRepository.save(chiTiet);
            }

            // ✅ LƯU ĐƠN: Không xử lý trừ số lượng sản phẩm/phụ kiện và voucher
            System.out.println("ℹ️ DEBUG: Lưu đơn - Không trừ số lượng sản phẩm/phụ kiện và voucher");

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
            System.out.println("🔍 DEBUG: ChotDonRequest = " + request);
            System.out.println("🔍 DEBUG: UserId = " + request.getUserId());
            System.out.println("🔍 DEBUG: MaDonHang = " + request.getMaDonHang());
            System.out.println("🔍 DEBUG: ChiTietDonHangs = " + request.getChiTietDonHangs());

            Users user = usersRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + request.getUserId()));

            UserVoucher userVoucher = null;
            if (request.getUserVoucherId() != null) {
                userVoucher = new UserVoucher();
                userVoucher.setId(request.getUserVoucherId());
            }

            DonHang donHang;

            // ✅ KIỂM TRA maDonHang - TẠO MỚI hoặc CẬP NHẬT
            if (request.getMaDonHang() != null) {
                // ✅ CẬP NHẬT ĐƠN HÀNG CŨ (ĐÃ LƯU)
                System.out.println("🔄 Thanh toán đơn hàng đã lưu: " + request.getMaDonHang());
                donHang = donHangRepository.findByMaDonHang(request.getMaDonHang());
                if (donHang == null) {
                    throw new RuntimeException("Không tìm thấy đơn hàng với mã: " + request.getMaDonHang());
                }

                // ✅ QUAN TRỌNG: Giải phóng IMEI cũ về trạng thái 1 (available)
                // Vì sản phẩm có thể đã thay đổi, cần giải phóng IMEI cũ trước khi xóa chi tiết
                System.out.println("🔄 Giải phóng IMEI cũ về trạng thái 1 (available)");
                for (ChiTietDonHang chiTietCu : donHang.getChiTietDonHangs()) {
                    if (chiTietCu.getImei() != null) {
                        chiTietCu.getImei().setTrangThai(1); // Available
                        imeiRepository.save(chiTietCu.getImei());
                        System.out.println("✅ Đã giải phóng IMEI: " + chiTietCu.getImei().getImei());
                    }
                }

                // ✅ QUAN TRỌNG: Xóa toàn bộ chi tiết đơn hàng cũ
                // Vì sản phẩm có thể đã thay đổi (thêm/bớt sản phẩm)
                System.out.println("🗑️ Xóa toàn bộ chi tiết đơn hàng cũ");
                chiTietDonHangRepository.deleteByDonHangId(request.getMaDonHang());
                System.out.println("✅ Đã xóa chi tiết đơn hàng cũ");

            } else {
                // ✅ TẠO ĐƠN HÀNG MỚI
                System.out.println("🆕 Tạo đơn hàng mới");
                donHang = new DonHang();
                donHang.setUser(user);
                donHang.setNgayDat(LocalDateTime.now());
                donHang.setUserVoucher(userVoucher);
            }

            // Cập nhật thông tin đơn hàng
            donHang.setTrangThai(2); // Trạng thái: 2 = Đã thanh toán
            donHang.setTongTien(request.getTongTien());
            donHang.setDiaChiGiaoHang(request.getDiaChiGiaoHang());
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

            // ✅ TẠO CHI TIẾT ĐƠN HÀNG MỚI (cho cả đơn mới và đơn đã lưu)
            // Với đơn đã lưu, chi tiết cũ đã được xóa ở trên, giờ tạo chi tiết mới
            System.out.println("📝 Tạo chi tiết đơn hàng mới");
            System.out.println("🔍 DEBUG: Số lượng chi tiết từ frontend: " + request.getChiTietDonHangs().size());
            
            int chiTietIndex = 0;
            for (ChiTietDonHangBanHangTaiQuayDTO chiTietDTO : request.getChiTietDonHangs()) {
                chiTietIndex++;
                System.out.println("\n📦 Xử lý chi tiết #" + chiTietIndex + ":");
                System.out.println("  - maSKU: " + chiTietDTO.getMaSKU());
                System.out.println("  - maSKUPhuKien: " + chiTietDTO.getMaSKUPhuKien());
                System.out.println("  - loaiSanPham: " + chiTietDTO.getLoaiSanPham());
                System.out.println("  - soLuong: " + chiTietDTO.getSoLuong());
                ChiTietDonHang chiTiet = new ChiTietDonHang();
                chiTiet.setDonHang(savedDonHang);
                chiTiet.setSoLuong(chiTietDTO.getSoLuong());
                chiTiet.setGia(chiTietDTO.getGia());

                // Xử lý sản phẩm chính hoặc phụ kiện
                if ("sanpham".equals(chiTietDTO.getLoaiSanPham())) {
                    // Sản phẩm chính - tìm từ database
                    System.out.println("🔍 DEBUG: Xử lý sản phẩm chính với SKU: " + chiTietDTO.getMaSKU());
                    BienTheSanPham bienTheSanPham = bienTheSanPhamRepository.findByMaSKU(chiTietDTO.getMaSKU())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể sản phẩm với SKU: " + chiTietDTO.getMaSKU()));
                    chiTiet.setBienTheSanPham(bienTheSanPham);
                    System.out.println("✅ DEBUG: Đã tìm thấy sản phẩm chính: " + bienTheSanPham.getMaSKU());
                } else if ("phukien".equals(chiTietDTO.getLoaiSanPham())) {
                    // Phụ kiện - tìm từ database
                    System.out.println("🔍 DEBUG: Xử lý phụ kiện với SKU: " + chiTietDTO.getMaSKUPhuKien());
                    BienThePhuKien bienThePhuKien = bienThePhuKienRepository.findByMaSKUPhuKien(chiTietDTO.getMaSKUPhuKien())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể phụ kiện với SKU: " + chiTietDTO.getMaSKUPhuKien()));
                    chiTiet.setBienThePhuKien(bienThePhuKien);
                    System.out.println("✅ DEBUG: Đã tìm thấy phụ kiện: " + bienThePhuKien.getMaSKUPhuKien());
                } else {
                    System.out.println("⚠️ DEBUG: Loại sản phẩm không xác định: " + chiTietDTO.getLoaiSanPham());
                }

                // Xử lý IMEI nếu có - CẬP NHẬT TRẠNG THÁI ĐÃ BÁN
                if (chiTietDTO.getImeiId() != null) {
                    System.out.println("🔍 DEBUG: Xử lý IMEI với ID: " + chiTietDTO.getImeiId());

                    // ✅ KIỂM TRA: Nếu ID là string temp (từ đơn hàng lưu)
                    if (chiTietDTO.getImeiId().toString().startsWith("temp_")) {
                        System.out.println("ℹ️ DEBUG: IMEI ID là temp string, tìm IMEI theo số IMEI");
                        // ✅ TÌM IMEI THEO SỐ IMEI thay vì bỏ qua
                        if (chiTietDTO.getImei() != null && !chiTietDTO.getImei().trim().isEmpty()) {
                            IMEI imei = imeiRepository.findByImei(chiTietDTO.getImei()).orElse(null);
                            if (imei != null) {
                                chiTiet.setImei(imei);
                                // ✅ THANH TOÁN: Chuyển IMEI về trạng thái 0 (đã bán)
                                imei.setTrangThai(0);
                                imeiRepository.save(imei);
                                System.out.println("✅ DEBUG: Đã tìm thấy và cập nhật IMEI theo số: " + chiTietDTO.getImei());
                            } else {
                                System.out.println("⚠️ DEBUG: Không tìm thấy IMEI với số: " + chiTietDTO.getImei());
                            }
                        } else {
                            System.out.println("⚠️ DEBUG: Không có số IMEI để tìm");
                        }
                    } else {
                        // Xử lý IMEI với ID thực tế
                        try {
                            Integer imeiId = Integer.parseInt(chiTietDTO.getImeiId().toString());
                            IMEI imei = imeiRepository.findById(imeiId).orElse(null);
                            if (imei != null) {
                                chiTiet.setImei(imei);
                                // ✅ THANH TOÁN: Chuyển IMEI về trạng thái 0 (đã bán)
                                imei.setTrangThai(0);
                                imeiRepository.save(imei);
                                System.out.println("✅ DEBUG: Đã cập nhật IMEI sang trạng thái đã bán (0)");
                            } else {
                                System.out.println("⚠️ DEBUG: Không tìm thấy IMEI với ID: " + imeiId);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("⚠️ DEBUG: IMEI ID không phải số nguyên: " + chiTietDTO.getImeiId());
                        }
                    }
                } else {
                    System.out.println("ℹ️ DEBUG: Không có IMEI cho sản phẩm này");
                }

                ChiTietDonHang savedChiTiet = chiTietDonHangRepository.save(chiTiet);
                System.out.println("✅ Đã lưu chi tiết #" + chiTietIndex + " với ID: " + savedChiTiet.getId());

                // ✅ THÊM LOGIC MỚI: Trừ số lượng sản phẩm/phụ kiện nếu được yêu cầu
                if (Boolean.TRUE.equals(request.getUpdateProductQuantities())) {
                    System.out.println("🔍 DEBUG: Trừ số lượng sản phẩm/phụ kiện");

                    if ("sanpham".equals(chiTietDTO.getLoaiSanPham())) {
                        // Trừ số lượng sản phẩm chính
                        System.out.println("🔍 DEBUG: Tìm sản phẩm với SKU: " + chiTietDTO.getMaSKU());
                        BienTheSanPham bienTheSanPham = bienTheSanPhamRepository.findByMaSKU(chiTietDTO.getMaSKU())
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể sản phẩm với SKU: " + chiTietDTO.getMaSKU()));

                        System.out.println("🔍 DEBUG: Sản phẩm tìm thấy: " + bienTheSanPham.getSanPham().getTenSanPham() + 
                                          ", Số lượng hiện tại: " + bienTheSanPham.getSoLuong() + 
                                          ", Cần trừ: " + chiTietDTO.getSoLuong());

                        if (bienTheSanPham.getSoLuong() < chiTietDTO.getSoLuong()) {
                            throw new RuntimeException("Sản phẩm " + bienTheSanPham.getSanPham().getTenSanPham() + " không đủ số lượng tồn.");
                        }

                        bienTheSanPham.setSoLuong(bienTheSanPham.getSoLuong() - chiTietDTO.getSoLuong());
                        bienTheSanPhamRepository.save(bienTheSanPham);
                        System.out.println("✅ DEBUG: Đã trừ số lượng sản phẩm: " + chiTietDTO.getSoLuong());

                    } else if ("phukien".equals(chiTietDTO.getLoaiSanPham())) {
                        // Trừ số lượng phụ kiện
                        BienThePhuKien bienThePhuKien = bienThePhuKienRepository.findByMaSKUPhuKien(chiTietDTO.getMaSKUPhuKien())
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể phụ kiện với SKU: " + chiTietDTO.getMaSKUPhuKien()));

                        if (bienThePhuKien.getSoLuong() < chiTietDTO.getSoLuong()) {
                            throw new RuntimeException("Phụ kiện " + bienThePhuKien.getPhuKien().getTenPhuKien() + " không đủ số lượng tồn.");
                        }

                        bienThePhuKien.setSoLuong(bienThePhuKien.getSoLuong() - chiTietDTO.getSoLuong());
                        bienThePhuKienRepository.save(bienThePhuKien);
                        System.out.println("✅ DEBUG: Đã trừ số lượng phụ kiện: " + chiTietDTO.getSoLuong());
                    }
                } else {
                    System.out.println("ℹ️ DEBUG: Không trừ số lượng sản phẩm/phụ kiện");
                }
            }
            
            System.out.println("✅ Đã tạo xong tất cả " + chiTietIndex + " chi tiết đơn hàng");

            // ✅ THÊM LOGIC MỚI: Trừ số lượng voucher nếu được yêu cầu
            if (Boolean.TRUE.equals(request.getUpdateVoucherQuantities()) && request.getUserVoucherId() != null) {
                try {
                    System.out.println("🔍 DEBUG: Trừ số lượng voucher");

                    Voucher voucher = voucherRepository.findById(request.getUserVoucherId())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher với ID: " + request.getUserVoucherId()));

                    if (voucher.getSoLanSuDung() <= 0) {
                        throw new RuntimeException("Voucher đã hết số lần sử dụng");
                    }

                    voucher.setSoLanSuDung(voucher.getSoLanSuDung() - 1);
                    voucherRepository.save(voucher);
                    System.out.println("✅ DEBUG: Đã trừ số lượng voucher: " + voucher.getSoLanSuDung());

                    // ✅ THÊM LOGIC MỚI: Cộng số lần sử dụng trong UserVoucher
                    System.out.println("🔍 DEBUG: Cộng số lần sử dụng UserVoucher");
                    System.out.println("🔍 DEBUG: request.getUserId() = " + request.getUserId());
                    System.out.println("🔍 DEBUG: request.getUserVoucherId() = " + request.getUserVoucherId());

                    // Tìm UserVoucher theo userId và voucherId
                    List<UserVoucher> userVoucherList = userVoucherRepository.findByUserIdAndVoucherId(request.getUserId(), request.getUserVoucherId());
                    System.out.println("🔍 DEBUG: userVoucherList tìm được: " + userVoucherList.size() + " bản ghi");

                    // Xử lý trường hợp có nhiều bản ghi (duplicate data)
                    if (userVoucherList.size() > 1) {
                        System.out.println("⚠️ WARNING: Tìm thấy " + userVoucherList.size() + " bản ghi UserVoucher duplicate!");
                        System.out.println("🔍 DEBUG: Sẽ sử dụng bản ghi đầu tiên và xóa các bản ghi duplicate");

                        // Sử dụng bản ghi đầu tiên
                        UserVoucher userVoucherToUpdate = userVoucherList.get(0);
                        userVoucherToUpdate.setSoLanSuDung(userVoucherToUpdate.getSoLanSuDung() + 1);
                        userVoucherRepository.save(userVoucherToUpdate);
                        System.out.println("✅ DEBUG: Đã cập nhật UserVoucher đầu tiên: " + userVoucherToUpdate.getSoLanSuDung());

                        // Xóa các bản ghi duplicate (từ index 1 trở đi)
                        for (int i = 1; i < userVoucherList.size(); i++) {
                            userVoucherRepository.delete(userVoucherList.get(i));
                            System.out.println("🗑️ DEBUG: Đã xóa UserVoucher duplicate: " + userVoucherList.get(i).getId());
                        }

                    } else if (userVoucherList.size() == 1) {
                        // Trường hợp bình thường: có đúng 1 bản ghi
                        UserVoucher userVoucherToUpdate = userVoucherList.get(0);
                        userVoucherToUpdate.setSoLanSuDung(userVoucherToUpdate.getSoLanSuDung() + 1);
                        userVoucherRepository.save(userVoucherToUpdate);
                        System.out.println("✅ DEBUG: Đã cập nhật UserVoucher: " + userVoucherToUpdate.getSoLanSuDung());

                    } else {
                        // Trường hợp không tìm thấy UserVoucher
                        System.out.println("⚠️ WARNING: Không tìm thấy UserVoucher với userId=" + request.getUserId() + " và voucherId=" + request.getUserVoucherId());
                        System.out.println("🔍 DEBUG: Tạo UserVoucher mới");

                        // Tạo UserVoucher mới
                        UserVoucher newUserVoucher = new UserVoucher();
                        newUserVoucher.setUser(user);
                        newUserVoucher.setVoucher(voucher);
                        newUserVoucher.setSoLanSuDung(1);
                        newUserVoucher.setTrangThai(1); // Đã sử dụng
                        userVoucherRepository.save(newUserVoucher);
                        System.out.println("✅ DEBUG: Đã tạo UserVoucher mới với soLanSuDung=1");
                    }

                } catch (Exception e) {
                    System.out.println("❌ ERROR: Lỗi khi xử lý voucher: " + e.getMessage());
                    e.printStackTrace();
                    throw new RuntimeException("Lỗi khi xử lý voucher: " + e.getMessage(), e);
                }
            } else {
                System.out.println("ℹ️ DEBUG: Không trừ số lượng voucher");
            }

            return convertToDTO(savedDonHang);

        } catch (Exception e) {
            System.out.println("❌ ERROR: Lỗi chi tiết khi thanh toán đơn hàng: " + e.getMessage());
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
