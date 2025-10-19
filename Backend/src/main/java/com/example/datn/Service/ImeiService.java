package com.example.datn.Service;

import com.example.datn.Config.DuplicateImeiException;
import com.example.datn.Config.ImeiLimitExceededException;
import com.example.datn.Config.NotFoundException;
import com.example.datn.DTO.IMEI.ImeiDTO;
import com.example.datn.Model.BienTheSanPham;
import com.example.datn.Model.IMEI;
import com.example.datn.Repository.BienThePhuKienRepository;
import com.example.datn.Repository.BienTheSanPhamRepository;
import com.example.datn.Repository.IMEIRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImeiService {

    private final IMEIRepository imeiRepo;
    private final BienTheSanPhamRepository bienTheSanPhamRepo;
    private final BienThePhuKienRepository bienThePhuKienRepo;

    @Transactional(readOnly = true)
    public List<ImeiDTO> getAll() {
        return imeiRepo.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ImeiDTO getById(Integer id) {
        return imeiRepo.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy IMEI với ID: " + id));
    }

//    @Transactional
//    public IMEI create(ImeiDTO dto) {
//        if (imeiRepo.existsByImei(dto.getImei())) {
//            throw new DuplicateImeiException("IMEI '" + dto.getImei() + "' đã tồn tại.");
//        }
//
//        IMEI imei = new IMEI();
//        mapToEntity(dto, imei);
//        imei.setNgayNhapKho(LocalDateTime.now()); // Set ngày nhập kho là hiện tại
//        return imeiRepo.save(imei);
//    }

//    private void validateImeiLimit(String maSKU, String maSKUPhuKien) {
//        if (maSKU != null && !maSKU.isBlank()) {
//            BienTheSanPham bts = bienTheSanPhamRepo.findById(maSKU)
//                    .orElseThrow(() -> new NotFoundException("Không tìm thấy biến thể sản phẩm với SKU: " + maSKU));
//            long currentImeiCount = imeiRepo.countByBienTheSanPham_MaSKU(maSKU);
//            if (currentImeiCount >= bts.getSoLuong()) {
//                throw new ImeiLimitExceededException(
//                        "Số lượng IMEI cho SKU '" + maSKU + "' đã đạt giới hạn (" + bts.getSoLuong() + ")."
//                );
//            }
//        }
//        // Tương tự cho phụ kiện nếu cần
//    }
// SỬA LẠI HÀM NÀY
private void validateImeiLimit(String maSKU, String maSKUPhuKien) {
    if (maSKU != null && !maSKU.isBlank()) {
        BienTheSanPham bts = bienTheSanPhamRepo.findById(maSKU)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy biến thể sản phẩm với SKU: " + maSKU));
        long currentImeiCount = imeiRepo.countByBienTheSanPham_MaSKU(maSKU);
        if (currentImeiCount >= bts.getSoLuong()) {
            throw new ImeiLimitExceededException(
                    "Số lượng IMEI cho SKU sản phẩm '" + maSKU + "' đã đạt giới hạn (" + bts.getSoLuong() + ")."
            );
        }
    } else if (maSKUPhuKien != null && !maSKUPhuKien.isBlank()) { // THÊM KHỐI LỆNH ELSE IF NÀY
        com.example.datn.Model.BienThePhuKien btpk = bienThePhuKienRepo.findById(maSKUPhuKien)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy biến thể phụ kiện với SKU: " + maSKUPhuKien));
        long currentImeiCount = imeiRepo.countByBienThePhuKien_MaSKUPhuKien(maSKUPhuKien);
        if (currentImeiCount >= btpk.getSoLuong()) {
            throw new ImeiLimitExceededException(
                    "Số lượng IMEI cho SKU phụ kiện '" + maSKUPhuKien + "' đã đạt giới hạn (" + btpk.getSoLuong() + ")."
            );
        }
    }
}
    @Transactional
    public IMEI create(ImeiDTO dto) {
        if (imeiRepo.existsByImei(dto.getImei())) {
            throw new DuplicateImeiException("IMEI '" + dto.getImei() + "' đã tồn tại.");
        }

        // --- LOGIC KIỂM TRA SỐ LƯỢNG MỚI ---
        validateImeiLimit(dto.getMaSKU(), dto.getMaSKUPhuKien());

        IMEI imei = new IMEI();
        mapToEntity(dto, imei);
        imei.setNgayNhapKho(LocalDateTime.now());
        return imeiRepo.save(imei);
    }

//    @Transactional
//    public IMEI update(Integer id, ImeiDTO dto) {
//        IMEI imei = imeiRepo.findById(id)
//                .orElseThrow(() -> new NotFoundException("Không tìm thấy IMEI với ID: " + id));
//
//        // Kiểm tra nếu IMEI mới đã tồn tại ở một bản ghi khác
//        imeiRepo.findByImeiAndIdNot(dto.getImei(), id).ifPresent(existing -> {
//            throw new DuplicateImeiException("IMEI '" + dto.getImei() + "' đã tồn tại ở một bản ghi khác.");
//        });
//
//        mapToEntity(dto, imei);
//        return imeiRepo.save(imei);
//    }

//    @Transactional
//    public IMEI update(Integer id, ImeiDTO dto) {
//        IMEI imei = imeiRepo.findById(id)
//                .orElseThrow(() -> new NotFoundException("Không tìm thấy IMEI với ID: " + id));
//
//        imeiRepo.findByImeiAndIdNot(dto.getImei(), id).ifPresent(existing -> {
//            throw new DuplicateImeiException("IMEI '" + dto.getImei() + "' đã tồn tại ở một bản ghi khác.");
//        });
//
//        // --- LOGIC KIỂM TRA SỐ LƯỢNG KHI UPDATE SKU ---
//        String oldSKU = imei.getBienTheSanPham() != null ? imei.getBienTheSanPham().getMaSKU() : null;
//        String newSKU = dto.getMaSKU();
//        // Chỉ kiểm tra nếu SKU bị thay đổi
//        if(oldSKU == null || !oldSKU.equals(newSKU)){
//            validateImeiLimit(dto.getMaSKU(), dto.getMaSKUPhuKien());
//        }
//
//        mapToEntity(dto, imei);
//        return imeiRepo.save(imei);
//    }
@Transactional
public IMEI update(Integer id, ImeiDTO dto) {
    IMEI imei = imeiRepo.findById(id)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy IMEI với ID: " + id));

    imeiRepo.findByImeiAndIdNot(dto.getImei(), id).ifPresent(existing -> {
        throw new DuplicateImeiException("IMEI '" + dto.getImei() + "' đã tồn tại ở một bản ghi khác.");
    });

    // --- LOGIC KIỂM TRA SỐ LƯỢNG KHI UPDATE SKU ---
    String oldSKU = imei.getBienTheSanPham() != null ? imei.getBienTheSanPham().getMaSKU() : null;
    String newSKU = dto.getMaSKU();

    // Tương tự cho Phụ kiện
    String oldSKUPhuKien = imei.getBienThePhuKien() != null ? imei.getBienThePhuKien().getMaSKUPhuKien() : null;
    String newSKUPhuKien = dto.getMaSKUPhuKien();

    // Chỉ kiểm tra nếu SKU bị thay đổi
    if ( (oldSKU == null || !oldSKU.equals(newSKU)) || (oldSKUPhuKien == null || !oldSKUPhuKien.equals(newSKUPhuKien)) ) {
        validateImeiLimit(dto.getMaSKU(), dto.getMaSKUPhuKien());
    }

    mapToEntity(dto, imei);
    return imeiRepo.save(imei);
}
    @Transactional
    public void delete(Integer id) {
        if (!imeiRepo.existsById(id)) {
            throw new NotFoundException("Không tìm thấy IMEI với ID: " + id);
        }
        imeiRepo.deleteById(id);
    }

    // --- Helper Methods ---
    private ImeiDTO mapToDTO(IMEI imei) {
        ImeiDTO dto = ImeiDTO.builder()
                .id(imei.getId())
                .imei(imei.getImei())
                .ngayNhapKho(imei.getNgayNhapKho())
                .trangThai(imei.getTrangThai())
                .build();

        if (imei.getBienTheSanPham() != null) {
            dto.setMaSKU(imei.getBienTheSanPham().getMaSKU());
            dto.setTenSanPham(imei.getBienTheSanPham().getSanPham().getTenSanPham());
            dto.setMaSanPham(imei.getBienTheSanPham().getSanPham().getMaSanPham());
            dto.setSoLuongBienThe(imei.getBienTheSanPham().getSoLuong());
        }
        if (imei.getBienThePhuKien() != null) {
            dto.setMaSKUPhuKien(imei.getBienThePhuKien().getMaSKUPhuKien());
            dto.setTenPhuKien(imei.getBienThePhuKien().getPhuKien().getTenPhuKien());
            dto.setMaSanPham(imei.getBienThePhuKien().getPhuKien().getMaPhuKien());
            dto.setSoLuongBienThe(imei.getBienThePhuKien().getSoLuong());
        }
        return dto;
    }

    // File: ImeiService.java





    private void mapToEntity(ImeiDTO dto, IMEI imei) {
        imei.setImei(dto.getImei());
        imei.setTrangThai(dto.getTrangThai());

        if (dto.getMaSKU() != null && !dto.getMaSKU().isBlank()) {
            imei.setBienTheSanPham(bienTheSanPhamRepo.findById(dto.getMaSKU())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy biến thể sản phẩm với SKU: " + dto.getMaSKU())));
            imei.setBienThePhuKien(null);
        } else if (dto.getMaSKUPhuKien() != null && !dto.getMaSKUPhuKien().isBlank()) {
            imei.setBienThePhuKien(bienThePhuKienRepo.findById(dto.getMaSKUPhuKien())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy biến thể phụ kiện với SKU: " + dto.getMaSKUPhuKien())));
            imei.setBienTheSanPham(null);
        } else {
            throw new NotFoundException("Phải cung cấp maSKU hoặc maSKUPhuKien.");
        }
    }
}