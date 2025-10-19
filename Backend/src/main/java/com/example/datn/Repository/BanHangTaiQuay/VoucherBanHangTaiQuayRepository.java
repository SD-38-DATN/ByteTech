package com.example.datn.Repository.BanHangTaiQuay;

import com.example.datn.Model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherBanHangTaiQuayRepository extends JpaRepository<Voucher, Integer> {
    
    /**
     * Lấy danh sách voucher hợp lệ cho bán hàng tại quầy
     * Điều kiện:
     * 1. trangThai = 1
     * 2. Ngày hiện tại trong khoảng ngayBatDau và ngayKetThuc
     * 3. dieuKienGiam > 0
     */
    @Query("SELECT v FROM Voucher v WHERE " +
           "v.trangThai = 1 AND " +
           "CURRENT_TIMESTAMP BETWEEN v.ngayBatDau AND v.ngayKetThuc AND " +
           "v.dieuKienGiam > 0 " +
           "ORDER BY v.ngayBatDau DESC")
    List<Voucher> findValidVouchersForBanHangTaiQuay();
    
    /**
     * Tìm voucher theo mã code và kiểm tra điều kiện
     * Điều kiện:
     * 1. trangThai = 1
     * 2. Ngày hiện tại trong khoảng ngayBatDau và ngayKetThuc
     * 3. dieuKienGiam > 0
     */
    @Query("SELECT v FROM Voucher v WHERE " +
           "v.codeVoucher = :codeVoucher AND " +
           "v.trangThai = 1 AND " +
           "CURRENT_TIMESTAMP BETWEEN v.ngayBatDau AND v.ngayKetThuc AND " +
           "v.dieuKienGiam > 0")
    Optional<Voucher> findValidVoucherByCode(@Param("codeVoucher") String codeVoucher);
    
    /**
     * Lấy danh sách voucher hợp lệ theo tổng tiền đơn hàng
     * Điều kiện:
     * 1. trangThai = 1
     * 2. Ngày hiện tại trong khoảng ngayBatDau và ngayKetThuc
     * 3. dieuKienGiam > 0
     * 4. dieuKienGiam <= tongTienDonHang
     */
    @Query("SELECT v FROM Voucher v WHERE " +
           "v.trangThai = 1 AND " +
           "CURRENT_TIMESTAMP BETWEEN v.ngayBatDau AND v.ngayKetThuc AND " +
           "v.dieuKienGiam > 0 AND " +
           "v.dieuKienGiam <= :tongTienDonHang " +
           "ORDER BY v.ngayBatDau DESC")
    List<Voucher> findValidVouchersForBanHangTaiQuayByAmount(@Param("tongTienDonHang") java.math.BigDecimal tongTienDonHang);
    
    /**
     * Kiểm tra voucher có hợp lệ với tổng tiền đơn hàng không
     */
    @Query("SELECT v FROM Voucher v WHERE " +
           "v.codeVoucher = :codeVoucher AND " +
           "v.trangThai = 1 AND " +
           "CURRENT_TIMESTAMP BETWEEN v.ngayBatDau AND v.ngayKetThuc AND " +
           "v.dieuKienGiam > 0 AND " +
           "v.dieuKienGiam <= :tongTienDonHang")
    Optional<Voucher> findValidVoucherByCodeAndAmount(
        @Param("codeVoucher") String codeVoucher, 
        @Param("tongTienDonHang") java.math.BigDecimal tongTienDonHang
    );
}