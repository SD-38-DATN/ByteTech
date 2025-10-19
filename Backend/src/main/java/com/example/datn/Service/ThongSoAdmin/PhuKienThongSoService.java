package com.example.datn.Service.ThongSoAdmin;

import com.example.datn.Config.ProductNotFoundException; // Bạn có thể tạo Exception tương tự cho Phụ Kiện
import com.example.datn.DTO.CRUDthongso.LoaiThongSoDTO;
import com.example.datn.DTO.CRUDthongso.ThongSoKyThuatDTO;
import com.example.datn.Model.LoaiThongSo;
import com.example.datn.Model.PhuKien;
import com.example.datn.Model.ThongSoKyThuat;
import com.example.datn.Repository.LoaiThongSoRepository;
import com.example.datn.Repository.PhuKienRepository;
import com.example.datn.Repository.ThongSoKyThuatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhuKienThongSoService {

    private final PhuKienRepository phuKienRepository;
    private final ThongSoKyThuatRepository thongSoRepository;
    private final LoaiThongSoRepository loaiThongSoRepository;

    // Lấy danh sách thông số của một phụ kiện
    @Transactional(readOnly = true)
    public List<ThongSoKyThuatDTO> getThongSoForPhuKien(Integer maPhuKien) {
        return thongSoRepository.findByPhuKien_MaPhuKien(maPhuKien).stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    // Lấy danh sách loại thông số chưa được sử dụng cho phụ kiện
    @Transactional(readOnly = true)
    public List<LoaiThongSoDTO> getAvailableLoaiThongSo(Integer maPhuKien) {
        PhuKien phuKien = phuKienRepository.findById(maPhuKien)
                .orElseThrow(() -> new ProductNotFoundException("Không tìm thấy phụ kiện ID: " + maPhuKien));

        if (phuKien.getDanhMucPhuKien() == null) {
            return List.of(); // Hoặc throw exception
        }

        Integer maDanhMucPhuKien = phuKien.getDanhMucPhuKien().getMaDanhMucPhuKien();
        Set<Integer> usedLoaiThongSoIds = thongSoRepository.findByPhuKien_MaPhuKien(maPhuKien).stream()
                .map(ts -> ts.getLoaiThongSo().getLoaiThongSoId())
                .collect(Collectors.toSet());

        return loaiThongSoRepository.findByDanhMucPhuKien_MaDanhMucPhuKien(maDanhMucPhuKien).stream()
                .filter(loai -> !usedLoaiThongSoIds.contains(loai.getLoaiThongSoId()))
                .map(loai -> new LoaiThongSoDTO(loai.getLoaiThongSoId(), loai.getTenLoaiThongSo()))
                .collect(Collectors.toList());
    }

    // Tạo thông số mới cho phụ kiện
    @Transactional
    public void createThongSo(Integer maPhuKien, ThongSoKyThuatDTO dto) {
        PhuKien phuKien = phuKienRepository.findById(maPhuKien)
                .orElseThrow(() -> new ProductNotFoundException("Không tìm thấy phụ kiện ID: " + maPhuKien));
        LoaiThongSo loaiThongSo = loaiThongSoRepository.findById(dto.getLoaiThongSoId())
                .orElseThrow(() -> new ProductNotFoundException("Không tìm thấy loại thông số ID: " + dto.getLoaiThongSoId()));

        ThongSoKyThuat thongSo = new ThongSoKyThuat();
        thongSo.setPhuKien(phuKien);
        thongSo.setLoaiThongSo(loaiThongSo);
        thongSo.setTenThongSo(dto.getTenThongSo());
        thongSo.setGiaTriThongSo(dto.getGiaTriThongSo());
        thongSo.setTrangThai(1); // Mặc định là hoạt động khi tạo mới
        thongSoRepository.save(thongSo);
    }

    // Phương thức map Entity sang DTO
    private ThongSoKyThuatDTO mapEntityToDto(ThongSoKyThuat ts) {
        return ThongSoKyThuatDTO.builder()
                .maThongSo(ts.getMaThongSo())
                .tenThongSo(ts.getTenThongSo())
                .giaTriThongSo(ts.getGiaTriThongSo())
                .trangThai(ts.getTrangThai())
                .loaiThongSoId(ts.getLoaiThongSo().getLoaiThongSoId())
                .tenLoaiThongSo(ts.getLoaiThongSo().getTenLoaiThongSo())
                .build();
    }
}