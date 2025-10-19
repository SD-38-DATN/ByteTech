package com.example.datn.Controller;


import com.example.datn.DTO.TrangMuaHang.AddUserVoucherRequest;
import com.example.datn.Service.UserVoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user-vouchers")
@RequiredArgsConstructor
public class UserVoucherController {

    private final UserVoucherService userVoucherService;

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()") // Yêu cầu người dùng phải đăng nhập
    public ResponseEntity<?> addUserVoucherForCurrentUser(@Valid @RequestBody AddUserVoucherRequest request) {
        try {
            userVoucherService.addUserVoucher(request.getCodeVoucher());
            // Trả về một JSON object thay vì string đơn thuần
            return ResponseEntity.ok(Map.of("message", "Thêm voucher thành công!"));
        } catch (Exception e) {
            // Bắt các lỗi từ service và trả về Bad Request
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }




}
