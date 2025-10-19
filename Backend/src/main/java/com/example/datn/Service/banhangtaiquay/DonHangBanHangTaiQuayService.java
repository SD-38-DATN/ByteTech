  package com.example.datn.Service.banhangtaiquay;

import com.example.datn.DTO.dtoBanHangTaiQuay.*;
import com.example.datn.Model.*;
import com.example.datn.Repository.BanHangTaiQuay.*;
import com.example.datn.Repository.UsersRepository;
import com.example.datn.Repository.BienTheSanPhamRepository;
import com.example.datn.Repository.BienThePhuKienRepository;
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
