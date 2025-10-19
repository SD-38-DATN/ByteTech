package com.example.datn.DTO.CRUDSanPhamAdmin;


import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Data
public class ThuocTinhDTO {
    private Integer maThuocTinh;
    private String tenThuocTinh;
    private String tenThuocTinhBienThe;
    private Integer trangThai;
}
