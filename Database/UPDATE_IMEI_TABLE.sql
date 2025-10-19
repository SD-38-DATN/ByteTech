-- Script để cập nhật bảng IMEI với trường ngayGiuTam
-- Chạy script này để thêm cột ngayGiuTam vào bảng IMEI

-- Thêm cột ngayGiuTam vào bảng IMEI
ALTER TABLE IMEI 
ADD ngayGiuTam DATETIME NULL;

-- Cập nhật comment cho cột trangThai để rõ ràng hơn
-- (Tùy chọn - chỉ cần thiết nếu muốn cập nhật comment)
-- ALTER TABLE IMEI 
-- ALTER COLUMN trangThai INT NOT NULL 
-- COMMENT '1 = Còn hàng, 5 = Tạm giữ, 0 = Đã bán';

-- Kiểm tra cấu trúc bảng sau khi cập nhật
-- SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT 
-- FROM INFORMATION_SCHEMA.COLUMNS 
-- WHERE TABLE_NAME = 'IMEI' 
-- ORDER BY ORDINAL_POSITION;

-- Test query để kiểm tra IMEI tạm giữ quá 1 tiếng
-- SELECT imei, trangThai, ngayGiuTam, 
--        DATEDIFF(MINUTE, ngayGiuTam, GETDATE()) as phut_tam_giu
-- FROM IMEI 
-- WHERE trangThai = 5 
--   AND ngayGiuTam IS NOT NULL 
--   AND DATEDIFF(MINUTE, ngayGiuTam, GETDATE()) > 60;
