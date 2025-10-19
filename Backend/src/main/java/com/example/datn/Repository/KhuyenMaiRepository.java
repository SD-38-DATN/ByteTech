package com.example.datn.Repository;

import com.example.datn.Model.KhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai , Integer> {




    @Modifying
    @Transactional
    @Query(value = """
    UPDATE KhuyenMai
    SET trangThai = CASE 
                       WHEN CAST(GETDATE() AS DATE) BETWEEN ngayBatDau AND ngayKetThuc 
                       THEN 1 ELSE 0 
                    END
    """, nativeQuery = true)
    int capNhatTrangThaiTheoNgay();

}
