package com.example.datn.Service.ThongSoAdmin;


import com.example.datn.Config.NotFoundException;
import com.example.datn.DTO.CRUDthongso.LoaiThongSoDTO;
import com.example.datn.DTO.CRUDthongso.ThongSoKyThuatDTO;

import com.example.datn.Model.LoaiThongSo;
import com.example.datn.Model.SanPham;
import com.example.datn.Model.ThongSoKyThuat;
import com.example.datn.Repository.LoaiThongSoRepository;
import com.example.datn.Repository.SanPhamRepository;
import com.example.datn.Repository.ThongSoKyThuatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ThongSoService {

    private final ThongSoKyThuatRepository thongSoRepo;
    private final LoaiThongSoRepository loaiThongSoRepo;
    private final SanPhamRepository sanPhamRepo;

    // Lấy tất cả thông số của 1 sản phẩm
    @Transactional(readOnly = true)
    public List<ThongSoKyThuatDTO> getThongSoForSanPham(Integer maSanPham) {
        return thongSoRepo.findBySanPham_MaSanPham(maSanPham).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LoaiThongSoDTO> getAvailableLoaiThongSo(Integer maSanPham) {
        SanPham sanPham = sanPhamRepo.findById(maSanPham)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm với mã: " + maSanPham));

        List<LoaiThongSo> allLoaiThongSoForCategory = loaiThongSoRepo.findByDanhMuc_MaDanhMuc(sanPham.getDanhMuc().getMaDanhMuc());

        List<Integer> usedLoaiThongSoIds = thongSoRepo.findBySanPham_MaSanPham(maSanPham).stream()
                .map(ts -> ts.getLoaiThongSo().getLoaiThongSoId())
                .collect(Collectors.toList());

        return allLoaiThongSoForCategory.stream()
                .filter(lts -> !usedLoaiThongSoIds.contains(lts.getLoaiThongSoId()))
                .map(lts -> new LoaiThongSoDTO(lts.getLoaiThongSoId(), lts.getTenLoaiThongSo()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ThongSoKyThuat createThongSo(Integer maSanPham, ThongSoKyThuatDTO dto) {
        SanPham sanPham = sanPhamRepo.findById(maSanPham).orElseThrow(() -> new NotFoundException("Sản phẩm không tồn tại"));
        LoaiThongSo loaiThongSo = loaiThongSoRepo.findById(dto.getLoaiThongSoId()).orElseThrow(() -> new NotFoundException("Loại thông số không tồn tại"));

        ThongSoKyThuat newThongSo = new ThongSoKyThuat();
        newThongSo.setSanPham(sanPham);
        newThongSo.setLoaiThongSo(loaiThongSo);
        newThongSo.setTenThongSo(dto.getTenThongSo());
        newThongSo.setGiaTriThongSo(dto.getGiaTriThongSo());
        newThongSo.setTrangThai(1);

        return thongSoRepo.save(newThongSo);
    }

    @Transactional
    public ThongSoKyThuat updateThongSo(Integer maThongSo, ThongSoKyThuatDTO dto) {
        ThongSoKyThuat existingThongSo = thongSoRepo.findById(maThongSo)
                .orElseThrow(() -> new NotFoundException("Thông số không tồn tại: " + maThongSo));

        existingThongSo.setTenThongSo(dto.getTenThongSo());
        existingThongSo.setGiaTriThongSo(dto.getGiaTriThongSo());

        // ✅ YÊU CẦU 3: Bổ sung dòng này để cập nhật trạng thái
        existingThongSo.setTrangThai(dto.getTrangThai());

        return thongSoRepo.save(existingThongSo);
    }

    @Transactional
    public void deleteThongSo(Integer maThongSo) {
        if (!thongSoRepo.existsById(maThongSo)) {
            throw new NotFoundException("Thông số không tồn tại");
        }
        thongSoRepo.deleteById(maThongSo);
    }

    private ThongSoKyThuatDTO mapToDTO(ThongSoKyThuat ts) {
        return new ThongSoKyThuatDTO(ts.getMaThongSo(), ts.getTenThongSo(), ts.getGiaTriThongSo(), ts.getTrangThai(), ts.getLoaiThongSo().getLoaiThongSoId(), ts.getLoaiThongSo().getTenLoaiThongSo());
    }



























}