package com.example.datn.Service;

import com.example.datn.Model.BienTheSanPham;
import com.example.datn.Model.KhuyenMai;
import com.example.datn.Repository.BienTheSanPhamRepository;
import com.example.datn.Repository.KhuyenMaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KhuyenMaiService {

    // Chỉ cần khai báo 1 lần
    private final KhuyenMaiRepository khuyenMaiRepository;

    /**
     * Phương thức này chứa logic cập nhật trạng thái khuyến mãi.
     * Nó sẽ được gọi bởi scheduler bên dưới.
     */
    public void capNhatTrangThai() {
        int rows = khuyenMaiRepository.capNhatTrangThaiTheoNgay();
        System.out.println("Đã cập nhật " + rows + " khuyến mãi.");
    }

    /**
     * Scheduler sẽ tự động chạy vào lúc 00:05 (12 giờ 5 phút đêm) hàng ngày
     * để gọi phương thức capNhatTrangThai().
     */
    @Scheduled(cron = "0 5 0 * * ?")
    public void autoUpdateTrangThaiKhuyenMai() {
        System.out.println("Scheduler đang chạy để cập nhật trạng thái khuyến mãi...");
        this.capNhatTrangThai(); // Gọi phương thức cập nhật ở trên
    }






        public List<KhuyenMai> getAllKhuyenMai() {
            return khuyenMaiRepository.findAll();
        }

        public Optional<KhuyenMai> getKhuyenMaiById(Integer id) {
            return khuyenMaiRepository.findById(id);
        }

        public KhuyenMai saveKhuyenMai(KhuyenMai khuyenMai) {
            return khuyenMaiRepository.save(khuyenMai);
        }

        public KhuyenMai updateKhuyenMai(Integer id, KhuyenMai khuyenMaiDetails) {
            KhuyenMai khuyenMai = khuyenMaiRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mại với id: " + id));

            khuyenMai.setTenKhuyenMai(khuyenMaiDetails.getTenKhuyenMai());
            khuyenMai.setMoTa(khuyenMaiDetails.getMoTa());
            khuyenMai.setLoaiGiam(khuyenMaiDetails.getLoaiGiam());
            khuyenMai.setGiaTriGiam(khuyenMaiDetails.getGiaTriGiam());
            khuyenMai.setNgayBatDau(khuyenMaiDetails.getNgayBatDau());
            khuyenMai.setNgayKetThuc(khuyenMaiDetails.getNgayKetThuc());
            khuyenMai.setTrangThai(khuyenMaiDetails.getTrangThai());

            return khuyenMaiRepository.save(khuyenMai);
        }

        public void deleteKhuyenMai(Integer id) {
            // Thực hiện xóa mềm bằng cách cập nhật trạng thái
            KhuyenMai khuyenMai = khuyenMaiRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mại với id: " + id));
            khuyenMai.setTrangThai(0); // 0 = inactive
            khuyenMaiRepository.save(khuyenMai);

            // Hoặc xóa cứng nếu muốn
            // khuyenMaiRepository.deleteById(id);
        }



    // ... bên trong class KhuyenMaiService

// Bạn cần inject thêm BienTheSanPhamRepository vào Service này
 private final BienTheSanPhamRepository bienTheSanPhamRepository;

    @Transactional
    public List<String> getAppliedVariantSkus(Integer khuyenMaiId) {
        KhuyenMai khuyenMai = khuyenMaiRepository.findById(khuyenMaiId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mại với id: " + khuyenMaiId));

        // Sử dụng Stream API để lấy danh sách SKU từ List<BienTheSanPham>
        return khuyenMai.getBienTheSanPhamList()
                .stream()
                .map(BienTheSanPham::getMaSKU)
                .collect(Collectors.toList());
    }

    @Transactional
    public void applyKhuyenMaiToVariants(Integer khuyenMaiId, List<String> skus) {
        KhuyenMai khuyenMai = khuyenMaiRepository.findById(khuyenMaiId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mại id: " + khuyenMaiId));

        // 1. Gỡ bỏ khuyến mại này khỏi tất cả các sản phẩm đang áp dụng nó
        List<BienTheSanPham> previouslyApplied = bienTheSanPhamRepository.findByKhuyenMai(khuyenMai);
        for (BienTheSanPham variant : previouslyApplied) {
            variant.setKhuyenMai(null);
        }
        bienTheSanPhamRepository.saveAll(previouslyApplied);

        // 2. Áp dụng khuyến mại cho danh sách SKU mới
        List<BienTheSanPham> variantsToApply = bienTheSanPhamRepository.findAllById(skus);
        for (BienTheSanPham variant : variantsToApply) {
            variant.setKhuyenMai(khuyenMai);
        }
        bienTheSanPhamRepository.saveAll(variantsToApply);
    }
}