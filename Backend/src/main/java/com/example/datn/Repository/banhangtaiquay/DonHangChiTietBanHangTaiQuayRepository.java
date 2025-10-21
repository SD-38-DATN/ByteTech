package com.example.datn.Repository.banhangtaiquay;


import com.example.datn.Model.ChiTietDonHang;
import com.example.datn.Model.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface DonHangChiTietBanHangTaiQuayRepository extends JpaRepository<ChiTietDonHang, Integer> {

    // Xóa chi tiết đơn hàng theo đơn hàng
    @Modifying
    @Transactional
    @Query("DELETE FROM ChiTietDonHang c WHERE c.donHang = :donHang")
    void deleteByDonHang(@Param("donHang") DonHang donHang);


    // Xóa chi tiết đơn hàng theo đơn hàng
    // Xóa chi tiết đơn hàng theo mã đơn hàng
    @Modifying
    @Transactional
    @Query("DELETE FROM ChiTietDonHang c WHERE c.donHang.maDonHang = :maDonHang")
    void deleteByDonHangId(@Param("maDonHang") Integer maDonHang);


}
