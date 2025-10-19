package com.example.datn.Service;



import com.example.datn.DTO.CRUDphukien.DanhMucPhuKienDTO;

import com.example.datn.Repository.DanhMucPhuKienRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class DanhMucPhuKienService {
    private final DanhMucPhuKienRepository danhMucPhuKienRepository;

    public List<DanhMucPhuKienDTO> getAllDanhMucPhuKienWithCount() {
        return danhMucPhuKienRepository.findAllWithCount();
    }

    // Giả định bạn cần lấy chi tiết các Phụ kiện trong danh mục (tương tự getDanhMuc)
    // Cần Repository cho PhuKien để thực hiện hàm này.
    // public List<PhuKienDTO> getPhuKienByDanhMuc(Integer maDanhMucPhuKien) { ... }
}