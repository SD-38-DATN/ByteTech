package com.example.datn.DTO.thongke;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeChartDoanhThuDTO {
    private List<ChartDataItem> data;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartDataItem {
        private String date;    // Format: "yyyy-MM-dd" cho trường hợp theo ngày
        private String month;   // Format: "yyyy-MM" cho trường hợp theo tháng
        private String hour;    // Format: "00", "01", ..., "23" cho trường hợp theo giờ
        private Double value;   // Doanh thu
    }
}