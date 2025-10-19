package com.example.datn.Repository.BanHangTaiQuay;



import com.example.datn.Model.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DonHangBanHangTaiQuayRepository extends JpaRepository<DonHang, Integer> {

    // Tìm đơn hàng theo userId và trạng thái
    @Query("SELECT d FROM DonHang d WHERE d.user.id = :userId AND d.trangThai = :trangThai ORDER BY d.ngayDat DESC")
    List<DonHang> findByUserIdAndTrangThai(@Param("userId") Integer userId, @Param("trangThai") Integer trangThai);

}
