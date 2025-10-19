package com.example.datn.Repository;



import com.example.datn.Model.FeedBack;
import com.example.datn.Model.PhuKien;
import com.example.datn.Model.SanPham;
import com.example.datn.Model.Users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface FeedBackRepository extends JpaRepository<FeedBack, Integer> {
    List<FeedBack> findBySanPham(SanPham sanPham);
    List<FeedBack> findByUser(Users user);


    Page<FeedBack> findBySanPham_MaSanPham(Integer sanPhamId, Pageable pageable);

    @Query("SELECT AVG(f.danhGia) FROM FeedBack f WHERE f.sanPham.maSanPham = :sanPhamId")
    Double findAverageRatingBySanPhamId(@Param("sanPhamId") Integer sanPhamId);




    List<FeedBack> findByPhuKien_MaPhuKien(Integer maPhuKien);

    List<FeedBack> findByPhuKien(PhuKien phuKien);
}
