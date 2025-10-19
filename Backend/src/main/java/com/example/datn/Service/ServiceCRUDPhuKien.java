package com.example.datn.Service;


import com.example.datn.Config.ProductNotFoundException;
import com.example.datn.DTO.CRUDphukien.BienThePhuKienDTO;
import com.example.datn.DTO.CRUDphukien.PhuKienDTO;
import com.example.datn.DTO.CRUDphukien.ThuocTinhPhuKienDTO;
import com.example.datn.Model.*;
import com.example.datn.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceCRUDPhuKien {

    private final PhuKienRepository phuKienRepo;
    private final BienThePhuKienRepository bienThePhuKienRepo;
    private final ThuocTinhPhuKienRepository thuocTinhPhuKienRepo;
    private final UsersRepository usersRepo;
    private final SanPhamRepository sanPhamRepo;
    private final DanhMucPhuKienRepository danhMucPhuKienRepo;

    // READ Operations
    @Transactional(readOnly = true)
    public List<PhuKienDTO> getAllPhuKienDTOs() {
        return phuKienRepo.findAll().stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PhuKienDTO getPhuKienDTOById(Integer id) {
        return phuKienRepo.findById(id)
                .map(this::mapEntityToDto)
                .orElseThrow(() -> new ProductNotFoundException("Không tìm thấy phụ kiện với ID: " + id));
    }

    // CREATE Operation
    @Transactional
    public PhuKien createPhuKien(PhuKienDTO dto) {
        PhuKien pk = new PhuKien();
        mapDtoToEntity(dto, pk);

        if (dto.getBienThePhuKienList() != null && !dto.getBienThePhuKienList().isEmpty()) {
            dto.getBienThePhuKienList().forEach(bienTheDTO -> {
                BienThePhuKien btpk = new BienThePhuKien();
                mapBienTheDtoToEntity(bienTheDTO, btpk);
                btpk.setPhuKien(pk);
                pk.getBienThePhuKienList().add(btpk);

                if (bienTheDTO.getThuocTinhPhuKienList() != null) {
                    bienTheDTO.getThuocTinhPhuKienList().forEach(thuocTinhDTO -> {
                        ThuocTinhPhuKien ttpk = new ThuocTinhPhuKien();
                        mapThuocTinhDtoToEntity(thuocTinhDTO, ttpk);
                        ttpk.setBienThePhuKien(btpk);
                        btpk.getThuocTinhPhuKienList().add(ttpk);
                    });
                }
            });
        }

        updatePhuKienTotals(pk);
        return phuKienRepo.save(pk);
    }

    // UPDATE Operation
    @Transactional
    public PhuKien updatePhuKien(Integer id, PhuKienDTO dto) {
        PhuKien pk = phuKienRepo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Không tìm thấy phụ kiện với ID: " + id));

        mapDtoToEntity(dto, pk);

        Map<String, BienThePhuKien> existingBienTheMap = pk.getBienThePhuKienList().stream()
                .collect(Collectors.toMap(BienThePhuKien::getMaSKUPhuKien, Function.identity()));

        pk.getBienThePhuKienList().clear();

        if (dto.getBienThePhuKienList() != null) {
            for (BienThePhuKienDTO bienTheDTO : dto.getBienThePhuKienList()) {
                BienThePhuKien btpk = existingBienTheMap.getOrDefault(bienTheDTO.getMaSKUPhuKien(), new BienThePhuKien());
                mapBienTheDtoToEntity(bienTheDTO, btpk);
                btpk.setPhuKien(pk);
                pk.getBienThePhuKienList().add(btpk);

                updateThuocTinhForBienThe(btpk, bienTheDTO.getThuocTinhPhuKienList());
            }
        }

        updatePhuKienTotals(pk);
        return phuKienRepo.save(pk);
    }

    // DELETE Operations
    @Transactional
    public void deletePhuKien(Integer id) {
        if (!phuKienRepo.existsById(id)) {
            throw new ProductNotFoundException("Không tìm thấy phụ kiện với ID: " + id);
        }
        phuKienRepo.deleteById(id);
    }

    @Transactional
    public void deleteBienThePhuKien(String maSKU) {
        BienThePhuKien btpk = bienThePhuKienRepo.findById(maSKU)
                .orElseThrow(() -> new ProductNotFoundException("Không tìm thấy biến thể phụ kiện với SKU: " + maSKU));
        PhuKien pk = btpk.getPhuKien();
        bienThePhuKienRepo.delete(btpk);

        if (pk != null) {
            updatePhuKienTotals(pk);
            phuKienRepo.save(pk);
        }
    }

    @Transactional
    public void deleteThuocTinhPhuKien(Integer id) {
        if (!thuocTinhPhuKienRepo.existsById(id)) {
            throw new ProductNotFoundException("Không tìm thấy thuộc tính phụ kiện với ID: " + id);
        }
        thuocTinhPhuKienRepo.deleteById(id);
    }

    // ================== PRIVATE HELPER METHODS ==================

    private void updatePhuKienTotals(PhuKien pk) {
        if (pk.getBienThePhuKienList() == null || pk.getBienThePhuKienList().isEmpty()) {
            pk.setSoLuong(0);
            pk.setGia(BigDecimal.ZERO);
        } else {
            int totalQuantity = pk.getBienThePhuKienList().stream()
                    .mapToInt(BienThePhuKien::getSoLuong)
                    .sum();
            pk.setSoLuong(totalQuantity);

            BigDecimal minPrice = pk.getBienThePhuKienList().stream()
                    .map(BienThePhuKien::getGia)
                    .filter(Objects::nonNull)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
            pk.setGia(minPrice);
        }
    }

    private void updateThuocTinhForBienThe(BienThePhuKien bienThe, List<ThuocTinhPhuKienDTO> thuocTinhDTOs) {
        Map<Integer, ThuocTinhPhuKien> existingThuocTinhMap = bienThe.getThuocTinhPhuKienList().stream()
                .collect(Collectors.toMap(ThuocTinhPhuKien::getMaThuocTinhPhuKien, Function.identity()));

        bienThe.getThuocTinhPhuKienList().clear();

        if (thuocTinhDTOs != null) {
            for (ThuocTinhPhuKienDTO thuocTinhDTO : thuocTinhDTOs) {
                ThuocTinhPhuKien ttpk = existingThuocTinhMap.getOrDefault(thuocTinhDTO.getMaThuocTinhPhuKien(), new ThuocTinhPhuKien());
                mapThuocTinhDtoToEntity(thuocTinhDTO, ttpk);
                ttpk.setBienThePhuKien(bienThe);
                bienThe.getThuocTinhPhuKienList().add(ttpk);
            }
        }
    }

    // MAPPING METHODS
    private void mapDtoToEntity(PhuKienDTO dto, PhuKien pk) {
        pk.setTenPhuKien(dto.getTenPhuKien());
        pk.setThuongHieu(dto.getThuongHieu());
        pk.setMoTa(dto.getMoTa());
        pk.setTrangThai(dto.getTrangThai());
        if (dto.getUserId() != null) {
            pk.setUser(usersRepo.findById(dto.getUserId()).orElse(null));
        }
        if (dto.getMaSanPham() != null) {
            pk.setSanPham(sanPhamRepo.findById(dto.getMaSanPham()).orElse(null));
        }
        if (dto.getMaDanhMucPhuKien() != null) {
            pk.setDanhMucPhuKien(danhMucPhuKienRepo.findById(dto.getMaDanhMucPhuKien()).orElse(null));
        }
    }

    private void mapBienTheDtoToEntity(BienThePhuKienDTO dto, BienThePhuKien btpk) {
        btpk.setMaSKUPhuKien(dto.getMaSKUPhuKien());
        btpk.setGia(dto.getGia());
        btpk.setSoLuong(dto.getSoLuong());
        btpk.setTrangThai(dto.getTrangThai());
    }

    private void mapThuocTinhDtoToEntity(ThuocTinhPhuKienDTO dto, ThuocTinhPhuKien ttpk) {
        ttpk.setTenThuocTinh(dto.getTenThuocTinh());
        ttpk.setGiaTriThuocTinh(dto.getGiaTriThuocTinh());
    }

    private PhuKienDTO mapEntityToDto(PhuKien pk) {
        return PhuKienDTO.builder()
                .maPhuKien(pk.getMaPhuKien())
                .tenPhuKien(pk.getTenPhuKien())
                .thuongHieu(pk.getThuongHieu())
                .moTa(pk.getMoTa())
                .soLuong(pk.getSoLuong())
                .gia(pk.getGia())
                .trangThai(pk.getTrangThai())
                .userId(pk.getUser() != null ? pk.getUser().getId() : null)
                .maSanPham(pk.getSanPham() != null ? pk.getSanPham().getMaSanPham() : null)
                .maDanhMucPhuKien(pk.getDanhMucPhuKien() != null ? pk.getDanhMucPhuKien().getMaDanhMucPhuKien() : null)
                .tenDanhMucPhuKien(pk.getDanhMucPhuKien() != null ? pk.getDanhMucPhuKien().getTenDanhMucPhuKien() : null)
                .bienThePhuKienList(pk.getBienThePhuKienList().stream().map(this::mapBienTheEntityToDto).collect(Collectors.toList()))
                .build();
    }

    private BienThePhuKienDTO mapBienTheEntityToDto(BienThePhuKien btpk) {
        return BienThePhuKienDTO.builder()
                .maSKUPhuKien(btpk.getMaSKUPhuKien())
                .gia(btpk.getGia())
                .soLuong(btpk.getSoLuong())
                .trangThai(btpk.getTrangThai())
                .thuocTinhPhuKienList(btpk.getThuocTinhPhuKienList().stream().map(this::mapThuocTinhEntityToDto).collect(Collectors.toList()))
                .build();
    }

    private ThuocTinhPhuKienDTO mapThuocTinhEntityToDto(ThuocTinhPhuKien ttpk) {
        return ThuocTinhPhuKienDTO.builder()
                .maThuocTinhPhuKien(ttpk.getMaThuocTinhPhuKien())
                .tenThuocTinh(ttpk.getTenThuocTinh())
                .giaTriThuocTinh(ttpk.getGiaTriThuocTinh())
                .build();
    }
}