-- ========================================
-- SQL Script: Insert IMEI Test Data
-- Mục đích: Tạo dữ liệu test cho chức năng tìm kiếm IMEI
-- ========================================

-- Lưu ý: Thay đổi maSKU và maSKUPhuKien theo dữ liệu thực tế trong database của bạn

-- ========================================
-- 1. INSERT IMEI CHO IPHONE 15 PRO MAX
-- ========================================
-- Giả sử maSKU của iPhone 15 Pro Max 1TB Đen là: IP15PM-1TB-DEN-1

INSERT INTO IMEI (maSKU, maSKUPhuKien, imei, ngayNhapKho, trangThai) 
VALUES 
    ('IP15PM-1TB-DEN-1', NULL, '123456789012345', GETDATE(), 1),
    ('IP15PM-1TB-DEN-1', NULL, '123456789012346', GETDATE(), 1),
    ('IP15PM-1TB-DEN-1', NULL, '123456789012347', GETDATE(), 1),
    ('IP15PM-1TB-DEN-1', NULL, '123456789012348', GETDATE(), 0), -- Đã bán
    ('IP15PM-1TB-DEN-1', NULL, '123456789012349', GETDATE(), 1);

-- ========================================
-- 2. INSERT IMEI CHO DELL XPS 13
-- ========================================
-- Giả sử maSKU của Dell XPS 13 8GB Đen là: XPS13-8GB-DEN-1

INSERT INTO IMEI (maSKU, maSKUPhuKien, imei, ngayNhapKho, trangThai) 
VALUES 
    ('XPS13-8GB-DEN-1', NULL, 'DXPS9876543210A', GETDATE(), 1),
    ('XPS13-8GB-DEN-1', NULL, 'DXPS9876543210B', GETDATE(), 1),
    ('XPS13-8GB-DEN-1', NULL, 'DXPS9876543210C', GETDATE(), 1);

-- ========================================
-- 3. INSERT IMEI CHO DELL XPS 13 16GB
-- ========================================
-- Giả sử maSKU của Dell XPS 13 16GB Bạc là: XPS13-16GB-BAC-1

INSERT INTO IMEI (maSKU, maSKUPhuKien, imei, ngayNhapKho, trangThai) 
VALUES 
    ('XPS13-16GB-BAC-1', NULL, 'DXPS16GB001234A', GETDATE(), 1),
    ('XPS13-16GB-BAC-1', NULL, 'DXPS16GB001234B', GETDATE(), 1);

-- ========================================
-- 4. KIỂM TRA DỮ LIỆU VỪA INSERT
-- ========================================

-- Kiểm tra tất cả IMEI vừa insert
SELECT * FROM IMEI 
WHERE imei IN (
    '123456789012345', '123456789012346', '123456789012347', '123456789012348', '123456789012349',
    'DXPS9876543210A', 'DXPS9876543210B', 'DXPS9876543210C',
    'DXPS16GB001234A', 'DXPS16GB001234B'
);

-- Đếm số lượng IMEI theo trạng thái
SELECT 
    trangThai,
    CASE 
        WHEN trangThai = 1 THEN 'Trong kho'
        WHEN trangThai = 0 THEN 'Đã bán'
        ELSE 'Khác'
    END AS TrangThaiText,
    COUNT(*) AS SoLuong
FROM IMEI
GROUP BY trangThai;

-- ========================================
-- 5. TEST QUERY TÌM KIẾM (để debug)
-- ========================================

-- Test tìm IMEI cụ thể
SELECT * FROM IMEI WHERE imei = '123456789012345';

-- Test join với BienTheSanPham
SELECT 
    i.imei,
    i.maSKU,
    bt.gia,
    sp.tenSanPham
FROM IMEI i
LEFT JOIN BienTheSanPham bt ON i.maSKU = bt.maSKU
LEFT JOIN SanPham sp ON bt.maSanPham = sp.maSanPham
WHERE i.imei = '123456789012345';

-- ========================================
-- 6. XÓA DỮ LIỆU TEST (nếu cần reset)
-- ========================================
/*
-- CẢNH BÁO: Uncomment dòng dưới để xóa tất cả IMEI test
DELETE FROM IMEI 
WHERE imei IN (
    '123456789012345', '123456789012346', '123456789012347', '123456789012348', '123456789012349',
    'DXPS9876543210A', 'DXPS9876543210B', 'DXPS9876543210C',
    'DXPS16GB001234A', 'DXPS16GB001234B'
);
*/

-- ========================================
-- LƯU Ý QUAN TRỌNG
-- ========================================
-- 1. Đảm bảo các mã SKU đã tồn tại trong bảng BienTheSanPham
-- 2. Kiểm tra constraint UNIQUE trên cột 'imei' trước khi insert
-- 3. Thay đổi GETDATE() thành NOW() nếu dùng MySQL/PostgreSQL
-- 4. Trường 'trangThai': 1 = Trong kho, 0 = Đã bán
-- ========================================

