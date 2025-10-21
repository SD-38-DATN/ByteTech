
use datn
		SELECT
            sp.tenSanPham,
            bt.maSKU,
            bt.gia,
            STRING_AGG(CONCAT(tt.tenThuocTinh, ': ', tt.tenThuocTinhBienThe), ', ') AS ThuocTinh,
			bt.soLuong
        FROM SanPham sp
        JOIN BienTheSanPham bt ON sp.maSanPham = bt.maSanPham
        LEFT JOIN ThuocTinh tt ON bt.maSKU = tt.maSKU
        GROUP BY sp.tenSanPham, bt.maSKU, bt.gia,bt.soLuong
        ORDER BY sp.tenSanPham, bt.maSKU, bt.gia,bt.soLuong;


		SELECT maSKU, imei, trangThai FROM IMEI  
               WHERE maSKU = null 
			   AND imei like '%358456090000101%'


               AND (imei IS NULL OR imei LIKE %imei%)

			   select * from IMEI
			   select *from BienTheSanPham
			   where maSKU = 'ANK-20K-BLK'
			   select * from BienThePhuKien
			   where maSKUPhuKien = 'ANK-20K-BLK'
			   select *from DonHang
			   select * from ChiTietDonHang
			   select* from Voucher
			   select*from UserVoucher
			   select*from Users
			   select * from SanPham
			   select * from PhuKien
			   ALTER TABLE DonHang
ALTER COLUMN trangThai INT;
SELECT name
FROM sys.indexes
WHERE object_id = OBJECT_ID('DonHang');
ALTER TABLE DonHang

DROP CONSTRAINT DF__DonHang__trangTh__02084FDA;
DROP INDEX idx_donhang_trangthai_user ON DonHang;

SELECT name
FROM sys.indexes
WHERE object_id = OBJECT_ID('DonHang');

ALTER TABLE DonHang
ALTER COLUMN trangThai INT;