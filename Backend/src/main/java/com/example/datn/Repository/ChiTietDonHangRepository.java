package com.example.datn.Repository;


import com.example.datn.Model.ChiTietDonHang;
import com.example.datn.Model.GioHang;
import com.example.datn.Model.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, Integer> {


}
