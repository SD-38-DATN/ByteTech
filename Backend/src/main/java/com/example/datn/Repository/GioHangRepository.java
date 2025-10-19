package com.example.datn.Repository;

import com.example.datn.Model.GioHang;
import com.example.datn.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface GioHangRepository extends JpaRepository<GioHang, Integer> {


    Optional<GioHang> findByUser(Users user);
    Optional<GioHang> findByUser_Id(Integer userId);

    GioHang findByUserId(Integer userId);

}
