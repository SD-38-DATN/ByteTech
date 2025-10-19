package com.example.datn.DTO.TrangMuaHang;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVoucherDTO {
    private Integer id;
    private Integer userId;
    private Integer soLanSuDung;
    private LocalDateTime ngayNhan;
    private Integer trangThai;
    private VoucherDTO voucher;
}
