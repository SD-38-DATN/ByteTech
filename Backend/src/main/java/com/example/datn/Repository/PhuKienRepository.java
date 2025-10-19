package com.example.datn.Repository;

import com.example.datn.DTO.CRUDphukien.PhuKienDTO;
import com.example.datn.Model.PhuKien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhuKienRepository extends JpaRepository<PhuKien , Integer> {

    @Query("SELECT new com.example.datn.DTO.CRUDphukien.PhuKienDTO(pk.maPhuKien, pk.tenPhuKien, pk.thuongHieu, pk.soLuong, pk.gia) " +
            "FROM PhuKien pk " +
            // Đảm bảo tên trường entity là 'danhMucPhuKien' (chữ 'd' thường)
            "WHERE pk.danhMucPhuKien.maDanhMucPhuKien = :maDmPK AND pk.trangThai = 1")
    List<PhuKienDTO> getPhuKienByDanhMucId(@Param("maDmPK") Integer maDanhMucPhuKien);
}
