-- ========================================
-- TEST QUERY TRỰC TIẾP - Giống Backend
-- ========================================

-- Test query giống như backend sử dụng
-- File: SanPhamChonRepo.java

PRINT '=== TEST QUERY: Lấy tất cả sản phẩm ===';
GO

SELECT
    sp.tenSanPham,
    bt.maSKU,
    bt.gia,
    STRING_AGG(CONCAT(tt.tenThuocTinh, ': ', tt.tenThuocTinhBienThe), ', ') AS ThuocTinh,
    bt.soLuong
FROM SanPham sp
JOIN BienTheSanPham bt ON sp.maSanPham = bt.maSanPham
LEFT JOIN ThuocTinh tt ON bt.maSKU = tt.maSKU
GROUP BY sp.tenSanPham, bt.maSKU, bt.gia, bt.soLuong
ORDER BY sp.tenSanPham, bt.maSKU, bt.gia, bt.soLuong;
GO

PRINT '=== TEST: Tìm sản phẩm theo mã SKU cụ thể ===';
GO

-- Thay 'YOUR_MASKU' bằng mã SKU thực tế
DECLARE @TestMaSKU VARCHAR(50);
SELECT TOP 1 @TestMaSKU = maSKU FROM BienTheSanPham;

PRINT 'Test với mã SKU: ' + ISNULL(@TestMaSKU, 'NULL');

SELECT
    sp.tenSanPham,
    bt.maSKU,
    bt.gia,
    STRING_AGG(CONCAT(tt.tenThuocTinh, ': ', tt.tenThuocTinhBienThe), ', ') AS ThuocTinh,
    bt.soLuong
FROM SanPham sp
JOIN BienTheSanPham bt ON sp.maSanPham = bt.maSanPham
LEFT JOIN ThuocTinh tt ON bt.maSKU = tt.maSKU
WHERE bt.maSKU = @TestMaSKU
GROUP BY sp.tenSanPham, bt.maSKU, bt.gia, bt.soLuong;
GO

PRINT '=== TEST: Luồng tìm kiếm IMEI đầy đủ ===';
GO

-- Bước 1: Tìm IMEI
DECLARE @SearchIMEI VARCHAR(50) = 'TEST123456789';
DECLARE @FoundMaSKU VARCHAR(50);
DECLARE @IMEIExists INT;

PRINT 'Bước 1: Tìm IMEI: ' + @SearchIMEI;

SELECT @IMEIExists = COUNT(*), @FoundMaSKU = MAX(maSKU)
FROM IMEI 
WHERE imei = @SearchIMEI;

IF @IMEIExists > 0
BEGIN
    PRINT '  ✅ Tìm thấy IMEI';
    PRINT '  ✅ Mã SKU: ' + ISNULL(@FoundMaSKU, 'NULL');
    
    IF @FoundMaSKU IS NOT NULL
    BEGIN
        PRINT 'Bước 2: Tìm sản phẩm theo mã SKU...';
        
        SELECT
            sp.tenSanPham,
            bt.maSKU,
            bt.gia,
            STRING_AGG(CONCAT(tt.tenThuocTinh, ': ', tt.tenThuocTinhBienThe), ', ') AS ThuocTinh,
            bt.soLuong
        FROM SanPham sp
        JOIN BienTheSanPham bt ON sp.maSanPham = bt.maSanPham
        LEFT JOIN ThuocTinh tt ON bt.maSKU = tt.maSKU
        WHERE bt.maSKU = @FoundMaSKU
        GROUP BY sp.tenSanPham, bt.maSKU, bt.gia, bt.soLuong;
        
    END
    ELSE
    BEGIN
        PRINT '  ❌ IMEI không có maSKU!';
    END
END
ELSE
BEGIN
    PRINT '  ❌ Không tìm thấy IMEI: ' + @SearchIMEI;
    PRINT '  Danh sách IMEI hiện có:';
    SELECT TOP 5 imei, maSKU FROM IMEI;
END
GO

PRINT '=== HOÀN THÀNH ===';

