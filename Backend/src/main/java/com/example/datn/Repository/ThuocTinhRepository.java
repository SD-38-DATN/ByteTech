package com.example.datn.Repository;


import com.example.datn.Model.ThuocTinh;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ThuocTinhRepository extends JpaRepository<ThuocTinh, Integer> {
    List<ThuocTinh> findByBienTheSanPham_MaSKU(String maSKU);
}
