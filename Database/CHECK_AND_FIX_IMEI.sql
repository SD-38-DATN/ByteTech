-- ========================================
-- KIỂM TRA VÀ SỬA LỖI DỮ LIỆU IMEI
-- ========================================

PRINT '=== BƯỚC 1: KIỂM TRA DỮ LIỆU ===';
GO

-- 1.1 Kiểm tra số lượng IMEI
PRINT 'Số lượng IMEI trong database:';
SELECT COUNT(*) AS TongSoIMEI FROM IMEI;
GO

-- 1.2 Kiểm tra IMEI có maSKU
PRINT 'IMEI có liên kết với BienTheSanPham:';
SELECT COUNT(*) AS SoIMEI_CoMaSKU 
FROM IMEI 
WHERE maSKU IS NOT NULL;
GO

-- 1.3 Kiểm tra IMEI có maSKUPhuKien
PRINT 'IMEI có liên kết với BienThePhuKien:';
SELECT COUNT(*) AS SoIMEI_CoMaSKUPhuKien 
FROM IMEI 
WHERE maSKUPhuKien IS NOT NULL;
GO

-- 1.4 Kiểm tra IMEI không có liên kết (lỗi dữ liệu)
PRINT 'IMEI KHÔNG CÓ LIÊN KẾT (CẦN SỬA):';
SELECT * 
FROM IMEI 
WHERE maSKU IS NULL AND maSKUPhuKien IS NULL;
GO

PRINT '=== BƯỚC 2: KIỂM TRA BienTheSanPham ===';
GO

-- 2.1 Số lượng biến thể sản phẩm
PRINT 'Số lượng biến thể sản phẩm:';
SELECT COUNT(*) AS TongBienThe FROM BienTheSanPham;
GO

-- 2.2 Lấy 5 mã SKU mẫu
PRINT 'Danh sách 5 mã SKU mẫu:';
SELECT TOP 5 
    bt.maSKU,
    sp.tenSanPham,
    bt.gia,
    bt.soLuong
FROM BienTheSanPham bt
LEFT JOIN SanPham sp ON bt.maSanPham = sp.maSanPham
ORDER BY bt.maSKU;
GO

PRINT '=== BƯỚC 3: KIỂM TRA DỮ LIỆU LIÊN KẾT ===';
GO

-- 3.1 IMEI join với BienTheSanPham
PRINT 'IMEI join với BienTheSanPham (10 bản ghi đầu):';
SELECT TOP 10
    i.id,
    i.imei,
    i.maSKU,
    bt.maSKU AS bt_maSKU,
    sp.tenSanPham,
    bt.gia
FROM IMEI i
LEFT JOIN BienTheSanPham bt ON i.maSKU = bt.maSKU
LEFT JOIN SanPham sp ON bt.maSanPham = sp.maSanPham
WHERE i.maSKU IS NOT NULL;
GO

-- 3.2 Tìm IMEI có maSKU nhưng không tồn tại trong BienTheSanPham (lỗi FK)
PRINT 'IMEI có maSKU KHÔNG HỢP LỆ (cần sửa):';
SELECT i.*
FROM IMEI i
LEFT JOIN BienTheSanPham bt ON i.maSKU = bt.maSKU
WHERE i.maSKU IS NOT NULL 
  AND bt.maSKU IS NULL;
GO

PRINT '=== BƯỚC 4: CHUẨN BỊ DỮ LIỆU TEST ===';
GO

-- 4.1 Lấy maSKU hợp lệ đầu tiên
DECLARE @ValidMaSKU VARCHAR(50);
SELECT TOP 1 @ValidMaSKU = maSKU 
FROM BienTheSanPham 
WHERE soLuong > 0;

PRINT 'Mã SKU hợp lệ để test: ' + ISNULL(@ValidMaSKU, 'KHÔNG TÌM THẤY');

-- 4.2 Kiểm tra xem có IMEI test không
IF EXISTS (SELECT 1 FROM IMEI WHERE imei = 'TEST123456789')
BEGIN
    PRINT 'IMEI test đã tồn tại: TEST123456789';
    SELECT * FROM IMEI WHERE imei = 'TEST123456789';
END
ELSE
BEGIN
    PRINT 'Chưa có IMEI test';
END
GO

PRINT '=== BƯỚC 5: INSERT DỮ LIỆU TEST (NẾU CẦN) ===';
GO

-- 5.1 Lấy maSKU hợp lệ
DECLARE @TestMaSKU VARCHAR(50);
SELECT TOP 1 @TestMaSKU = maSKU 
FROM BienTheSanPham 
WHERE soLuong > 0
ORDER BY maSKU;

-- 5.2 Insert IMEI test nếu chưa có
IF @TestMaSKU IS NOT NULL AND NOT EXISTS (SELECT 1 FROM IMEI WHERE imei = 'TEST123456789')
BEGIN
    PRINT 'Đang insert IMEI test...';
    
    INSERT INTO IMEI (maSKU, maSKUPhuKien, imei, ngayNhapKho, trangThai)
    VALUES (@TestMaSKU, NULL, 'TEST123456789', GETDATE(), 1);
    
    PRINT 'Đã insert IMEI test: TEST123456789 với maSKU: ' + @TestMaSKU;
    
    -- Hiển thị kết quả
    SELECT * FROM IMEI WHERE imei = 'TEST123456789';
END
ELSE IF @TestMaSKU IS NULL
BEGIN
    PRINT 'KHÔNG THỂ INSERT: Không có maSKU hợp lệ trong BienTheSanPham!';
    PRINT 'Vui lòng kiểm tra lại dữ liệu BienTheSanPham và SanPham';
END
ELSE
BEGIN
    PRINT 'IMEI test đã tồn tại, không cần insert';
END
GO

PRINT '=== BƯỚC 6: TEST QUERY TÌM KIẾM ===';
GO

-- 6.1 Test tìm IMEI
DECLARE @SearchIMEI VARCHAR(50) = 'TEST123456789';

PRINT 'Test tìm IMEI: ' + @SearchIMEI;
SELECT * FROM IMEI WHERE imei = @SearchIMEI;
GO

-- 6.2 Test query tìm sản phẩm theo IMEI (giống logic backend)
DECLARE @SearchIMEI2 VARCHAR(50) = 'TEST123456789';
DECLARE @FoundMaSKU VARCHAR(50);

SELECT TOP 1 @FoundMaSKU = maSKU 
FROM IMEI 
WHERE imei = @SearchIMEI2;

IF @FoundMaSKU IS NOT NULL
BEGIN
    PRINT 'Tìm thấy maSKU: ' + @FoundMaSKU;
    
    -- Query giống như SanPhamChonRepo
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
    PRINT 'Không tìm thấy maSKU cho IMEI: ' + @SearchIMEI2;
END
GO

PRINT '=== HOÀN THÀNH ===';
PRINT 'Bây giờ bạn có thể test API với IMEI: TEST123456789';
PRINT 'curl "http://localhost:8081/api/admin/san-pham-chon/tim-kiem?keyword=TEST123456789"';
GO

-- ========================================
-- SCRIPT SỬA LỖI (NẾU CẦN)
-- ========================================

/*
-- Nếu có IMEI không có liên kết, cập nhật với maSKU hợp lệ:
UPDATE IMEI 
SET maSKU = (SELECT TOP 1 maSKU FROM BienTheSanPham WHERE soLuong > 0)
WHERE maSKU IS NULL AND maSKUPhuKien IS NULL;

-- Hoặc xóa IMEI không hợp lệ:
DELETE FROM IMEI 
WHERE maSKU IS NULL AND maSKUPhuKien IS NULL;
*/

