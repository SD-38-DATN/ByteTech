USE datn;
GO

/********************************************************
 1) XÓA TOÀN BỘ DỮ LIỆU CŨ (theo thứ tự tránh FK error)
 ********************************************************/
SET NOCOUNT ON;

DELETE FROM ChiTietDonHang;
DELETE FROM DonHang;

DELETE FROM GioHangChiTiet;
DELETE FROM GioHang;

DELETE FROM IMEI;

DELETE FROM Messages;

DELETE FROM FeedBack;

DELETE FROM ThuocTinhPhuKien;
DELETE FROM BienThePhuKien;
DELETE FROM PhuKien;

DELETE FROM ThuocTinh;
DELETE FROM BienTheSanPham;

DELETE FROM ThongSoKyThuat;
DELETE FROM LoaiThongSo;

DELETE FROM SanPham;

DELETE FROM DanhMucPhuKien;
DELETE FROM DanhMuc;

DELETE FROM UserVoucher;
DELETE FROM Voucher;
DELETE FROM KhuyenMai;

DELETE FROM Users;
DELETE FROM roles;

-- reset identity seeds (nếu muốn bắt đầu lại ID từ 1)
DBCC CHECKIDENT ('roles', RESEED, 0);
DBCC CHECKIDENT ('Users', RESEED, 0);
DBCC CHECKIDENT ('Voucher', RESEED, 0);
DBCC CHECKIDENT ('UserVoucher', RESEED, 0);
DBCC CHECKIDENT ('KhuyenMai', RESEED, 0);
DBCC CHECKIDENT ('DanhMuc', RESEED, 0);
DBCC CHECKIDENT ('DanhMucPhuKien', RESEED, 0);
DBCC CHECKIDENT ('SanPham', RESEED, 0);
DBCC CHECKIDENT ('PhuKien', RESEED, 0);
DBCC CHECKIDENT ('ThongSoKyThuat', RESEED, 0);
DBCC CHECKIDENT ('LoaiThongSo', RESEED, 0);
DBCC CHECKIDENT ('FeedBack', RESEED, 0);
DBCC CHECKIDENT ('GioHang', RESEED, 0);
DBCC CHECKIDENT ('DonHang', RESEED, 0);
DBCC CHECKIDENT ('IMEI', RESEED, 0);
DBCC CHECKIDENT ('ChiTietDonHang', RESEED, 0);
DBCC CHECKIDENT ('ThuocTinh', RESEED, 0);
DBCC CHECKIDENT ('ThuocTinhPhuKien', RESEED, 0);
/********************************************************
DBCC CHECKIDENT ('BienThePhuKien', RESEED, 0);
DBCC CHECKIDENT ('BienTheSanPham', RESEED, 0);
 ********************************************************/
GO

/********************************************************
 2) CHÈN DỮ LIỆU MỚI (khoảng ~10 dòng mỗi bảng)
    - các giá trị chọn theo kịch bản "thực tế" điện thoại + phụ kiện
 ********************************************************/

-- 2.1 roles
INSERT INTO roles (roleName) VALUES 
('USER'), ('ADMIN'), ('STAFF');
GO

-- 2.2 users (3-6 user mẫu)
INSERT INTO Users (tenHienThi, username, passwords, hoTen, gioiTinh, email, soDienThoai, diaChiGiaoHang, roleID)
VALUES
(N'Quản trị viên', 'admin', 'admin123', N'Nguyễn Văn A', 1, 'admin@shop.com', '0987000001', N'123 Đường A, Q1, HCM', 2),
(N'Người dùng 1', 'user1', 'user123', N'Trần Thị B', 0, 'user1@mail.com', '0987000002', N'45 Đường B, Q3, HCM', 1),
(N'Nhân viên 1', 'staff1', 'staff123', N'Phạm Văn C', 1, 'staff1@mail.com', '0987000003', N'78 Đường C, Q5, HCM', 3),
(N'Người dùng 2', 'user2', 'user234', N'Lê Thị D', 0, 'user2@mail.com', '0987000004', N'99 Đường D, HN', 1),
(N'Khách E', 'user3', 'pass123', N'Hoàng Văn E', 1, 'user3@mail.com', '0987000005', N'12 Đường E, DN', 1);
GO

-- 2.3 DanhMuc (sản phẩm)
INSERT INTO DanhMuc (tenDanhMuc, moTa)
VALUES
(N'Điện thoại', N'Smartphone các hãng'),
(N'Máy tính bảng', N'Tablet, iPad'),
(N'Laptop', N'Laptop cá nhân'),
(N'Phụ kiện', N'Ốp lưng, sạc, cáp, tai nghe'),
(N'Âm thanh', N'Tai nghe, loa');
GO

-- 2.4 DanhMucPhuKien
INSERT INTO DanhMucPhuKien (tenDanhMucPhuKien, moTa)
VALUES
(N'Ốp lưng', N'Ốp bảo vệ điện thoại'),
(N'Sạc & Cáp', N'Cáp sạc, sạc nhanh'),
(N'Tai nghe', N'Tai nghe bluetooth/wired'),
(N'Sạc dự phòng', N'Pin dự phòng'),
(N'Dây cáp', N'Cáp kết nối');
GO

-- 2.5 SanPham (khoảng 8-10)
INSERT INTO SanPham (tenSanPham, thuongHieu, moTa, soLuong, gia, trangThai, maDanhMuc, userId)
VALUES
(N'iPhone 15 Pro Max', N'Apple', N'Flagship Apple 2025', 50, 34000000, 1, 1, 1),
(N'Samsung Galaxy S24 Ultra', N'Samsung', N'Smartphone cao cấp', 40, 28000000, 1, 1, 1),
(N'Xiaomi 13T Pro', N'Xiaomi', N'Chụp ảnh tốt, sạc nhanh', 80, 15000000, 1, 1, 1),
(N'iPad Pro M2 11"', N'Apple', N'Tablet cho công việc và sáng tạo', 25, 23000000, 1, 2, 1),
(N'MacBook Air M2 13"', N'Apple', N'Laptop mỏng nhẹ', 20, 29000000, 1, 3, 1),
(N'AirPods Pro 2', N'Apple', N'Tai nghe chống ồn', 120, 6000000, 1, 4, 1),
(N'Logitech MX Master 3S', N'Logitech', N'Chuột công thái học', 150, 2500000, 1, 4, 3),
(N'Anker PowerCore 20000', N'Anker', N'Pin dự phòng 20000mAh', 80, 1200000, 1, 4, 3),
(N'Ốp lưng iPhone 15', N'Baseus', N'Ốp silicon bảo vệ', 200, 290000, 1, 4, 3);
GO

-- 2.6 KhuyenMai (một số khuyến mãi)
INSERT INTO KhuyenMai (tenKhuyenMai, moTa, loaiGiam, giaTriGiam, ngayBatDau, ngayKetThuc, trangThai)
VALUES
(N'KM Toàn cửa hàng 10%', N'Giảm 10% cho toàn bộ sản phẩm', 1, 0.10, '2025-09-01', '2025-09-30', 1),
(N'KM Phụ kiện 5%', N'Giảm 5% phụ kiện', 1, 0.05, '2025-09-15', '2025-09-25', 1),
(N'KM Giảm 200k', N'Giảm 200.000đ cho đơn trên 2.000.000đ', 0, 200000.00, '2025-09-10', '2025-10-10', 1),
(N'KM Flash 20%', N'Flash sale 3 ngày', 1, 0.20, '2025-09-18', '2025-09-20', 1);
GO

-- 2.7 BienTheSanPham (biến thể, ~10)
INSERT INTO BienTheSanPham (maSKU, gia, giaKhongKhuyenMai, soLuong, trangThai, maSanPham, maKhuyenMai)
VALUES
('IP15-256-GRY', 34000000, 34000000, 15, 1, 1, 1),
('IP15-512-SLV', 38000000, 38000000, 10, 1, 1, 1),
('SS24-256-BLK', 28000000, 28000000, 20, 1, 2, NULL),
('X13T-256-BLK',15000000,15000000,30,1,3,NULL),
('IPAD-PRO-11-128',23000000,23000000,8,1,4,4),
('MBAIR-M2-8-256',29000000,29000000,6,1,5,NULL),
('AIRP2-WHT',6000000,6000000,60,1,6,2),
('LOG-MX3S-BLK',2500000,2500000,80,1,7,NULL),
('ANK-PC20K',1200000,1200000,50,1,8,NULL),
('OP-I15-BLK',290000,290000,150,1,9,NULL);
GO

-- 2.8 ThuocTinh (thuộc tính từng biến thể)
INSERT INTO ThuocTinh (maSKU, tenThuocTinh, tenThuocTinhBienThe, TrangThai)
VALUES
('IP15-256-GRY', N'Dung lượng', N'256GB', 1),
('IP15-512-SLV', N'Dung lượng', N'512GB', 1),
('SS24-256-BLK', N'Dung lượng', N'256GB', 1),
('X13T-256-BLK', N'Dung lượng', N'256GB', 1),
('IPAD-PRO-11-128', N'Kết nối', N'Wi-Fi', 1),
('MBAIR-M2-8-256', N'RAM', N'8GB', 1),
('AIRP2-WHT', N'Trạng thái', N'Mới', 1),
('LOG-MX3S-BLK', N'Loại', N'Chuột công thái học', 1),
('ANK-PC20K', N'Dung lượng', N'20000mAh', 1),
('OP-I15-BLK', N'Màu sắc', N'Den', 1);
GO

-- 2.9 PhuKien (~6-8)
INSERT INTO PhuKien (tenPhuKien, thuongHieu, moTa, maSanPham, maDanhMucPhuKien, userId, soLuong, gia, trangThai)
VALUES
(N'AirPods Pro 2', N'Apple', N'Tai nghe chống ồn chủ động', 6, 1, 1, 100, 6000000, 1),
(N'Anker PowerCore 20000', N'Anker', N'Pin dự phòng 20000mAh', 8, 4, 1, 60, 1200000, 1),
(N'Cáp Type-C 1m', N'Baseus', N'Cáp bọc dù, sạc nhanh', 8, 5, 3, 300, 190000, 1),
(N'Ốp lưng iPhone 15', N'Baseus', N'Ốp bảo vệ chống sốc', 9, 1, 3, 250, 290000, 1),
(N'Dock sạc 45W', N'Samsung', N'Cốc sạc nhanh PD 45W', 2, 2, 3, 80, 490000, 1);
GO

-- 2.10 BienThePhuKien (~8)
INSERT INTO BienThePhuKien (maSKUPhuKien, gia, soLuong, trangThai, maPhuKien)
VALUES
('APRO2-WHT', 6000000, 40, 1, 1),
('APRO2-BLK', 6100000, 20, 1, 1),
('ANK-20K-BLK',1200000,30,1,2),
('CABLE-TYPEC-1M',190000,150,1,3),
('OP-I15-BLK',290000,120,1,4),
('DOCK-45W',490000,80,1,5),
('ANK-20K-WHT',1250000,20,1,2),
('CABLE-TYPEC-2M',220000,60,1,3);
GO

-- 2.11 ThuocTinhPhuKien
INSERT INTO ThuocTinhPhuKien (tenThuocTinh, giaTriThuocTinh, maSKUPhuKien)
VALUES
(N'Màu sắc', N'Trắng', 'APRO2-WHT'),
(N'Màu sắc', N'Den', 'APRO2-BLK'),
(N'Dung lượng', N'20000mAh', 'ANK-20K-BLK'),
(N'Độ dài', N'1m', 'CABLE-TYPEC-1M'),
(N'Chất liệu', N'Silicon', 'OP-I15-BLK'),
(N'Công suất', N'45W', 'DOCK-45W'),
(N'Màu sắc', N'Trắng', 'ANK-20K-WHT'),
(N'Độ dài', N'2m', 'CABLE-TYPEC-2M');
GO

-- 2.12 LoaiThongSo
INSERT INTO LoaiThongSo (tenLoaiThongSo, maDanhMuc, maDanhMucPhuKien)
VALUES
(N'Màn hình', 1, NULL),
(N'Camera', 1, NULL),
(N'CPU & RAM', 1, NULL),
(N'Pin & Sạc', 4, 2),
(N'Thông số chung', NULL, NULL);
GO

-- 2.13 ThongSoKyThuat (~10)
INSERT INTO ThongSoKyThuat (tenThongSo, giaTriThongSo, trangThai, loaiThongSoId, maSanPham, maPhuKien)
VALUES
(N'Công nghệ màn hình', N'OLED 120Hz', 1, 1, 1, NULL),
(N'Độ phân giải', N'1290 x 2796', 1, 1, 1, NULL),
(N'Chip xử lý', N'Apple A17 Pro', 1, 3, 1, NULL),
(N'RAM', N'12 GB', 1, 3, 3, NULL),
(N'Dung lượng', N'256GB', 1, 3, 1, NULL),
(N'Pin', N'5000 mAh', 1, 4, 2, NULL),
(N'Chuẩn sạc', N'PD 45W', 1, 4, NULL, 5),
(N'Kết nối', N'USB-C, Bluetooth', 1, 5, NULL, 3),
(N'Loại bụi-nước', N'IP68', 1, 5, 1, NULL),
(N'Khối lượng', N'188 g', 1, 5, 1, NULL);
GO

-- 2.14 Voucher (một số)
INSERT INTO Voucher (codeVoucher, tenVoucher, soLanSuDung, moTa, loaiGiam, giaTriGiam, dieuKienGiam, giamToiDa, ngayBatDau, ngayKetThuc, trangThai)
VALUES
('ADMIN10', N'Giảm 10% cho Admin', 100, N'Voucher dành cho Admin', 1, 0.10, 0, 1000000, '2025-08-01', '2025-12-31', 1),
('USER50K', N'Giảm 50k', 100, N'Giảm 50k cho đơn hàng', 0, 50000, 300000, 50000, '2025-08-01', '2025-12-31', 1),
('NEWUSER20', N'Giảm 20%', 50, N'Voucher cho user mới', 1, 0.20, 0, 200000, '2025-09-01', '2026-01-01', 1),
('FLASH15', N'Flash sale 15%', 100, N'15% trong 24h', 1, 0.15, 0, 150000, '2025-09-18', '2025-09-20', 1);
GO

-- 2.15 UserVoucher (gán cho user)
INSERT INTO UserVoucher (userId, voucherId, soLanSuDung, ngayNhan, trangThai)
VALUES
(1, 1, 100, GETDATE(), 1),
(2, 2, 1, GETDATE(), 1),
(4, 3, 1, GETDATE(), 1),
(2, 4, 1, GETDATE(), 1);
GO

-- 2.16 GioHang (khoảng 3)
INSERT INTO GioHang (userId, tongTien, ngayTao)
VALUES
(2, 34000000, GETDATE()),
(4, 1500000, GETDATE()),
(5, 1200000, GETDATE());
GO

-- 2.17 GioHangChiTiet
INSERT INTO GioHangChiTiet (maGioHang, maSKU, maSKUPhuKien, soLuong, gia)
VALUES
(1, 'IP15-256-GRY', NULL, 1, 34000000),
(1, 'AIRP2-WHT', NULL, 1, 6000000),
(2, NULL, 'CABLE-TYPEC-1M', 2, 190000),
(3, NULL, 'ANK-20K-BLK', 1, 1200000);  -- sửa dòng này
GO


-- 2.18 IMEI (gán IMEI cho các biến thể có IMEI)
INSERT INTO IMEI (maSKU, maSKUPhuKien, imei, ngayNhapKho, trangThai)
VALUES
('IP15-256-GRY', NULL, '358456090000101', GETDATE(), 1),
('IP15-256-GRY', NULL, '358456090000102', GETDATE(), 1),
('IP15-512-SLV', NULL, '358456090000201', GETDATE(), 1),
('SS24-256-BLK', NULL, '358456090000301', GETDATE(), 1),
('X13T-256-BLK', NULL, '358456090000401', GETDATE(), 1);
GO

-- 2.19 DonHang (3 đơn hàng mẫu)
INSERT INTO DonHang (userId, ngayDat, trangThai, tongTien, diaChiGiaoHang, soDienThoai, phuongThucThanhToan, ghiChu, userVoucherId)
VALUES
(2, GETDATE(), N'Chờ xác nhận', 40000000, N'45 Đường B, Q3, HCM', N'0987000002', N'COD', N'Giao trong giờ hành chính', NULL),
(4, GETDATE(), N'Đang giao', 1500000, N'99 Đường D, HN', N'0987000004', N'Transfer', N'Nhắn số zalo khi giao', 2),
(5, GETDATE(), N'Hoàn thành', 1200000, N'12 Đường E, DN', N'0987000005', N'COD', N'Khách nhận giúp', NULL);
GO

-- 2.20 ChiTietDonHang (liên kết các đơn)
-- note: imei_id cần tồn tại trong IMEI hoặc NULL
INSERT INTO ChiTietDonHang (maDonHang, maSKU, maSKUPhuKien, imei_id, soLuong, gia)
VALUES
(1, 'IP15-256-GRY', 'ANK-20K-WHT', 1, 1, 34000000),
(1, 'ANK-PC20K', 'APRO2-BLK',1, 1, 550000)
GO

-- 2.21 FeedBack (phản hồi)
INSERT INTO FeedBack (userId, maSanPham, maPhuKien, noiDung, danhGia, ngayDanhGia)
VALUES
(2, 1, NULL, N'Điện thoại dùng rất mượt, hoàn thiện tốt.', 5, GETDATE()),
(4, 2, NULL, N'Camera quá ổn cho tầm giá.', 4, GETDATE()),
(2, NULL, 1, N'AirPods rất chất lượng, chống ồn ngon.', 5, GETDATE()),
(5, 3, NULL, N'Pin dùng lâu, đáng tiền.', 4, GETDATE());
GO

-- 2.22 Messages (tin nhắn giữa user và admin)
INSERT INTO Messages (senderId, receiverId, content, timestamp, isRead)
VALUES
(1, 2, N'Xin chào, cảm ơn bạn đã đặt hàng. Chúng tôi sẽ xác nhận sớm.', GETDATE(), 0),
(2, 1, N'Cảm ơn, tôi muốn hủy 1 món nếu được.', GETDATE(), 0),
(1, 4, N'Nhắc lịch bảo hành: vui lòng mang đến cửa hàng trong tuần.', GETDATE(), 0);
GO

/********************************************************
 3) KIỂM TRA NHANH: hiện một vài dòng để bạn verify
 ********************************************************/
-- 1. Roles
SELECT TOP 10 * FROM Roles;

-- 2. Users
SELECT TOP 10 * FROM Users;

-- 3. KhuyenMai
SELECT TOP 10 * FROM KhuyenMai;

-- 4. DanhMuc
SELECT TOP 10 * FROM DanhMuc;

-- 5. DanhMucPhuKien
SELECT TOP 10 * FROM DanhMucPhuKien;

-- 6. SanPham
SELECT TOP 10 * FROM SanPham;

-- 7. BienTheSanPham
SELECT TOP 20 * FROM BienTheSanPham;

-- 8. ThuocTinh
SELECT TOP 10 * FROM ThuocTinh;

-- 9. PhuKien
SELECT TOP 10 * FROM PhuKien;

-- 10. BienThePhuKien
SELECT TOP 20 * FROM BienThePhuKien;

-- 11. ThuocTinhPhuKien
SELECT TOP 10 * FROM ThuocTinhPhuKien;

-- 12. LoaiThongSo
SELECT TOP 10 * FROM LoaiThongSo;

-- 13. ThongSoKyThuat
SELECT TOP 10 * FROM ThongSoKyThuat;

-- 14. Voucher
SELECT TOP 10 * FROM Voucher;

-- 15. UserVoucher
SELECT TOP 10 * FROM UserVoucher;

-- 16. GioHang
SELECT TOP 10 * FROM GioHang;

-- 17. GioHangChiTiet
SELECT TOP 20 * FROM GioHangChiTiet;

-- 18. DonHang
SELECT TOP 10 * FROM DonHang;

-- 19. ChiTietDonHang
SELECT TOP 20 * FROM ChiTietDonHang;

-- 20. IMEI
SELECT TOP 20 * FROM IMEI;

-- 21. FeedBack
SELECT TOP 10 * FROM FeedBack;

-- 22. Messages
SELECT TOP 10 * FROM Messages;

GO
