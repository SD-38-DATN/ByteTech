package com.example.datn.Controller;



import com.example.datn.DTO.CRUDphukien.PhuKienDTO;
import com.example.datn.DTO.PhuKienDTO.PhuKienChiTietDTO;
import com.example.datn.DTO.TrangMuaHang.TrangChuPhuKien;
import com.example.datn.Service.PhuKienService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phu-kien")
@RequiredArgsConstructor
public class PhuKienController {

    private final PhuKienService phuKienService;



    @GetMapping
    public List<TrangChuPhuKien> getAllPhuKien() {
        return phuKienService.getAllPhuKien();
    }

    // Lấy chi tiết phụ kiện theo id
    @GetMapping("/{id}")
    public PhuKienChiTietDTO getPhuKienChiTiet(@PathVariable int id) {
        return phuKienService.getPhuKienChiTiet(id);
    }


    @GetMapping("/by-danh-muc/{maDanhMucPhuKien}")
    public List<PhuKienDTO> getPhuKienByDanhMuc(@PathVariable Integer maDanhMucPhuKien) {
        return phuKienService.getPhuKienByDanhMucId(maDanhMucPhuKien);
    }





}
