package com.example.datn.DTO.IMEI;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VariantSummaryDTO {
    private String sku;
    private String tenHienThi;
    private String loai; // "sanpham" hoặc "phukien"
}