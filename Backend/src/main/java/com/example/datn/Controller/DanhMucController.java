package com.example.datn.Controller;

import com.example.datn.DTO.TrangMuaHang.DanhMucDTO;
import com.example.datn.Service.DanhMucService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/danh-muc")
public class DanhMucController {

    private final DanhMucService danhMucService;

    @GetMapping
    public List<DanhMucDTO> getAllDanhMuc() {
        return danhMucService.getAllDanhMucWithCount();
    }
}
