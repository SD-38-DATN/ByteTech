package com.example.datn.Model;



import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "GioHang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GioHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maGioHang;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Users user;   // tham chiếu đến bảng Users

    private LocalDateTime ngayTao;

    private BigDecimal tongTien ;

    @OneToMany(mappedBy = "gioHang")
    private List<GioHangChiTiet> chiTietList;

    @PrePersist
    public void prePersist() {
        if (ngayTao == null) {
            ngayTao = LocalDateTime.now();
        }
    }
}
