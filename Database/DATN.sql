Create database datn


use datn
CREATE TABLE roles (
    id INT PRIMARY KEY IDENTITY(1,1),
    roleName VARCHAR(50) NOT NULL
);

INSERT INTO roles (roleName)
VALUES 
('USER'),        -- id = 1
('ADMIN'),     -- id = 2
('STAFF');       -- id = 2


CREATE TABLE Users (
    id INT PRIMARY KEY IDENTITY(1,1),
	tenHienThi VARCHAR(100),
    username VARCHAR(100),
    passwords VARCHAR(100),
	hoTen  VARCHAR(100),
	gioiTinh int, --gioiTinh: 1 = Nam, 0 = Nữ
    email VARCHAR(100),
	soDienThoai VARCHAR(10),
	diaChiGiaoHang  VARCHAR(100),
	roleID int  , 
	FOREIGN KEY (roleID) REFERENCES roles(id)

);

INSERT INTO Users (tenHienThi, username, passwords, hoTen, gioiTinh, email, soDienThoai, diaChiGiaoHang, roleID)

VALUES 
('Admin Account', 'admin', 'admin123', 'Nguyễn Văn A', 1, 'admin@example.com', '0987654321', '123 Đường ABC, Quận 1, TP. Hồ Chí Minh', 2),
('Admin Account', 'user', 'user123', 'Nguyễn Văn b', 1, 'admin@example.com', '0987654321', '123 Đường ABC, Quận 1, TP. Hồ Chí Minh', 1),
('Admin Account', 'staff', 'staff123', 'Nguyễn Văn c', 1, 'admin@example.com', '0987654321', '123 Đường ABC, Quận 1, TP. Hồ Chí Minh', 3),
('Admin Account', 'staff1', 'staff123', 'Nguyễn Văn c', 1, 'admin@example.com', '0987654321', '123 Đường ABC, Quận 1, TP. Hồ Chí Minh', 3)

-- Bảng tin nhắn giữa Admin và User
CREATE TABLE Messages (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    senderId INT NOT NULL,        -- Người gửi (User hoặc Admin)
    receiverId INT NOT NULL,      -- Người nhận
    content NVARCHAR(MAX) NOT NULL, -- Nội dung tin nhắn
    timestamp DATETIME DEFAULT GETDATE(),
    isRead BIT DEFAULT 0,         -- Đã đọc hay chưa
    FOREIGN KEY (senderId) REFERENCES Users(id),
    FOREIGN KEY (receiverId) REFERENCES Users(id)
);



-- Bảng Khuyến Mãi
--giaSauKhuyenMai = gia - (gia * giaTriGiam / 100)

CREATE TABLE KhuyenMai (
    maKhuyenMai INT PRIMARY KEY IDENTITY(1,1),
    tenKhuyenMai NVARCHAR(100),
    moTa NVARCHAR(255),
	 loaiGiam int ,  -- =1 giảm % , =0 giảm tiền
    giaTriGiam DECIMAL(10,2),   -- số tiền giảm hoặc % giảm
    ngayBatDau DATE,
    ngayKetThuc DATE,
    trangThai INT DEFAULT 1     -- 1=active, 0=inactive
);


CREATE TABLE DanhMuc (
    maDanhMuc INT PRIMARY KEY identity(1,1),
    tenDanhMuc NVARCHAR(100),
    moTa NVARCHAR(100)
);


CREATE TABLE DanhMucPhuKien (
    maDanhMucPhuKien INT PRIMARY KEY identity(1,1),
    tenDanhMucPhuKien NVARCHAR(100),
    moTa NVARCHAR(100)
);

-- Tạo bảng SanPham
CREATE TABLE SanPham (
    maSanPham INT PRIMARY KEY identity(1,1),
    tenSanPham NVARCHAR(255),
	thuongHieu nvarchar(100),
    moTa NVARCHAR(100),
	soLuong INT,
	gia DECIMAL(10,0),
	trangThai int DEFAULT 1 , 
	maDanhMuc INT,
	userId INT,
	FOREIGN KEY (userId) REFERENCES Users(id),
    FOREIGN KEY (maDanhMuc) REFERENCES DanhMuc(maDanhMuc)
);



-- Tạo bảng BienTheSanPham
CREATE TABLE BienTheSanPham (
	maSKU VARCHAR(50) PRIMARY KEY ,
    gia DECIMAL(10,0),
	giaKhongKhuyenMai DECIMAL(10,0),
    soLuong INT,
	trangThai int DEFAULT 1, 
	maSanPham int,
	maKhuyenMai INT ,   -- mỗi biến thể chỉ có thể gắn 1 khuyến mãi
    FOREIGN KEY (maKhuyenMai) REFERENCES KhuyenMai(maKhuyenMai),
    FOREIGN KEY (maSanPham) REFERENCES SanPham(maSanPham)
);

CREATE TABLE ThuocTinh (
	maThuocTinh INT  PRIMARY KEY  identity(1,1),
	maSKU VARCHAR(50)  ,
	tenThuocTinh NVARCHAR(100),
	tenThuocTinhBienThe NVARCHAR(100),
	TrangThai int , 
    FOREIGN KEY (maSKU) REFERENCES BienTheSanPham(maSKU)
);





-- Bảng Phụ Kiện
CREATE TABLE PhuKien (
    maPhuKien INT PRIMARY KEY IDENTITY(1,1),
    tenPhuKien NVARCHAR(255),
	thuongHieu nvarchar(100),
    moTa NVARCHAR(500),
    maSanPham INT,                 -- FK: 1 sản phẩm có nhiều phụ kiện
    maDanhMucPhuKien INT,
	userId INT,
	soLuong INT,
	gia DECIMAL(10,0),
	trangThai int default 1  , 
	FOREIGN KEY (userId) REFERENCES Users(id),
	FOREIGN KEY (maSanPham) REFERENCES SanPham(maSanPham),
	FOREIGN KEY ( maDanhMucPhuKien) REFERENCES DanhMucPhuKien(maDanhMucPhuKien)
);

-- Bảng Biến Thể Phụ Kiện
CREATE TABLE BienThePhuKien (
    maSKUPhuKien VARCHAR(50) PRIMARY KEY,  -- SKU duy nhất cho từng biến thể
    gia DECIMAL(10,0),
    soLuong INT,
    trangThai INT DEFAULT 1,               -- 1=hoạt động, 0=ngừng
    maPhuKien INT,                         -- FK: 1 phụ kiện có nhiều biến thể
    FOREIGN KEY (maPhuKien) REFERENCES PhuKien(maPhuKien)
);

-- Bảng Thuộc Tính Biến Thể Phụ Kiện
CREATE TABLE ThuocTinhPhuKien (
    maThuocTinhPhuKien INT PRIMARY KEY IDENTITY(1,1),
    tenThuocTinh NVARCHAR(100),        -- Ví dụ: Màu sắc
    giaTriThuocTinh NVARCHAR(100),     -- Ví dụ: Đỏ, Xanh
    maSKUPhuKien VARCHAR(50),          -- FK: 1 biến thể có nhiều thuộc tính
    FOREIGN KEY (maSKUPhuKien) REFERENCES BienThePhuKien(maSKUPhuKien)
);


CREATE TABLE LoaiThongSo (
    loaiThongSoId INT PRIMARY KEY IDENTITY(1,1),
    tenLoaiThongSo NVARCHAR(255) ,
	maDanhMuc INT ,
	maDanhMucPhuKien int , 
	FOREIGN KEY (maDanhMuc) REFERENCES DanhMuc(maDanhMuc),
	FOREIGN KEY (maDanhMucPhuKien) REFERENCES DanhMucPhuKien(maDanhMucPhuKien)
);

-- Bảng Thông số kỹ thuật
CREATE TABLE ThongSoKyThuat (
    maThongSo INT PRIMARY KEY IDENTITY(1,1),
    tenThongSo NVARCHAR(255),
    giaTriThongSo NVARCHAR(255),
	trangThai int  , 
	loaiThongSoId INT,
	maSanPham int  , 
	maPhuKien INT  , 
	FOREIGN KEY (loaiThongSoId) REFERENCES LoaiThongSo(loaiThongSoId),
	FOREIGN KEY (maSanPham) REFERENCES SanPham(maSanPham),
	FOREIGN KEY (maPhuKien) REFERENCES PhuKien(maPhuKien)
);


CREATE TABLE FeedBack (
    id INT PRIMARY KEY IDENTITY(1,1),
    userId INT NOT NULL,           -- Người dùng gửi phản hồi
    maSanPham INT ,          -- Sản phẩm được phản hồi
	maPhuKien INT , 
    noiDung NVARCHAR(1000),        -- Nội dung phản hồi
    danhGia INT CHECK (danhGia BETWEEN 1 AND 5), -- Điểm đánh giá từ 1 đến 5
    ngayDanhGia DATETIME DEFAULT GETDATE(),      -- Ngày gửi phản hồi
    FOREIGN KEY (userId) REFERENCES Users(id),
    FOREIGN KEY (maSanPham) REFERENCES SanPham(maSanPham), 
	 FOREIGN KEY (maPhuKien) REFERENCES PhuKien(maPhuKien)
);




-- Bảng Cart (giỏ hàng)
CREATE TABLE GioHang (
    maGioHang INT PRIMARY KEY IDENTITY(1,1),
    userId INT NOT NULL, 
	tongTien DECIMAL(10,0), -- mỗi giỏ hàng thuộc về 1 user
    ngayTao DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (userId) REFERENCES Users(id)
);

-- Bảng CartItem (chi tiết giỏ hàng)
-- Bảng CartItem (chi tiết giỏ hàng)
CREATE TABLE GioHangChiTiet (
    id INT PRIMARY KEY IDENTITY(1,1),
    maGioHang INT,
    maSKU VARCHAR(50) NULL,          -- biến thể sản phẩm
    maSKUPhuKien VARCHAR(50) NULL,   -- biến thể phụ kiện
    soLuong INT,
    gia DECIMAL(10,0),               -- giá tại thời điểm thêm vào giỏ
    FOREIGN KEY (maGioHang) REFERENCES GioHang(maGioHang),
    FOREIGN KEY (maSKU) REFERENCES BienTheSanPham(maSKU),
    FOREIGN KEY (maSKUPhuKien) REFERENCES BienThePhuKien(maSKUPhuKien)
);


CREATE TABLE Voucher (
    id INT PRIMARY KEY IDENTITY(1,1),
    codeVoucher VARCHAR(50) ,     -- mã voucher, ví dụ: GYM10P
    tenVoucher NVARCHAR(100),     -- tên gọi 
	soLanSuDung int , 
    moTa NVARCHAR(255),
    loaiGiam int ,  -- =1 giảm % , =0 giảm tiền
	giaTriGiam DECIMAL(10,2) ,
    dieuKienGiam DECIMAL(10,0),		-- điều kiện đơn tối thiểu
    giamToiDa DECIMAL(10,0) ,       -- giảm tối đa (cho %)
	ngayBatDau DATE,
    ngayKetThuc DATE,
	trangThai int DEFAULT 1
);





CREATE TABLE UserVoucher (
    id INT PRIMARY KEY IDENTITY(1,1),
    userId INT ,
    voucherId INT ,
    soLanSuDung int ,               -- 0 = chưa dùng, 1 = đã dùng
    ngayNhan DATE DEFAULT GETDATE(),     -- ngày user nhận voucher
	trangThai int , 
    FOREIGN KEY (userId) REFERENCES Users(id),
    FOREIGN KEY (voucherId) REFERENCES Voucher(id)
);


-- Bảng Đơn Hàng
CREATE TABLE DonHang (
    maDonHang INT PRIMARY KEY IDENTITY(1,1),
    userId INT NOT NULL,                  -- Khách hàng đặt đơn
    ngayDat DATETIME DEFAULT GETDATE(),   -- Ngày đặt
    trangThai NVARCHAR(50) DEFAULT N'Chờ xác nhận', 
    tongTien DECIMAL(10,0),               -- Tổng tiền đơn hàng
    diaChiGiaoHang NVARCHAR(255),         -- Địa chỉ giao hàng (có thể lấy từ Users hoặc user nhập mới)
    soDienThoai NVARCHAR(15),             -- SĐT nhận hàng
    phuongThucThanhToan NVARCHAR(50),     -- COD, chuyển khoản, ví điện tử...
    ghiChu NVARCHAR(255),                 -- Ghi chú đơn hàng
	 userVoucherId int  , 
	 FOREIGN KEY (userVoucherId) REFERENCES UserVoucher(id)  ,
    FOREIGN KEY (userId) REFERENCES Users(id)
);

CREATE TABLE IMEI (
    id INT PRIMARY KEY IDENTITY(1,1),
    maSKU VARCHAR(50) ,            -- Khóa ngoại, trỏ tới biến thể sản phẩm tương ứng
    maSKUPhuKien VARCHAR(50)  , 
	imei VARCHAR(80)  ,      -- Số IMEI hoặc Sê-ri duy nhất của sản phẩm
    ngayNhapKho DATETIME DEFAULT GETDATE(),-- Ngày nhập kho, dùng cho FIFO
    trangThai int default 1,
    FOREIGN KEY (maSKU) REFERENCES BienTheSanPham(maSKU),
	 FOREIGN KEY (maSKUPhuKien) REFERENCES BienThePhuKien(maSKUPhuKien)
);

-- Bảng Chi tiết đơn hàng
CREATE TABLE ChiTietDonHang (
    id INT PRIMARY KEY IDENTITY(1,1),
    maDonHang INT,
    maSKU VARCHAR(50) NULL,          -- biến thể sản phẩm
    maSKUPhuKien VARCHAR(50) NULL,   -- biến thể phụ kiện
    imei_id int  , 
	soLuong INT,
    gia DECIMAL(10,0),               -- giá tại thời điểm mua
    FOREIGN KEY (maDonHang) REFERENCES DonHang(maDonHang),
    FOREIGN KEY (maSKU) REFERENCES BienTheSanPham(maSKU),
    FOREIGN KEY (maSKUPhuKien) REFERENCES BienThePhuKien(maSKUPhuKien),
	FOREIGN KEY (imei_id) REFERENCES IMEI(id)
);



-- Tối ưu lọc đơn theo user + trạng thái
CREATE INDEX idx_donhang_trangthai_user 
ON DonHang(trangThai, userId);

-- Tối ưu khi join chi tiết đơn hàng
CREATE INDEX idx_chitietdonhang_donhang 
ON ChiTietDonHang(maDonHang);


-- 1. Thêm Voucher mẫu (giả sử có 4 voucher)
INSERT INTO Voucher (codeVoucher, tenVoucher, soLanSuDung, moTa, loaiGiam, giaTriGiam, dieuKienGiam, giamToiDa, ngayBatDau, ngayKetThuc, trangThai)
VALUES
('ADMIN10', N'Giảm 10% cho Admin', 100, N'Voucher dành cho Admin', 1, 0.5, 10000000, 40000000, '2025-08-01', '2025-09-30', 1),
('ADMIN50K', N'Giảm 50k cho Admin', 50, N'Voucher dành cho Admin', 0, 500000, 0, 500000, '2025-08-01', '2025-09-30', 1),
('ADMIN20', N'Giảm 20% cho Admin', 20, N'Voucher dành cho Admin', 1, 0.2, 0, 200000, '2025-08-01', '2025-09-30', 1),
('USER10', N'Giảm 10% cho User', 100, N'Voucher dành cho User', 1, 0.7, 0, 100000, '2025-08-01', '2025-09-30', 1);

-- 2. Thêm UserVoucher
-- Admin (id = 1) có 3 voucher: ADMIN10, ADMIN50K, ADMIN20
INSERT INTO UserVoucher (userId, voucherId)
VALUES
(1, 1), 
(1, 2), 
(1, 3);


-- Insert dữ liệu Khuyến Mãi mẫu
INSERT INTO KhuyenMai (tenKhuyenMai, moTa, loaiGiam, giaTriGiam, ngayBatDau, ngayKetThuc)
VALUES
(N'Giảm 10% cho tất cả sản phẩm', N'Áp dụng toàn bộ sản phẩm trong cửa hàng', 1, 0.15, '2025-09-01', '2025-09-30'),

(N'Giảm 200.000đ cho đơn hàng trên 2 triệu', N'Chỉ áp dụng cho đơn từ 2.000.000đ trở lên', 0, 200000.00, '2025-09-10', '2025-10-10'),

(N'Khuyến mãi 5% phụ kiện', N'Áp dụng cho toàn bộ phụ kiện trong cửa hàng', 1, 0.5, '2025-09-15', '2025-09-25'),

(N'Giảm 500.000đ khi mua điện thoại', N'Áp dụng cho danh mục điện thoại', 0, 500000.00, '2025-08-01', '2025-08-31'), -- đã hết hạn

(N'Flash Sale 20%', N'Áp dụng trong 3 ngày duy nhất', 1, 0.2 , '2025-09-18', '2025-09-20');





























--voucher và uservoucher
-- check xem có hoat dong của voucher 
CREATE TRIGGER trg_Check_Voucher_Date
ON Voucher
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE v
    SET v.trangThai = 
        CASE 
            WHEN CAST(GETDATE() AS DATE) BETWEEN v.ngayBatDau AND v.ngayKetThuc 
            THEN 1 ELSE 0 
        END
    FROM Voucher v
    INNER JOIN inserted i ON v.id = i.id;
END;
go


--trigger voucher
--uservoucher khi insert lấy soLanSuDung , trangThai của voucher
CREATE TRIGGER trg_Insert_UserVoucher
ON UserVoucher
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE uv
    SET 
        uv.soLanSuDung = v.soLanSuDung, 
        uv.trangThai = v.trangThai
    FROM UserVoucher uv
    INNER JOIN inserted i ON uv.id = i.id
    INNER JOIN Voucher v ON i.voucherId = v.id;
END;
GO

-- cập nhật trang thái voucher thì cập nhật luôn uservoucher

-- Trigger 1: Khi update trạng thái Voucher
CREATE TRIGGER trg_UpdateUserVoucher_WhenVoucherUpdate
ON Voucher
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Nếu trạng thái voucher thay đổi thì ép tất cả UserVoucher về cùng trạng thái
    UPDATE uv
    SET uv.trangThai = v.trangThai
    FROM UserVoucher uv
    INNER JOIN inserted v ON uv.voucherId = v.id
    INNER JOIN deleted d ON d.id = v.id
    WHERE v.trangThai <> d.trangThai;  -- chỉ khi trạng thái thay đổi
END;
GO


-- Trigger 2: Khi update soLanSuDung của UserVoucher
CREATE TRIGGER trg_UpdateUserVoucher_WhenUse
ON UserVoucher
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Nếu số lần sử dụng < 1 mà voucher vẫn đang active thì set trạng thái = 2
    UPDATE uv
    SET uv.trangThai = 2
    FROM UserVoucher uv
    INNER JOIN inserted i ON uv.id = i.id
    INNER JOIN Voucher v ON uv.voucherId = v.id
    WHERE i.soLanSuDung < 1
      AND v.trangThai = 1;   -- chỉ cập nhật nếu voucher vẫn còn hiệu lực
END;
GO












--trigger sự tồn tại của khuyến mại
CREATE TRIGGER trg_UpdateTrangThaiKhuyenMai
ON KhuyenMai
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE k
    SET trangThai = CASE 
                       WHEN CAST(GETDATE() AS DATE) BETWEEN k.ngayBatDau AND k.ngayKetThuc 
                            THEN 1 
                       ELSE 0 
                    END
    FROM KhuyenMai k
    INNER JOIN inserted i ON k.maKhuyenMai = i.maKhuyenMai;
END;
GO









create TRIGGER trg_CapNhatGiaBienThe
ON BienTheSanPham
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE b
    SET 
        -- Luôn lưu giá gốc từ inserted (ưu tiên giaKhongKhuyenMai, fallback sang gia)
        b.giaKhongKhuyenMai = COALESCE(i.giaKhongKhuyenMai, i.gia),

        -- Tính toán giá bán
        b.gia = CASE
            -- Nếu maKhuyenMai NULL thì giá = giá gốc
            WHEN i.maKhuyenMai IS NULL THEN COALESCE(i.giaKhongKhuyenMai, i.gia)

            -- Nếu có KM hợp lệ thì áp dụng
            WHEN k.maKhuyenMai IS NOT NULL
                 AND k.trangThai = 1
                 AND CAST(GETDATE() AS DATE) BETWEEN k.ngayBatDau AND k.ngayKetThuc THEN
                CASE 
                    WHEN k.loaiGiam = 1 
                        THEN i.giaKhongKhuyenMai - (i.giaKhongKhuyenMai * k.giaTriGiam)
                    WHEN k.loaiGiam = 0 
                        THEN i.giaKhongKhuyenMai - k.giaTriGiam
                END

            -- Nếu có KM nhưng không hợp lệ => trả về giá gốc
            ELSE i.giaKhongKhuyenMai
        END,

        -- Nếu KM không hợp lệ thì tự động NULL
        b.maKhuyenMai = CASE
            WHEN k.maKhuyenMai IS NOT NULL
                 AND k.trangThai = 1
                 AND CAST(GETDATE() AS DATE) BETWEEN k.ngayBatDau AND k.ngayKetThuc 
                 THEN i.maKhuyenMai
            ELSE NULL
        END
    FROM 
        BienTheSanPham b
    INNER JOIN 
        inserted i ON b.maSKU = i.maSKU
    LEFT JOIN 
        KhuyenMai k ON i.maKhuyenMai = k.maKhuyenMai;
END;
GO



CREATE TRIGGER trg_XoaKhuyenMaiKhiInactive
ON KhuyenMai
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Cập nhật các biến thể có maKhuyenMai vừa bị vô hiệu hóa
    UPDATE b
    SET 
        b.maKhuyenMai = NULL,
        b.gia = b.giaKhongKhuyenMai
    FROM BienTheSanPham b
    INNER JOIN inserted i ON b.maKhuyenMai = i.maKhuyenMai
    INNER JOIN deleted d ON i.maKhuyenMai = d.maKhuyenMai
    WHERE i.trangThai = 0 AND d.trangThai = 1;
END;
GO































