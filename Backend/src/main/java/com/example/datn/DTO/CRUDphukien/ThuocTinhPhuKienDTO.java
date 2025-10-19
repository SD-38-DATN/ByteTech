package com.example.datn.DTO.CRUDphukien;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThuocTinhPhuKienDTO {
    private Integer maThuocTinhPhuKien;
    private String tenThuocTinh;
    private String giaTriThuocTinh;
}