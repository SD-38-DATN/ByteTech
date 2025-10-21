package com.example.datn.Repository.banhangtaiquay;

import com.example.datn.Model.IMEI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface IMEIBanHangTaiQuayRepository extends JpaRepository<IMEI , Integer> {

    // Tìm IMEI theo số IMEI chính xác (bất kỳ trạng thái nào)
    @Query("SELECT i FROM IMEI i WHERE i.imei = :imei")
    Optional<IMEI> findByImei(String imei);

    // Đếm số lượng IMEI theo SKU sản phẩm (chỉ trạng thái = 1)
    @Query("SELECT COUNT(i) FROM IMEI i WHERE i.bienTheSanPham.maSKU = :maSKU AND i.trangThai = 1")
    long countByBienTheSanPham_MaSKU(String maSKU);
    
    // Đếm số lượng IMEI theo SKU phụ kiện (chỉ trạng thái = 1)
    @Query("SELECT COUNT(i) FROM IMEI i WHERE i.bienThePhuKien.maSKUPhuKien = :maSKUPhuKien AND i.trangThai = 1")
    long countByBienThePhuKien_MaSKUPhuKien(String maSKUPhuKien);

    // Tìm danh sách IMEI theo mã SKU (chỉ trạng thái = 1)
    @Query(value = "SELECT i.id, i.maSKU, i.imei, i.trangThai FROM IMEI i " +
            "WHERE i.maSKU = :skuMaBT AND i.trangThai = 1", nativeQuery = true)
    List<Object[]> findImeisBySkuMaBT(@Param("skuMaBT") String skuMaBT);

    // Tìm IMEI theo SKU và IMEI (bắt đầu với pattern) (chỉ trạng thái = 1)
    @Query(value = "SELECT i.id, i.maSKU, i.imei, i.trangThai FROM IMEI i " +
            "WHERE i.maSKU = :skuMaBT " +
            "AND i.trangThai = 1 " +
            "AND (:imei IS NULL OR i.imei LIKE CONCAT(:imei, '%'))", nativeQuery = true)
    List<Object[]> findImeisBySkuMaBTAndImei(
            @Param("skuMaBT") String skuMaBT,
            @Param("imei") String imei);

    // Đếm số lượng IMEI khớp với pattern (bắt đầu với) (chỉ trạng thái = 1)
    @Query(value = "SELECT COUNT(*) FROM IMEI i " +
            "WHERE i.maSKU = :skuMaBT " +
            "AND i.trangThai = 1 " +
            "AND i.imei LIKE CONCAT(:imei, '%')", nativeQuery = true)
    long countBySkuAndImeiLike(@Param("skuMaBT") String skuMaBT, @Param("imei") String imei);

    // Tìm danh sách IMEI theo SKU phụ kiện (chỉ trạng thái = 1)
    @Query(value = "SELECT i.id, i.maSKUPhuKien, i.imei, i.trangThai FROM IMEI i " +
            "WHERE i.maSKUPhuKien = :skuPhuKien AND i.trangThai = 1", nativeQuery = true)
    List<Object[]> findImeisBySkuPhuKien(@Param("skuPhuKien") String skuPhuKien);

    // Tìm IMEI theo SKU phụ kiện và IMEI (bắt đầu với pattern) (chỉ trạng thái = 1)
    @Query(value = "SELECT i.id, i.maSKUPhuKien, i.imei, i.trangThai FROM IMEI i " +
            "WHERE i.maSKUPhuKien = :skuPhuKien " +
            "AND i.trangThai = 1 " +
            "AND (:imei IS NULL OR i.imei LIKE CONCAT(:imei, '%'))", nativeQuery = true)
    List<Object[]> findImeisBySkuPhuKienAndImei(
            @Param("skuPhuKien") String skuPhuKien,
            @Param("imei") String imei);

    // Đếm số lượng IMEI phụ kiện khớp với pattern (bắt đầu với) (chỉ trạng thái = 1)
    @Query(value = "SELECT COUNT(*) FROM IMEI i " +
            "WHERE i.maSKUPhuKien = :skuPhuKien " +
            "AND i.trangThai = 1 " +
            "AND i.imei LIKE CONCAT(:imei, '%')", nativeQuery = true)
    long countBySkuPhuKienAndImeiLike(@Param("skuPhuKien") String skuPhuKien, @Param("imei") String imei);

    // Đếm số lượng IMEI phụ kiện (chỉ trạng thái = 1)
    @Query(value = "SELECT COUNT(*) FROM IMEI i " +
            "WHERE i.maSKUPhuKien = :skuPhuKien " +
            "AND i.trangThai = 1", nativeQuery = true)
    long countBySkuPhuKien(@Param("skuPhuKien") String skuPhuKien);

    // ===== BATCH UPDATE METHODS =====
    
    // Cập nhật trạng thái nhiều IMEI cùng lúc (tối ưu hóa)
    @Modifying
    @Transactional
    @Query("UPDATE IMEI i SET i.trangThai = :status WHERE i.imei IN :imeis")
    int capNhatTrangThaiNhieuImei(@Param("imeis") List<String> imeis, @Param("status") int status);
    
    // Cập nhật trạng thái một IMEI
    @Modifying
    @Transactional
    @Query("UPDATE IMEI i SET i.trangThai = :status WHERE i.imei = :imei")
    int capNhatTrangThaiMotImei(@Param("imei") String imei, @Param("status") int status);
}