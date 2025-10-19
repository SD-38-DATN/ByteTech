package com.example.datn.Repository;



import com.example.datn.DTO.CRUDphukien.DanhMucPhuKienDTO;
import com.example.datn.Model.DanhMucPhuKien; // Giả định bạn đã tạo Model này
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface DanhMucPhuKienRepository extends JpaRepository<DanhMucPhuKien, Integer> {

    // Truy vấn đếm số lượng PhuKien đang hoạt động trong mỗi DanhMucPhuKien
    @Query("SELECT new com.example.datn.DTO.CRUDphukien.DanhMucPhuKienDTO(d.maDanhMucPhuKien, d.tenDanhMucPhuKien, d.moTa, COUNT(pk)) " +
            "FROM DanhMucPhuKien d " +
            "LEFT JOIN PhuKien pk ON pk.danhMucPhuKien.maDanhMucPhuKien = d.maDanhMucPhuKien AND pk.trangThai = 1 " +
            "GROUP BY d.maDanhMucPhuKien, d.tenDanhMucPhuKien, d.moTa")
    List<DanhMucPhuKienDTO> findAllWithCount();
}