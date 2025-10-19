package com.example.datn.DTO.CRUDthongso;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoaiThongSoDetailDTO {
    private Integer loaiThongSoId;
    private String tenLoaiThongSo;
    private Integer maDanhMuc;
    private String tenDanhMuc;

    private Integer maDanhMucPhuKien; // Thêm trường này
    private String tenDanhMucPhuKien; // Thêm trường này
}