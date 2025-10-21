package com.example.datn.DTO.banhangtaiquay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhuKienBanHangTaiQuayDTO {
    private String maSKUPhuKien;
    private String tenPhuKien;
    private String thuocTinh;
    private BigDecimal gia;
    private Integer soLuong;
    private String loai; // "Phụ kiện"
    private Integer trangThai; // Trạng thái phụ kiện
    private Integer soIMEI; // ✅ THÊM: Số lượng IMEI
    private List<ImeiBanHangTaiQuayDTO> imeiList; // Danh sách IMEI của phụ kiện
}
