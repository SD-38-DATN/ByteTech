

package com.example.datn.Service; // Đừng quên giữ đúng package của bạn

import com.example.datn.Config.JwtUtil;

import com.example.datn.DTO.DonHang.ImeiResponseDTO;
import com.example.datn.DTO.DonHang.XacNhanDonHangRequestDTO;
import com.example.datn.DTO.TrangMuaHang.DonHangRequestDTO;
import com.example.datn.DTO.TrangMuaHang.DonHangResponseDTO;
import com.example.datn.Model.*;
import com.example.datn.Repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DonHangService {
    private final DonHangRepository donHangRepo;
    private final ChiTietDonHangRepository chiTietDonHangRepo;
    private final GioHangRepository gioHangRepo;
    private final GioHangChiTietRepository gioHangChiTietRepo;
    private final UsersRepository usersRepo;
    private final UserVoucherRepository userVoucherRepository;
    private final BienTheSanPhamRepository bienTheRepo;
    private final BienThePhuKienRepository bienThePhuKienRepo; // ✅ Thêm repository cho biến thể phụ kiện
    private final JwtUtil jwtUtil;



    @Transactional
    public DonHangResponseDTO taoDonHang(String token, DonHangRequestDTO req) {
        // 1. Lấy username từ token
        String username = jwtUtil.extractUsername(token);

        // 2. Tìm user trong DB
        Users user = usersRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        // 3. Lấy giỏ hàng và các item được chọn
        List<GioHangChiTiet> allSelectedItems = new ArrayList<>();
        if (req.getGioHangChiTietSanPhamIds() != null && !req.getGioHangChiTietSanPhamIds().isEmpty()) {
            allSelectedItems.addAll(gioHangChiTietRepo.findAllById(req.getGioHangChiTietSanPhamIds()));
        }
        if (req.getGioHangChiTietPhuKienIds() != null && !req.getGioHangChiTietPhuKienIds().isEmpty()) {
            allSelectedItems.addAll(gioHangChiTietRepo.findAllById(req.getGioHangChiTietPhuKienIds()));
        }

        if (allSelectedItems.isEmpty()) {
            throw new RuntimeException("Không có sản phẩm hoặc phụ kiện nào được chọn từ giỏ hàng.");
        }

        // 4. Tạo đơn hàng mới
        DonHang donHang = new DonHang();
        donHang.setUser(user);
        donHang.setDiaChiGiaoHang(
                req.getDiaChiGiaoHang() != null ? req.getDiaChiGiaoHang() : user.getDiaChiGiaoHang()
        );
        donHang.setSoDienThoai(
                req.getSoDienThoai() != null ? req.getSoDienThoai() : user.getSoDienThoai()
        );
        donHang.setPhuongThucThanhToan(req.getPhuongThucThanhToan());
        donHang.setGhiChu(req.getGhiChu());
        donHang.setTrangThai(0); // Set trạng thái ban đầu

        // Gắn voucher nếu có
        UserVoucher userVoucher = null;
        if (req.getUserVoucherId() != null) {
            userVoucher = userVoucherRepository.findById(req.getUserVoucherId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy userVoucher"));
            donHang.setUserVoucher(userVoucher);
        }

        // Lưu đơn hàng trước để có ID
        DonHang savedDonHang = donHangRepo.save(donHang);

        // 5. Tạo chi tiết đơn hàng và trừ tồn kho (LOGIC ĐÃ ĐƯỢC CẬP NHẬT)
        BigDecimal tongTien = BigDecimal.ZERO;
        List<ChiTietDonHang> chiTietDonHangs = new ArrayList<>();

        for (GioHangChiTiet ghct : allSelectedItems) {
            // Tính tổng tiền trước khi vào vòng lặp
            tongTien = tongTien.add(ghct.getGia().multiply(BigDecimal.valueOf(ghct.getSoLuong())));

            // ✅ BẮT ĐẦU LOGIC MỚI: Dùng vòng lặp để "rã" số lượng thành nhiều bản ghi
            for (int i = 0; i < ghct.getSoLuong(); i++) {
                ChiTietDonHang ctdh = new ChiTietDonHang();
                ctdh.setDonHang(savedDonHang);
                ctdh.setSoLuong(1); // QUAN TRỌNG: Mỗi bản ghi chi tiết giờ chỉ có số lượng là 1
                ctdh.setGia(ghct.getGia()); // Giá cho một sản phẩm

                // Gán biến thể cho từng chi tiết đơn hàng được tạo ra
                if (ghct.getBienThe() != null) {
                    ctdh.setBienTheSanPham(ghct.getBienThe());
                } else if (ghct.getBienThePhuKien() != null) {
                    ctdh.setBienThePhuKien(ghct.getBienThePhuKien());
                }
                chiTietDonHangs.add(ctdh);
            }
            // ✅ KẾT THÚC LOGIC MỚI

            // Trừ tồn kho tổng số lượng một lần cho mỗi loại sản phẩm/phụ kiện
            if (ghct.getBienThe() != null) { // Là sản phẩm
                BienTheSanPham bienThe = ghct.getBienThe();
                if (bienThe.getSoLuong() < ghct.getSoLuong()) {
                    throw new RuntimeException("Sản phẩm " + bienThe.getSanPham().getTenSanPham() + " không đủ số lượng tồn.");
                }
                bienThe.setSoLuong(bienThe.getSoLuong() - ghct.getSoLuong());
                bienTheRepo.save(bienThe);
            } else if (ghct.getBienThePhuKien() != null) { // Là phụ kiện
                BienThePhuKien bienThePhuKien = ghct.getBienThePhuKien();
                if (bienThePhuKien.getSoLuong() < ghct.getSoLuong()) {
                    throw new RuntimeException("Phụ kiện " + bienThePhuKien.getPhuKien().getTenPhuKien() + " không đủ số lượng tồn.");
                }
                bienThePhuKien.setSoLuong(bienThePhuKien.getSoLuong() - ghct.getSoLuong());
                bienThePhuKienRepo.save(bienThePhuKien);
            }
        }

        // Lưu tất cả các chi tiết đơn hàng đã được tạo
        chiTietDonHangRepo.saveAll(chiTietDonHangs);


        if (userVoucher != null) {
            Voucher voucher = userVoucher.getVoucher();
            BigDecimal giamGia = BigDecimal.ZERO;

            // Kiểm tra điều kiện áp dụng
            if (voucher.getDieuKienGiam() == null || tongTien.compareTo(voucher.getDieuKienGiam()) >= 0) {

                if (voucher.getLoaiGiam() == 0) {
                    // Giảm số tiền cố định
                    giamGia = voucher.getGiaTriGiam();
                } else if (voucher.getLoaiGiam() == 1) {
                    // Giảm theo %
                    // 👉 Ở DB giaTriGiam lưu dạng 0.1 = 10%
                    giamGia = tongTien.multiply(voucher.getGiaTriGiam());
                }

                // Giới hạn giảm tối đa
                if (voucher.getGiamToiDa() != null) {
                    giamGia = giamGia.min(voucher.getGiamToiDa());
                }

                // Trừ số lần sử dụng
                int newSoLanSuDung = userVoucher.getSoLanSuDung() - 1;
                userVoucher.setSoLanSuDung(newSoLanSuDung);

                // Trừ tiền
                tongTien = tongTien.subtract(giamGia);
                if (tongTien.compareTo(BigDecimal.ZERO) < 0) {
                    tongTien = BigDecimal.ZERO;
                }
            }
        }

        savedDonHang.setTongTien(tongTien);
        donHangRepo.save(savedDonHang);

        savedDonHang.setTongTien(tongTien);
        donHangRepo.save(savedDonHang);

        // 7. Xóa các chi tiết giỏ hàng đã đặt
        gioHangChiTietRepo.deleteAll(allSelectedItems);

        // 8. Build response DTO
        return DonHangResponseDTO.builder()
                .maDonHang(savedDonHang.getMaDonHang())
                .trangThai(savedDonHang.getTrangThai())
                .tongTien(savedDonHang.getTongTien())
                .diaChiGiaoHang(savedDonHang.getDiaChiGiaoHang())
                .soDienThoai(savedDonHang.getSoDienThoai())
                .ngayDat(savedDonHang.getNgayDat())
                .chiTietList(
                        chiTietDonHangs.stream()
                                .map(ct -> {
                                    if (ct.getBienTheSanPham() != null) {
                                        return DonHangResponseDTO.ChiTietDonHangDTO.builder()
                                                .maSKU(ct.getBienTheSanPham().getMaSKU())
                                                .tenSanPham(ct.getBienTheSanPham().getSanPham().getTenSanPham())
                                                .soLuong(ct.getSoLuong())
                                                .gia(ct.getGia())
                                                .maChiTietDonHang(ct.getId()) // Trả về ID cho mỗi dòng
                                                .build();
                                    } else {
                                        return DonHangResponseDTO.ChiTietDonHangDTO.builder()
                                                .maSKUPhuKien(ct.getBienThePhuKien().getMaSKUPhuKien())
                                                .tenPhuKien(ct.getBienThePhuKien().getPhuKien().getTenPhuKien())
                                                .soLuong(ct.getSoLuong())
                                                .gia(ct.getGia())
                                                .maChiTietDonHang(ct.getId()) // Trả về ID cho mỗi dòng
                                                .build();
                                    }
                                }).collect(Collectors.toList())
                )
                .build();
    }

    private final DonHangRepository donHangRepository;


    public List<DonHangResponseDTO> getDonHangTheoTrangThai(Integer trangThai) {
        // Gọi đến phương thức repository mới
        List<DonHang> donHangList = donHangRepository.findByTrangThaiWithDetails(trangThai);

        if (donHangList.isEmpty()) {
            return Collections.emptyList();
        }

        // Tái sử dụng logic chuyển đổi DonHang -> DTO có sẵn
        return donHangList.stream()
                .map(this::buildResponseDTOFromDonHang)
                .collect(Collectors.toList());
    }


    public List<DonHangResponseDTO> getDonHangCuaUserTheoTrangThai(String username, Integer trangThai) {
        List<DonHang> donHangList = donHangRepository.findByUserAndTrangThaiWithDetails(username, trangThai);

        if (donHangList.isEmpty()) {
            return Collections.emptyList();
        }

        return donHangList.stream()
                .map(this::buildResponseDTOFromDonHang)
                .collect(Collectors.toList());
    }



    /**
     * Hàm helper chịu trách nhiệm chuyển đổi một DonHang Entity sang DonHangResponseDTO.
     */
    private DonHangResponseDTO buildResponseDTOFromDonHang(DonHang donHang) {

        // Chuyển đổi danh sách ChiTietDonHang entity thành danh sách ChiTietDonHangDTO
        List<DonHangResponseDTO.ChiTietDonHangDTO> chiTietDTOList = donHang.getChiTietDonHangs().stream().map(ctdh -> {
            DonHangResponseDTO.ChiTietDonHangDTO.ChiTietDonHangDTOBuilder builder = DonHangResponseDTO.ChiTietDonHangDTO.builder()
                    .soLuong(ctdh.getSoLuong())
                    .gia(ctdh.getGia())
             .maChiTietDonHang(ctdh.getId()); // Giả sử tên trường ID trong Entity của bạn là 'id'


            // ✅ BỔ SUNG LOGIC GÁN IMEI TẠI ĐÂY
            if (ctdh.getImei() != null) {
                builder.imei(ctdh.getImei().getImei());
            }

            // ✅ Xử lý Biến Thể Sản Phẩm
            if (ctdh.getBienTheSanPham() != null) {
                BienTheSanPham bts = ctdh.getBienTheSanPham();
                String thuocTinhFormatted = formatThuocTinhSanPham(bts.getThuocTinhList()); // Lấy thuộc tính sản phẩm




                builder.maSKU(bts.getMaSKU())
                        .tenSanPham(bts.getSanPham().getTenSanPham())
                        .thuocTinh(thuocTinhFormatted); // Gán thuộc tính đã format

                // ✅ Xử lý Biến Thể Phụ Kiện
            } else if (ctdh.getBienThePhuKien() != null) {
                BienThePhuKien btpk = ctdh.getBienThePhuKien();
                String thuocTinhFormatted = formatThuocTinhPhuKien(btpk.getThuocTinhPhuKienList()); // Lấy thuộc tính phụ kiện

                builder.maSKUPhuKien(btpk.getMaSKUPhuKien())
                        .tenPhuKien(btpk.getPhuKien().getTenPhuKien())
                        .thuocTinh(thuocTinhFormatted); // Gán thuộc tính đã format
            }
            return builder.build();
        }).collect(Collectors.toList());

        // ... Phần xử lý voucher và tính tổng tiền giữ nguyên ...
        DonHangResponseDTO.VoucherDTO voucherDTO = null;
        BigDecimal soTienGiam = BigDecimal.ZERO;

//        if (donHang.getUserVoucher() != null) {
//            Voucher v = donHang.getUserVoucher().getVoucher();
//            voucherDTO = DonHangResponseDTO.VoucherDTO.builder()
//                    .voucherId(v.getId()).maVoucher(v.getCodeVoucher())
//                    .giaTriGiam(v.getGiaTriGiam()).loaiGiam(v.getLoaiGiam() == 1 ? "PERCENT" : "AMOUNT")
//                    .build();
//            soTienGiam = tinhTienGiam(donHang.getTongTien().add(soTienGiam), v);
//        }

        if (donHang.getUserVoucher() != null) {
            Voucher v = donHang.getUserVoucher().getVoucher();
            voucherDTO = DonHangResponseDTO.VoucherDTO.builder()
                    .voucherId(v.getId())
                    .maVoucher(v.getCodeVoucher())
                    .tenVoucher(v.getTenVoucher())
                    .moTa(v.getMoTa())
                    .soLanSuDung(v.getSoLanSuDung())
                    .giaTriGiam(v.getGiaTriGiam())
                    .loaiGiam(v.getLoaiGiam() == 1 ? "Giảm theo phần trăm" : "Giảm số tiền")
                    .dieuKienGiam(v.getDieuKienGiam())
                    .giamToiDa(v.getGiamToiDa())
                    .ngayBatDau(v.getNgayBatDau())
                    .ngayKetThuc(v.getNgayKetThuc())

                    .build();

            soTienGiam = tinhTienGiam(donHang.getTongTien().add(soTienGiam), v);
        }

        BigDecimal tongTienTruocGiam = donHang.getTongTien().add(soTienGiam);

        // Xây dựng DTO cuối cùng
        return DonHangResponseDTO.builder()
                .maDonHang(donHang.getMaDonHang()).trangThai(donHang.getTrangThai())
                .tongTien(donHang.getTongTien()).tongTienTruocGiam(tongTienTruocGiam)
                .soTienGiam(soTienGiam).diaChiGiaoHang(donHang.getDiaChiGiaoHang())
                .soDienThoai(donHang.getSoDienThoai()).ngayDat(donHang.getNgayDat())
                .tenKhachHang(donHang.getUser().getHoTen())
                .voucher(voucherDTO).chiTietList(chiTietDTOList)
                .build();
    }

    // Hàm helper tính tiền giảm giá
    private BigDecimal tinhTienGiam(BigDecimal tongTien, Voucher voucher) {
        if (voucher == null) return BigDecimal.ZERO;
        if (tongTien.compareTo(voucher.getDieuKienGiam()) < 0) return BigDecimal.ZERO;
        if (voucher.getLoaiGiam() == 1) { // Giảm theo %
            BigDecimal tienGiam = tongTien.multiply(voucher.getGiaTriGiam().divide(BigDecimal.valueOf(100)));
            return tienGiam.compareTo(voucher.getGiamToiDa()) > 0 ? voucher.getGiamToiDa() : tienGiam;
        } else {
            return voucher.getGiaTriGiam();
        }
    }

    // ✅ HÀM HELPER MỚI: Format thuộc tính cho Sản Phẩm Chính
    private String formatThuocTinhSanPham(List<ThuocTinh> thuocTinhs) {
        if (thuocTinhs == null || thuocTinhs.isEmpty()) {
            return null;
        }
        // Format theo yêu cầu: tenThuocTinh : tenThuocTinhBienThe
        return thuocTinhs.stream()
                .map(tt -> tt.getTenThuocTinh() + ": " + tt.getTenThuocTinhBienThe())
                .collect(Collectors.joining(", ")); // Ví dụ: "Màu: Đen, Dung lượng: 256GB"
    }

    // ✅ HÀM HELPER MỚI: Format thuộc tính cho Phụ Kiện
    private String formatThuocTinhPhuKien(List<ThuocTinhPhuKien> thuocTinhs) {
        if (thuocTinhs == null || thuocTinhs.isEmpty()) {
            return null;
        }
        // Format theo yêu cầu: tenThuocTinh: giaTriThuocTinh
        return thuocTinhs.stream()
                .map(tt -> tt.getTenThuocTinh() + ": " + tt.getGiaTriThuocTinh())
                .collect(Collectors.joining(", ")); // Ví dụ: "Loại dây: Vải dù, Màu sắc: Xanh"
    }















    private final ChiTietDonHangRepository chiTietDonHangRepository;

    private final IMEIRepository imeiRepository ;




//    @Transactional
//    public void xacNhanDonHang(Integer maDonHang, List<XacNhanDonHangRequestDTO.ChiTietDonHangIMEIDTO> items) {
//        // 1. Tìm đơn hàng, nếu không thấy hoặc trạng thái không phải "Chờ xác nhận" -> báo lỗi
//        DonHang donHang = donHangRepository.findById(maDonHang)
//                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng #" + maDonHang));
//
//        if (!"Chờ xác nhận".equals(donHang.getTrangThai())) {
//            throw new IllegalStateException("Chỉ có thể xác nhận đơn hàng ở trạng thái 'Chờ xác nhận'.");
//        }
//
//        // 2. Lặp qua từng item (sản phẩm/phụ kiện có IMEI)
//        for (XacNhanDonHangRequestDTO.ChiTietDonHangIMEIDTO itemDTO : items) {
//
//            // Tìm chi tiết đơn hàng tương ứng
//            ChiTietDonHang chiTiet = chiTietDonHangRepository.findById(itemDTO.getChiTietDonHangId())
//                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chi tiết đơn hàng ID: " + itemDTO.getChiTietDonHangId()));
//
//            // Tìm IMEI trong kho
//            IMEI imeiEntity = imeiRepository.findByImei(itemDTO.getImei())
//                    .orElseThrow(() -> new EntityNotFoundException("IMEI '" + itemDTO.getImei() + "' không tồn tại trong hệ thống."));
//
//            // 3. Thực hiện các bước kiểm tra quan trọng
//            // Kiểm tra IMEI có đang ở trạng thái "Trong kho" (trangThai=1) không?
//            if (imeiEntity.getTrangThai() != 1) {
//                throw new IllegalStateException("IMEI '" + imeiEntity.getImei() + "' đã được bán hoặc đang ở trạng thái không hợp lệ.");
//            }
//
//            // 4. Kiểm tra xem IMEI có khớp với sản phẩm hoặc phụ kiện trong chi tiết đơn hàng không
//            // Logic đã sửa: Truy cập qua các đối tượng entity được ánh xạ
//            boolean isProductItem = chiTiet.getBienTheSanPham() != null;
//            boolean isAccessoryItem = chiTiet.getBienThePhuKien() != null;
//
//            if (isProductItem) { // Chi tiết đơn hàng là sản phẩm
//                if (imeiEntity.getBienTheSanPham() == null || !imeiEntity.getBienTheSanPham().getMaSKU().equals(chiTiet.getBienTheSanPham().getMaSKU())) {
//                    throw new IllegalArgumentException("IMEI '" + imeiEntity.getImei() + "' không thuộc về sản phẩm có SKU: " + chiTiet.getBienTheSanPham().getMaSKU());
//                }
//            } else if (isAccessoryItem) { // Chi tiết đơn hàng là phụ kiện
//                if (imeiEntity.getBienThePhuKien() == null || !imeiEntity.getBienThePhuKien().getMaSKUPhuKien().equals(chiTiet.getBienThePhuKien().getMaSKUPhuKien())) {
//                    throw new IllegalArgumentException("IMEI '" + imeiEntity.getImei() + "' không thuộc về phụ kiện có SKU: " + chiTiet.getBienThePhuKien().getMaSKUPhuKien());
//                }
//            } else {
//                // Trường hợp chi tiết đơn hàng không hợp lệ (không phải sản phẩm cũng không phải phụ kiện)
//                throw new IllegalArgumentException("Chi tiết đơn hàng không hợp lệ.");
//            }
//
//            // 5. Mọi thứ hợp lệ -> Gán IMEI vào chi tiết đơn hàng
//            chiTiet.setImei(imeiEntity);
//            chiTietDonHangRepository.save(chiTiet);
//
//            // 6. Cập nhật trạng thái của IMEI thành "Đã bán" (trangThai=0)
//            imeiEntity.setTrangThai(0);
//            imeiRepository.save(imeiEntity);
//        }
//
//        // 7. Sau khi xử lý hết các IMEI, cập nhật trạng thái của toàn bộ đơn hàng
//        donHang.setTrangThai(1);
//        donHangRepository.save(donHang);
//    }



    // File: DonHangService.java

    @Transactional
    public void xacNhanDonHang(Integer maDonHang, List<XacNhanDonHangRequestDTO.ChiTietDonHangIMEIDTO> items) {
        // 1. Tìm đơn hàng
        DonHang donHang = donHangRepository.findById(maDonHang)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng #" + maDonHang));

        // ✅ THAY ĐỔI QUAN TRỌNG TẠI ĐÂY
        // So sánh trạng thái với SỐ 0, thay vì chuỗi "Chờ xác nhận"
        if (donHang.getTrangThai() != 0) { // Nếu trạng thái KHÔNG PHẢI là 0 (Chờ xác nhận)
            throw new IllegalStateException("Chỉ có thể xác nhận đơn hàng ở trạng thái 'Chờ xác nhận'.");
        }

        // 2. Lặp qua từng item (sản phẩm/phụ kiện có IMEI)
        for (XacNhanDonHangRequestDTO.ChiTietDonHangIMEIDTO itemDTO : items) {

            // Tìm chi tiết đơn hàng tương ứng
            ChiTietDonHang chiTiet = chiTietDonHangRepository.findById(itemDTO.getChiTietDonHangId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chi tiết đơn hàng ID: " + itemDTO.getChiTietDonHangId()));

            // Tìm IMEI trong kho
            IMEI imeiEntity = imeiRepository.findByImei(itemDTO.getImei())
                    .orElseThrow(() -> new EntityNotFoundException("IMEI '" + itemDTO.getImei() + "' không tồn tại trong hệ thống."));

            // 3. Kiểm tra IMEI có đang ở trạng thái "Trong kho" (trangThai=1) không?
            if (imeiEntity.getTrangThai() != 1) {
                throw new IllegalStateException("IMEI '" + imeiEntity.getImei() + "' đã được bán hoặc đang ở trạng thái không hợp lệ.");
            }

            // 4. Kiểm tra xem IMEI có khớp với sản phẩm hoặc phụ kiện không
            boolean isProductItem = chiTiet.getBienTheSanPham() != null;
            boolean isAccessoryItem = chiTiet.getBienThePhuKien() != null;

            if (isProductItem) {
                if (imeiEntity.getBienTheSanPham() == null || !imeiEntity.getBienTheSanPham().getMaSKU().equals(chiTiet.getBienTheSanPham().getMaSKU())) {
                    throw new IllegalArgumentException("IMEI '" + imeiEntity.getImei() + "' không thuộc về sản phẩm có SKU: " + chiTiet.getBienTheSanPham().getMaSKU());
                }
            } else if (isAccessoryItem) {
                if (imeiEntity.getBienThePhuKien() == null || !imeiEntity.getBienThePhuKien().getMaSKUPhuKien().equals(chiTiet.getBienThePhuKien().getMaSKUPhuKien())) {
                    throw new IllegalArgumentException("IMEI '" + imeiEntity.getImei() + "' không thuộc về phụ kiện có SKU: " + chiTiet.getBienThePhuKien().getMaSKUPhuKien());
                }
            } else {
                throw new IllegalArgumentException("Chi tiết đơn hàng không hợp lệ.");
            }

            // 5. Gán IMEI vào chi tiết đơn hàng
            chiTiet.setImei(imeiEntity);
            chiTietDonHangRepository.save(chiTiet);

            // 6. Cập nhật trạng thái của IMEI thành "Đã bán" (trangThai=0)
            imeiEntity.setTrangThai(0);
            imeiRepository.save(imeiEntity);
        }

        // 7. Cập nhật trạng thái của đơn hàng thành "Đã xác nhận" (trangThai=1)
        donHang.setTrangThai(1); // Bây giờ dòng này sẽ được thực thi
        donHangRepository.save(donHang);
    }

    @PersistenceContext
    private EntityManager entityManager;
    public List<ImeiResponseDTO> findAvailableImeisBySku(String sku) {
        // Gọi Stored Procedure đã tạo trong database
        @SuppressWarnings("unchecked")
        List<Object[]> results = entityManager
                .createNativeQuery("EXEC sp_GetProductInfoAndAvailableImeis @SKU = :sku")
                .setParameter("sku", sku)
                .getResultList();

        // Chuyển đổi kết quả trả về từ dạng Object[] sang List<ImeiResponseDTO>
        return results.stream()
                .map(result -> new ImeiResponseDTO(
                        (String) result[0], // tenSanPham
                        (String) result[1]  // imei
                ))
                .collect(Collectors.toList());
    }





    // Trong file: DonHangService.java
// ... (thêm vào cuối file, trước dấu } cuối cùng)

    /**
     * Cập nhật trạng thái đơn hàng từ "Đang giao hàng" (1) sang "Hoàn thành" (2).
     * @param maDonHang ID của đơn hàng cần cập nhật.
     */
    @Transactional
    public void hoanThanhDonHang(Integer maDonHang) {
        // 1. Tìm đơn hàng theo ID
        DonHang donHang = donHangRepository.findById(maDonHang)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng #" + maDonHang));

        // 2. Kiểm tra trạng thái hiện tại phải là "Đang giao hàng" (1)
        if (donHang.getTrangThai() != 1) {
            throw new IllegalStateException("Chỉ có thể hoàn thành đơn hàng đang ở trạng thái 'Đang giao hàng'.");
        }

        // 3. Cập nhật trạng thái sang "Hoàn thành" (2) và lưu lại
        donHang.setTrangThai(2);
        donHangRepository.save(donHang);
    }

    /**
     * Cập nhật trạng thái đơn hàng từ "Đang giao hàng" (1) sang "Đã hủy" (3).
     * @param maDonHang ID của đơn hàng cần hủy.
     */
    @Transactional
    public void huyDonHangKhiDangGiao(Integer maDonHang) {
        // 1. Tìm đơn hàng theo ID
        DonHang donHang = donHangRepository.findById(maDonHang)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng #" + maDonHang));

        // 2. Kiểm tra trạng thái hiện tại phải là "Đang giao hàng" (1)
        if (donHang.getTrangThai() != 1) {
            throw new IllegalStateException("Chỉ có thể hủy đơn hàng đang ở trạng thái 'Đang giao hàng'.");
        }

        // 3. Hoàn lại số lượng tồn kho cho các sản phẩm/phụ kiện trong đơn hàng
        for (ChiTietDonHang ctdh : donHang.getChiTietDonHangs()) {
            // Hoàn lại tồn kho cho sản phẩm
            if (ctdh.getBienTheSanPham() != null) {
                BienTheSanPham bts = ctdh.getBienTheSanPham();
                bts.setSoLuong(bts.getSoLuong() + ctdh.getSoLuong()); // ctdh.getSoLuong() luôn là 1
                bienTheRepo.save(bts);
            }
            // Hoàn lại tồn kho cho phụ kiện
            else if (ctdh.getBienThePhuKien() != null) {
                BienThePhuKien btpk = ctdh.getBienThePhuKien();
                btpk.setSoLuong(btpk.getSoLuong() + ctdh.getSoLuong());
                bienThePhuKienRepo.save(btpk);
            }

            // Hoàn lại trạng thái IMEI thành "Trong kho" (1)
            if (ctdh.getImei() != null) {
                IMEI imei = ctdh.getImei();
                imei.setTrangThai(1); // 1 = Trong kho
                imeiRepository.save(imei);
            }
        }

        // 4. Cập nhật trạng thái sang "Đã hủy" (3) và lưu lại
        donHang.setTrangThai(3);
        donHangRepository.save(donHang);
    }



    // Trong file: DonHangService.java
// ... (thêm vào cuối file, trước dấu } cuối cùng)

    /**
     * Hủy một đơn hàng đang ở trạng thái "Chờ xác nhận" (0).
     * Sẽ hoàn lại số lượng tồn kho và số lần sử dụng voucher (nếu có).
     * @param maDonHang ID của đơn hàng cần hủy.
     */
    @Transactional
    public void huyDonHangChoXacNhan(Integer maDonHang) {
        // 1. Tìm đơn hàng
        DonHang donHang = donHangRepository.findById(maDonHang)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng #" + maDonHang));

        // 2. Kiểm tra trạng thái phải là "Chờ xác nhận" (0)
        if (donHang.getTrangThai() != 0) {
            throw new IllegalStateException("Chỉ có thể hủy đơn hàng đang ở trạng thái 'Chờ xác nhận'.");
        }

        // 3. Hoàn lại số lượng tồn kho cho các sản phẩm/phụ kiện
        for (ChiTietDonHang ctdh : donHang.getChiTietDonHangs()) {
            if (ctdh.getBienTheSanPham() != null) {
                BienTheSanPham bts = ctdh.getBienTheSanPham();
                bts.setSoLuong(bts.getSoLuong() + ctdh.getSoLuong()); // ctdh.getSoLuong() là 1
                bienTheRepo.save(bts);
            } else if (ctdh.getBienThePhuKien() != null) {
                BienThePhuKien btpk = ctdh.getBienThePhuKien();
                btpk.setSoLuong(btpk.getSoLuong() + ctdh.getSoLuong());
                bienThePhuKienRepo.save(btpk);
            }
            // Không cần xử lý IMEI vì ở trạng thái này IMEI chưa được gán
        }

        // 4. Hoàn lại lượt sử dụng voucher nếu đơn hàng có áp dụng
        if (donHang.getUserVoucher() != null) {
            UserVoucher userVoucher = donHang.getUserVoucher();
            // Cộng lại 1 lượt sử dụng
            userVoucher.setSoLanSuDung(userVoucher.getSoLanSuDung() + 1);
            userVoucherRepository.save(userVoucher);
        }

        // 5. Cập nhật trạng thái đơn hàng sang "Đã hủy" (3)
        donHang.setTrangThai(3);
        donHangRepository.save(donHang);
    }
}
