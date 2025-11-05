package com.example.datn.Repository.thongke;

import com.example.datn.Model.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ThongKeDonHangRepository extends JpaRepository<DonHang, Integer> {

    /**
     * Lấy tất cả thống kê tổng hợp từ đơn hàng có trạng thái = 1
     * Trả về: [tongDoanhThu, soLuongSanPham, soLuongDonHang]
     *
     * @return Object array với 3 giá trị
     */
    @Query(value = "SELECT " +
            "ISNULL(SUM(dh.tongTien), 0) AS tongDoanhThu, " +
            "ISNULL(SUM(ctdh.soLuong), 0) AS soLuongSanPham, " +
            "COUNT(DISTINCT dh.maDonHang) AS soLuongDonHang " +
            "FROM DonHang dh " +
            "LEFT JOIN ChiTietDonHang ctdh ON dh.maDonHang = ctdh.maDonHang " +
            "WHERE dh.trangThai IN (2)", nativeQuery = true)
    List<Object[]> getThongKeTongQuan();


    /**
     * Lấy tất cả thống kê tổng hợp từ đơn hàng có trạng thái = 1 theo năm
     * Trả về: [tongDoanhThu, soLuongSanPham, soLuongDonHang]
     *
     * @param year Năm hiện tại
     * @return Object array với 3 giá trị
     */
    @Query(value = "SELECT " +
            "ISNULL(SUM(dh.tongTien), 0) AS tongDoanhThu, " +
            "ISNULL(SUM(ctdh.soLuong), 0) AS soLuongSanPham, " +
            "COUNT(DISTINCT dh.maDonHang) AS soLuongDonHang " +
            "FROM DonHang dh " +
            "LEFT JOIN ChiTietDonHang ctdh ON dh.maDonHang = ctdh.maDonHang " +
            "WHERE dh.trangThai IN ( 2) AND YEAR(dh.ngayDat) = :year", nativeQuery = true)
    List<Object[]> getThongKeTheoNam(@Param("year") int year);

    /**
     * Lấy danh sách các năm có đơn hàng (từ năm sớm nhất đến năm hiện tại)
     *
     * @return Danh sách năm (Integer)
     */
    @Query(value = "SELECT DISTINCT YEAR(ngayDat) AS nam " +
            "FROM DonHang " +
            "ORDER BY nam DESC", nativeQuery = true)
    List<Integer> getDanhSachNam();

    /**
     * Lấy thống kê với filter theo điều kiện
     * Trả về: [tongDoanhThu, soLuongSanPham, soLuongDonHangThanhCong, soLuongDonHangHuy]
     *
     * @param dateFrom Ngày bắt đầu (có thể null)
     * @param dateTo   Ngày kết thúc (có thể null)
     * @param year     Năm (có thể null)
     * @param month    Tháng (1-12, có thể null)
     * @return Object array với 4 giá trị
     */
    @Query(value = "SELECT " +
            "ISNULL(SUM(CASE WHEN dh.trangThai = 2 THEN dh.tongTien ELSE 0 END), 0) AS tongDoanhThu, " +
            "ISNULL(SUM(CASE WHEN dh.trangThai = 2 THEN ctdh.soLuong ELSE 0 END), 0) AS soLuongSanPham, " +
            "COUNT(DISTINCT CASE WHEN dh.trangThai = 2 THEN dh.maDonHang END) AS soLuongDonHangThanhCong, " +
            "COUNT(DISTINCT CASE WHEN dh.trangThai = 3 THEN dh.maDonHang END) AS soLuongDonHangHuy " +
            "FROM DonHang dh " +
            "LEFT JOIN ChiTietDonHang ctdh ON dh.maDonHang = ctdh.maDonHang " +
            "WHERE 1=1 " +
            "AND (:dateFrom IS NULL OR CAST(dh.ngayDat AS DATE) >= CAST(:dateFrom AS DATE)) " +
            "AND (:dateTo IS NULL OR CAST(dh.ngayDat AS DATE) <= CAST(:dateTo AS DATE)) " +
            "AND (:year IS NULL OR YEAR(dh.ngayDat) = :year) " +
            "AND (:month IS NULL OR MONTH(dh.ngayDat) = :month)", nativeQuery = true)
    List<Object[]> getThongKeFilter(
            @Param("dateFrom") String dateFrom,
            @Param("dateTo") String dateTo,
            @Param("year") Integer year,
            @Param("month") Integer month
    );


    /**
     * Lấy doanh thu theo từng ngày trong khoảng thời gian để vẽ biểu đồ
     * Chỉ lấy đơn hàng có trạng thái = 2 (Đã giao hàng)
     * Trả về: [ngay (yyyy-MM-dd), doanhThu]
     *
     * @param dateFrom Ngày bắt đầu (có thể null)
     * @param dateTo   Ngày kết thúc (có thể null)
     * @param year     Năm (có thể null)
     * @param month    Tháng (1-12, có thể null)
     * @return Object array với 2 giá trị: [ngay, doanhThu]
     */
    @Query(value = "SELECT " +
            "FORMAT(CAST(dh.ngayDat AS DATE), 'yyyy-MM-dd') AS ngay, " +
            "ISNULL(SUM(dh.tongTien), 0) AS doanhThu " +
            "FROM DonHang dh " +
            "WHERE dh.trangThai = 2 " +
            "AND (:dateFrom IS NULL OR CAST(dh.ngayDat AS DATE) >= CAST(:dateFrom AS DATE)) " +
            "AND (:dateTo IS NULL OR CAST(dh.ngayDat AS DATE) <= CAST(:dateTo AS DATE)) " +
            "AND (:year IS NULL OR YEAR(dh.ngayDat) = :year) " +
            "AND (:month IS NULL OR MONTH(dh.ngayDat) = :month) " +
            "GROUP BY CAST(dh.ngayDat AS DATE) " +
            "ORDER BY CAST(dh.ngayDat AS DATE) ASC", nativeQuery = true)
    List<Object[]> getDoanhThuChartData(
            @Param("dateFrom") String dateFrom,
            @Param("dateTo") String dateTo,
            @Param("year") Integer year,
            @Param("month") Integer month
    );

    /**
     * Lấy doanh thu theo từng tháng trong năm để vẽ biểu đồ
     * Chỉ lấy đơn hàng có trạng thái = 2 (Đã giao hàng)
     * Trả về: [thang (yyyy-MM), doanhThu]
     *
     * @param year Năm
     * @return Object array với 2 giá trị: [thang, doanhThu]
     */
    @Query(value = "SELECT " +
            "FORMAT(dh.ngayDat, 'yyyy-MM') AS thang, " +
            "ISNULL(SUM(dh.tongTien), 0) AS doanhThu " +
            "FROM DonHang dh " +
            "WHERE dh.trangThai = 2 " +
            "AND YEAR(dh.ngayDat) = :year " +
            "GROUP BY FORMAT(dh.ngayDat, 'yyyy-MM') " +
            "ORDER BY FORMAT(dh.ngayDat, 'yyyy-MM') ASC", nativeQuery = true)
    List<Object[]> getDoanhThuChartDataTheoThang(
            @Param("year") Integer year
    );

    /**
     * Lấy doanh thu theo từng giờ trong ngày để vẽ biểu đồ
     * Chỉ lấy đơn hàng có trạng thái = 2 (Đã giao hàng)
     * Trả về: [gio (HH), doanhThu]
     *
     * @param dateFrom Ngày bắt đầu (yyyy-MM-dd)
     * @param dateTo   Ngày kết thúc (yyyy-MM-dd)
     * @return Object array với 2 giá trị: [gio, doanhThu]
     */
    @Query(value = "SELECT " +
            "RIGHT('0' + CAST(DATEPART(HOUR, dh.ngayDat) AS VARCHAR(2)), 2) AS gio, " +
            "ISNULL(SUM(dh.tongTien), 0) AS doanhThu " +
            "FROM DonHang dh " +
            "WHERE dh.trangThai = 2 " +
            "AND CAST(dh.ngayDat AS DATE) = CAST(:dateFrom AS DATE) " +
            "AND CAST(dh.ngayDat AS DATE) = CAST(:dateTo AS DATE) " +
            "GROUP BY RIGHT('0' + CAST(DATEPART(HOUR, dh.ngayDat) AS VARCHAR(2)), 2) " +
            "ORDER BY DATEPART(HOUR, dh.ngayDat) ASC", nativeQuery = true)
    List<Object[]> getDoanhThuChartDataTheoGio(
            @Param("dateFrom") String dateFrom,
            @Param("dateTo") String dateTo
    );
}

