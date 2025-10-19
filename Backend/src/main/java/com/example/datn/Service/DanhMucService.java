package com.example.datn.Service;

import com.example.datn.DTO.TrangMuaHang.DanhMucDTO;
import com.example.datn.Repository.DanhMucRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class DanhMucService {
    private final DanhMucRepository danhMucRepository ;

    public List<DanhMucDTO> getAllDanhMucWithCount() {
        return danhMucRepository.findAllWithCount();
    }
}
