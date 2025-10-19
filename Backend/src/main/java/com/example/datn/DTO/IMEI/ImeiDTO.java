package com.example.datn.DTO.IMEI;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImeiDTO {
    private Integer id;
    private String imei;
    private LocalDateTime ngayNhapKho;
    private Integer trangThai;
    private String maSKU;
    private String maSKUPhuKien;

    // Thêm các trường để hiển thị thông tin sản phẩm/phụ kiện
    private String tenSanPham;
    private String tenPhuKien;



    private Integer maSanPham;
    private Integer soLuongBienThe; // Số lượng tồn kho của biến thể
}