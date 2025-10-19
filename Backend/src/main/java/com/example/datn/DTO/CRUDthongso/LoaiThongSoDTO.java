package com.example.datn.DTO.CRUDthongso;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoaiThongSoDTO {
    private Integer loaiThongSoId;
    private String tenLoaiThongSo;
}