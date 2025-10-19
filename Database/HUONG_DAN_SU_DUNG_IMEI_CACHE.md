# Hướng dẫn sử dụng hệ thống quản lý trạng thái IMEI với Cache

## Tổng quan

Hệ thống quản lý trạng thái IMEI sử dụng **cache trong bộ nhớ** thay vì database để lưu trữ thời gian tạm giữ. Điều này giúp:
- **Không cần thay đổi database** - Không cần thêm cột `ngayGiuTam`
- **Hiệu suất cao** - Cache trong bộ nhớ nhanh hơn database
- **Tự động quản lý** - Timer tự động xóa IMEI quá hạn

## Cấu trúc hệ thống

### 1. **ImeiCacheService** - Quản lý cache
- `ConcurrentHashMap<String, LocalDateTime>`: Lưu trữ IMEI và thời gian tạm giữ
- `ScheduledExecutorService`: Timer chạy mỗi 5 phút
- Tự động xóa IMEI tạm giữ quá 1 tiếng

### 2. **Trạng thái IMEI**
- **1**: Còn hàng (IMEI có thể bán)
- **5**: Tạm giữ (IMEI đang trong giỏ hàng, có thời hạn 1 tiếng)
- **0**: Đã bán (IMEI đã được bán)

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

### 3. Thanh toán thành công
```http
POST /api/banhangtaiquay/imei/thanh-toan
Content-Type: application/json

{
    "imei": "352987654321098"
}
```

### 4. Kiểm tra trạng thái IMEI
```http
GET /api/banhangtaiquay/imei/kiem-tra-trang-thai/{imei}
```

### 5. Lấy thông tin cache
```http
GET /api/banhangtaiquay/imei/thong-tin-cache
```

**Response:**
```json
{
    "success": true,
    "data": {
        "soLuongImeiTamGiu": 3,
        "danhSachImei": ["352987654321098", "352987654321099", "352987654321100"],
        "thoiGianChiTiet": {
            "352987654321098": "Tạm giữ 15 phút",
            "352987654321099": "Tạm giữ 30 phút",
            "352987654321100": "Tạm giữ 45 phút"
        }
    }
}
```

### 6. Chạy thủ công cron job
```http
POST /api/banhangtaiquay/imei/chay-cron-job
```

## Luồng hoạt động

### 1. **Thêm sản phẩm vào giỏ hàng**
1. Nhân viên chọn sản phẩm và IMEI
2. Gọi API `them-vao-gio-hang`
3. Hệ thống chuyển IMEI sang trạng thái 5 (tạm giữ) trong database
4. Thêm IMEI vào cache với thời gian hiện tại

### 2. **Xóa sản phẩm khỏi giỏ hàng**
1. Khách đổi ý hoặc chọn máy khác
2. Gọi API `xoa-khoi-gio-hang`
3. Hệ thống chuyển IMEI về trạng thái 1 (còn hàng) trong database
4. Xóa IMEI khỏi cache

### 3. **Thanh toán thành công**
1. Bán hàng xong
2. Gọi API `thanh-toan`
3. Hệ thống chuyển IMEI sang trạng thái 0 (đã bán) trong database
4. Xóa IMEI khỏi cache

### 4. **Tự động chuyển trạng thái**
1. **Timer cache**: Chạy mỗi 5 phút, tự động xóa IMEI quá 1 tiếng khỏi cache
2. **Cron job**: Chạy mỗi 5 phút, chuyển IMEI từ trạng thái 5 về 1 trong database
3. **Đồng bộ**: Cache và database luôn đồng bộ

## Ưu điểm của hệ thống cache

### ✅ **Không cần thay đổi database**
- Không cần thêm cột `ngayGiuTam`
- Không cần chạy script SQL
- Tương thích với database hiện tại

### ✅ **Hiệu suất cao**
- Cache trong bộ nhớ nhanh hơn database
- Giảm tải cho database
- Phản hồi nhanh

### ✅ **Tự động quản lý**
- Timer tự động xóa IMEI quá hạn
- Không cần can thiệp thủ công
- Đảm bảo tính nhất quán

### ✅ **Dễ debug**
- API `thong-tin-cache` để xem trạng thái cache
- Log chi tiết cho từng thao tác
- Dễ theo dõi và troubleshoot

## Lưu ý quan trọng

1. **Thời gian tạm giữ**: 1 tiếng (60 phút)
2. **Timer cache**: Chạy mỗi 5 phút để tự động xóa IMEI quá hạn
3. **Cron job**: Chạy mỗi 5 phút để đồng bộ database
4. **Validation**: Chỉ IMEI có trạng thái 1 mới có thể chuyển sang tạm giữ
5. **Transaction**: Tất cả operations đều có `@Transactional`

## Troubleshooting

### Lỗi thường gặp:
1. **IMEI không tồn tại**: Kiểm tra IMEI có trong database không
2. **IMEI không khả dụng**: Kiểm tra trạng thái IMEI (phải là 1)
3. **Cache không đồng bộ**: Sử dụng API `thong-tin-cache` để kiểm tra

### Debug:
- Sử dụng API `thong-tin-cache` để xem trạng thái cache
- Xem log console để theo dõi hoạt động
- Kiểm tra cron job có chạy không qua log
- Sử dụng API `chay-cron-job` để test thủ công

## So sánh với hệ thống database

| Tiêu chí | Database | Cache |
|----------|----------|-------|
| **Thay đổi DB** | Cần thêm cột | Không cần |
| **Hiệu suất** | Chậm hơn | Nhanh hơn |
| **Bộ nhớ** | Không tốn RAM | Tốn RAM |
| **Persistence** | Lưu trữ lâu dài | Mất khi restart |
| **Đồng bộ** | Tự động | Cần cron job |

**Kết luận**: Hệ thống cache phù hợp cho việc quản lý trạng thái tạm thời, không cần thay đổi database và có hiệu suất cao.
