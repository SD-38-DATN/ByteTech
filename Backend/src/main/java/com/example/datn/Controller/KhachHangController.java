package com.example.datn.Controller;

import com.example.datn.DTO.UserAdmin.CreateUserAdminReq;
import com.example.datn.DTO.UserAdmin.UpdateUserAdminReq;
import com.example.datn.DTO.UserAdmin.UserAdminDto;
import com.example.datn.Service.KhachHangService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Pattern;

@Validated
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor

public class KhachHangController {

    private final KhachHangService service;

    @GetMapping
    public ResponseEntity<Page<UserAdminDto>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false)
            @Pattern(regexp = "ACTIVE|INACTIVE|BANNED|BLOCKED") // <-- cho phép cả BLOCKED
            String status
    ) {
        return ResponseEntity.ok(service.find(search, gender, normalizeStatus(status), page, size));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<UserAdminDto> updateStatus(@PathVariable Integer id, @RequestBody StatusReq req) {
        if (req == null || req.getStatus() == null || req.getStatus().isBlank()) {
            throw new IllegalArgumentException("Thiếu status");
        }
        String s = normalizeStatus(req.getStatus());
        if (!s.equals("ACTIVE") && !s.equals("INACTIVE") && !s.equals("BANNED")) {
            throw new IllegalArgumentException("Status không hợp lệ (ACTIVE/INACTIVE/BANNED)");
        }
        return ResponseEntity.ok(service.updateStatus(id, s));
    }
    private String normalizeStatus(String s) {
        if (s == null) return null;
        String v = s.trim().toUpperCase();
        // các bí danh → chuẩn hoá
        if (v.equals("BLOCKED")) return "BANNED";
        if (v.equals("DISABLED")) return "INACTIVE";
        return v;
    }

    // ⬇️ cập nhật thông tin (kể cả đặt mật khẩu mới qua newPassword)
    @PutMapping("/{id}")
    public ResponseEntity<UserAdminDto> updateInfo(
            @PathVariable @Min(1) Integer id,
            @RequestBody @Valid UpdateUserAdminReq req
    ) {
        return ResponseEntity.ok(service.updateInfo(id, req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAdminDto> getOne(@PathVariable @Min(1) Integer id) {
        return ResponseEntity.ok(service.getOne(id));
    }

    @PostMapping
    public ResponseEntity<UserAdminDto> create(@RequestBody @Valid CreateUserAdminReq req) {
        UserAdminDto dto = service.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }


    @Data
    public static class StatusReq {
        @NotBlank
        @Pattern(regexp = "ACTIVE|INACTIVE|BANNED")
        private String status;
    }
}
