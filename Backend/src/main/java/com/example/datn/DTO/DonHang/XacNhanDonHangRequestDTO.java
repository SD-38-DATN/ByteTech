package com.example.datn.DTO.DonHang;



import lombok.Data;
import java.util.List;

@Data
public class XacNhanDonHangRequestDTO {
    // Danh sách các sản phẩm cần xác nhận và gán IMEI
    private List<ChiTietDonHangIMEIDTO> items;

    @Data
    public static class ChiTietDonHangIMEIDTO {
        private Integer chiTietDonHangId;
        private String imei;
    }
}