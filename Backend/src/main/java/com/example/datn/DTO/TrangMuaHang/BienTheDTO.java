package com.example.datn.DTO.TrangMuaHang;

// BienTheDTO.java
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BienTheDTO {
    private String maSKU;
    private BigDecimal gia;
    private int soLuong;

    private Integer trangThai ;
//    private List<ThuocTinhDTO> thuocTinhList;

    private Map<String, String> thuocTinh;
}
