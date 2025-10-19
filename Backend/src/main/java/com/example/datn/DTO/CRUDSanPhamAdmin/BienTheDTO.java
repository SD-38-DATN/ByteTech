package com.example.datn.DTO.CRUDSanPhamAdmin;



import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Data
public class BienTheDTO {
    private String maSKU;
    private BigDecimal gia;
    private BigDecimal giaKhongKhuyenMai;
    private Integer soLuong;
    private Integer trangThai;
    private Integer maKhuyenMai; // chỉ lấy ID

    private List<ThuocTinhDTO> thuocTinhList;
}
