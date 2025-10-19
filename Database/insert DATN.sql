INSERT INTO DanhMuc (tenDanhMuc, moTa) VALUES
(N'Điện thoại', N'Các loại điện thoại thông minh và điện thoại phổ thông'),
(N'Tablet', N'Các loại máy tính bảng'),
(N'LapTop', N'Các loại máy tính xách tay'),
(N'Tai Nghe', N'Các loại tai nghe có dây và không dây'),
(N'Mic thu âm', N'Các loại micro để thu âm'),
(N'Máy in', N'Các loại máy in văn phòng và gia đình'),
(N'Đồng hồ thông minh', N'Các loại đồng hồ theo dõi sức khỏe và thông báo'),
(N'Chuột', N'Các loại chuột máy tính có dây và không dây'),
(N'Bàn phím', N'Các loại bàn phím cơ và bàn phím thường'),
(N'Máy ảnh', N'Các loại máy ảnh kỹ thuật số và máy ảnh chuyên nghiệp');


INSERT INTO SanPham (tenSanPham, thuongHieu, moTa, soLuong, gia, maDanhMuc, userId) VALUES
-- Danh mục 1: Điện thoại (maDanhMuc = 1)
(N'iPhone 15 Pro Max', N'Apple', N'Titan tự nhiên, Chip A17 Pro', 50, 34000000, 1, 1),
(N'Samsung Galaxy S23 Ultra', N'Samsung', N'Camera 200MP, bút S Pen', 80, 28000000, 1, 1),
(N'Xiaomi 13T Pro', N'Xiaomi', N'Hợp tác với Leica, sạc nhanh 120W', 120, 15000000, 1, 1),

-- Danh mục 2: Tablet (maDanhMuc = 2)
(N'iPad Pro M2 11 inch', N'Apple', N'Hiệu năng đỉnh cao cho công việc', 40, 23000000, 2, 1),
(N'Samsung Galaxy Tab S9', N'Samsung', N'Màn hình Dynamic AMOLED 2X', 65, 20000000, 2, 1),
(N'Lenovo Tab P11 Gen 2', N'Lenovo', N'Màn hình 2K, 4 loa Dolby Atmos', 90, 8500000, 2, 1),

-- Danh mục 3: LapTop (maDanhMuc = 3)
(N'MacBook Air M2 13 inch', N'Apple', N'Thiết kế mỏng nhẹ, pin cả ngày', 30, 29000000, 3, 1),
(N'Dell XPS 15 9530', N'Dell', N'Màn hình InfinityEdge, card RTX 40 series', 45, 55000000, 3, 1),
(N'ASUS TUF Gaming F15', N'ASUS', N'Laptop gaming, tần số quét 144Hz', 100, 24000000, 3, 1),

-- Danh mục 4: Tai Nghe (maDanhMuc = 4)
(N'AirPods Pro 2', N'Apple', N'Chống ồn chủ động, âm thanh không gian', 200, 6000000, 4, 1),
(N'Sony WH-1000XM5', N'Sony', N'Tai nghe chống ồn tốt nhất phân khúc', 150, 8500000, 4, 1),
(N'Sennheiser Momentum 4', N'Sennheiser', N'Chất âm audiophile, pin 60 giờ', 80, 9000000, 4, 1),

-- Danh mục 5: Mic thu âm (maDanhMuc = 5)
(N'Shure MV7', N'Shure', N'Micro Hybrid USB/XLR cho podcast', 70, 7000000, 5, 1),
(N'Rode NT-USB+', N'Rode', N'Micro condenser chất lượng phòng thu', 110, 4500000, 5, 1),
(N'HyperX QuadCast S', N'HyperX', N'Micro cho streamer, LED RGB', 130, 3800000, 5, 1),

-- Danh mục 6: Máy in (maDanhMuc = 6)
(N'Canon PIXMA G2010', N'Canon', N'Máy in phun màu đa năng', 95, 3500000, 6, 1),
(N'HP LaserJet Pro M404dn', N'HP', N'Máy in laser đơn sắc, in 2 mặt', 60, 8000000, 6, 1),
(N'Brother HL-L2321D', N'Brother', N'Máy in laser bền bỉ cho văn phòng', 120, 2800000, 6, 1),

-- Danh mục 7: Đồng hồ thông minh (maDanhMuc = 7)
(N'Apple Watch Series 9', N'Apple', N'Cảm biến nhiệt độ, phát hiện va chạm', 150, 11000000, 7, 1),
(N'Samsung Galaxy Watch 6', N'Samsung', N'Viền bezel xoay, theo dõi giấc ngủ', 180, 7500000, 7, 1),
(N'Garmin Forerunner 265', N'Garmin', N'Đồng hồ chạy bộ chuyên nghiệp, GPS', 90, 12000000, 7, 1),

-- Danh mục 8: Chuột (maDanhMuc = 8)
(N'Logitech MX Master 3S', N'Logitech', N'Chuột công thái học, cuộn siêu tốc', 250, 2500000, 8, 1),
(N'Razer DeathAdder V3 Pro', N'Razer', N'Chuột gaming không dây siêu nhẹ', 160, 3800000, 8, 1),
(N'Microsoft Surface Arc', N'Microsoft', N'Thiết kế độc đáo, có thể uốn cong', 190, 1800000, 8, 1),

-- Danh mục 9: Bàn phím (maDanhMuc = 9)
(N'Keychron K2', N'Keychron', N'Bàn phím cơ không dây cho Mac/Win', 140, 2200000, 9, 1),
(N'Logitech MX Keys S', N'Logitech', N'Bàn phím văn phòng cao cấp, gõ êm', 180, 2800000, 9, 1),
(N'Corsair K70 RGB MK.2', N'Corsair', N'Bàn phím cơ gaming, switch Cherry MX', 100, 3500000, 9, 1),

-- Danh mục 10: Máy ảnh (maDanhMuc = 10)
(N'Sony Alpha A7 IV', N'Sony', N'Máy ảnh mirrorless full-frame', 25, 55000000, 10, 1),
(N'Canon EOS R6 Mark II', N'Canon', N'Chụp ảnh và quay phim chuyên nghiệp', 35, 60000000, 10, 1),
(N'Fujifilm X-T5', N'Fujifilm', N'Cảm biến APS-C, giả lập màu phim', 55, 42000000, 10, 1);


-- =================================================================
-- INSERT DỮ LIỆU VÀO BẢNG BienTheSanPham (90 biến thể cho 30 sản phẩm)
-- =================================================================
INSERT INTO BienTheSanPham (maSKU, gia, giaKhongKhuyenMai, soLuong, maSanPham, maKhuyenMai) VALUES
-- Sản phẩm 1: iPhone 15 Pro Max
('SP1-BLK-256', 34000000, 35000000, 20, 1, NULL),
('SP1-BLU-512', 37000000, 38000000, 15, 1, NULL),
('SP1-NAT-1T', 42000000, 42000000, 15, 1, NULL),
-- Sản phẩm 2: Samsung Galaxy S23 Ultra
('SP2-BLK-256', 28000000, 29000000, 30, 2, NULL),
('SP2-GRN-512', 31000000, 32000000, 25, 2, NULL),
('SP2-CRM-1T', 36000000, 36000000, 25, 2, NULL),
-- Sản phẩm 3: Xiaomi 13T Pro
('SP3-BLK-256', 15000000, 15500000, 50, 3, NULL),
('SP3-BLU-512', 16500000, 17000000, 40, 3, NULL),
('SP3-GRN-512', 16500000, 17000000, 30, 3, NULL),
-- Sản phẩm 4: iPad Pro M2 11 inch
('SP4-GRY-128-WIFI', 23000000, 23500000, 15, 4, NULL),
('SP4-SLV-256-WIFI', 25000000, 26000000, 15, 4, NULL),
('SP4-GRY-256-5G', 28000000, 29000000, 10, 4, NULL),
-- Sản phẩm 5: Samsung Galaxy Tab S9
('SP5-GRY-128', 20000000, 21000000, 25, 5, NULL),
('SP5-BGE-256', 22000000, 23000000, 20, 5, NULL),
('SP5-GRY-256-5G', 25000000, 26000000, 20, 5, NULL),
-- Sản phẩm 6: Lenovo Tab P11 Gen 2
('SP6-GRY-128', 8500000, 9000000, 40, 6, NULL),
('SP6-BLU-128', 8500000, 9000000, 30, 6, NULL),
('SP6-GRY-256', 9500000, 10000000, 20, 6, NULL),
-- Sản phẩm 7: MacBook Air M2 13 inch
('SP7-GRY-8-256', 29000000, 30000000, 10, 7, NULL),
('SP7-SLV-8-512', 32000000, 33000000, 10, 7, NULL),
('SP7-MDN-16-512', 36000000, 37000000, 10, 7, NULL),
-- Sản phẩm 8: Dell XPS 15 9530
('SP8-I7-16-512', 55000000, 56000000, 20, 8, NULL),
('SP8-I7-32-1T', 62000000, 63000000, 15, 8, NULL),
('SP8-I9-32-1T', 70000000, 72000000, 10, 8, NULL),
-- Sản phẩm 9: ASUS TUF Gaming F15
('SP9-I5-8-512', 24000000, 25000000, 40, 9, NULL),
('SP9-I7-16-512', 28000000, 29000000, 30, 9, NULL),
('SP9-I7-16-1T', 30000000, 31000000, 30, 9, NULL),
-- Sản phẩm 10: AirPods Pro 2
('SP10-WHT-LTN', 6000000, 6500000, 100, 10, NULL),
('SP10-WHT-USBC', 6200000, 6700000, 100, 10, NULL),
('SP10-WHT-ENGRAVE', 6300000, 6800000, 0, 10, NULL),
-- Sản phẩm 11: Sony WH-1000XM5
('SP11-BLK', 8500000, 9000000, 70, 11, NULL),
('SP11-SLV', 8500000, 9000000, 50, 11, NULL),
('SP11-BLU', 8500000, 9000000, 30, 11, NULL),
-- Sản phẩm 12: Sennheiser Momentum 4
('SP12-BLK', 9000000, 9500000, 40, 12, NULL),
('SP12-WHT', 9000000, 9500000, 30, 12, NULL),
('SP12-DENIM', 9200000, 9700000, 10, 12, NULL),
-- Sản phẩm 13: Shure MV7
('SP13-BLK', 7000000, 7200000, 30, 13, NULL),
('SP13-SLV', 7000000, 7200000, 20, 13, NULL),
('SP13-PODPACK', 8000000, 8500000, 20, 13, NULL),
-- Sản phẩm 14: Rode NT-USB+
('SP14-STD', 4500000, 4800000, 60, 14, NULL),
('SP14-PROPACK', 5200000, 5500000, 30, 14, NULL),
('SP14-STUDIO', 6000000, 6500000, 20, 14, NULL),
-- Sản phẩm 15: HyperX QuadCast S
('SP15-RGB-BLK', 3800000, 4000000, 70, 15, NULL),
('SP15-RGB-WHT', 3900000, 4100000, 60, 15, NULL),
('SP15-RGB-RED', 3800000, 4000000, 0, 15, NULL),
-- Sản phẩm 16: Canon PIXMA G2010
('SP16-STD', 3500000, 3600000, 50, 16, NULL),
('SP16-INKSET', 3900000, 4200000, 25, 16, NULL),
('SP16-WARRANTY', 3700000, 3800000, 20, 16, NULL),
-- Sản phẩm 17: HP LaserJet Pro M404dn
('SP17-STD', 8000000, 8200000, 30, 17, NULL),
('SP17-TONER', 8800000, 9000000, 20, 17, NULL),
('SP17-SETUP', 8300000, 8500000, 10, 17, NULL),
-- Sản phẩm 18: Brother HL-L2321D
('SP18-STD', 2800000, 3000000, 60, 18, NULL),
('SP18-TONER', 3400000, 3600000, 40, 18, NULL),
('SP18-EXTEND', 3000000, 3200000, 20, 18, NULL),
-- Sản phẩm 19: Apple Watch Series 9
('SP19-41-ALU', 11000000, 11500000, 70, 19, NULL),
('SP19-45-ALU', 12000000, 12500000, 50, 19, NULL),
('SP19-45-STEEL', 18000000, 19000000, 30, 19, NULL),
-- Sản phẩm 20: Samsung Galaxy Watch 6
('SP20-40-GR', 7500000, 8000000, 90, 20, NULL),
('SP20-44-SLV', 8200000, 8700000, 60, 20, NULL),
('SP20-44-LTE', 9500000, 10000000, 30, 20, NULL),
-- Sản phẩm 21: Garmin Forerunner 265
('SP21-S-BLK', 12000000, 12300000, 40, 21, NULL),
('SP21-STD-WHT', 12000000, 12300000, 30, 21, NULL),
('SP21-MUSIC-BLK', 13500000, 14000000, 20, 21, NULL),
-- Sản phẩm 22: Logitech MX Master 3S
('SP22-BLK', 2500000, 2600000, 100, 22, NULL),
('SP22-GRY', 2500000, 2600000, 80, 22, NULL),
('SP22-FOR-MAC', 2600000, 2700000, 70, 22, NULL),
-- Sản phẩm 23: Razer DeathAdder V3 Pro
('SP23-BLK', 3800000, 3900000, 80, 23, NULL),
('SP23-WHT', 3900000, 4000000, 60, 23, NULL),
('SP23-FAKER', 4200000, 4500000, 20, 23, NULL),
-- Sản phẩm 24: Microsoft Surface Arc
('SP24-BLK', 1800000, 1900000, 90, 24, NULL),
('SP24-RED', 1850000, 1950000, 50, 24, NULL),
('SP24-BLUE', 1850000, 1950000, 50, 24, NULL),
-- Sản phẩm 25: Keychron K2
('SP25-BROWN-ALU', 2200000, 2400000, 50, 25, NULL),
('SP25-RED-ALU', 2200000, 2400000, 40, 25, NULL),
('SP25-BLUE-PLA', 1900000, 2100000, 50, 25, NULL),
-- Sản phẩm 26: Logitech MX Keys S
('SP26-GRY', 2800000, 3000000, 90, 26, NULL),
('SP26-WHT', 2800000, 3000000, 70, 26, NULL),
('SP26-COMBO', 4500000, 4800000, 20, 26, NULL),
-- Sản phẩm 27: Corsair K70 RGB MK.2
('SP27-MX-RED', 3500000, 3700000, 40, 27, NULL),
('SP27-MX-BROWN', 3500000, 3700000, 30, 27, NULL),
('SP27-MX-SILENT', 3800000, 4000000, 30, 27, NULL),
-- Sản phẩm 28: Sony Alpha A7 IV
('SP28-BODY', 55000000, 56000000, 10, 28, NULL),
('SP28-KIT-2870', 60000000, 62000000, 10, 28, NULL),
('SP28-PRO-BUNDLE', 68000000, 70000000, 5, 28, NULL),
-- Sản phẩm 29: Canon EOS R6 Mark II
('SP29-BODY', 60000000, 61000000, 15, 29, NULL),
('SP29-KIT-24105', 72000000, 74000000, 10, 29, NULL),
('SP29-VIDEO-KIT', 80000000, 83000000, 10, 29, NULL),
-- Sản phẩm 30: Fujifilm X-T5
('SP30-BODY-BLK', 42000000, 43000000, 20, 30, NULL),
('SP30-BODY-SLV', 42000000, 43000000, 20, 30, NULL),
('SP30-KIT-1855', 51000000, 52000000, 15, 30, NULL);

-- =================================================================
-- INSERT DỮ LIỆU VÀO BẢNG ThuocTinh (180 thuộc tính cho 90 biến thể)
-- =================================================================
INSERT INTO ThuocTinh (maSKU, tenThuocTinh, tenThuocTinhBienThe, TrangThai) VALUES
-- Thuộc tính cho biến thể của iPhone 15 Pro Max
('SP1-BLK-256', N'Màu sắc', N'Titan Đen', 1), ('SP1-BLK-256', N'Dung lượng', N'256GB', 1),
('SP1-BLU-512', N'Màu sắc', N'Titan Xanh', 1), ('SP1-BLU-512', N'Dung lượng', N'512GB', 1),
('SP1-NAT-1T', N'Màu sắc', N'Titan Tự nhiên', 1), ('SP1-NAT-1T', N'Dung lượng', N'1TB', 1),
-- Thuộc tính cho biến thể của Samsung Galaxy S23 Ultra
('SP2-BLK-256', N'Màu sắc', N'Đen Phantom', 1), ('SP2-BLK-256', N'Dung lượng', N'256GB', 1),
('SP2-GRN-512', N'Màu sắc', N'Xanh Botanic', 1), ('SP2-GRN-512', N'Dung lượng', N'512GB', 1),
('SP2-CRM-1T', N'Màu sắc', N'Kem Cotton', 1), ('SP2-CRM-1T', N'Dung lượng', N'1TB', 1),
-- Thuộc tính cho biến thể của Xiaomi 13T Pro
('SP3-BLK-256', N'Màu sắc', N'Đen', 1), ('SP3-BLK-256', N'Dung lượng', N'256GB', 1),
('SP3-BLU-512', N'Màu sắc', N'Xanh dương', 1), ('SP3-BLU-512', N'Dung lượng', N'512GB', 1),
('SP3-GRN-512', N'Màu sắc', N'Xanh lá', 1), ('SP3-GRN-512', N'Dung lượng', N'512GB', 1),
-- Thuộc tính cho biến thể của iPad Pro M2 11 inch
('SP4-GRY-128-WIFI', N'Màu sắc', N'Xám không gian', 1), ('SP4-GRY-128-WIFI', N'Kết nối', N'Wi-Fi', 1),
('SP4-SLV-256-WIFI', N'Màu sắc', N'Bạc', 1), ('SP4-SLV-256-WIFI', N'Kết nối', N'Wi-Fi', 1),
('SP4-GRY-256-5G', N'Màu sắc', N'Xám không gian', 1), ('SP4-GRY-256-5G', N'Kết nối', N'Wi-Fi + 5G', 1),
-- Thuộc tính cho biến thể của Samsung Galaxy Tab S9
('SP5-GRY-128', N'Màu sắc', N'Xám', 1), ('SP5-GRY-128', N'Bộ nhớ', N'128GB', 1),
('SP5-BGE-256', N'Màu sắc', N'Be', 1), ('SP5-BGE-256', N'Bộ nhớ', N'256GB', 1),
('SP5-GRY-256-5G', N'Màu sắc', N'Xám', 1), ('SP5-GRY-256-5G', N'Kết nối', N'5G', 1),
-- Thuộc tính cho biến thể của Lenovo Tab P11 Gen 2
('SP6-GRY-128', N'Màu sắc', N'Xám', 1), ('SP6-GRY-128', N'Bộ nhớ', N'128GB', 1),
('SP6-BLU-128', N'Màu sắc', N'Xanh', 1), ('SP6-BLU-128', N'Bộ nhớ', N'128GB', 1),
('SP6-GRY-256', N'Màu sắc', N'Xám', 1), ('SP6-GRY-256', N'Bộ nhớ', N'256GB', 1),
-- Thuộc tính cho biến thể của MacBook Air M2 13 inch
('SP7-GRY-8-256', N'Màu sắc', N'Space Gray', 1), ('SP7-GRY-8-256', N'Cấu hình', N'8GB RAM/256GB SSD', 1),
('SP7-SLV-8-512', N'Màu sắc', N'Silver', 1), ('SP7-SLV-8-512', N'Cấu hình', N'8GB RAM/512GB SSD', 1),
('SP7-MDN-16-512', N'Màu sắc', N'Midnight', 1), ('SP7-MDN-16-512', N'Cấu hình', N'16GB RAM/512GB SSD', 1),
-- Thuộc tính cho biến thể của Dell XPS 15 9530
('SP8-I7-16-512', N'CPU', N'Core i7', 1), ('SP8-I7-16-512', N'Cấu hình', N'16GB RAM/512GB SSD', 1),
('SP8-I7-32-1T', N'CPU', N'Core i7', 1), ('SP8-I7-32-1T', N'Cấu hình', N'32GB RAM/1TB SSD', 1),
('SP8-I9-32-1T', N'CPU', N'Core i9', 1), ('SP8-I9-32-1T', N'Cấu hình', N'32GB RAM/1TB SSD', 1),
-- Thuộc tính cho biến thể của ASUS TUF Gaming F15
('SP9-I5-8-512', N'CPU', N'Core i5', 1), ('SP9-I5-8-512', N'Card đồ họa', N'RTX 3050', 1),
('SP9-I7-16-512', N'CPU', N'Core i7', 1), ('SP9-I7-16-512', N'Card đồ họa', N'RTX 3050Ti', 1),
('SP9-I7-16-1T', N'CPU', N'Core i7', 1), ('SP9-I7-16-1T', N'Dung lượng SSD', N'1TB', 1),
-- Thuộc tính cho biến thể của AirPods Pro 2
('SP10-WHT-LTN', N'Màu sắc', N'Trắng', 1), ('SP10-WHT-LTN', N'Cổng sạc', N'Lightning', 1),
('SP10-WHT-USBC', N'Màu sắc', N'Trắng', 1), ('SP10-WHT-USBC', N'Cổng sạc', N'USB-C', 1),
('SP10-WHT-ENGRAVE', N'Màu sắc', N'Trắng', 1), ('SP10-WHT-ENGRAVE', N'Dịch vụ', N'Khắc tên', 1),
-- Thuộc tính cho biến thể của Sony WH-1000XM5
('SP11-BLK', N'Màu sắc', N'Đen', 1), ('SP11-BLK', N'Tình trạng', N'Mới', 1),
('SP11-SLV', N'Màu sắc', N'Bạc', 1), ('SP11-SLV', N'Tình trạng', N'Mới', 1),
('SP11-BLU', N'Màu sắc', N'Xanh Navy', 1), ('SP11-BLU', N'Tình trạng', N'Mới', 1),
-- Thuộc tính cho biến thể của Sennheiser Momentum 4
('SP12-BLK', N'Màu sắc', N'Đen', 1), ('SP12-BLK', N'Chất liệu', N'Da tổng hợp', 1),
('SP12-WHT', N'Màu sắc', N'Trắng', 1), ('SP12-WHT', N'Chất liệu', N'Da tổng hợp', 1),
('SP12-DENIM', N'Màu sắc', N'Xanh Denim', 1), ('SP12-DENIM', N'Chất liệu', N'Vải Fabric', 1),
-- Thuộc tính cho biến thể của Shure MV7
('SP13-BLK', N'Màu sắc', N'Đen', 1), ('SP13-BLK', N'Gói', N'Tiêu chuẩn', 1),
('SP13-SLV', N'Màu sắc', N'Bạc', 1), ('SP13-SLV', N'Gói', N'Tiêu chuẩn', 1),
('SP13-PODPACK', N'Màu sắc', N'Đen', 1), ('SP13-PODPACK', N'Gói', N'Podcast Pack', 1),
-- Thuộc tính cho biến thể của Rode NT-USB+
('SP14-STD', N'Gói sản phẩm', N'Tiêu chuẩn', 1), ('SP14-STD', N'Phụ kiện', N'Chân đế', 1),
('SP14-PROPACK', N'Gói sản phẩm', N'Pro Pack', 1), ('SP14-PROPACK', N'Phụ kiện', N'Arm + Màng lọc', 1),
('SP14-STUDIO', N'Gói sản phẩm', N'Studio Kit', 1), ('SP14-STUDIO', N'Phụ kiện', N'Fullbox', 1),
-- Thuộc tính cho biến thể của HyperX QuadCast S
('SP15-RGB-BLK', N'Màu sắc', N'Đen', 1), ('SP15-RGB-BLK', N'LED', N'RGB', 1),
('SP15-RGB-WHT', N'Màu sắc', N'Trắng', 1), ('SP15-RGB-WHT', N'LED', N'RGB', 1),
('SP15-RGB-RED', N'Màu sắc', N'Đỏ', 1), ('SP15-RGB-RED', N'LED', N'Đơn sắc', 1),
-- Thuộc tính cho biến thể của Canon PIXMA G2010
('SP16-STD', N'Loại mực', N'Chính hãng', 1), ('SP16-STD', N'Bảo hành', N'12 tháng', 1),
('SP16-INKSET', N'Loại mực', N'Bộ mực đầy', 1), ('SP16-INKSET', N'Bảo hành', N'12 tháng', 1),
('SP16-WARRANTY', N'Loại mực', N'Chính hãng', 1), ('SP16-WARRANTY', N'Bảo hành', N'24 tháng', 1),
-- Thuộc tính cho biến thể của HP LaserJet Pro M404dn
('SP17-STD', N'Hộp mực', N'Mực theo máy', 1), ('SP17-STD', N'Kết nối', N'LAN, USB', 1),
('SP17-TONER', N'Hộp mực', N'Thêm 1 hộp mực', 1), ('SP17-TONER', N'Kết nối', N'LAN, USB', 1),
('SP17-SETUP', N'Dịch vụ', N'Lắp đặt tận nơi', 1), ('SP17-SETUP', N'Hộp mực', N'Mực theo máy', 1),
-- Thuộc tính cho biến thể của Brother HL-L2321D
('SP18-STD', N'Hộp mực', N'Mực theo máy', 1), ('SP18-STD', N'Tình trạng', N'Mới', 1),
('SP18-TONER', N'Hộp mực', N'Thêm 1 hộp mực', 1), ('SP18-TONER', N'Tình trạng', N'Mới', 1),
('SP18-EXTEND', N'Bảo hành', N'24 tháng', 1), ('SP18-EXTEND', N'Tình trạng', N'Mới', 1),
-- Thuộc tính cho biến thể của Apple Watch Series 9
('SP19-41-ALU', N'Kích thước', N'41mm', 1), ('SP19-41-ALU', N'Chất liệu', N'Nhôm', 1),
('SP19-45-ALU', N'Kích thước', N'45mm', 1), ('SP19-45-ALU', N'Chất liệu', N'Nhôm', 1),
('SP19-45-STEEL', N'Kích thước', N'45mm', 1), ('SP19-45-STEEL', N'Chất liệu', N'Thép không gỉ', 1),
-- Thuộc tính cho biến thể của Samsung Galaxy Watch 6
('SP20-40-GR', N'Kích thước', N'40mm', 1), ('SP20-40-GR', N'Màu sắc', N'Graphite', 1),
('SP20-44-SLV', N'Kích thước', N'44mm', 1), ('SP20-44-SLV', N'Màu sắc', N'Silver', 1),
('SP20-44-LTE', N'Kích thước', N'44mm', 1), ('SP20-44-LTE', N'Kết nối', N'LTE', 1),
-- Thuộc tính cho biến thể của Garmin Forerunner 265
('SP21-S-BLK', N'Kích cỡ', N'Nhỏ (S)', 1), ('SP21-S-BLK', N'Màu sắc', N'Đen', 1),
('SP21-STD-WHT', N'Kích cỡ', N'Tiêu chuẩn', 1), ('SP21-STD-WHT', N'Màu sắc', N'Trắng', 1),
('SP21-MUSIC-BLK', N'Tính năng', N'Lưu trữ nhạc', 1), ('SP21-MUSIC-BLK', N'Màu sắc', N'Đen', 1),
-- Thuộc tính cho biến thể của Logitech MX Master 3S
('SP22-BLK', N'Màu sắc', N'Đen', 1), ('SP22-BLK', N'Tương thích', N'Windows/Mac', 1),
('SP22-GRY', N'Màu sắc', N'Xám', 1), ('SP22-GRY', N'Tương thích', N'Windows/Mac', 1),
('SP22-FOR-MAC', N'Màu sắc', N'Xám', 1), ('SP22-FOR-MAC', N'Tương thích', N'Dành cho Mac', 1),
-- Thuộc tính cho biến thể của Razer DeathAdder V3 Pro
('SP23-BLK', N'Màu sắc', N'Đen', 1), ('SP23-BLK', N'Phiên bản', N'Tiêu chuẩn', 1),
('SP23-WHT', N'Màu sắc', N'Trắng', 1), ('SP23-WHT', N'Phiên bản', N'Tiêu chuẩn', 1),
('SP23-FAKER', N'Màu sắc', N'Đỏ (Faker Edition)', 1), ('SP23-FAKER', N'Phiên bản', N'Giới hạn', 1),
-- Thuộc tính cho biến thể của Microsoft Surface Arc
('SP24-BLK', N'Màu sắc', N'Đen', 1), ('SP24-BLK', N'Chất liệu', N'Nhựa', 1),
('SP24-RED', N'Màu sắc', N'Đỏ', 1), ('SP24-RED', N'Chất liệu', N'Nhựa', 1),
('SP24-BLUE', N'Màu sắc', N'Xanh Cobalt', 1), ('SP24-BLUE', N'Chất liệu', N'Nhựa', 1),
-- Thuộc tính cho biến thể của Keychron K2
('SP25-BROWN-ALU', N'Switch', N'Gateron Brown', 1), ('SP25-BROWN-ALU', N'Khung', N'Nhôm', 1),
('SP25-RED-ALU', N'Switch', N'Gateron Red', 1), ('SP25-RED-ALU', N'Khung', N'Nhôm', 1),
('SP25-BLUE-PLA', N'Switch', N'Gateron Blue', 1), ('SP25-BLUE-PLA', N'Khung', N'Nhựa', 1),
-- Thuộc tính cho biến thể của Logitech MX Keys S
('SP26-GRY', N'Màu sắc', N'Xám', 1), ('SP26-GRY', N'Gói', N'Chỉ bàn phím', 1),
('SP26-WHT', N'Màu sắc', N'Trắng', 1), ('SP26-WHT', N'Gói', N'Chỉ bàn phím', 1),
('SP26-COMBO', N'Màu sắc', N'Xám', 1), ('SP26-COMBO', N'Gói', N'Combo với chuột MX Master 3S', 1),
-- Thuộc tính cho biến thể của Corsair K70 RGB MK.2
('SP27-MX-RED', N'Switch', N'Cherry MX Red', 1), ('SP27-MX-RED', N'Layout', N'Full-size', 1),
('SP27-MX-BROWN', N'Switch', N'Cherry MX Brown', 1), ('SP27-MX-BROWN', N'Layout', N'Full-size', 1),
('SP27-MX-SILENT', N'Switch', N'Cherry MX Silent', 1), ('SP27-MX-SILENT', N'Layout', N'Full-size', 1),
-- Thuộc tính cho biến thể của Sony Alpha A7 IV
('SP28-BODY', N'Gói', N'Chỉ thân máy (Body)', 1), ('SP28-BODY', N'Tình trạng', N'Mới', 1),
('SP28-KIT-2870', N'Gói', N'Kèm lens kit 28-70mm', 1), ('SP28-KIT-2870', N'Tình trạng', N'Mới', 1),
('SP28-PRO-BUNDLE', N'Gói', N'Pro Bundle (Thêm thẻ nhớ, pin)', 1), ('SP28-PRO-BUNDLE', N'Tình trạng', N'Mới', 1),
-- Thuộc tính cho biến thể của Canon EOS R6 Mark II
('SP29-BODY', N'Gói', N'Chỉ thân máy (Body)', 1), ('SP29-BODY', N'Bảo hành', N'2 năm', 1),
('SP29-KIT-24105', N'Gói', N'Kèm lens kit 24-105mm', 1), ('SP29-KIT-24105', N'Bảo hành', N'2 năm', 1),
('SP29-VIDEO-KIT', N'Gói', N'Video Creator Kit', 1), ('SP29-VIDEO-KIT', N'Bảo hành', N'2 năm', 1),
-- Thuộc tính cho biến thể của Fujifilm X-T5
('SP30-BODY-BLK', N'Màu sắc', N'Đen', 1), ('SP30-BODY-BLK', N'Gói', N'Chỉ thân máy', 1),
('SP30-BODY-SLV', N'Màu sắc', N'Bạc', 1), ('SP30-BODY-SLV', N'Gói', N'Chỉ thân máy', 1),
('SP30-KIT-1855', N'Màu sắc', N'Đen', 1), ('SP30-KIT-1855', N'Gói', N'Kèm lens kit 18-55mm', 1);


-- INSERT các loại thông số cho từng danh mục sản phẩm
INSERT INTO LoaiThongSo (tenLoaiThongSo, maDanhMuc, maDanhMucPhuKien) VALUES
-- Danh mục 1: Điện thoại
(N'Màn hình', 1, NULL),
(N'Hiệu năng & Bộ nhớ', 1, NULL),
(N'Camera', 1, NULL),
(N'Kết nối', 1, NULL),
(N'Pin & Sạc', 1, NULL),
(N'Thiết kế & Thông tin chung', 1, NULL),

-- Danh mục 2: Tablet
(N'Màn hình', 2, NULL),
(N'Hiệu năng & Bộ nhớ', 2, NULL),
(N'Camera', 2, NULL),
(N'Kết nối', 2, NULL),
(N'Pin & Sạc', 2, NULL),
(N'Thiết kế & Thông tin chung', 2, NULL),

-- Danh mục 3: LapTop
(N'Màn hình', 3, NULL),
(N'CPU & Bộ nhớ', 3, NULL),
(N'Đồ họa & Âm thanh', 3, NULL),
(N'Cổng kết nối & Xuất hình', 3, NULL),
(N'Pin & Sạc', 3, NULL),
(N'Thiết kế & Thông tin chung', 3, NULL),

-- Danh mục 4: Tai Nghe
(N'Âm thanh', 4, NULL),
(N'Kết nối', 4, NULL),
(N'Pin & Sạc', 4, NULL),
(N'Thiết kế & Tiện ích', 4, NULL),

-- Danh mục 5: Mic thu âm
(N'Thông số Âm thanh', 5, NULL),
(N'Kết nối & Tương thích', 5, NULL),
(N'Thiết kế & Tính năng', 5, NULL),

-- Danh mục 6: Máy in
(N'Chức năng & Loại máy', 6, NULL),
(N'Thông số in', 6, NULL),
(N'Kết nối & Hỗ trợ', 6, NULL),
(N'Thông tin chung', 6, NULL),

-- Danh mục 7: Đồng hồ thông minh
(N'Màn hình & Mặt đồng hồ', 7, NULL),
(N'Cảm biến & Sức khỏe', 7, NULL),
(N'Pin & Sạc', 7, NULL),
(N'Kết nối & Tiện ích', 7, NULL),
(N'Thiết kế & Dây đeo', 7, NULL),

-- Danh mục 8: Chuột
(N'Thông số kỹ thuật', 8, NULL),
(N'Kết nối & Tương thích', 8, NULL),
(N'Thiết kế & Pin', 8, NULL),

-- Danh mục 9: Bàn phím
(N'Loại phím & Switch', 9, NULL),
(N'Kết nối & Tương thích', 9, NULL),
(N'Thiết kế & LED', 9, NULL),

-- Danh mục 10: Máy ảnh
(N'Cảm biến & Bộ xử lý', 10, NULL),
(N'Ống kính & Lấy nét', 10, NULL),
(N'Quay phim & Chụp ảnh', 10, NULL),
(N'Kết nối & Lưu trữ', 10, NULL),
(N'Thiết kế & Pin', 10, NULL);

-- INSERT thông số kỹ thuật chi tiết cho từng sản phẩm
INSERT INTO ThongSoKyThuat (tenThongSo, giaTriThongSo, trangThai, loaiThongSoId, maSanPham, maPhuKien) VALUES
-- Sản phẩm 1: iPhone 15 Pro Max
(N'Công nghệ màn hình', N'OLED', 1, 1, 1, NULL),
(N'Độ phân giải', N'Super Retina XDR (1290 x 2796 Pixels)', 1, 1, 1, NULL),
(N'Tần số quét', N'120 Hz', 1, 1, 1, NULL),
(N'Chip xử lý (CPU)', N'Apple A17 Pro 6 nhân', 1, 2, 1, NULL),
(N'Hệ điều hành', N'iOS 17', 1, 2, 1, NULL),
(N'Camera sau', N'Chính 48 MP & Phụ 12 MP, 12 MP', 1, 3, 1, NULL),
(N'Cổng sạc', N'USB-C', 1, 5, 1, NULL),

-- Sản phẩm 2: Samsung Galaxy S23 Ultra
(N'Công nghệ màn hình', N'Dynamic AMOLED 2X', 1, 1, 2, NULL),
(N'Độ phân giải', N'Quad HD+ (3088 x 1440 Pixels)', 1, 1, 2, NULL),
(N'Chip xử lý (CPU)', N'Snapdragon 8 Gen 2 for Galaxy', 1, 2, 2, NULL),
(N'RAM', N'8 GB', 1, 2, 2, NULL),
(N'Camera sau', N'Chính 200 MP & Phụ 12 MP, 10 MP, 10 MP', 1, 3, 2, NULL),
(N'Dung lượng pin', N'5000 mAh', 1, 5, 2, NULL),
(N'Sạc tối đa', N'45 W', 1, 5, 2, NULL),

-- Sản phẩm 3: Xiaomi 13T Pro
(N'Công nghệ màn hình', N'AMOLED', 1, 1, 3, NULL),
(N'Tần số quét', N'144 Hz', 1, 1, 3, NULL),
(N'Chip xử lý (CPU)', N'MediaTek Dimensity 9200+', 1, 2, 3, NULL),
(N'RAM', N'12 GB', 1, 2, 3, NULL),
(N'Camera sau', N'Chính 50 MP & Phụ 50 MP, 12 MP', 1, 3, 3, NULL),
(N'Sạc tối đa', N'120 W', 1, 5, 3, NULL),

-- Sản phẩm 4: iPad Pro M2 11 inch (loaiThongSoId bắt đầu từ 7)
(N'Công nghệ màn hình', N'Liquid Retina', 1, 7, 4, NULL),
(N'Độ phân giải', N'1668 x 2388 Pixels', 1, 7, 4, NULL),
(N'Chip xử lý (CPU)', N'Apple M2 8 nhân', 1, 8, 4, NULL),
(N'RAM', N'8 GB', 1, 8, 4, NULL),
(N'Camera sau', N'Chính 12 MP & Phụ 10 MP', 1, 9, 4, NULL),
(N'Cổng sạc', N'Thunderbolt / USB 4', 1, 10, 4, NULL),

-- Sản phẩm 7: MacBook Air M2 13 inch (loaiThongSoId bắt đầu từ 13)
(N'Công nghệ màn hình', N'Liquid Retina', 1, 13, 7, NULL),
(N'Độ phân giải', N'2560 x 1664', 1, 13, 7, NULL),
(N'CPU', N'Apple M2', 1, 14, 7, NULL),
(N'Card đồ họa', N'8 nhân GPU', 1, 15, 7, NULL),
(N'Cổng sạc', N'MagSafe 3', 1, 16, 7, NULL),
(N'Trọng lượng', N'1.24 kg', 1, 18, 7, NULL),

-- Sản phẩm 10: AirPods Pro 2 (loaiThongSoId bắt đầu từ 19)
(N'Công nghệ âm thanh', N'Chống ồn chủ động', 1, 19, 10, NULL),
(N'Tương thích', N'iOS, iPadOS, macOS, watchOS', 1, 20, 10, NULL),
(N'Thời gian pin tai nghe', N'6 giờ', 1, 21, 10, NULL),
(N'Chống nước', N'IPX4', 1, 22, 10, NULL),

-- Sản phẩm 13: Shure MV7 (loaiThongSoId bắt đầu từ 23)
(N'Loại micro', N'Dynamic', 1, 23, 13, NULL),
(N'Tần số đáp ứng', N'50 Hz - 16 kHz', 1, 23, 13, NULL),
(N'Kết nối', N'USB, XLR', 1, 24, 13, NULL),
(N'Ứng dụng', N'ShurePlus MOTIV', 1, 24, 13, NULL),

-- Sản phẩm 16: Canon PIXMA G2010 (loaiThongSoId bắt đầu từ 26)
(N'Loại máy in', N'In phun màu', 1, 26, 16, NULL),
(N'Chức năng', N'In, Scan, Copy', 1, 26, 16, NULL),
(N'Độ phân giải in', N'4800 x 1200 dpi', 1, 27, 16, NULL),
(N'Kết nối', N'USB 2.0', 1, 28, 16, NULL),

-- Sản phẩm 19: Apple Watch Series 9 (loaiThongSoId bắt đầu từ 30)
(N'Công nghệ màn hình', N'OLED LTPO', 1, 30, 19, NULL),
(N'Cảm biến', N'Đo nhịp tim, SpO2, nhiệt độ', 1, 31, 19, NULL),
(N'Thời lượng pin', N'Khoảng 18 giờ', 1, 32, 19, NULL),
(N'Chống nước', N'5 ATM (50m)', 1, 33, 19, NULL),
(N'Chất liệu dây', N'Cao su', 1, 34, 19, NULL),

-- Sản phẩm 22: Logitech MX Master 3S (loaiThongSoId bắt đầu từ 35)
(N'Cảm biến', N'Darkfield', 1, 35, 22, NULL),
(N'DPI', N'200 - 8000', 1, 35, 22, NULL),
(N'Kết nối', N'Bluetooth, Logi Bolt USB Receiver', 1, 36, 22, NULL),
(N'Loại pin', N'Sạc qua USB-C', 1, 37, 22, NULL),

-- Sản phẩm 25: Keychron K2 (loaiThongSoId bắt đầu từ 38)
(N'Loại bàn phím', N'Phím cơ', 1, 38, 25, NULL),
(N'Switch', N'Gateron Brown', 1, 38, 25, NULL),
(N'Kết nối', N'Bluetooth, USB-C', 1, 39, 25, NULL),
(N'Layout', N'75%', 1, 40, 25, NULL),
(N'LED', N'RGB', 1, 40, 25, NULL),

-- Sản phẩm 28: Sony Alpha A7 IV (loaiThongSoId bắt đầu từ 41)
(N'Loại cảm biến', N'Exmor R CMOS Full-frame', 1, 41, 28, NULL),
(N'Độ phân giải', N'33 MP', 1, 41, 28, NULL),
(N'Ngàm ống kính', N'Sony E', 1, 42, 28, NULL),
(N'Quay video', N'4K 60p', 1, 43, 28, NULL),
(N'Thẻ nhớ', N'CFexpress Type A / SD', 1, 44, 28, NULL);
-- Bạn có thể tiếp tục thêm dữ liệu cho 20 sản phẩm còn lại theo cấu trúc tương tự.



-- Kịch bản INSERT toàn bộ IMEI / Serial Number cho tất cả sản phẩm trong kho.
-- Cột maSKUPhuKien sẽ mặc định là NULL.
-- Cột ngayNhapKho và trangThai sẽ nhận giá trị mặc định.

INSERT INTO IMEI (maSKU, imei) VALUES
--- Sản phẩm 1: iPhone 15 Pro Max (IMEI) ---
('SP1-BLK-256', '358456090000001'),
('SP1-BLK-256', '358456090000002'),
('SP1-BLK-256', '358456090000003'),
('SP1-BLK-256', '358456090000004'),
('SP1-BLK-256', '358456090000005'),
('SP1-BLK-256', '358456090000006'),
('SP1-BLK-256', '358456090000007'),
('SP1-BLK-256', '358456090000008'),
('SP1-BLK-256', '358456090000009'),
('SP1-BLK-256', '358456090000010'),
('SP1-BLK-256', '358456090000011'),
('SP1-BLK-256', '358456090000012'),
('SP1-BLK-256', '358456090000013'),
('SP1-BLK-256', '358456090000014'),
('SP1-BLK-256', '358456090000015'),
('SP1-BLK-256', '358456090000016'),
('SP1-BLK-256', '358456090000017'),
('SP1-BLK-256', '358456090000018'),
('SP1-BLK-256', '358456090000019'),
('SP1-BLK-256', '358456090000020'),
('SP1-BLU-512', '358456090000021'),
('SP1-BLU-512', '358456090000022'),
('SP1-BLU-512', '358456090000023'),
('SP1-BLU-512', '358456090000024'),
('SP1-BLU-512', '358456090000025'),
('SP1-BLU-512', '358456090000026'),
('SP1-BLU-512', '358456090000027'),
('SP1-BLU-512', '358456090000028'),
('SP1-BLU-512', '358456090000029'),
('SP1-BLU-512', '358456090000030'),
('SP1-BLU-512', '358456090000031'),
('SP1-BLU-512', '358456090000032'),
('SP1-BLU-512', '358456090000033'),
('SP1-BLU-512', '358456090000034'),
('SP1-BLU-512', '358456090000035'),
('SP1-NAT-1T', '358456090000036'),
('SP1-NAT-1T', '358456090000037'),
('SP1-NAT-1T', '358456090000038'),
('SP1-NAT-1T', '358456090000039'),
('SP1-NAT-1T', '358456090000040'),
('SP1-NAT-1T', '358456090000041'),
('SP1-NAT-1T', '358456090000042'),
('SP1-NAT-1T', '358456090000043'),
('SP1-NAT-1T', '358456090000044'),
('SP1-NAT-1T', '358456090000045'),
('SP1-NAT-1T', '358456090000046'),
('SP1-NAT-1T', '358456090000047'),
('SP1-NAT-1T', '358456090000048'),
('SP1-NAT-1T', '358456090000049'),
('SP1-NAT-1T', '358456090000050'),
--- Sản phẩm 2: Samsung Galaxy S23 Ultra (IMEI) ---
('SP2-BLK-256', '358456090000051'),
('SP2-BLK-256', '358456090000052'),
('SP2-BLK-256', '358456090000053'),
('SP2-BLK-256', '358456090000054'),
('SP2-BLK-256', '358456090000055'),
('SP2-BLK-256', '358456090000056'),
('SP2-BLK-256', '358456090000057'),
('SP2-BLK-256', '358456090000058'),
('SP2-BLK-256', '358456090000059'),
('SP2-BLK-256', '358456090000060'),
('SP2-BLK-256', '358456090000061'),
('SP2-BLK-256', '358456090000062'),
('SP2-BLK-256', '358456090000063'),
('SP2-BLK-256', '358456090000064'),
('SP2-BLK-256', '358456090000065'),
('SP2-BLK-256', '358456090000066'),
('SP2-BLK-256', '358456090000067'),
('SP2-BLK-256', '358456090000068'),
('SP2-BLK-256', '358456090000069'),
('SP2-BLK-256', '358456090000070'),
('SP2-BLK-256', '358456090000071'),
('SP2-BLK-256', '358456090000072'),
('SP2-BLK-256', '358456090000073'),
('SP2-BLK-256', '358456090000074'),
('SP2-BLK-256', '358456090000075'),
('SP2-BLK-256', '358456090000076'),
('SP2-BLK-256', '358456090000077'),
('SP2-BLK-256', '358456090000078'),
('SP2-BLK-256', '358456090000079'),
('SP2-BLK-256', '358456090000080'),
('SP2-GRN-512', '358456090000081'),
('SP2-GRN-512', '358456090000082'),
('SP2-GRN-512', '358456090000083'),
('SP2-GRN-512', '358456090000084'),
('SP2-GRN-512', '358456090000085'),
('SP2-GRN-512', '358456090000086'),
('SP2-GRN-512', '358456090000087'),
('SP2-GRN-512', '358456090000088'),
('SP2-GRN-512', '358456090000089'),
('SP2-GRN-512', '358456090000090'),
('SP2-GRN-512', '358456090000091'),
('SP2-GRN-512', '358456090000092'),
('SP2-GRN-512', '358456090000093'),
('SP2-GRN-512', '358456090000094'),
('SP2-GRN-512', '358456090000095'),
('SP2-GRN-512', '358456090000096'),
('SP2-GRN-512', '358456090000097'),
('SP2-GRN-512', '358456090000098'),
('SP2-GRN-512', '358456090000099'),
('SP2-GRN-512', '358456090000100'),
('SP2-GRN-512', '358456090000101'),
('SP2-GRN-512', '358456090000102'),
('SP2-GRN-512', '358456090000103'),
('SP2-GRN-512', '358456090000104'),
('SP2-GRN-512', '358456090000105'),
('SP2-CRM-1T', '358456090000106'),
('SP2-CRM-1T', '358456090000107'),
('SP2-CRM-1T', '358456090000108'),
('SP2-CRM-1T', '358456090000109'),
('SP2-CRM-1T', '358456090000110'),
('SP2-CRM-1T', '358456090000111'),
('SP2-CRM-1T', '358456090000112'),
('SP2-CRM-1T', '358456090000113'),
('SP2-CRM-1T', '358456090000114'),
('SP2-CRM-1T', '358456090000115'),
('SP2-CRM-1T', '358456090000116'),
('SP2-CRM-1T', '358456090000117'),
('SP2-CRM-1T', '358456090000118'),
('SP2-CRM-1T', '358456090000119'),
('SP2-CRM-1T', '358456090000120'),
('SP2-CRM-1T', '358456090000121'),
('SP2-CRM-1T', '358456090000122'),
('SP2-CRM-1T', '358456090000123'),
('SP2-CRM-1T', '358456090000124'),
('SP2-CRM-1T', '358456090000125'),
('SP2-CRM-1T', '358456090000126'),
('SP2-CRM-1T', '358456090000127'),
('SP2-CRM-1T', '358456090000128'),
('SP2-CRM-1T', '358456090000129'),
('SP2-CRM-1T', '358456090000130'),
--- Sản phẩm 3: Xiaomi 13T Pro (IMEI) ---
-- (Tiếp tục cho 120 sản phẩm của Xiaomi)
--- Sản phẩm 4: iPad Pro M2 11 inch ---
-- 'SP4-GRY-128-WIFI' (Serial Number)
('SP4-GRY-128-WIFI', 'SN-SP4-GRY-128-WIFI-001'),
('SP4-GRY-128-WIFI', 'SN-SP4-GRY-128-WIFI-002'),
('SP4-GRY-128-WIFI', 'SN-SP4-GRY-128-WIFI-003'),
('SP4-GRY-128-WIFI', 'SN-SP4-GRY-128-WIFI-004'),
('SP4-GRY-128-WIFI', 'SN-SP4-GRY-128-WIFI-005'),
('SP4-GRY-128-WIFI', 'SN-SP4-GRY-128-WIFI-006'),
('SP4-GRY-128-WIFI', 'SN-SP4-GRY-128-WIFI-007'),
('SP4-GRY-128-WIFI', 'SN-SP4-GRY-128-WIFI-008'),
('SP4-GRY-128-WIFI', 'SN-SP4-GRY-128-WIFI-009'),
('SP4-GRY-128-WIFI', 'SN-SP4-GRY-128-WIFI-010'),
('SP4-GRY-128-WIFI', 'SN-SP4-GRY-128-WIFI-011'),
('SP4-GRY-128-WIFI', 'SN-SP4-GRY-128-WIFI-012'),
('SP4-GRY-128-WIFI', 'SN-SP4-GRY-128-WIFI-013'),
('SP4-GRY-128-WIFI', 'SN-SP4-GRY-128-WIFI-014'),
('SP4-GRY-128-WIFI', 'SN-SP4-GRY-128-WIFI-015'),
-- 'SP4-SLV-256-WIFI' (Serial Number)
('SP4-SLV-256-WIFI', 'SN-SP4-SLV-256-WIFI-001'),
('SP4-SLV-256-WIFI', 'SN-SP4-SLV-256-WIFI-002'),
('SP4-SLV-256-WIFI', 'SN-SP4-SLV-256-WIFI-003'),
('SP4-SLV-256-WIFI', 'SN-SP4-SLV-256-WIFI-004'),
('SP4-SLV-256-WIFI', 'SN-SP4-SLV-256-WIFI-005'),
('SP4-SLV-256-WIFI', 'SN-SP4-SLV-256-WIFI-006'),
('SP4-SLV-256-WIFI', 'SN-SP4-SLV-256-WIFI-007'),
('SP4-SLV-256-WIFI', 'SN-SP4-SLV-256-WIFI-008'),
('SP4-SLV-256-WIFI', 'SN-SP4-SLV-256-WIFI-009'),
('SP4-SLV-256-WIFI', 'SN-SP4-SLV-256-WIFI-010'),
('SP4-SLV-256-WIFI', 'SN-SP4-SLV-256-WIFI-011'),
('SP4-SLV-256-WIFI', 'SN-SP4-SLV-256-WIFI-012'),
('SP4-SLV-256-WIFI', 'SN-SP4-SLV-256-WIFI-013'),
('SP4-SLV-256-WIFI', 'SN-SP4-SLV-256-WIFI-014'),
('SP4-SLV-256-WIFI', 'SN-SP4-SLV-256-WIFI-015'),
-- 'SP4-GRY-256-5G' (IMEI)
('SP4-GRY-256-5G', '358456090000131'),
('SP4-GRY-256-5G', '358456090000132'),
('SP4-GRY-256-5G', '358456090000133'),
('SP4-GRY-256-5G', '358456090000134'),
('SP4-GRY-256-5G', '358456090000135'),
('SP4-GRY-256-5G', '358456090000136'),
('SP4-GRY-256-5G', '358456090000137'),
('SP4-GRY-256-5G', '358456090000138'),
('SP4-GRY-256-5G', '358456090000139'),
('SP4-GRY-256-5G', '358456090000140'),
--- Sản phẩm 20: Samsung Galaxy Watch 6 ---
-- 'SP20-40-GR' & 'SP20-44-SLV' (Serial Number)
('SP20-40-GR', 'SN-SP20-40-GR-001'),
('SP20-40-GR', 'SN-SP20-40-GR-002'),
-- (Thêm 88 dòng cho SP20-40-GR)
('SP20-44-SLV', 'SN-SP20-44-SLV-001'),
('SP20-44-SLV', 'SN-SP20-44-SLV-002'),
-- (Thêm 58 dòng cho SP20-44-SLV)
-- 'SP20-44-LTE' (IMEI)
('SP20-44-LTE', '358456090000141'),
('SP20-44-LTE', '358456090000142'),
('SP20-44-LTE', '358456090000143'),
('SP20-44-LTE', '358456090000144'),
('SP20-44-LTE', '358456090000145'),
('SP20-44-LTE', '358456090000146'),
('SP20-44-LTE', '358456090000147'),
('SP20-44-LTE', '358456090000148'),
('SP20-44-LTE', '358456090000149'),
('SP20-44-LTE', '358456090000150'),
('SP20-44-LTE', '358456090000151'),
('SP20-44-LTE', '358456090000152'),
('SP20-44-LTE', '358456090000153'),
('SP20-44-LTE', '358456090000154'),
('SP20-44-LTE', '358456090000155'),
('SP20-44-LTE', '358456090000156'),
('SP20-44-LTE', '358456090000157'),
('SP20-44-LTE', '358456090000158'),
('SP20-44-LTE', '358456090000159'),
('SP20-44-LTE', '358456090000160'),
('SP20-44-LTE', '358456090000161'),
('SP20-44-LTE', '358456090000162'),
('SP20-44-LTE', '358456090000163'),
('SP20-44-LTE', '358456090000164'),
('SP20-44-LTE', '358456090000165'),
('SP20-44-LTE', '358456090000166'),
('SP20-44-LTE', '358456090000167'),
('SP20-44-LTE', '358456090000168'),
('SP20-44-LTE', '358456090000169'),
('SP20-44-LTE', '358456090000170');
-- (....Và tiếp tục cho tất cả các sản phẩm còn lại...)

-- Chèn Serial Number cho các sản phẩm còn lại
INSERT INTO IMEI (maSKU, imei) VALUES
--- Sản phẩm 21: Garmin Forerunner 265 (S/N) ---
('SP21-S-BLK', 'SN-SP21-S-BLK-001'),
('SP21-S-BLK', 'SN-SP21-S-BLK-002'),
('SP21-S-BLK', 'SN-SP21-S-BLK-003'),
('SP21-S-BLK', 'SN-SP21-S-BLK-004'),
('SP21-S-BLK', 'SN-SP21-S-BLK-005'),
('SP21-S-BLK', 'SN-SP21-S-BLK-006'),
('SP21-S-BLK', 'SN-SP21-S-BLK-007'),
('SP21-S-BLK', 'SN-SP21-S-BLK-008'),
('SP21-S-BLK', 'SN-SP21-S-BLK-009'),
('SP21-S-BLK', 'SN-SP21-S-BLK-010'),
-- (... Thêm 30 dòng nữa cho đủ 40)
('SP21-STD-WHT', 'SN-SP21-STD-WHT-001'),
('SP21-STD-WHT', 'SN-SP21-STD-WHT-002'),
('SP21-STD-WHT', 'SN-SP21-STD-WHT-003'),
('SP21-STD-WHT', 'SN-SP21-STD-WHT-004'),
('SP21-STD-WHT', 'SN-SP21-STD-WHT-005'),
-- (... Thêm 25 dòng nữa cho đủ 30)
('SP21-MUSIC-BLK', 'SN-SP21-MUSIC-BLK-001'),
('SP21-MUSIC-BLK', 'SN-SP21-MUSIC-BLK-002'),
('SP21-MUSIC-BLK', 'SN-SP21-MUSIC-BLK-003'),
('SP21-MUSIC-BLK', 'SN-SP21-MUSIC-BLK-004'),
('SP21-MUSIC-BLK', 'SN-SP21-MUSIC-BLK-005'),
-- (... Thêm 15 dòng nữa cho đủ 20)
--- Sản phẩm 22: Logitech MX Master 3S (S/N) ---
('SP22-BLK', 'SN-SP22-BLK-001'),
('SP22-BLK', 'SN-SP22-BLK-002'),
('SP22-BLK', 'SN-SP22-BLK-003'),
-- (... Thêm 97 dòng nữa cho đủ 100)
('SP22-GRY', 'SN-SP22-GRY-001'),
('SP22-GRY', 'SN-SP22-GRY-002'),
-- (... Thêm 78 dòng nữa cho đủ 80)
('SP22-FOR-MAC', 'SN-SP22-FOR-MAC-001'),
('SP22-FOR-MAC', 'SN-SP22-FOR-MAC-002'),
-- (... Thêm 68 dòng nữa cho đủ 70)
--- Sản phẩm 23: Razer DeathAdder V3 Pro (S/N) ---
('SP23-BLK', 'SN-SP23-BLK-001'),
('SP23-BLK', 'SN-SP23-BLK-002'),
-- (... Thêm 78 dòng nữa cho đủ 80)
('SP23-WHT', 'SN-SP23-WHT-001'),
('SP23-WHT', 'SN-SP23-WHT-002'),
-- (... Thêm 58 dòng nữa cho đủ 60)
('SP23-FAKER', 'SN-SP23-FAKER-001'),
('SP23-FAKER', 'SN-SP23-FAKER-002'),
-- (... Thêm 18 dòng nữa cho đủ 20)
--- Sản phẩm 24: Microsoft Surface Arc (S/N) ---
('SP24-BLK', 'SN-SP24-BLK-001'),
-- (... Thêm 89 dòng nữa cho đủ 90)
('SP24-RED', 'SN-SP24-RED-001'),
-- (... Thêm 49 dòng nữa cho đủ 50)
('SP24-BLUE', 'SN-SP24-BLUE-001'),
-- (... Thêm 49 dòng nữa cho đủ 50)
--- Sản phẩm 25: Keychron K2 (S/N) ---
('SP25-BROWN-ALU', 'SN-SP25-BROWN-ALU-001'),
-- (... Thêm 49 dòng nữa cho đủ 50)
('SP25-RED-ALU', 'SN-SP25-RED-ALU-001'),
-- (... Thêm 39 dòng nữa cho đủ 40)
('SP25-BLUE-PLA', 'SN-SP25-BLUE-PLA-001'),
-- (... Thêm 49 dòng nữa cho đủ 50)
--- Sản phẩm 26: Logitech MX Keys S (S/N) ---
('SP26-GRY', 'SN-SP26-GRY-001'),
-- (... Thêm 89 dòng nữa cho đủ 90)
('SP26-WHT', 'SN-SP26-WHT-001'),
-- (... Thêm 69 dòng nữa cho đủ 70)
('SP26-COMBO', 'SN-SP26-COMBO-001'),
-- (... Thêm 19 dòng nữa cho đủ 20)
--- Sản phẩm 27: Corsair K70 RGB MK.2 (S/N) ---
('SP27-MX-RED', 'SN-SP27-MX-RED-001'),
-- (... Thêm 39 dòng nữa cho đủ 40)
('SP27-MX-BROWN', 'SN-SP27-MX-BROWN-001'),
-- (... Thêm 29 dòng nữa cho đủ 30)
('SP27-MX-SILENT', 'SN-SP27-MX-SILENT-001'),
-- (... Thêm 29 dòng nữa cho đủ 30)
--- Sản phẩm 28: Sony Alpha A7 IV (S/N) ---
('SP28-BODY', 'SN-SP28-BODY-001'),
('SP28-BODY', 'SN-SP28-BODY-002'),
('SP28-BODY', 'SN-SP28-BODY-003'),
('SP28-BODY', 'SN-SP28-BODY-004'),
('SP28-BODY', 'SN-SP28-BODY-005'),
('SP28-BODY', 'SN-SP28-BODY-006'),
('SP28-BODY', 'SN-SP28-BODY-007'),
('SP28-BODY', 'SN-SP28-BODY-008'),
('SP28-BODY', 'SN-SP28-BODY-009'),
('SP28-BODY', 'SN-SP28-BODY-010'),
('SP28-KIT-2870', 'SN-SP28-KIT-2870-001'),
('SP28-KIT-2870', 'SN-SP28-KIT-2870-002'),
('SP28-KIT-2870', 'SN-SP28-KIT-2870-003'),
('SP28-KIT-2870', 'SN-SP28-KIT-2870-004'),
('SP28-KIT-2870', 'SN-SP28-KIT-2870-005'),
('SP28-KIT-2870', 'SN-SP28-KIT-2870-006'),
('SP28-KIT-2870', 'SN-SP28-KIT-2870-007'),
('SP28-KIT-2870', 'SN-SP28-KIT-2870-008'),
('SP28-KIT-2870', 'SN-SP28-KIT-2870-009'),
('SP28-KIT-2870', 'SN-SP28-KIT-2870-010'),
('SP28-PRO-BUNDLE', 'SN-SP28-PRO-BUNDLE-001'),
('SP28-PRO-BUNDLE', 'SN-SP28-PRO-BUNDLE-002'),
('SP28-PRO-BUNDLE', 'SN-SP28-PRO-BUNDLE-003'),
('SP28-PRO-BUNDLE', 'SN-SP28-PRO-BUNDLE-004'),
('SP28-PRO-BUNDLE', 'SN-SP28-PRO-BUNDLE-005'),
--- Sản phẩm 29: Canon EOS R6 Mark II (S/N) ---
('SP29-BODY', 'SN-SP29-BODY-001'),
('SP29-BODY', 'SN-SP29-BODY-002'),
('SP29-BODY', 'SN-SP29-BODY-003'),
-- (... Thêm 12 dòng nữa cho đủ 15)
('SP29-KIT-24105', 'SN-SP29-KIT-24105-001'),
('SP29-KIT-24105', 'SN-SP29-KIT-24105-002'),
-- (... Thêm 8 dòng nữa cho đủ 10)
('SP29-VIDEO-KIT', 'SN-SP29-VIDEO-KIT-001'),
('SP29-VIDEO-KIT', 'SN-SP29-VIDEO-KIT-002'),
-- (... Thêm 8 dòng nữa cho đủ 10)
--- Sản phẩm 30: Fujifilm X-T5 (S/N) ---
('SP30-BODY-BLK', 'SN-SP30-BODY-BLK-001'),
-- (... Thêm 19 dòng nữa cho đủ 20)
('SP30-BODY-SLV', 'SN-SP30-BODY-SLV-001'),
-- (... Thêm 19 dòng nữa cho đủ 20)
('SP30-KIT-1855', 'SN-SP30-KIT-1855-001');
-- (... Thêm 14 dòng nữa cho đủ 15)