package com.example.datn.DTO.CRUDphukien;



import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DanhMucPhuKienDTO {
    private Integer maDanhMucPhuKien;
    private String tenDanhMucPhuKien;
    private String moTa;
    private Long tongPhuKien; // Đổi tên trường để phản ánh số lượng Phụ kiện

    public DanhMucPhuKienDTO(Integer maDanhMucPhuKien, String tenDanhMucPhuKien, String moTa, Long tongPhuKien) {
        this.maDanhMucPhuKien = maDanhMucPhuKien;
        this.tenDanhMucPhuKien = tenDanhMucPhuKien;
        this.moTa = moTa;
        this.tongPhuKien = tongPhuKien;
    }
}