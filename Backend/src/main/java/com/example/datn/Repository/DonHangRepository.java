package com.example.datn.Repository;



import com.example.datn.Model.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DonHangRepository extends JpaRepository<DonHang, Integer> {


    List<DonHang> findByTrangThai(Integer trangThai);

//    List<DonHang> findByUser_UsernameAndTrangThai(String username, String trangThai);

//    @Query("""
//    SELECT DISTINCT d FROM DonHang d
//    LEFT JOIN FETCH d.chiTietDonHangs ctdh
//    LEFT JOIN FETCH ctdh.bienTheSanPham bts
//    LEFT JOIN FETCH bts.sanPham sp
//    LEFT JOIN FETCH ctdh.bienThePhuKien btpk
//    LEFT JOIN FETCH btpk.phuKien pk
//    LEFT JOIN FETCH d.userVoucher uv
//    LEFT JOIN FETCH uv.voucher v
//    WHERE d.user.username = :username
//      AND d.trangThai = :trangThai
//""")
//    List<DonHang> findByUserAndTrangThaiWithDetails(
//            @Param("username") String username,
//            @Param("trangThai") String trangThai);


//    @Query("SELECT dh FROM DonHang dh " +
//            "LEFT JOIN FETCH dh.chiTietDonHangs ctdh " +
//            "LEFT JOIN FETCH ctdh.bienTheSanPham bts " +
//            "LEFT JOIN FETCH ctdh.bienThePhuKien btpk " +
//            "WHERE dh.user.username = :username AND dh.trangThai = :trangThai")
//    List<DonHang> findByUserAndTrangThaiWithDetails(@Param("username") String username, @Param("trangThai") int trangThai);

    @Query("""
    SELECT DISTINCT d FROM DonHang d
    LEFT JOIN FETCH d.chiTietDonHangs ctdh
    LEFT JOIN FETCH ctdh.bienTheSanPham bts
    LEFT JOIN FETCH bts.sanPham sp
    LEFT JOIN FETCH ctdh.bienThePhuKien btpk
    LEFT JOIN FETCH btpk.phuKien pk
    LEFT JOIN FETCH d.userVoucher uv
    LEFT JOIN FETCH uv.voucher v
    WHERE d.user.username = :username
      AND d.trangThai = :trangThai
""")
    List<DonHang> findByUserAndTrangThaiWithDetails(
            @Param("username") String username,
            @Param("trangThai") Integer trangThai);


    // Trong file DonHangRepository.java

    /**
     * Lấy danh sách đơn hàng theo trạng thái, không phân biệt người dùng.
     * Bao gồm tất cả các chi tiết cần thiết để tránh lỗi N+1.
     */
    @Query("""
    SELECT DISTINCT d FROM DonHang d
    LEFT JOIN FETCH d.chiTietDonHangs ctdh
    LEFT JOIN FETCH ctdh.bienTheSanPham bts
    LEFT JOIN FETCH bts.sanPham sp
    LEFT JOIN FETCH ctdh.bienThePhuKien btpk
    LEFT JOIN FETCH btpk.phuKien pk
    LEFT JOIN FETCH d.userVoucher uv
    LEFT JOIN FETCH uv.voucher v
    WHERE d.trangThai = :trangThai
""")
    List<DonHang> findByTrangThaiWithDetails(@Param("trangThai") Integer trangThai);
}
