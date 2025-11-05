package com.example.datn.Service.banhangtaiquay;

import com.example.datn.DTO.banhangtaiquay.VoucherBanHangTaiQuayDTO;
import com.example.datn.DTO.banhangtaiquay.VoucherValidateRequest;
import com.example.datn.DTO.banhangtaiquay.VoucherValidateResponse;
import com.example.datn.Model.Voucher;
import com.example.datn.Repository.banhangtaiquay.VoucherBanHangTaiQuayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherBanHangTaiQuayService {

    private final VoucherBanHangTaiQuayRepository voucherRepository;

    /**
     * Lấy danh sách voucher hợp lệ cho bán hàng tại quầy
     * Điều kiện:
     * 1. trangThai = 1
     * 2. Ngày hiện tại trong khoảng ngayBatDau và ngayKetThuc
     * 3. dieuKienGiam > 0
     */
    public List<VoucherBanHangTaiQuayDTO> getValidVouchers() {
        List<Voucher> vouchers = voucherRepository.findValidVouchersForBanHangTaiQuay();
        return vouchers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách voucher hợp lệ cho bán hàng tại quầy theo tổng tiền đơn hàng
     * Điều kiện:
     * 1. trangThai = 1
     * 2. Ngày hiện tại trong khoảng ngayBatDau và ngayKetThuc
     * 3. dieuKienGiam > 0
     * 4. dieuKienGiam <= tongTienDonHang
     */
    public List<VoucherBanHangTaiQuayDTO> getValidVouchersByAmount(BigDecimal tongTienDonHang) {
        List<Voucher> vouchers = voucherRepository.findValidVouchersForBanHangTaiQuayByAmount(tongTienDonHang);
        return vouchers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Kiểm tra voucher có hợp lệ với đơn hàng không
     * Điều kiện:
     * 1. Voucher phải hợp lệ (trangThai = 1, trong thời gian, dieuKienGiam > 0)
     * 2. Tổng tiền đơn hàng >= dieuKienGiam
     * 3. Tính toán số tiền giảm
     */
    public VoucherValidateResponse validateVoucher(VoucherValidateRequest request) {
        try {
            // Kiểm tra voucher có hợp lệ với tổng tiền đơn hàng không
            Voucher voucher = voucherRepository.findValidVoucherByCodeAndAmount(
                    request.getCodeVoucher(),
                    request.getTongTienDonHang()
            ).orElse(null);

            if (voucher == null) {
                return VoucherValidateResponse.error("Voucher không hợp lệ hoặc đơn hàng không đủ điều kiện!");
            }

            // Tính toán số tiền giảm
            BigDecimal soTienGiam = calculateDiscount(voucher, request.getTongTienDonHang());

            if (soTienGiam.compareTo(BigDecimal.ZERO) <= 0) {
                return VoucherValidateResponse.error("Không thể tính toán số tiền giảm!");
            }

            return VoucherValidateResponse.success(soTienGiam, convertToDTO(voucher));

        } catch (Exception e) {
            return VoucherValidateResponse.error("Lỗi khi xử lý voucher: " + e.getMessage());
        }
    }

    /**
     * Tính toán số tiền giảm
     */
    private BigDecimal calculateDiscount(Voucher voucher, BigDecimal tongTienDonHang) {
        BigDecimal soTienGiam;

        if (voucher.getLoaiGiam() == 1) {
            // Giảm theo phần trăm
            soTienGiam = tongTienDonHang.multiply(voucher.getGiaTriGiam());

            // Kiểm tra giới hạn tối đa

            if (voucher.getGiamToiDa() != null && voucher.getGiamToiDa().compareTo(BigDecimal.ZERO) > 0 && soTienGiam.compareTo(voucher.getGiamToiDa()) > 0) {
                soTienGiam = voucher.getGiamToiDa();
            }
        } else {
            // Giảm theo số tiền cố định
            soTienGiam = voucher.getGiaTriGiam();
        }
        // Đảm bảo số tiền giảm không vượt quá tổng tiền đơn hàng
        if (soTienGiam.compareTo(tongTienDonHang) > 0) {
            soTienGiam = tongTienDonHang;
        }
        return soTienGiam;
    }

    /**
     * Convert Voucher entity to DTO
     */
    private VoucherBanHangTaiQuayDTO convertToDTO(Voucher voucher) {
        return new VoucherBanHangTaiQuayDTO(
                voucher.getId(),
                voucher.getCodeVoucher(),
                voucher.getTenVoucher(),
                voucher.getSoLanSuDung(),
                voucher.getMoTa(),
                voucher.getLoaiGiam(),
                voucher.getGiaTriGiam(),
                voucher.getDieuKienGiam(),
                voucher.getGiamToiDa(),
                voucher.getNgayBatDau(),
                voucher.getNgayKetThuc(),
                voucher.getTrangThai()
        );
    }
}