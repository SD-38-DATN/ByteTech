package com.example.datn.Repository;

import com.example.datn.DTO.TrangMuaHang.DanhMucDTO;
import com.example.datn.Model.DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {


        @Query("SELECT new com.example.datn.DTO.TrangMuaHang.DanhMucDTO(d.maDanhMuc, d.tenDanhMuc, d.moTa, COUNT(sp)) " +
                "FROM DanhMuc d " +
                "LEFT JOIN SanPham sp ON sp.danhMuc.maDanhMuc = d.maDanhMuc AND sp.trangThai = 1 " +
                "GROUP BY d.maDanhMuc, d.tenDanhMuc, d.moTa")
        List<DanhMucDTO> findAllWithCount();
    }



