package com.example.datn.Repository;

import com.example.datn.Model.LoaiThongSo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoaiThongSoRepository extends JpaRepository<LoaiThongSo , Integer> {





    List<LoaiThongSo> findByDanhMuc_MaDanhMuc(Integer maDanhMuc);



    List<LoaiThongSo> findByDanhMucPhuKien_MaDanhMucPhuKien(Integer maDanhMucPhuKien);


}
