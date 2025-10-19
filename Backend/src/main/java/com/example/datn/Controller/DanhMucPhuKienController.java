package com.example.datn.Controller;


import com.example.datn.DTO.CRUDphukien.DanhMucPhuKienDTO;

import com.example.datn.Service.DanhMucPhuKienService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/danh-muc-phu-kien") // Đổi path API
public class DanhMucPhuKienController {

    private final DanhMucPhuKienService danhMucPhuKienService;

    @GetMapping
    public List<DanhMucPhuKienDTO> getAllDanhMucPhuKien() {
        return danhMucPhuKienService.getAllDanhMucPhuKienWithCount();
    }
}
