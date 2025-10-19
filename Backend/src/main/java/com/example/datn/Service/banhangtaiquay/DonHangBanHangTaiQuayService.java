package com.example.datn.Service.banhangtaiquay;

import com.example.datn.DTO.dtoBanHangTaiQuay.*;
import com.example.datn.Model.*;
import com.example.datn.Repository.BanHangTaiQuay.*;
// import com.example.datn.Repository.UsersRepository; // ❌ SAI - không sử dụng
import com.example.datn.Repository.BienTheSanPhamRepository;
import com.example.datn.Repository.BienThePhuKienRepository;
import com.example.datn.Repository.BanHangTaiQuay.VoucherBanHangTaiQuayRepository;
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
public class DonHangBanHangTaiQuayService {

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
     * Logic: LUÔN TẠO ĐƠN HÀNG MỚI
     */
    @Transactional
    public DonHangBanHangTaiQuayDTO luuDon(LuuDonRequest request) {
        try {
            System.out.println("🔍 DEBUG: LuuDonRequest = " + request);
            System.out.println("🔍 DEBUG: UserId = " + request.getUserId());
            System.out.println("🔍 DEBUG: ChiTietDonHangs = " + request.getChiTietDonHangs());

            // ✅ LUÔN TẠO ĐƠN HÀNG MỚI - KHÔNG CẦN CHECK ĐƠN HÀNG ĐÃ LƯU
            Users user = usersRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + request.getUserId()));

            UserVoucher userVoucher = null;
            if (request.getUserVoucherId() != null) {
                userVoucher = new UserVoucher();
                userVoucher.setId(request.getUserVoucherId());
            }

            DonHang donHang = new DonHang();
            donHang.setUser(user);
            donHang.setNgayDat(LocalDateTime.now());
            donHang.setUserVoucher(userVoucher);
            System.out.println("🆕 Tạo đơn hàng mới (luôn tạo mới)");

            // Cập nhật thông tin đơn hàng
            donHang.setTrangThai(1); // Trạng thái: 1 = Chờ thanh toán
            donHang.setTongTien(request.getTongTien());
            donHang.setDiaChiGiaoHang(request.getDiaChiGiaoHang());
            donHang.setSoDienThoai(request.getSoDienThoai());
            donHang.setPhuongThucThanhToan(request.getPhuongThucThanhToan());
            donHang.setGhiChu(request.getGhiChu());

            // Cập nhật UserVoucher nếu có
            if (request.getUserVoucherId() != null) {
                UserVoucher userVoucherId = new UserVoucher();
                userVoucher.setId(request.getUserVoucherId());
                donHang.setUserVoucher(userVoucher);
            }

            DonHang savedDonHang = donHangRepository.save(donHang);

            // Tạo chi tiết đơn hàng (KHÔNG trừ kho)
            for (ChiTietDonHangBanHangTaiQuayDTO chiTietDTO : request.getChiTietDonHangs()) {
                ChiTietDonHang chiTiet = new ChiTietDonHang();
                chiTiet.setDonHang(savedDonHang);
                chiTiet.setSoLuong(chiTietDTO.getSoLuong());
                chiTiet.setGia(chiTietDTO.getGia());

                // Xử lý logic maSKU và maSKUPhuKien - LẤY TỪ DATABASE
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

                // Xử lý IMEI nếu có - CẬP NHẬT TRẠNG THÁI TẠM GIỮ
                if (chiTietDTO.getImeiId() != null) {
                    System.out.println("🔍 DEBUG: Xử lý IMEI với ID: " + chiTietDTO.getImeiId());
                    IMEI imei = imeiRepository.findById(chiTietDTO.getImeiId()).orElse(null);
                    if (imei != null) {
                        chiTiet.setImei(imei);
                        // Cập nhật trạng thái IMEI = 5 (tạm giữ)
                        imei.setTrangThai(5);
                        imeiRepository.save(imei);
                        System.out.println("✅ DEBUG: Đã cập nhật IMEI sang trạng thái tạm giữ");
                    } else {
                        System.out.println("⚠️ DEBUG: Không tìm thấy IMEI với ID: " + chiTietDTO.getImeiId());
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
     * Logic: LUÔN TẠO ĐƠN HÀNG MỚI
     */
    @Transactional
    public DonHangBanHangTaiQuayDTO thanhToan(ChotDonRequest request) {
        try {
            // ✅ LUÔN TẠO ĐƠN HÀNG MỚI - KHÔNG CẦN CHECK ĐƠN HÀNG ĐÃ LƯU
            Users user = usersRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + request.getUserId()));

            UserVoucher userVoucher = null;
            if (request.getUserVoucherId() != null) {
                userVoucher = new UserVoucher();
                userVoucher.setId(request.getUserVoucherId());
            }

            DonHang donHang = new DonHang();
            donHang.setUser(user);
            donHang.setNgayDat(LocalDateTime.now());
            donHang.setUserVoucher(userVoucher);
            System.out.println("🆕 Tạo đơn hàng mới (luôn tạo mới)");

            // Cập nhật thông tin đơn hàng
            donHang.setTrangThai(2); // Trạng thái: 2 = Đã thanh toán
            donHang.setTongTien(request.getTongTien());
            donHang.setDiaChiGiaoHang(request.getDiaChiGiaoHang());
            donHang.setSoDienThoai(request.getSoDienThoai());
            donHang.setPhuongThucThanhToan(request.getPhuongThucThanhToan());
            donHang.setGhiChu(request.getGhiChu());

            // Cập nhật UserVoucher nếu có
            if (request.getUserVoucherId() != null) {
                UserVoucher userVoucherId = new UserVoucher();
                userVoucher.setId(request.getUserVoucherId());
                donHang.setUserVoucher(userVoucher);
            }

            DonHang savedDonHang = donHangRepository.save(donHang);

            // Tạo chi tiết đơn hàng và TRỪ KHO
            for (ChiTietDonHangBanHangTaiQuayDTO chiTietDTO : request.getChiTietDonHangs()) {
                System.out.println("🔍 DEBUG: Xử lý chi tiết đơn hàng: " + chiTietDTO);
                System.out.println("🔍 DEBUG: Loại sản phẩm: " + chiTietDTO.getLoaiSanPham());
                System.out.println("🔍 DEBUG: MaSKU: " + chiTietDTO.getMaSKU());
                System.out.println("🔍 DEBUG: MaSKUPhuKien: " + chiTietDTO.getMaSKUPhuKien());

                ChiTietDonHang chiTiet = new ChiTietDonHang();
                chiTiet.setDonHang(savedDonHang);
                chiTiet.setSoLuong(chiTietDTO.getSoLuong());
                chiTiet.setGia(chiTietDTO.getGia());

                // Xử lý logic maSKU và maSKUPhuKien - LẤY TỪ DATABASE
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
                    IMEI imei = imeiRepository.findById(chiTietDTO.getImeiId()).orElse(null);
                    if (imei != null) {
                        chiTiet.setImei(imei);
                        // Cập nhật trạng thái IMEI = 0 (đã bán)
                        imei.setTrangThai(0);
                        imeiRepository.save(imei);
                        System.out.println("✅ DEBUG: Đã cập nhật IMEI sang trạng thái đã bán");
                    } else {
                        System.out.println("⚠️ DEBUG: Không tìm thấy IMEI với ID: " + chiTietDTO.getImeiId());
                    }
                } else {
                    System.out.println("ℹ️ DEBUG: Không có IMEI cho sản phẩm này");
                }

                chiTietDonHangRepository.save(chiTiet);

                // ✅ THÊM LOGIC MỚI: Trừ số lượng sản phẩm/phụ kiện nếu được yêu cầu
                if (Boolean.TRUE.equals(request.getUpdateProductQuantities())) {
                    System.out.println("🔍 DEBUG: Trừ số lượng sản phẩm/phụ kiện");

                    if ("sanpham".equals(chiTietDTO.getLoaiSanPham())) {
                        // Trừ số lượng sản phẩm chính
                        BienTheSanPham bienTheSanPham = bienTheSanPhamRepository.findByMaSKU(chiTietDTO.getMaSKU())
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể sản phẩm với SKU: " + chiTietDTO.getMaSKU()));

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
                    userVoucher = userVoucherList.get(0);
                    
                    // Xóa các bản ghi duplicate (giữ lại bản ghi đầu tiên)
                    for (int i = 1; i < userVoucherList.size(); i++) {
                        userVoucherRepository.delete(userVoucherList.get(i));
                        System.out.println("🗑️ DEBUG: Đã xóa bản ghi duplicate thứ " + (i + 1));
                    }
                } else if (userVoucherList.size() == 1) {
                    userVoucher = userVoucherList.get(0);
                } else {
                    userVoucher = null;
                }
                
                System.out.println("🔍 DEBUG: userVoucher cuối cùng: " + (userVoucher != null ? "EXISTS" : "NULL"));
                    
                    if (userVoucher == null) {
                        // Tạo mới UserVoucher nếu chưa có
                        System.out.println("🔍 DEBUG: Tạo mới UserVoucher cho user " + request.getUserId() + " và voucher " + request.getUserVoucherId());
                        
                        // Lấy User và Voucher objects
                        Users userObj = usersRepository.findById(request.getUserId())
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + request.getUserId()));
                        Voucher voucherObj = voucherRepository.findById(request.getUserVoucherId())
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher với ID: " + request.getUserVoucherId()));
                        
                        userVoucher = new UserVoucher();
                        userVoucher.setUser(userObj);
                        userVoucher.setVoucher(voucherObj);
                        userVoucher.setSoLanSuDung(1);
                        userVoucher.setNgayNhan(java.time.LocalDateTime.now());
                        userVoucher.setTrangThai(1);
                    } else {
                        // Cộng thêm 1 lần sử dụng
                        System.out.println("🔍 DEBUG: Cộng thêm 1 lần sử dụng cho UserVoucher hiện tại: " + userVoucher.getSoLanSuDung());
                        userVoucher.setSoLanSuDung(userVoucher.getSoLanSuDung() + 1);
                    }
                    
                    userVoucherRepository.save(userVoucher);
                    System.out.println("✅ DEBUG: Đã cộng số lần sử dụng UserVoucher: " + userVoucher.getSoLanSuDung());
                    
                } catch (Exception e) {
                    System.err.println("❌ ERROR: Lỗi khi xử lý UserVoucher: " + e.getMessage());
                    e.printStackTrace();
                    throw new RuntimeException("Lỗi khi xử lý UserVoucher: " + e.getMessage(), e);
                }
                
            } else {
                System.out.println("ℹ️ DEBUG: Không trừ số lượng voucher");
            }

            return convertToDTO(savedDonHang);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi thanh toán đơn hàng: " + e.getMessage(), e);
        }
    }

    /**
     * Convert DonHang entity to DTO
     */
    private DonHangBanHangTaiQuayDTO convertToDTO(DonHang donHang) {
        List<ChiTietDonHangBanHangTaiQuayDTO> chiTietDTOs = donHang.getChiTietDonHangs() != null ?
                donHang.getChiTietDonHangs().stream()
                        .map(this::convertChiTietToDTO)
                        .collect(Collectors.toList()) : null;

        // Tạo DTO bằng constructor thay vì builder
        DonHangBanHangTaiQuayDTO dto = new DonHangBanHangTaiQuayDTO();
        dto.setMaDonHang(donHang.getMaDonHang());
        dto.setUserId(donHang.getUser().getId());
        dto.setNgayDat(donHang.getNgayDat());
        dto.setTrangThai(donHang.getTrangThai());
        dto.setTongTien(donHang.getTongTien());
        dto.setDiaChiGiaoHang(donHang.getDiaChiGiaoHang());
        dto.setSoDienThoai(donHang.getSoDienThoai());
        dto.setPhuongThucThanhToan(donHang.getPhuongThucThanhToan());
        dto.setGhiChu(donHang.getGhiChu());
        dto.setUserVoucherId(donHang.getUserVoucher() != null ? donHang.getUserVoucher().getId() : null);
        dto.setChiTietDonHangs(chiTietDTOs);
        return dto;
    }

    /**
     * Convert ChiTietDonHang entity to DTO
     */
    private ChiTietDonHangBanHangTaiQuayDTO convertChiTietToDTO(ChiTietDonHang chiTiet) {
        String loaiSanPham = null;
        String maSKU = null;
        String maSKUPhuKien = null;

        if (chiTiet.getBienTheSanPham() != null) {
            loaiSanPham = "sanpham";
            maSKU = chiTiet.getBienTheSanPham().getMaSKU();
        } else if (chiTiet.getBienThePhuKien() != null) {
            loaiSanPham = "phukien";
            maSKUPhuKien = chiTiet.getBienThePhuKien().getMaSKUPhuKien();
        }

        // Tạo DTO bằng constructor thay vì builder
        ChiTietDonHangBanHangTaiQuayDTO dto = new ChiTietDonHangBanHangTaiQuayDTO();
        dto.setId(chiTiet.getId());
        dto.setMaDonHang(chiTiet.getDonHang().getMaDonHang());
        dto.setMaSKU(maSKU);
        dto.setMaSKUPhuKien(maSKUPhuKien);
        dto.setImeiId(chiTiet.getImei() != null ? chiTiet.getImei().getId() : null);
        dto.setSoLuong(chiTiet.getSoLuong());
        dto.setGia(chiTiet.getGia());
        dto.setLoaiSanPham(loaiSanPham);
        return dto;
    }
}
