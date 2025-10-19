package com.example.datn.DTO.CRUDthongso;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThongSoKyThuatDTO {
    private Integer maThongSo;
    private String tenThongSo;
    private String giaTriThongSo;
    private Integer trangThai;
    private Integer loaiThongSoId;
    private String tenLoaiThongSo; // Thêm để hiển thị ở frontend
}