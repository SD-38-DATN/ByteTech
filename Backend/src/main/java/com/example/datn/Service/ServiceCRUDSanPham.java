//package com.example.datn.Service;
//
//import com.example.datn.DTO.CRUDSanPhamAdmin.BienTheDTO;
//import com.example.datn.DTO.CRUDSanPhamAdmin.SanPhamDTO;
//import com.example.datn.DTO.CRUDSanPhamAdmin.ThuocTinhDTO;
//import com.example.datn.Model.*;
//import com.example.datn.Repository.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class ServiceCRUDSanPham {
//
//    private final SanPhamRepository sanPhamRepo;
//    private final BienTheSanPhamRepository bienTheRepo;
//    private final ThuocTinhRepository thuocTinhRepo;
//    private final UsersRepository usersRepo;
//    private final DanhMucRepository danhMucRepo;
//
//    @Transactional(readOnly = true)
//    public List<SanPhamDTO> listAllDTO() {
//        return sanPhamRepo.findAll().stream()
//                .map(this::mapToDTO)
//                .toList();
//    }
//
//    @Transactional(readOnly = true)
//    public Optional<SanPhamDTO> getDTO(Integer id) {
//        return sanPhamRepo.findById(id).map(this::mapToDTO);
//    }
//
//
////    private SanPhamDTO mapToDTO(SanPham sp) {
////        SanPhamDTO dto = new SanPhamDTO();
////        dto.setMaSanPham(sp.getMaSanPham());
////        dto.setTenSanPham(sp.getTenSanPham());
////        dto.setMoTa(sp.getMoTa());
////        dto.setThuongHieu(sp.getThuongHieu());
////        dto.setSoLuong(sp.getSoLuong());
////        dto.setGia(sp.getGia());
////        dto.setTrangThai(sp.getTrangThai());
////        dto.setUserId(sp.getUser() != null ? sp.getUser().getId() : null);
////        dto.setMaDanhMuc(sp.getDanhMuc() != null ? sp.getDanhMuc().getMaDanhMuc() : null);
////
////        if (sp.getBienTheSanPhamList() != null) {
////            dto.setBienTheList(
////                    sp.getBienTheSanPhamList().stream().map(b -> {
////                        BienTheDTO bdto = new BienTheDTO();
////                        bdto.setMaSKU(b.getMaSKU());
////                        bdto.setGia(b.getGia());
////                        bdto.setGiaKhongKhuyenMai(b.getGiaKhongKhuyenMai());
////                        bdto.setSoLuong(b.getSoLuong());
////                        bdto.setTrangThai(b.getTrangThai());
////                        bdto.setMaKhuyenMai(b.getKhuyenMai() != null ? b.getKhuyenMai().getMaKhuyenMai() : null);
////
////                        if (b.getThuocTinhList() != null) {
////                            bdto.setThuocTinhList(
////                                    b.getThuocTinhList().stream().map(t -> {
////                                        ThuocTinhDTO tdto = new ThuocTinhDTO();
////                                        tdto.setMaThuocTinh(t.getMaThuocTinh());
////                                        tdto.setTenThuocTinh(t.getTenThuocTinh());
////                                        tdto.setTenThuocTinhBienThe(t.getTenThuocTinhBienThe());
////                                        tdto.setTrangThai(t.getTrangThai());
////                                        return tdto;
////                                    }).toList()
////                            );
////                        }
////                        return bdto;
////                    }).toList()
////            );
////        }
////        return dto;
////    }
//
//    private SanPhamDTO mapToDTO(SanPham sp) {
//        SanPhamDTO dto = new SanPhamDTO();
//        dto.setMaSanPham(sp.getMaSanPham());
//        dto.setTenSanPham(sp.getTenSanPham());
//        dto.setMoTa(sp.getMoTa());
//        dto.setThuongHieu(sp.getThuongHieu());
//        dto.setSoLuong(sp.getSoLuong());
//        dto.setGia(sp.getGia());
//        dto.setTrangThai(sp.getTrangThai());
//        dto.setUserId(sp.getUser() != null ? sp.getUser().getId() : null);
//
//        // Lấy ID và tên danh mục
//        dto.setMaDanhMuc(sp.getDanhMuc() != null ? sp.getDanhMuc().getMaDanhMuc() : null);
//        dto.setTenDanhMuc(sp.getDanhMuc() != null ? sp.getDanhMuc().getTenDanhMuc() : null);
//
//        if (sp.getBienTheSanPhamList() != null) {
//            dto.setBienTheList(
//                    sp.getBienTheSanPhamList().stream().map(b -> {
//                        BienTheDTO bdto = new BienTheDTO();
//                        bdto.setMaSKU(b.getMaSKU());
//                        bdto.setGia(b.getGia());
//                        bdto.setGiaKhongKhuyenMai(b.getGiaKhongKhuyenMai());
//                        bdto.setSoLuong(b.getSoLuong());
//                        bdto.setTrangThai(b.getTrangThai());
//                        bdto.setMaKhuyenMai(b.getKhuyenMai() != null ? b.getKhuyenMai().getMaKhuyenMai() : null);
//
//                        if (b.getThuocTinhList() != null) {
//                            bdto.setThuocTinhList(
//                                    b.getThuocTinhList().stream().map(t -> {
//                                        ThuocTinhDTO tdto = new ThuocTinhDTO();
//                                        tdto.setMaThuocTinh(t.getMaThuocTinh());
//                                        tdto.setTenThuocTinh(t.getTenThuocTinh());
//                                        tdto.setTenThuocTinhBienThe(t.getTenThuocTinhBienThe());
//                                        tdto.setTrangThai(t.getTrangThai());
//                                        return tdto;
//                                    }).toList()
//                            );
//                        }
//                        return bdto;
//                    }).toList()
//            );
//        }
//        return dto;
//    }
//
//
//
//
//    @Transactional(readOnly = true)
//    public java.util.List<SanPham> listAll() {
//        return sanPhamRepo.findAll();
//    }
//
//    @Transactional(readOnly = true)
//    public Optional<SanPham> get(Integer id) {
//        return sanPhamRepo.findById(id);
//    }
//
//    @Transactional
//    public void delete(Integer id) {
//        sanPhamRepo.deleteById(id);
//    }
//
//
//
//    @Transactional
//    public SanPham createSanPham(SanPhamDTO dto) {
//        SanPham sp = new SanPham();
//        sp.setTenSanPham(dto.getTenSanPham());
//        sp.setMoTa(dto.getMoTa());
//        sp.setThuongHieu(dto.getThuongHieu());
//        sp.setSoLuong(dto.getSoLuong());
//        sp.setGia(dto.getGia());
//        sp.setTrangThai(dto.getTrangThai());
//
//        if (dto.getUserId() != null) {
//            sp.setUser(usersRepo.findById(dto.getUserId()).orElse(null));
//        }
//        if (dto.getMaDanhMuc() != null) {
//            sp.setDanhMuc(danhMucRepo.findById(dto.getMaDanhMuc()).orElse(null));
//        }
//
//        return sanPhamRepo.save(sp);
//    }
//
//    @Transactional
//    public SanPham updateSanPham(Integer id, SanPhamDTO dto) {
//        SanPham sp = sanPhamRepo.findById(id).orElseThrow();
//        sp.setTenSanPham(dto.getTenSanPham());
//        sp.setMoTa(dto.getMoTa());
//        sp.setThuongHieu(dto.getThuongHieu());
//        sp.setSoLuong(dto.getSoLuong());
//        sp.setGia(dto.getGia());
//        sp.setTrangThai(dto.getTrangThai());
//
//        if (dto.getUserId() != null) {
//            sp.setUser(usersRepo.findById(dto.getUserId()).orElse(null));
//        }
//        if (dto.getMaDanhMuc() != null) {
//            sp.setDanhMuc(danhMucRepo.findById(dto.getMaDanhMuc()).orElse(null));
//        }
//
//        return sanPhamRepo.save(sp);
//    }
//
//    @Transactional
//    public BienTheSanPham createBienThe(Integer maSanPham, BienTheDTO dto) {
//        SanPham sp = sanPhamRepo.findById(maSanPham).orElseThrow();
//
//        BienTheSanPham bt = new BienTheSanPham();
//        bt.setMaSKU(dto.getMaSKU());
//        bt.setGia(dto.getGia());
//        bt.setGiaKhongKhuyenMai(dto.getGiaKhongKhuyenMai());
//        bt.setSoLuong(dto.getSoLuong());
//        bt.setTrangThai(dto.getTrangThai());
//        bt.setSanPham(sp);
//
//        return bienTheRepo.save(bt);
//    }
//
//    @Transactional
//    public BienTheSanPham updateBienThe(String maSKU, BienTheDTO dto) {
//        BienTheSanPham bt = bienTheRepo.findById(maSKU).orElseThrow();
//        bt.setGia(dto.getGia());
//        bt.setGiaKhongKhuyenMai(dto.getGiaKhongKhuyenMai());
//        bt.setSoLuong(dto.getSoLuong());
//        bt.setTrangThai(dto.getTrangThai());
//        return bienTheRepo.save(bt);
//    }
//
//    @Transactional
//    public ThuocTinh createThuocTinh(String maSKU, ThuocTinhDTO dto) {
//        BienTheSanPham bt = bienTheRepo.findById(maSKU).orElseThrow();
//
//        ThuocTinh tt = new ThuocTinh();
//        tt.setBienTheSanPham(bt);
//        tt.setTenThuocTinh(dto.getTenThuocTinh());
//        tt.setTenThuocTinhBienThe(dto.getTenThuocTinhBienThe());
//        tt.setTrangThai(dto.getTrangThai());
//
//        return thuocTinhRepo.save(tt);
//    }
//
//    @Transactional
//    public ThuocTinh updateThuocTinh(Integer maThuocTinh, ThuocTinhDTO dto) {
//        ThuocTinh tt = thuocTinhRepo.findById(maThuocTinh).orElseThrow();
//        tt.setTenThuocTinh(dto.getTenThuocTinh());
//        tt.setTenThuocTinhBienThe(dto.getTenThuocTinhBienThe());
//        tt.setTrangThai(dto.getTrangThai());
//        return thuocTinhRepo.save(tt);
//    }
//
//}


package com.example.datn.Service;

import com.example.datn.Config.ProductNotFoundException;
import com.example.datn.DTO.CRUDSanPhamAdmin.BienTheDTO;
import com.example.datn.DTO.CRUDSanPhamAdmin.SanPhamDTO;
import com.example.datn.DTO.CRUDSanPhamAdmin.ThuocTinhDTO;

import com.example.datn.DTO.IMEI.VariantSummaryDTO;
import com.example.datn.Model.*;
import com.example.datn.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceCRUDSanPham {

    private final SanPhamRepository sanPhamRepo;
    private final BienTheSanPhamRepository bienTheRepo;
    private final ThuocTinhRepository thuocTinhRepo;
    private final UsersRepository usersRepo;
    private final DanhMucRepository danhMucRepo;
    // Thêm KhuyenMaiRepository nếu cần gán khuyến mãi
    // private final KhuyenMaiRepository khuyenMaiRepo;

    // READ Operations (Get List & Get By Id)
    @Transactional(readOnly = true)
    public List<SanPhamDTO> getAllSanPhamDTOs() {
        return sanPhamRepo.findAll().stream()
                .map(this::mapEntityToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public SanPhamDTO getSanPhamDTOById(Integer id) {
        return sanPhamRepo.findById(id)
                .map(this::mapEntityToDto)
                .orElseThrow(() -> new ProductNotFoundException("Không tìm thấy sản phẩm với ID: " + id));
    }

    // CREATE Operation
    @Transactional
    public SanPham createSanPham(SanPhamDTO dto) {
        SanPham sp = new SanPham();
        mapDtoToEntity(dto, sp); // Map thông tin cơ bản từ DTO vào entity

        // Xử lý các biến thể con
        if (dto.getBienTheList() != null && !dto.getBienTheList().isEmpty()) {
            dto.getBienTheList().forEach(bienTheDTO -> {
                BienTheSanPham bt = new BienTheSanPham();
                mapBienTheDtoToEntity(bienTheDTO, bt);
                bt.setSanPham(sp); // Liên kết biến thể với sản phẩm cha
                sp.getBienTheSanPhamList().add(bt);

                // Xử lý các thuộc tính của biến thể
                if (bienTheDTO.getThuocTinhList() != null) {
                    bienTheDTO.getThuocTinhList().forEach(thuocTinhDTO -> {
                        ThuocTinh tt = new ThuocTinh();
                        mapThuocTinhDtoToEntity(thuocTinhDTO, tt);
                        tt.setBienTheSanPham(bt); // Liên kết thuộc tính với biến thể cha
                        bt.getThuocTinhList().add(tt);
                    });
                }
            });
        }

        updateSanPhamTotals(sp); // Cập nhật tổng số lượng và giá cho sản phẩm cha
        return sanPhamRepo.save(sp);
    }

    // UPDATE Operation
    @Transactional
    public SanPham updateSanPham(Integer id, SanPhamDTO dto) {
        SanPham sp = sanPhamRepo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Không tìm thấy sản phẩm với ID: " + id));

        mapDtoToEntity(dto, sp); // Cập nhật thông tin cơ bản của sản phẩm

        // Logic cập nhật biến thể (thêm mới, sửa, xóa)
        Map<String, BienTheSanPham> existingBienTheMap = sp.getBienTheSanPhamList().stream()
                .collect(Collectors.toMap(BienTheSanPham::getMaSKU, Function.identity()));

        // Clear list cũ và xây dựng lại để orphanRemoval hoạt động
        sp.getBienTheSanPhamList().clear();

        if (dto.getBienTheList() != null) {
            for (BienTheDTO bienTheDTO : dto.getBienTheList()) {
                BienTheSanPham bt = existingBienTheMap.getOrDefault(bienTheDTO.getMaSKU(), new BienTheSanPham());
                mapBienTheDtoToEntity(bienTheDTO, bt);
                bt.setSanPham(sp);
                sp.getBienTheSanPhamList().add(bt);

                // Tương tự, cập nhật thuộc tính cho từng biến thể
                updateThuocTinhForBienThe(bt, bienTheDTO.getThuocTinhList());
            }
        }

        updateSanPhamTotals(sp);
        return sanPhamRepo.save(sp);
    }

    // DELETE Operations
    @Transactional
    public void deleteSanPham(Integer id) {
        if (!sanPhamRepo.existsById(id)) {
            throw new ProductNotFoundException("Không tìm thấy sản phẩm với ID: " + id);
        }
        sanPhamRepo.deleteById(id);
    }

    @Transactional
    public void deleteBienThe(String maSKU) {
        BienTheSanPham bt = bienTheRepo.findById(maSKU)
                .orElseThrow(() -> new ProductNotFoundException("Không tìm thấy biến thể với SKU: " + maSKU));
        SanPham sp = bt.getSanPham();
        bienTheRepo.delete(bt);
        // Cập nhật lại thông tin sản phẩm cha sau khi xóa biến thể
        if (sp != null) {
            updateSanPhamTotals(sp);
            sanPhamRepo.save(sp);
        }
    }

    @Transactional
    public void deleteThuocTinh(Integer id) {
        if (!thuocTinhRepo.existsById(id)) {
            throw new ProductNotFoundException("Không tìm thấy thuộc tính với ID: " + id);
        }
        thuocTinhRepo.deleteById(id);
    }

    // ================== PRIVATE HELPER METHODS ==================

    // Cập nhật tổng số lượng và giá sản phẩm cha từ các biến thể
    private void updateSanPhamTotals(SanPham sp) {
        if (sp.getBienTheSanPhamList() == null || sp.getBienTheSanPhamList().isEmpty()) {
            sp.setSoLuong(0);
            sp.setGia(BigDecimal.ZERO);
        } else {
            int totalQuantity = sp.getBienTheSanPhamList().stream()
                    .mapToInt(BienTheSanPham::getSoLuong)
                    .sum();
            sp.setSoLuong(totalQuantity);

            BigDecimal minPrice = sp.getBienTheSanPhamList().stream()
                    .map(BienTheSanPham::getGia)
                    .filter(Objects::nonNull)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
            sp.setGia(minPrice);
        }
    }

    // Helper để map thuộc tính khi update
    private void updateThuocTinhForBienThe(BienTheSanPham bienThe, List<ThuocTinhDTO> thuocTinhDTOs) {
        Map<Integer, ThuocTinh> existingThuocTinhMap = bienThe.getThuocTinhList().stream()
                .collect(Collectors.toMap(ThuocTinh::getMaThuocTinh, Function.identity()));

        bienThe.getThuocTinhList().clear();

        if (thuocTinhDTOs != null) {
            for (ThuocTinhDTO thuocTinhDTO : thuocTinhDTOs) {
                ThuocTinh tt = existingThuocTinhMap.getOrDefault(thuocTinhDTO.getMaThuocTinh(), new ThuocTinh());
                mapThuocTinhDtoToEntity(thuocTinhDTO, tt);
                tt.setBienTheSanPham(bienThe);
                bienThe.getThuocTinhList().add(tt);
            }
        }
    }

    // Mapping Methods
    private void mapDtoToEntity(SanPhamDTO dto, SanPham sp) {
        sp.setTenSanPham(dto.getTenSanPham());
        sp.setMoTa(dto.getMoTa());
        sp.setThuongHieu(dto.getThuongHieu());
        sp.setTrangThai(dto.getTrangThai());
        if (dto.getUserId() != null) {
            sp.setUser(usersRepo.findById(dto.getUserId()).orElse(null));
        }
        if (dto.getMaDanhMuc() != null) {
            sp.setDanhMuc(danhMucRepo.findById(dto.getMaDanhMuc()).orElse(null));
        }
    }

    private void mapBienTheDtoToEntity(BienTheDTO dto, BienTheSanPham bt) {
        bt.setMaSKU(dto.getMaSKU());
        bt.setGia(dto.getGia());
        bt.setGiaKhongKhuyenMai(dto.getGiaKhongKhuyenMai());
        bt.setSoLuong(dto.getSoLuong());
        bt.setTrangThai(dto.getTrangThai());
        // if (dto.getMaKhuyenMai() != null) {
        //     bt.setKhuyenMai(khuyenMaiRepo.findById(dto.getMaKhuyenMai()).orElse(null));
        // }
    }

    private void mapThuocTinhDtoToEntity(ThuocTinhDTO dto, ThuocTinh tt) {
        tt.setTenThuocTinh(dto.getTenThuocTinh());
        tt.setTenThuocTinhBienThe(dto.getTenThuocTinhBienThe());
        tt.setTrangThai(dto.getTrangThai());
    }

    private SanPhamDTO mapEntityToDto(SanPham sp) {
        return SanPhamDTO.builder()
                .maSanPham(sp.getMaSanPham())
                .tenSanPham(sp.getTenSanPham())
                .moTa(sp.getMoTa())
                .thuongHieu(sp.getThuongHieu())
                .soLuong(sp.getSoLuong()) // Đã được tính toán
                .gia(sp.getGia())         // Đã được tính toán
                .trangThai(sp.getTrangThai())
                .userId(sp.getUser() != null ? sp.getUser().getId() : null)
                .maDanhMuc(sp.getDanhMuc() != null ? sp.getDanhMuc().getMaDanhMuc() : null)
                .tenDanhMuc(sp.getDanhMuc() != null ? sp.getDanhMuc().getTenDanhMuc() : null)
                .bienTheList(sp.getBienTheSanPhamList().stream().map(this::mapBienTheEntityToDto).toList())
                .build();
    }

    private BienTheDTO mapBienTheEntityToDto(BienTheSanPham b) {
        return BienTheDTO.builder()
                .maSKU(b.getMaSKU())
                .gia(b.getGia())
                .giaKhongKhuyenMai(b.getGiaKhongKhuyenMai())
                .soLuong(b.getSoLuong())
                .trangThai(b.getTrangThai())
                .maKhuyenMai(b.getKhuyenMai() != null ? b.getKhuyenMai().getMaKhuyenMai() : null)
                .thuocTinhList(b.getThuocTinhList().stream().map(this::mapThuocTinhEntityToDto).toList())
                .build();
    }

    private ThuocTinhDTO mapThuocTinhEntityToDto(ThuocTinh t) {
        return ThuocTinhDTO.builder()
                .maThuocTinh(t.getMaThuocTinh())
                .tenThuocTinh(t.getTenThuocTinh())
                .tenThuocTinhBienThe(t.getTenThuocTinhBienThe())
                .trangThai(t.getTrangThai())
                .build();
    }



    // Giả sử bạn có PhuKienRepository và BienThePhuKienRepository

    private  final PhuKienRepository phuKienRepository;

    // THÊM PHƯƠNG THỨC MỚI NÀY
    @Transactional(readOnly = true)
    public List<Map<String, String>> getAccessoryVariantsSummary() {
        return phuKienRepository.findAll().stream()
                .flatMap(pk -> pk.getBienThePhuKienList().stream()
                        .filter(btpk -> btpk.getTrangThai() == 1) // Chỉ lấy biến thể hoạt động
                        .map(btpk -> Map.of(
                                "maSKUPhuKien", btpk.getMaSKUPhuKien(),
                                "tenHienThi", pk.getTenPhuKien() + " - SKU: " + btpk.getMaSKUPhuKien()
                        ))
                )
                .collect(Collectors.toList());
    }

}