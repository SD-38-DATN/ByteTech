package com.example.datn.Repository;



import com.example.datn.DTO.TrangMuaHang.VoucherDTO;
import com.example.datn.Model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {

    // Tìm voucher theo mã

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE Voucher
        SET trangThai = CASE 
                           WHEN CAST(GETDATE() AS DATE) BETWEEN ngayBatDau AND ngayKetThuc 
                           THEN 1 ELSE 0 
                        END
    """, nativeQuery = true)
    int capNhatTrangThaiTheoNgayVoucher();


    @Query("SELECT new com.example.datn.DTO.TrangMuaHang.VoucherDTO(" +
            "v.id, v.codeVoucher, v.tenVoucher, v.soLanSuDung, v.moTa, " +
            "v.loaiGiam, v.giaTriGiam, v.dieuKienGiam, v.giamToiDa, " +
            "v.ngayBatDau, v.ngayKetThuc, v.trangThai) " +
            "FROM Voucher v WHERE v.trangThai = :status")
    List<VoucherDTO> findActiveVouchersAsDTO(@Param("status") int status);


    @Query("""
        SELECT new com.example.datn.DTO.TrangMuaHang.VoucherDTO(
            v.id, v.codeVoucher, v.tenVoucher, uv.soLanSuDung, v.moTa,
            v.loaiGiam, v.giaTriGiam, v.dieuKienGiam, v.giamToiDa,
            v.ngayBatDau, v.ngayKetThuc, uv.trangThai
        )
        FROM UserVoucher uv
        JOIN uv.voucher v
        WHERE uv.user.id = :userId
          AND uv.trangThai IN (0, 1 , 2)
    """)
    List<VoucherDTO> findUserVouchers(@Param("userId") int userId);


    Optional<Voucher> findByCodeVoucher(String codeVoucher);

}
