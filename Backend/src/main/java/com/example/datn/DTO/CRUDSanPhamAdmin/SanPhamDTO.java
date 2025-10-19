package com.example.datn.DTO.CRUDSanPhamAdmin;



import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Data
public class SanPhamDTO {
    private Integer maSanPham;
    private String tenSanPham;
    private String moTa;
    private String thuongHieu;
    private Integer soLuong;
    private BigDecimal gia;
    private Integer trangThai;

    private Integer userId;
    private Integer maDanhMuc; // chỉ lấy ID thôi


    private String tenDanhMuc ;

    private List<BienTheDTO> bienTheList;
}
