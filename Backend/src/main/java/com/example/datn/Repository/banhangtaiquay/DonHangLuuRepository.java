package com.example.datn.Repository.banhangtaiquay;

import com.example.datn.Model.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonHangLuuRepository extends JpaRepository<DonHang, Integer> {

    /**
     * Lấy danh sách đơn hàng đã lưu (trạng thái = 5) với thông tin chi tiết
     * @param userId - ID của user (null để lấy tất cả)
     * @return List<DonHang> - Danh sách đơn hàng đã lưu
     */
    @Query("""
        SELECT DISTINCT d FROM DonHang d
        LEFT JOIN FETCH d.chiTietDonHangs ctdh
        LEFT JOIN FETCH ctdh.bienTheSanPham bts
        LEFT JOIN FETCH bts.sanPham sp
        LEFT JOIN FETCH ctdh.bienThePhuKien btpk
        LEFT JOIN FETCH btpk.phuKien pk
        LEFT JOIN FETCH ctdh.imei imei
        LEFT JOIN FETCH d.user u
        LEFT JOIN FETCH d.userVoucher uv
        LEFT JOIN FETCH uv.voucher v
        WHERE d.trangThai = 5
        AND (:userId IS NULL OR d.user.id = :userId)
        ORDER BY d.ngayDat DESC
    """)
    List<DonHang> findDonHangLuuWithDetails(@Param("userId") Integer userId);

    /**
     * Lấy đơn hàng đã lưu theo mã đơn hàng
     * @param maDonHang - Mã đơn hàng
     * @return DonHang - Đơn hàng đã lưu
     */
    @Query("""
        SELECT DISTINCT d FROM DonHang d
        LEFT JOIN FETCH d.chiTietDonHangs ctdh
        LEFT JOIN FETCH ctdh.bienTheSanPham bts
        LEFT JOIN FETCH bts.sanPham sp
        LEFT JOIN FETCH ctdh.bienThePhuKien btpk
        LEFT JOIN FETCH btpk.phuKien pk
        LEFT JOIN FETCH ctdh.imei imei
        LEFT JOIN FETCH d.user u
        LEFT JOIN FETCH d.userVoucher uv
        LEFT JOIN FETCH uv.voucher v
        WHERE d.maDonHang = :maDonHang
        AND d.trangThai = 5
    """)
    DonHang findDonHangLuuByMaDonHang(@Param("maDonHang") Integer maDonHang);

    /**
     * Đếm số lượng đơn hàng đã lưu theo user
     * @param userId - ID của user (null để đếm tất cả)
     * @return Long - Số lượng đơn hàng
     */
    @Query("SELECT COUNT(d) FROM DonHang d WHERE d.trangThai = 5 AND (:userId IS NULL OR d.user.id = :userId)")
    Long countDonHangLuuByUser(@Param("userId") Integer userId);

    /**
     * Tìm đơn hàng theo mã đơn hàng (không phân biệt trạng thái)
     * @param maDonHang - Mã đơn hàng
     * @return DonHang - Đơn hàng tìm được
     */
    DonHang findByMaDonHang(Integer maDonHang);
}
