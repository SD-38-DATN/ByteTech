
use datn

DELETE FROM ChiTietDonHang;
DELETE FROM DonHang;
DBCC CHECKIDENT ('ChiTietDonHang', RESEED, 0);
DBCC CHECKIDENT ('DonHang', RESEED, 0);

INSERT INTO Users (tenHienThi, username, passwords, hoTen, gioiTinh, email, soDienThoai, diaChiGiaoHang, roleID)
VALUES
('Nhân Viên 2', 'nv_02', 'pass_nv_02', 'Trần Quốc Hưng', 1, 'hunghq94@example.com', '0912345002', '45 Nguyễn Thị Minh Khai, Quận 1, TP. Hồ Chí Minh', 3),
('Nhân Viên 3', 'nv_03', 'pass_nv_03', 'Phạm Thị Hòa', 0, 'hoapham98@example.com', '0912345003', '25 Hoàng Diệu, Quận Ba Đình, Hà Nội', 3),
('Nhân Viên 4', 'nv_04', 'pass_nv_04', 'Đỗ Văn Lâm', 1, 'lamdv93@example.com', '0912345004', '66 Phan Chu Trinh, Quận Hải Châu, Đà Nẵng', 3),
('Nhân Viên 5', 'nv_05', 'pass_nv_05', 'Lê Minh Châu', 0, 'chaulm96@example.com', '0912345005', '89 Lý Thường Kiệt, Quận 10, TP. Hồ Chí Minh', 3),
('Nhân Viên 6', 'nv_06', 'pass_nv_06', 'Vũ Hồng Phúc', 1, 'phucvh99@example.com', '0912345006', '23 Lê Duẩn, Quận Hoàn Kiếm, Hà Nội', 3),
('Nhân Viên 7', 'nv_07', 'pass_nv_07', 'Ngô Thị Hạnh', 0, 'hanhngo00@example.com', '0912345007', '34 Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh', 3),
('Nhân Viên 8', 'nv_08', 'pass_nv_08', 'Trịnh Văn Dũng', 1, 'dungtv92@example.com', '0912345008', '120 Trường Chinh, Quận Thanh Xuân, Hà Nội', 3),
('Nhân Viên 9', 'nv_09', 'pass_nv_09', 'Đặng Thị Lan', 0, 'landt95@example.com', '0912345009', '5B, Khu đô thị Linh Đàm, Quận Hoàng Mai, Hà Nội', 3),
('Nhân Viên 10', 'nv_10', 'pass_nv_10', 'Hoàng Văn Nam', 1, 'namhv91@example.com', '0912345010', '77 Điện Biên Phủ, Quận Bình Thạnh, TP. Hồ Chí Minh', 3),
('Nhân Viên 11', 'nv_11', 'pass_nv_11', 'Nguyễn Thị Thanh', 0, 'thanhnt@example.com', '0912345011', '22 Lạc Long Quân, Quận Tây Hồ, Hà Nội', 3),
('Nhân Viên 12', 'nv_12', 'pass_nv_12', 'Phạm Văn Duy', 1, 'duypv@example.com', '0912345012', '11 Võ Văn Tần, Quận 3, TP. Hồ Chí Minh', 3);
-- Giả sử bảng DonHang có các cột sau:
-- (maDonHang IDENTITY), userId, ngayDat, trangThai, tenNguoiNhan,
-- tongTien, diaChiGiaoHang, soDienThoai, phuongThucThanhToan, ghiChu, userVoucherId

DECLARE @year INT = 2022;

WHILE @year <= 2025
BEGIN
    DECLARE @month INT = 1;
    WHILE @month <= 12
    BEGIN
        DECLARE @i INT = 1;
        WHILE @i <= 10  -- 10 nhân viên mỗi tháng
        BEGIN
            DECLARE @userId INT = (ABS(CHECKSUM(NEWID())) % 10) + 1; -- userId từ 1-10
            DECLARE @ngay DATETIME = DATEFROMPARTS(@year, @month, (ABS(CHECKSUM(NEWID())) % 28) + 1);
            DECLARE @tongTien DECIMAL(18,0) = ((ABS(CHECKSUM(NEWID())) % 90) + 10) * 1000000;
            DECLARE @trangThai INT = CASE WHEN RAND() < 0.8 THEN 2 ELSE 3 END;  -- 80% hoàn thành, 20% hủy
            DECLARE @pttt NVARCHAR(50) = CASE ABS(CHECKSUM(NEWID())) % 3
                WHEN 0 THEN N'COD'
                WHEN 1 THEN N'Chuyển khoản'
                ELSE N'Ví điện tử' END;

            INSERT INTO DonHang (
                userId,
                ngayDat,
                trangThai,
                tenNguoiNhan,
                tongTien,
                diaChiGiaoHang,
                soDienThoai,
                phuongThucThanhToan,
                ghiChu,
                userVoucherId
            )
            VALUES (
                @userId,
                @ngay,
                @trangThai,
                N'Khách hàng ' + CAST(@userId AS NVARCHAR),
                @tongTien,
                N'Số ' + CAST((ABS(CHECKSUM(NEWID())) % 100) + 1 AS NVARCHAR) + N' Đường ABC, TP.HCM',
                '09' + RIGHT('00000000' + CAST(ABS(CHECKSUM(NEWID())) % 100000000 AS NVARCHAR), 8),
                @pttt,
                N'Giao hàng trong giờ hành chính',
                NULL
            );

            SET @i += 1;
        END
        SET @month += 1;
    END
    SET @year += 1;
END;

-- Giả sử bảng ChiTietDonHang có các cột:
-- maDonHang, maSKU, maSKUPhuKien, imei_id, soLuong, gia

DECLARE @maDon INT;

DECLARE don_cursor CURSOR FOR 
SELECT maDonHang FROM DonHang;

OPEN don_cursor;
FETCH NEXT FROM don_cursor INTO @maDon;

WHILE @@FETCH_STATUS = 0
BEGIN
    DECLARE @spCount INT = (ABS(CHECKSUM(NEWID())) % 3) + 1; -- mỗi đơn 1-3 sản phẩm
    DECLARE @n INT = 1;

    WHILE @n <= @spCount
    BEGIN
        DECLARE @isAccessory BIT = CASE WHEN RAND() < 0.4 THEN 1 ELSE 0 END;
        DECLARE @sku NVARCHAR(50);
        DECLARE @gia DECIMAL(18,0);

        IF @isAccessory = 1
            SELECT TOP 1 @sku = maSKUPhuKien, @gia = gia FROM BienThePhuKien ORDER BY NEWID();
        ELSE
            SELECT TOP 1 @sku = maSKU, @gia = gia FROM BienTheSanPham ORDER BY NEWID();

        DECLARE @soLuong INT = (ABS(CHECKSUM(NEWID())) % 3) + 1;
        DECLARE @imei INT = (ABS(CHECKSUM(NEWID())) % 200) + 1;

        INSERT INTO ChiTietDonHang (maDonHang, maSKU, maSKUPhuKien, imei_id, soLuong, gia)
        VALUES (
            @maDon,
            CASE WHEN @isAccessory = 1 THEN NULL ELSE @sku END,
            CASE WHEN @isAccessory = 1 THEN @sku ELSE NULL END,
            @imei,
            @soLuong,
            @gia
        );

        SET @n += 1;
    END

    FETCH NEXT FROM don_cursor INTO @maDon;
END;

CLOSE don_cursor;
DEALLOCATE don_cursor;


-- Xóa bảng tạm nếu đã tồn tại
IF OBJECT_ID('tempdb..#Phones') IS NOT NULL DROP TABLE #Phones;

-- ✅ Tạo bảng tạm có cột id tự tăng
CREATE TABLE #Phones (
    id INT IDENTITY(1,1) PRIMARY KEY,  -- Cột id rất quan trọng
    soDienThoai NVARCHAR(20)
);

-- ✅ Thêm 50 số điện thoại ngẫu nhiên
DECLARE @i INT = 1;
WHILE @i <= 50
BEGIN
    INSERT INTO #Phones
    SELECT '09' + RIGHT('00000000' + CAST(ABS(CHECKSUM(NEWID())) % 100000000 AS NVARCHAR), 8);
    SET @i += 1;
END;

-- ✅ Cập nhật ngẫu nhiên số điện thoại cho từng đơn hàng
WITH RandomPhones AS (
    SELECT 
        maDonHang,
        ROW_NUMBER() OVER (ORDER BY NEWID()) AS rn
    FROM DonHang
)
UPDATE d
SET soDienThoai = p.soDienThoai
FROM DonHang d
JOIN RandomPhones r ON d.maDonHang = r.maDonHang
JOIN #Phones p ON ((r.rn - 1) % 50) + 1 = p.id;

-- ✅ Kiểm tra kết quả
SELECT TOP 20 maDonHang, soDienThoai
FROM DonHang;
