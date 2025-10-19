# Hướng dẫn sử dụng hệ thống quản lý trạng thái IMEI

## Tổng quan

Hệ thống quản lý trạng thái IMEI cho phép:
- **Trạng thái 1**: Còn hàng (IMEI có thể bán)
- **Trạng thái 5**: Tạm giữ (IMEI đang trong giỏ hàng, có thời hạn 1 tiếng)
- **Trạng thái 0**: Đã bán (IMEI đã được bán)

## Cấu trúc Database

### Bảng IMEI
```sql
-- Cột mới được thêm
ALTER TABLE IMEI ADD ngayGiuTam DATETIME NULL;
```

### Ý nghĩa các trường:
- `trangThai`: 1 = Còn hàng, 5 = Tạm giữ, 0 = Đã bán
- `ngayGiuTam`: Thời gian bắt đầu tạm giữ (chỉ có khi trangThai = 5)

## API Endpoints

### 1. Thêm sản phẩm vào giỏ hàng
```http
POST /api/banhangtaiquay/imei/them-vao-gio-hang
Content-Type: application/json

{
    "imei": "352987654321098"
}
```

**Response thành công:**
```json
{
    "success": true,
    "message": "Đã thêm sản phẩm vào giỏ hàng thành công",
    "imei": "352987654321098",
    "trangThai": "tam_giu"
}
```

### 2. Xóa sản phẩm khỏi giỏ hàng
```http
DELETE /api/banhangtaiquay/imei/xoa-khoi-gio-hang
Content-Type: application/json

{
    "imei": "352987654321098"
}
```

**Response thành công:**
```json
{
    "success": true,
    "message": "Đã xóa sản phẩm khỏi giỏ hàng thành công",
    "imei": "352987654321098",
    "trangThai": "con_hang"
}
```

### 3. Thanh toán thành công
```http
POST /api/banhangtaiquay/imei/thanh-toan
Content-Type: application/json

{
    "imei": "352987654321098"
}
```

**Response thành công:**
```json
{
    "success": true,
    "message": "Thanh toán thành công",
    "imei": "352987654321098",
    "trangThai": "da_ban"
}
```

### 4. Kiểm tra trạng thái IMEI
```http
GET /api/banhangtaiquay/imei/kiem-tra-trang-thai/{imei}
```

**Response:**
```json
{
    "imei": "352987654321098",
    "isTamGiu": true,
    "trangThai": "tam_giu"
}
```

### 5. Lấy danh sách IMEI tạm giữ
```http
GET /api/banhangtaiquay/imei/danh-sach-tam-giu
```

### 6. Chạy thủ công cron job
```http
POST /api/banhangtaiquay/imei/chay-cron-job
```

## Cron Job

Hệ thống có 2 cron job:

1. **Chạy mỗi 5 phút**: `@Scheduled(cron = "0 */5 * * * *")`
2. **Chạy mỗi giờ**: `@Scheduled(cron = "0 0 * * * *")`

Cả hai đều kiểm tra và chuyển IMEI tạm giữ quá 1 tiếng về trạng thái còn hàng.

## Luồng hoạt động

### 1. Thêm sản phẩm vào giỏ hàng
1. Nhân viên chọn sản phẩm và IMEI
2. Gọi API `them-vao-gio-hang`
3. Hệ thống chuyển IMEI sang trạng thái 5 (tạm giữ)
4. Lưu thời gian bắt đầu tạm giữ

### 2. Xóa sản phẩm khỏi giỏ hàng
1. Khách đổi ý hoặc chọn máy khác
2. Gọi API `xoa-khoi-gio-hang`
3. Hệ thống chuyển IMEI về trạng thái 1 (còn hàng)
4. Xóa thời gian tạm giữ

### 3. Thanh toán thành công
1. Bán hàng xong
2. Gọi API `thanh-toan`
3. Hệ thống chuyển IMEI sang trạng thái 0 (đã bán)
4. Xóa thời gian tạm giữ

### 4. Tự động chuyển trạng thái
1. Cron job chạy định kỳ
2. Tìm IMEI có `trangThai = 5` và `ngayGiuTam < (NOW() - 1 tiếng)`
3. Chuyển về trạng thái 1 (còn hàng)
4. Xóa thời gian tạm giữ

## Lưu ý quan trọng

1. **Thời gian tạm giữ**: 1 tiếng (60 phút)
2. **Cron job**: Chạy mỗi 5 phút để đảm bảo IMEI không bị tạm giữ quá lâu
3. **Validation**: Chỉ IMEI có trạng thái 1 mới có thể chuyển sang tạm giữ
4. **Transaction**: Tất cả operations đều có `@Transactional` để đảm bảo tính nhất quán

## Troubleshooting

### Lỗi thường gặp:
1. **IMEI không tồn tại**: Kiểm tra IMEI có trong database không
2. **IMEI không khả dụng**: Kiểm tra trạng thái IMEI (phải là 1)
3. **Lỗi database**: Kiểm tra kết nối database và quyền truy cập

### Debug:
- Sử dụng API `kiem-tra-trang-thai` để kiểm tra trạng thái
- Xem log console để theo dõi hoạt động
- Kiểm tra cron job có chạy không qua log
- Sử dụng API `chay-cron-job` để test thủ công
