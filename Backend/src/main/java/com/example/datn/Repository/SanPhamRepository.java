package com.example.datn.Repository;

import com.example.datn.Model.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {










//        @Query(value =
//            "SELECT sp.maSanPham, sp.tenSanPham, sp.moTa, sp.soLuong, " +
//                    "       dm.maDanhMuc, dm.tenDanhMuc, " +
//                    "       CASE WHEN kmMin.maSKU IS NULL THEN sp.gia ELSE kmMin.giaSauKM END AS giaHienThi, " +
//                    "       kmMin.maSKU, kmMin.giaKhongKhuyenMai, kmMin.giaSauKM, kmMin.giaTriGiam, kmMin.loaiGiam " +
//                    "FROM SanPham sp " +
//                    "LEFT JOIN DanhMuc dm ON sp.maDanhMuc = dm.maDanhMuc " +
//                    "OUTER APPLY ( " +
//                    "    SELECT TOP 1 bt.maSKU, bt.giaKhongKhuyenMai, " +
//                    "           CASE WHEN km.loaiGiam = 1 " +
//                    "                THEN bt.giaKhongKhuyenMai - (bt.giaKhongKhuyenMai * km.giaTriGiam ) " +
//                    "                ELSE bt.giaKhongKhuyenMai - km.giaTriGiam END AS giaSauKM, " +
//                    "           km.giaTriGiam, km.loaiGiam " +
//                    "    FROM BienTheSanPham bt " +
//                    "    JOIN KhuyenMai km ON bt.maKhuyenMai = km.maKhuyenMai " +
//                    "    WHERE bt.maSanPham = sp.maSanPham " +
//                    "      AND km.trangThai = 1 " +
//                    "      AND GETDATE() BETWEEN km.ngayBatDau AND km.ngayKetThuc " +
//                    "    ORDER BY giaSauKM ASC " +
//                    ") kmMin",
//            nativeQuery = true)
//    List<Object[]> findAllWithPromoCheck();

    @Query(value =
            "WITH VariantPrices AS ( " +
                    "    SELECT " +
                    "        bt.maSKU, bt.maSanPham, bt.giaKhongKhuyenMai, bt.soLuong, bt.trangThai, " +
                    "        km.maKhuyenMai, km.giaTriGiam, km.loaiGiam, " +
                    "        CASE " +
                    "            WHEN km.maKhuyenMai IS NOT NULL " +
                    "                 AND km.trangThai = 1 " +
                    "                 AND CONVERT(date, GETDATE()) BETWEEN km.ngayBatDau AND km.ngayKetThuc " +
                    "            THEN CASE " +
                    "                     WHEN km.loaiGiam = 1 " +
                    "                     THEN bt.giaKhongKhuyenMai * (1.0 - (CASE WHEN km.giaTriGiam > 1 THEN km.giaTriGiam/100.0 ELSE km.giaTriGiam END)) " +
                    "                     ELSE bt.giaKhongKhuyenMai - ISNULL(km.giaTriGiam, 0) " +
                    "                 END " +
                    "            ELSE bt.giaKhongKhuyenMai " +
                    "        END AS giaSauKM " +
                    "    FROM BienTheSanPham bt " +
                    "    LEFT JOIN KhuyenMai km ON bt.maKhuyenMai = km.maKhuyenMai " +
                    "), " +
                    "MinPricePerProduct AS ( " +
                    "    SELECT maSanPham, MIN(giaSauKM) AS minGia " +
                    "    FROM VariantPrices " +
                    "    GROUP BY maSanPham " +
                    ") " +
                    "SELECT sp.maSanPham, sp.tenSanPham ,  sp.moTa , sp.thuongHieu, sp.soLuong, " +
                    "       dm.maDanhMuc, dm.tenDanhMuc, " +
                    "       vpMin.maSKU, vpMin.giaKhongKhuyenMai, vpMin.giaSauKM AS giaHienThi, " +
                    "       vpMin.giaTriGiam, vpMin.loaiGiam " +
                    "FROM SanPham sp " +
                    "LEFT JOIN DanhMuc dm ON sp.maDanhMuc = dm.maDanhMuc " +
                    "LEFT JOIN MinPricePerProduct mp ON mp.maSanPham = sp.maSanPham " +
                    "OUTER APPLY ( " +
                    "    SELECT TOP 1 v.* " +
                    "    FROM VariantPrices v " +
                    "    WHERE v.maSanPham = sp.maSanPham " +
                    "      AND mp.minGia IS NOT NULL " +
                    "      AND v.giaSauKM = mp.minGia " +
                    "    ORDER BY v.maSKU " +
                    ") vpMin",
            nativeQuery = true)
    List<Object[]> findAllWithPromoCheck();


//    @Query(value =
//            "WITH VariantPrices AS ( " +
//                    "    SELECT " +
//                    "        bt.maSKU, bt.maSanPham, bt.giaKhongKhuyenMai, bt.soLuong, bt.trangThai, " +
//                    "        km.maKhuyenMai, km.giaTriGiam, km.loaiGiam, " +
//                    "        CASE " +
//                    "            WHEN km.maKhuyenMai IS NOT NULL " +
//                    "                 AND km.trangThai = 1 " +
//                    "                 AND CONVERT(date, GETDATE()) BETWEEN km.ngayBatDau AND km.ngayKetThuc " +
//                    "            THEN CASE " +
//                    "                     WHEN km.loaiGiam = 1 " +
//                    "                     THEN bt.giaKhongKhuyenMai * (1.0 - (CASE WHEN km.giaTriGiam > 1 THEN km.giaTriGiam/100.0 ELSE km.giaTriGiam END)) " +
//                    "                     ELSE bt.giaKhongKhuyenMai - ISNULL(km.giaTriGiam, 0) " +
//                    "                 END " +
//                    "            ELSE bt.giaKhongKhuyenMai " +
//                    "        END AS giaSauKM " +
//                    "    FROM BienTheSanPham bt " +
//                    "    LEFT JOIN KhuyenMai km ON bt.maKhuyenMai = km.maKhuyenMai " +
//                    "), " +
//                    "MinPricePerProduct AS ( " +
//                    "    SELECT maSanPham, MIN(giaSauKM) AS minGia " +
//                    "    FROM VariantPrices " +
//                    "    GROUP BY maSanPham " +
//                    ") " +
//                    "SELECT sp.maSanPham, sp.tenSanPham, sp.moTa,sp.thuongHieu, sp.soLuong, " +
//                    "       dm.maDanhMuc, dm.tenDanhMuc, " +
//                    "       vpMin.maSKU, vpMin.giaKhongKhuyenMai, vpMin.giaSauKM AS giaHienThi, " +
//                    "       vpMin.giaTriGiam, vpMin.loaiGiam " +
//                    "FROM SanPham sp " +
//                    "LEFT JOIN DanhMuc dm ON sp.maDanhMuc = dm.maDanhMuc " +
//                    "LEFT JOIN MinPricePerProduct mp ON mp.maSanPham = sp.maSanPham " +
//                    "OUTER APPLY ( " +
//                    "    SELECT TOP 1 v.* " +
//                    "    FROM VariantPrices v " +
//                    "    WHERE v.maSanPham = sp.maSanPham " +
//                    "      AND mp.minGia IS NOT NULL " +
//                    "      AND v.giaSauKM = mp.minGia " +
//                    "    ORDER BY v.maSKU " +
//                    ") vpMin " +
//                    "WHERE sp.maDanhMuc = :maDanhMuc",   // ✅ chuyển WHERE xuống đây
//            nativeQuery = true)
//    List<Object[]> findWithPromoCheckByDanhMuc(@Param("maDanhMuc") Integer maDanhMuc);




/////////////////////////////////////////////////////////////quay lai day neu loi
    @Query(value = """
        -- B1: Tìm ra DUY NHẤT MỘT biến thể rẻ nhất để lấy thông tin giá và SKU hiển thị
        WITH CheapestVariantDetails AS (
            SELECT
                bt.maSanPham,
                bt.maSKU,
                bt.giaKhongKhuyenMai,
                km.giaTriGiam,
                km.loaiGiam,
                CASE
                    WHEN km.maKhuyenMai IS NOT NULL AND km.trangThai = 1 AND CONVERT(date, GETDATE()) BETWEEN km.ngayBatDau AND km.ngayKetThuc
                    THEN CASE
                             WHEN km.loaiGiam = 1 THEN bt.giaKhongKhuyenMai * (1.0 - IIF(km.giaTriGiam > 1, km.giaTriGiam/100.0, km.giaTriGiam))
                             ELSE bt.giaKhongKhuyenMai - ISNULL(km.giaTriGiam, 0)
                         END
                    ELSE bt.giaKhongKhuyenMai
                END AS giaSauKM,
                ROW_NUMBER() OVER(PARTITION BY bt.maSanPham ORDER BY
                    CASE
                        WHEN km.maKhuyenMai IS NOT NULL AND km.trangThai = 1 AND CONVERT(date, GETDATE()) BETWEEN km.ngayBatDau AND km.ngayKetThuc
                        THEN CASE
                                 WHEN km.loaiGiam = 1 THEN bt.giaKhongKhuyenMai * (1.0 - IIF(km.giaTriGiam > 1, km.giaTriGiam/100.0, km.giaTriGiam))
                                 ELSE bt.giaKhongKhuyenMai - ISNULL(km.giaTriGiam, 0)
                             END
                        ELSE bt.giaKhongKhuyenMai
                    END ASC, bt.maSKU ASC) as rn
            FROM BienTheSanPham bt
            LEFT JOIN KhuyenMai km ON bt.maKhuyenMai = km.maKhuyenMai
        ),
        -- B2: Lấy TẤT CẢ các thuộc tính của một sản phẩm (từ tất cả các biến thể của nó)
        AllProductAttributes AS (
            SELECT DISTINCT
                bt.maSanPham,
                tt.tenThuocTinh,
                tt.tenThuocTinhBienThe
            FROM BienTheSanPham bt
            JOIN ThuocTinh tt ON bt.maSKU = tt.maSKU
        )
        -- B3: Kết hợp thông tin sản phẩm, chi tiết của biến thể rẻ nhất, và TẤT CẢ thuộc tính
        SELECT
            sp.maSanPham, sp.tenSanPham, sp.moTa, sp.thuongHieu, sp.soLuong,
            dm.maDanhMuc, dm.tenDanhMuc,
            -- Lấy thông tin từ biến thể rẻ nhất để hiển thị
            cvd.maSKU,
            cvd.giaKhongKhuyenMai AS giaKhongKhuyenMaiBienThe,
            cvd.giaSauKM AS giaBienThe,
            cvd.giaTriGiam AS giaTriGiamKhuyenMai,
            cvd.loaiGiam,
            -- Lấy tất cả các thuộc tính có thể có của sản phẩm
            apa.tenThuocTinh,
            apa.tenThuocTinhBienThe
        FROM SanPham sp
        JOIN DanhMuc dm ON sp.maDanhMuc = dm.maDanhMuc
        -- Join để lấy thông tin của biến thể rẻ nhất (chỉ lấy rn=1)
        LEFT JOIN CheapestVariantDetails cvd ON sp.maSanPham = cvd.maSanPham AND cvd.rn = 1
        -- Join để lấy tất cả thuộc tính của sản phẩm đó
        LEFT JOIN AllProductAttributes apa ON sp.maSanPham = apa.maSanPham
        WHERE sp.maDanhMuc = :maDanhMuc
        """,
            nativeQuery = true)
    List<Object[]> findWithPromoCheckByDanhMuc(@Param("maDanhMuc") Integer maDanhMuc);























    @Query(value =
            "WITH VariantPrices AS ( " +
                    "    SELECT " +
                    "        bt.maSKU, bt.maSanPham, bt.giaKhongKhuyenMai, bt.soLuong, bt.trangThai, " +
                    "        km.maKhuyenMai, km.giaTriGiam, km.loaiGiam, " +
                    "        CASE " +
                    "            WHEN km.maKhuyenMai IS NOT NULL " +
                    "                 AND km.trangThai = 1 " +
                    "                 AND CONVERT(date, GETDATE()) BETWEEN km.ngayBatDau AND km.ngayKetThuc " +
                    "            THEN CASE " +
                    "                     WHEN km.loaiGiam = 1 " +
                    "                     THEN bt.giaKhongKhuyenMai * (1.0 - (CASE WHEN km.giaTriGiam > 1 THEN km.giaTriGiam/100.0 ELSE km.giaTriGiam END)) " +
                    "                     ELSE bt.giaKhongKhuyenMai - ISNULL(km.giaTriGiam, 0) " +
                    "                 END " +
                    "            ELSE bt.giaKhongKhuyenMai " +
                    "        END AS giaSauKM " +
                    "    FROM BienTheSanPham bt " +
                    "    LEFT JOIN KhuyenMai km ON bt.maKhuyenMai = km.maKhuyenMai " +
                    "), " +
                    "MinPricePerProduct AS ( " +
                    "    SELECT maSanPham, MIN(giaSauKM) AS minGia " +
                    "    FROM VariantPrices " +
                    "    GROUP BY maSanPham " +
                    ") " +
                    "SELECT sp.maSanPham, sp.tenSanPham, sp.moTa,sp.thuongHieu, sp.soLuong, " +
                    "       dm.maDanhMuc, dm.tenDanhMuc, " +
                    "       vpMin.maSKU, vpMin.giaKhongKhuyenMai, vpMin.giaSauKM AS giaHienThi, " +
                    "       vpMin.giaTriGiam, vpMin.loaiGiam " +
                    "FROM SanPham sp " +
                    "LEFT JOIN DanhMuc dm ON sp.maDanhMuc = dm.maDanhMuc " +
                    "LEFT JOIN MinPricePerProduct mp ON mp.maSanPham = sp.maSanPham " +
                    "OUTER APPLY ( " +
                    "    SELECT TOP 1 v.* " +
                    "    FROM VariantPrices v " +
                    "    WHERE v.maSanPham = sp.maSanPham " +
                    "      AND mp.minGia IS NOT NULL " +
                    "      AND v.giaSauKM = mp.minGia " +
                    "    ORDER BY v.maSKU " +
                    ") vpMin " +
                    "WHERE sp.tenSanPham LIKE %:tenSanPham%",
            nativeQuery = true)
    List<Object[]> findWithPromoCheckByTenSanPham(@Param("tenSanPham") String tenSanPham);




    @Query(value =
                "WITH VariantPrices AS ( " +
                        "    SELECT " +
                        "        bt.maSKU, bt.maSanPham, bt.giaKhongKhuyenMai, bt.soLuong, bt.trangThai, " +
                        "        km.maKhuyenMai, km.giaTriGiam, km.loaiGiam, " +
                        "        CASE " +
                        "            WHEN km.maKhuyenMai IS NOT NULL " +
                        "                 AND km.trangThai = 1 " +
                        "                 AND CONVERT(date, GETDATE()) BETWEEN km.ngayBatDau AND km.ngayKetThuc " +
                        "            THEN CASE " +
                        "                     WHEN km.loaiGiam = 1 " +
                        "                     THEN bt.giaKhongKhuyenMai * (1.0 - (CASE WHEN km.giaTriGiam > 1 THEN km.giaTriGiam/100.0 ELSE km.giaTriGiam END)) " +
                        "                     ELSE bt.giaKhongKhuyenMai - ISNULL(km.giaTriGiam, 0) " +
                        "                 END " +
                        "            ELSE bt.giaKhongKhuyenMai " +
                        "        END AS giaSauKM " +
                        "    FROM BienTheSanPham bt " +
                        "    LEFT JOIN KhuyenMai km ON bt.maKhuyenMai = km.maKhuyenMai " +
                        "), " +
                        "MinPricePerProduct AS ( " +
                        "    SELECT maSanPham, MIN(giaSauKM) AS minGia " +
                        "    FROM VariantPrices " +
                        "    GROUP BY maSanPham " +
                        ") " +
                        "SELECT sp.maSanPham, sp.tenSanPham, sp.moTa, sp.soLuong, " +
                        "       dm.maDanhMuc, dm.tenDanhMuc, " +
                        "       vpMin.maSKU, vpMin.giaKhongKhuyenMai, vpMin.giaSauKM AS giaHienThi, " +
                        "       vpMin.giaTriGiam, vpMin.loaiGiam " +
                        "FROM SanPham sp " +
                        "LEFT JOIN DanhMuc dm ON sp.maDanhMuc = dm.maDanhMuc " +
                        "LEFT JOIN MinPricePerProduct mp ON mp.maSanPham = sp.maSanPham " +
                        "OUTER APPLY ( " +
                        "    SELECT TOP 1 v.* " +
                        "    FROM VariantPrices v " +
                        "    WHERE v.maSanPham = sp.maSanPham " +
                        "      AND mp.minGia IS NOT NULL " +
                        "      AND v.giaSauKM = mp.minGia " +
                        "    ORDER BY v.maSKU " +
                        ") vpMin " +
                        "WHERE (:danhMucId IS NULL OR dm.maDanhMuc = :danhMucId) " +
                        "  AND (:minPrice IS NULL OR vpMin.giaSauKM >= :minPrice) " +
                        "  AND (:maxPrice IS NULL OR vpMin.giaSauKM <= :maxPrice)",
                nativeQuery = true)
        List<Object[]> filterSanPham(
                @Param("danhMucId") Long danhMucId,
                @Param("minPrice") BigDecimal minPrice,
                @Param("maxPrice") BigDecimal maxPrice
        );






}