package com.example.datn.Service.banhangtaiquay;

import com.example.datn.DTO.banhangtaiquay.PhuKienBanHangTaiQuayDTO;
import com.example.datn.DTO.banhangtaiquay.SanPhamBanHangTaiQuayDTO;
import com.example.datn.Model.BienThePhuKien;
import com.example.datn.Model.BienTheSanPham;
import com.example.datn.Model.IMEI;
import com.example.datn.Repository.banhangtaiquay.BienThePhuKienBanHangTaiQuayRepository;
import com.example.datn.Repository.banhangtaiquay.BienTheSanPhamBanHangTaiQuayRepository;
import com.example.datn.Repository.banhangtaiquay.IMEIBanHangTaiQuayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SanPhamBanHangTaiQuayService {

    @Autowired
    private BienTheSanPhamBanHangTaiQuayRepository bienTheSanPhamRepo;

    @Autowired
    private BienThePhuKienBanHangTaiQuayRepository bienThePhuKienRepo;

    @Autowired
    private IMEIBanHangTaiQuayRepository imeiRepo;

    // Lấy tất cả sản phẩm
    public List<SanPhamBanHangTaiQuayDTO> getSanPhamChonList() {
        List<BienTheSanPham> bienTheList = bienTheSanPhamRepo.findAllWithThuocTinh();
        return bienTheList.stream().map(this::convertToDTO).toList();
    }

    // Tìm kiếm cả sản phẩm và phụ kiện theo maSKU (ko có tim bang imei) (đã kiểm tra)
    public List<Object> timKiemSanPhamVaPhuKien(String keyword) {

        try {
            // Validation
            if (keyword == null || keyword.trim().isEmpty()) {
                throw new IllegalArgumentException("Keyword không được rỗng");
            }
            List<Object> result = new ArrayList<>();

            // 1. Tìm sản phẩm chính (chứa keyword)
            List<BienTheSanPham> sanPhamList = bienTheSanPhamRepo.findByMaSKUContainingIgnoreCaseWithThuocTinh(keyword);

            for (BienTheSanPham sanPham : sanPhamList) {
                SanPhamBanHangTaiQuayDTO dto = convertToDTO(sanPham);
                if (dto != null) {
                    result.add(dto);
                }
            }
            // 2. Tìm phụ kiện (chứa keyword)
            List<BienThePhuKien> phuKienList = bienThePhuKienRepo.findByMaSKUPhuKienContainingIgnoreCaseWithThuocTinh(keyword);

            for (BienThePhuKien phuKien : phuKienList) {
                PhuKienBanHangTaiQuayDTO dto = convertPhuKienToDTO(phuKien);
                if (dto != null) {
                    result.add(dto);
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi hệ thống khi tìm SKU: " + keyword, e);
        }
    }

    // Tìm kiếm sản phẩm theo mã SKU (đã kiểm tra)
    public List<SanPhamBanHangTaiQuayDTO> timKiemTheoMaSKU(String sku) {

        try {
            // tim thao masku gân đúng
            List<BienTheSanPham> bienTheList = bienTheSanPhamRepo.findByMaSKUContainingIgnoreCaseWithThuocTinh(sku);


            List<SanPhamBanHangTaiQuayDTO> result = new ArrayList<>();
            for (BienTheSanPham product : bienTheList) {
                try {
                    SanPhamBanHangTaiQuayDTO dto = convertToDTO(product);
                    if (dto != null) {
                        result.add(dto);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Tìm kiếm sản phẩm theo IMEI hoặc mã SKU (hiện tại không dùng)
    public List<Object> timKiemTheoImeiHoacSKU(String keyword) {
        List<Object> result = new ArrayList<>();

        try {
            // 1. Tìm theo IMEI chính xác (cả sản phẩm và phụ kiện)
            var imeiEntity = imeiRepo.findByImei(keyword);
            if (imeiEntity.isPresent()) {

                IMEI imeiObj = imeiEntity.get();
                if (imeiObj.getBienTheSanPham() != null) {
                    // Tìm lại BienTheSanPham với đầy đủ thuộc tính bằng JOIN FETCH
                    String sku = imeiObj.getBienTheSanPham().getMaSKU();
                    // Debug: Kiểm tra thuộc tính trực tiếp từ database
                    List<Object[]> thuocTinhData = bienTheSanPhamRepo.findThuocTinhByMaSKU(sku);


                    var bienTheEntity = bienTheSanPhamRepo.findByMaSKUWithThuocTinh(sku);
                    if (bienTheEntity.isPresent()) {
                        BienTheSanPham bienThe = bienTheEntity.get();

                        SanPhamBanHangTaiQuayDTO dto = convertToDTO(bienThe);
                        if (dto != null) {
                            result.add(dto);

                        }
                    }
                } else if (imeiObj.getBienThePhuKien() != null) {
                    // Tìm lại BienThePhuKien với đầy đủ thuộc tính bằng JOIN FETCH
                    String skuPhuKien = imeiObj.getBienThePhuKien().getMaSKUPhuKien();

                    var bienThePhuKienEntity = bienThePhuKienRepo.findByMaSKUPhuKienWithThuocTinh(skuPhuKien);
                    if (bienThePhuKienEntity.isPresent()) {
                        BienThePhuKien bienThePhuKien = bienThePhuKienEntity.get();

                        PhuKienBanHangTaiQuayDTO dto = convertPhuKienToDTO(bienThePhuKien);
                        if (dto != null) {
                            result.add(dto);

                        }
                    }
                }
            }

            // 2. Tìm theo SKU nếu chưa có kết quả hoặc keyword không phải là IMEI

            if (result.isEmpty()) {

                List<BienTheSanPham> productsBySku = bienTheSanPhamRepo.findByMaSKUContainingIgnoreCaseWithThuocTinh(keyword);


                for (BienTheSanPham product : productsBySku) {
                    try {

                        SanPhamBanHangTaiQuayDTO dto = convertToDTO(product);
                        if (dto != null) {
                            result.add(dto);
                        }
                    } catch (Exception e) {
                        System.err.println("❌ Service: Lỗi convertToDTO cho SKU " + product.getMaSKU() + ": " + e.getMessage());
                    }
                }

                // Tìm kiếm phụ kiện theo SKU
                List<BienThePhuKien> phuKienBySku = bienThePhuKienRepo.findByMaSKUPhuKienContainingIgnoreCaseWithThuocTinh(keyword);


                for (BienThePhuKien phuKien : phuKienBySku) {
                    try {

                        PhuKienBanHangTaiQuayDTO dto = convertPhuKienToDTO(phuKien);
                        if (dto != null) {
                            result.add(dto);

                        }
                    } catch (Exception e) {
                        System.err.println("❌ Service: Lỗi convertPhuKienToDTO cho SKU " + phuKien.getMaSKUPhuKien() + ": " + e.getMessage());
                    }
                }
            }

            return result.stream().distinct().collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    // Tìm kiếm kết hợp SKU + IMEI (hỗ trợ cả sản phẩm chính và phụ kiện)
    public List<SanPhamBanHangTaiQuayDTO> timKiemKetHop(String sku, String imei) {
        System.out.println("=== BẮT ĐẦU TÌM KIẾM KẾT HỢP ===");
        try {
            sku = sku.trim();
            imei = imei.trim();

            //  kiem tra imei ko được rỗng
            if (sku.isEmpty() || imei.isEmpty()) {

                throw new IllegalArgumentException("SKU và IMEI không được rỗng");
            }

            //  Tìm sản phẩm chính trước
            List<SanPhamBanHangTaiQuayDTO> productsBySku = timKiemTheoMaSKU(sku);

            //  Nếu không tìm thấy sản phẩm chính, tìm phụ kiện
            if (productsBySku.isEmpty()) {
                productsBySku = timKiemPhuKienTheoMaSKU(sku);
            }
            if (productsBySku.isEmpty()) {

                throw new RuntimeException("Không tìm thấy sản phẩm với SKU: " + sku);
            }

            // Lọc theo IMEI (dùng LIKE %imei%)
            List<SanPhamBanHangTaiQuayDTO> result = new ArrayList<>();
            for (SanPhamBanHangTaiQuayDTO product : productsBySku) {

                // Kiểm tra IMEI cho cả sản phẩm chính và phụ kiện
                String productSku = product.getMaSKU();
                String phuKienSku = product.getMaSKUPhuKien();

                long count = 0;
                if (productSku != null) {
                    // Kiểm tra sản phẩm chính
                    count = imeiRepo.countBySkuAndImeiLike(productSku, imei);
                }

                if (count == 0 && phuKienSku != null) {
                    // Kiểm tra phụ kiện
                    count = imeiRepo.countBySkuPhuKienAndImeiLike(phuKienSku, imei);
                }
                if (count > 0) {
                    result.add(product);
                }
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi hệ thống khi tìm kết hợp SKU: " + sku + " và IMEI: " + imei, e);
        }
    }

    // Tìm kiếm phụ kiện theo SKU ( đa kiem tra)
    private List<SanPhamBanHangTaiQuayDTO> timKiemPhuKienTheoMaSKU(String sku) {

        List<SanPhamBanHangTaiQuayDTO> result = new ArrayList<>();

        try {
            // Tìm phụ kiện theo SKU của phụ kiên nhưng chi tim gân giống
            List<BienThePhuKien> phuKienList = bienThePhuKienRepo.findByMaSKUPhuKienContainingIgnoreCaseWithThuocTinh(sku);

            for (BienThePhuKien bienThe : phuKienList) {
                // Sử dụng method đã có để convert thành PhuKienBanHangTaiQuayDTO
                PhuKienBanHangTaiQuayDTO phuKienDTO = convertPhuKienToDTO(bienThe);
                if (phuKienDTO != null) {
                    // Chuyển đổi PhuKienBanHangTaiQuayDTO thành SanPhamBanHangTaiQuayDTO
                    SanPhamBanHangTaiQuayDTO dto = convertPhuKienToSanPhamDTO(phuKienDTO);
                    if (dto != null) {
                        result.add(dto);
                    }
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Chuyển đổi PhuKienBanHangTaiQuayDTO sang SanPhamBanHangTaiQuayDTO
    private SanPhamBanHangTaiQuayDTO convertPhuKienToSanPhamDTO(PhuKienBanHangTaiQuayDTO phuKienDTO) {
        if (phuKienDTO == null) {
            return null;
        }

        try {
            SanPhamBanHangTaiQuayDTO dto = new SanPhamBanHangTaiQuayDTO();
            dto.setMaSKUPhuKien(phuKienDTO.getMaSKUPhuKien());
            dto.setTenPhuKien(phuKienDTO.getTenPhuKien());
            dto.setGia(phuKienDTO.getGia());
            dto.setSoLuong(phuKienDTO.getSoLuong());
            dto.setLoai("Phụ kiện");
            dto.setTrangThai(phuKienDTO.getTrangThai());
            dto.setThuocTinh(phuKienDTO.getThuocTinh());
            dto.setSoIMEI(phuKienDTO.getSoIMEI());
            return dto;
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }

    // Chuyển đổi BienTheSanPham sang SanPhamBanHangTaiQuayDTO
    private SanPhamBanHangTaiQuayDTO convertToDTO(BienTheSanPham bienThe) {
        if (bienThe == null) {
            return null;
        }

        try {
            // Debug: Kiểm tra thuộc tính có được load không
            if (bienThe.getThuocTinhList() != null && !bienThe.getThuocTinhList().isEmpty()) {
                // Debug: In tất cả thuộc tính
                for (int i = 0; i < bienThe.getThuocTinhList().size(); i++) {
                    var tt = bienThe.getThuocTinhList().get(i);

                }
            } else {
                System.out.println(" Service: Không có thuộc tính nào được load!");
            }

            // Tạo chuỗi thuộc tính theo format: "tên thuộc tính: giá trị"
            String thuocTinhString = "";
            if (bienThe.getThuocTinhList() != null && !bienThe.getThuocTinhList().isEmpty()) {
                thuocTinhString = bienThe.getThuocTinhList().stream()
                        .map(tt -> {
                            String tenThuocTinh = tt.getTenThuocTinh() != null ? tt.getTenThuocTinh() : "";
                            String giaTri = tt.getTenThuocTinhBienThe() != null ? tt.getTenThuocTinhBienThe() : "";
                            return tenThuocTinh + ": " + giaTri;
                        })
                        .filter(s -> !s.trim().equals(":") && !s.trim().isEmpty())
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("");
            } else {
                thuocTinhString = "N/A";
            }


            long soLuong = imeiRepo.countByBienTheSanPham_MaSKU(bienThe.getMaSKU());

            SanPhamBanHangTaiQuayDTO dto = SanPhamBanHangTaiQuayDTO.builder()
                    .maSKU(bienThe.getMaSKU())
                    .tenSanPham(bienThe.getSanPham().getTenSanPham())
                    .gia(bienThe.getGia())
                    .thuocTinh(thuocTinhString)
                    .soLuong((int) soLuong)
                    .loai("Sản phẩm chính")
                    .trangThai(bienThe.getTrangThai())
                    .build();
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Convert BienThePhuKien to PhuKienBanHangTaiQuayDTO
    private PhuKienBanHangTaiQuayDTO convertPhuKienToDTO(BienThePhuKien bienThePhuKien) {
        try {

            // Tạo chuỗi thuộc tính phụ kiện
            String thuocTinhString = "";
            if (bienThePhuKien.getThuocTinhPhuKienList() != null && !bienThePhuKien.getThuocTinhPhuKienList().isEmpty()) {
                thuocTinhString = bienThePhuKien.getThuocTinhPhuKienList().stream()
                        .map(tt -> {
                            String tenThuocTinh = tt.getTenThuocTinh() != null ? tt.getTenThuocTinh() : "";
                            String giaTri = tt.getGiaTriThuocTinh() != null ? tt.getGiaTriThuocTinh() : "";
                            return tenThuocTinh + ": " + giaTri;
                        })
                        .filter(s -> !s.trim().equals(":") && !s.trim().isEmpty())
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("");
            } else {
                thuocTinhString = "N/A";
            }

            // Lấy số lượng IMEI cho phụ kiện
            long soLuongImei = imeiRepo.countByBienThePhuKien_MaSKUPhuKien(bienThePhuKien.getMaSKUPhuKien());

            PhuKienBanHangTaiQuayDTO dto = PhuKienBanHangTaiQuayDTO.builder()
                    .maSKUPhuKien(bienThePhuKien.getMaSKUPhuKien())
                    .tenPhuKien(bienThePhuKien.getPhuKien().getTenPhuKien())
                    .gia(bienThePhuKien.getGia())
                    .thuocTinh(thuocTinhString)
                    .soLuong((int) soLuongImei)
                    .loai("Phụ kiện")
                    .trangThai(bienThePhuKien.getTrangThai())
                    .build();
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
