package com.example.datn.Service.banhangtaiquay;

import com.example.datn.DTO.dtoBanHangTaiQuay.SanPhamBanHangTaiQuayDTO;
import com.example.datn.DTO.dtoBanHangTaiQuay.PhuKienBanHangTaiQuayDTO;
import com.example.datn.Model.BienTheSanPham;
import com.example.datn.Model.BienThePhuKien;
import com.example.datn.Model.IMEI;
import com.example.datn.Repository.BanHangTaiQuay.BienTheSanPhamBanHangTaiQuayRepository;
import com.example.datn.Repository.BanHangTaiQuay.BienThePhuKienBanHangTaiQuayRepository;
import com.example.datn.Repository.BienTheSanPhamRepository;
import com.example.datn.Repository.BanHangTaiQuay.IMEIBanHangTaiQuayRepository;
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

    // Tìm kiếm cả sản phẩm và phụ kiện theo maSKU
    public List<Object> timKiemSanPhamVaPhuKien(String keyword) {
        List<Object> result = new ArrayList<>();

        System.out.println("🔍 Service: Tìm kiếm sản phẩm và phụ kiện với keyword: " + keyword);

        // 1. Tìm sản phẩm chính (chứa keyword)
        List<BienTheSanPham> sanPhamList = bienTheSanPhamRepo.findByMaSKUContainingIgnoreCaseWithThuocTinh(keyword);
        System.out.println("🔍 Service: Tìm thấy " + sanPhamList.size() + " sản phẩm chính (chứa keyword)");

        for (BienTheSanPham sanPham : sanPhamList) {
            SanPhamBanHangTaiQuayDTO dto = convertToDTO(sanPham);
            if (dto != null) {
                result.add(dto);
                System.out.println("✅ Service: Đã thêm sản phẩm chính: " + dto.getMaSKU());
            }
        }

        // 2. Tìm phụ kiện (chứa keyword)
        List<BienThePhuKien> phuKienList = bienThePhuKienRepo.findByMaSKUPhuKienContainingIgnoreCaseWithThuocTinh(keyword);
        System.out.println("🔍 Service: Tìm thấy " + phuKienList.size() + " phụ kiện (chứa keyword)");

        for (BienThePhuKien phuKien : phuKienList) {
            PhuKienBanHangTaiQuayDTO dto = convertPhuKienToDTO(phuKien);
            if (dto != null) {
                result.add(dto);
                System.out.println("✅ Service: Đã thêm phụ kiện: " + dto.getMaSKUPhuKien());
            }
        }

        System.out.println("✅ Service: Tổng cộng tìm thấy " + result.size() + " kết quả");
        return result;
    }

    // Tìm kiếm sản phẩm theo mã SKU
    public List<SanPhamBanHangTaiQuayDTO> timKiemTheoMaSKU(String sku) {
        System.out.println("🔍 Service: timKiemTheoMaSKU được gọi với SKU: " + sku);
        try {
            List<BienTheSanPham> bienTheList = bienTheSanPhamRepo.findByMaSKUContainingIgnoreCaseWithThuocTinh(sku);
            System.out.println("🔍 Service: Tìm thấy " + bienTheList.size() + " sản phẩm theo SKU với JOIN FETCH: " + sku);

            List<SanPhamBanHangTaiQuayDTO> result = new ArrayList<>();
            for (BienTheSanPham product : bienTheList) {
                try {
                    System.out.println("🔍 Service: SKU: " + product.getMaSKU() + ", Số thuộc tính: " + (product.getThuocTinhList() != null ? product.getThuocTinhList().size() : "null"));
                    SanPhamBanHangTaiQuayDTO dto = convertToDTO(product);
                    if (dto != null) {
                        result.add(dto);
                        System.out.println("✅ Service: Đã thêm sản phẩm từ SKU với thuộc tính: '" + dto.getThuocTinh() + "'");
                    }
                } catch (Exception e) {
                    System.err.println("❌ Service: Lỗi convertToDTO cho SKU " + product.getMaSKU() + ": " + e.getMessage());
                }
            }

            System.out.println("✅ Service: Trả về " + result.size() + " DTO cho SKU: " + sku);
            return result;
        } catch (Exception e) {
            System.err.println("❌ Service: Lỗi trong timKiemTheoMaSKU: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Tìm kiếm sản phẩm theo IMEI hoặc mã SKU
    public List<Object> timKiemTheoImeiHoacSKU(String keyword) {
        System.out.println("🔍 Service: timKiemTheoImeiHoacSKU được gọi với keyword: " + keyword);
        List<Object> result = new ArrayList<>();

        try {
            // 1. Tìm theo IMEI chính xác (cả sản phẩm và phụ kiện)
            var imeiEntity = imeiRepo.findByImei(keyword);
            if (imeiEntity.isPresent()) {
                System.out.println("🔍 Service: Tìm thấy IMEI entity");
                IMEI imeiObj = imeiEntity.get();
                if (imeiObj.getBienTheSanPham() != null) {
                    System.out.println("🔍 Service: Tìm thấy IMEI, SKU: " + imeiObj.getBienTheSanPham().getMaSKU());
                    // Tìm lại BienTheSanPham với đầy đủ thuộc tính bằng JOIN FETCH
                    String sku = imeiObj.getBienTheSanPham().getMaSKU();
                    System.out.println("🔍 Service: Tìm thuộc tính cho SKU: " + sku);

                    // Debug: Kiểm tra thuộc tính trực tiếp từ database
                    List<Object[]> thuocTinhData = bienTheSanPhamRepo.findThuocTinhByMaSKU(sku);
                    System.out.println("🔍 Service: Native query trả về " + thuocTinhData.size() + " thuộc tính");
                    for (Object[] row : thuocTinhData) {
                        System.out.println("🔍 Service: Thuộc tính từ DB: '" + row[0] + "' = '" + row[1] + "'");
                    }

                    var bienTheEntity = bienTheSanPhamRepo.findByMaSKUWithThuocTinh(sku);
                    if (bienTheEntity.isPresent()) {
                        BienTheSanPham bienThe = bienTheEntity.get();
                        System.out.println("🔍 Service: Load BienTheSanPham với JOIN FETCH cho SKU: " + bienThe.getMaSKU());
                        System.out.println("🔍 Service: Số thuộc tính được load: " + (bienThe.getThuocTinhList() != null ? bienThe.getThuocTinhList().size() : "null"));

                        SanPhamBanHangTaiQuayDTO dto = convertToDTO(bienThe);
                        if (dto != null) {
                            result.add(dto);
                            System.out.println("✅ Service: Đã thêm sản phẩm từ IMEI với thuộc tính: '" + dto.getThuocTinh() + "'");
                        }
                    } else {
                        System.out.println("❌ Service: Không tìm thấy BienTheSanPham cho SKU: " + sku);
                    }
                } else if (imeiObj.getBienThePhuKien() != null) {
                    System.out.println("🔍 Service: Tìm thấy IMEI phụ kiện, SKU: " + imeiObj.getBienThePhuKien().getMaSKUPhuKien());
                    // Tìm lại BienThePhuKien với đầy đủ thuộc tính bằng JOIN FETCH
                    String skuPhuKien = imeiObj.getBienThePhuKien().getMaSKUPhuKien();
                    System.out.println("🔍 Service: Tìm thuộc tính cho SKU phụ kiện: " + skuPhuKien);

                    var bienThePhuKienEntity = bienThePhuKienRepo.findByMaSKUPhuKienWithThuocTinh(skuPhuKien);
                    if (bienThePhuKienEntity.isPresent()) {
                        BienThePhuKien bienThePhuKien = bienThePhuKienEntity.get();
                        System.out.println("🔍 Service: Load BienThePhuKien với JOIN FETCH cho SKU: " + bienThePhuKien.getMaSKUPhuKien());
                        System.out.println("🔍 Service: Số thuộc tính phụ kiện được load: " + (bienThePhuKien.getThuocTinhPhuKienList() != null ? bienThePhuKien.getThuocTinhPhuKienList().size() : "null"));

                        PhuKienBanHangTaiQuayDTO dto = convertPhuKienToDTO(bienThePhuKien);
                        if (dto != null) {
                            result.add(dto);
                            System.out.println("✅ Service: Đã thêm phụ kiện từ IMEI với thuộc tính: '" + dto.getThuocTinh() + "'");
                        }
                    } else {
                        System.out.println("❌ Service: Không tìm thấy BienThePhuKien cho SKU: " + skuPhuKien);
                    }
                } else {
                    System.out.println("❌ Service: IMEI không có BienTheSanPham hoặc BienThePhuKien");
                }
            } else {
                System.out.println("🔍 Service: Không tìm thấy IMEI entity");
            }

            // 2. Tìm theo SKU nếu chưa có kết quả hoặc keyword không phải là IMEI
            System.out.println("🔍 Service: Kiểm tra result.isEmpty(): " + result.isEmpty());
            if (result.isEmpty()) {
                System.out.println("🔍 Service: Tìm kiếm theo SKU vì result.isEmpty() = true");
                List<BienTheSanPham> productsBySku = bienTheSanPhamRepo.findByMaSKUContainingIgnoreCaseWithThuocTinh(keyword);
                System.out.println("🔍 Service: Tìm thấy " + productsBySku.size() + " sản phẩm theo SKU với JOIN FETCH");

                for (BienTheSanPham product : productsBySku) {
                    try {
                        System.out.println("🔍 Service: SKU: " + product.getMaSKU() + ", Số thuộc tính: " + (product.getThuocTinhList() != null ? product.getThuocTinhList().size() : "null"));
                        SanPhamBanHangTaiQuayDTO dto = convertToDTO(product);
                        if (dto != null) {
                            result.add(dto);
                            System.out.println("✅ Service: Đã thêm sản phẩm từ SKU với thuộc tính: '" + dto.getThuocTinh() + "'");
                        }
                    } catch (Exception e) {
                        System.err.println("❌ Service: Lỗi convertToDTO cho SKU " + product.getMaSKU() + ": " + e.getMessage());
                    }
                }

                // Tìm kiếm phụ kiện theo SKU
                List<BienThePhuKien> phuKienBySku = bienThePhuKienRepo.findByMaSKUPhuKienContainingIgnoreCaseWithThuocTinh(keyword);
                System.out.println("🔍 Service: Tìm thấy " + phuKienBySku.size() + " phụ kiện theo SKU với JOIN FETCH");

                for (BienThePhuKien phuKien : phuKienBySku) {
                    try {
                        System.out.println("🔍 Service: SKU phụ kiện: " + phuKien.getMaSKUPhuKien() + ", Số thuộc tính: " + (phuKien.getThuocTinhPhuKienList() != null ? phuKien.getThuocTinhPhuKienList().size() : "null"));
                        PhuKienBanHangTaiQuayDTO dto = convertPhuKienToDTO(phuKien);
                        if (dto != null) {
                            result.add(dto);
                            System.out.println("✅ Service: Đã thêm phụ kiện từ SKU với thuộc tính: '" + dto.getThuocTinh() + "'");
                        }
                    } catch (Exception e) {
                        System.err.println("❌ Service: Lỗi convertPhuKienToDTO cho SKU " + phuKien.getMaSKUPhuKien() + ": " + e.getMessage());
                    }
                }
            } else {
                System.out.println("🔍 Service: Bỏ qua tìm kiếm theo SKU vì đã có kết quả từ IMEI");
            }

            System.out.println("✅ Service: Trả về " + result.size() + " kết quả");
            return result.stream().distinct().collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("❌ Service: Lỗi trong timKiemTheoImeiHoacSKU: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    // Tìm kiếm kết hợp SKU + IMEI (hỗ trợ cả sản phẩm chính và phụ kiện)
    public List<SanPhamBanHangTaiQuayDTO> timKiemKetHop(String sku, String imei) {
        System.out.println("=== BẮT ĐẦU TÌM KIẾM KẾT HỢP ===");
        System.out.println("🔍 Tìm kiếm kết hợp với SKU: '" + sku + "', IMEI: '" + imei + "'");

        sku = sku.trim();
        imei = imei.trim();

        // ✅ SỬA: Tìm sản phẩm chính trước
        List<SanPhamBanHangTaiQuayDTO> productsBySku = timKiemTheoMaSKU(sku);
        System.out.println("📦 Sản phẩm chính tìm theo SKU: " + productsBySku.size());

        // ✅ SỬA: Nếu không tìm thấy sản phẩm chính, tìm phụ kiện
        if (productsBySku.isEmpty()) {
            System.out.println("🔍 Không tìm thấy sản phẩm chính, tìm kiếm phụ kiện...");
            productsBySku = timKiemPhuKienTheoMaSKU(sku);
            System.out.println("📦 Phụ kiện tìm theo SKU: " + productsBySku.size());
        }

        if (productsBySku.isEmpty()) {
            System.out.println("❌ Không tìm thấy sản phẩm với SKU: " + sku);
            return new ArrayList<>();
        }

        // Lọc theo IMEI (dùng LIKE %imei%)
        List<SanPhamBanHangTaiQuayDTO> result = new ArrayList<>();
        for (SanPhamBanHangTaiQuayDTO product : productsBySku) {
            // ✅ SỬA: Kiểm tra IMEI cho cả sản phẩm chính và phụ kiện
            String productSku = product.getMaSKU();
            String phuKienSku = product.getMaSKUPhuKien();

            long count = 0;
            if (productSku != null) {
                // Kiểm tra sản phẩm chính
                count = imeiRepo.countBySkuAndImeiLike(productSku, imei);
                System.out.println("🔍 Service: Kiểm tra IMEI cho sản phẩm chính " + productSku + ": " + count);
            }

            if (count == 0 && phuKienSku != null) {
                // Kiểm tra phụ kiện
                count = imeiRepo.countBySkuPhuKienAndImeiLike(phuKienSku, imei);
                System.out.println("🔍 Service: Kiểm tra IMEI cho phụ kiện " + phuKienSku + ": " + count);
            }

            if (count > 0) {
                result.add(product);
                System.out.println("✅ Sản phẩm có IMEI khớp: " + (productSku != null ? productSku : phuKienSku) + " (count: " + count + ")");
            }
        }

        System.out.println("📊 Kết quả kết hợp: " + result.size() + " sản phẩm");
        System.out.println("=== KẾT THÚC TÌM KIẾM KẾT HỢP ===");
        return result;
    }

    // Tìm kiếm phụ kiện theo SKU
    private List<SanPhamBanHangTaiQuayDTO> timKiemPhuKienTheoMaSKU(String sku) {
        System.out.println("🔍 Service: timKiemPhuKienTheoMaSKU được gọi với SKU: " + sku);
        List<SanPhamBanHangTaiQuayDTO> result = new ArrayList<>();

        try {
            // Tìm phụ kiện theo SKU
            List<BienThePhuKien> phuKienList = bienThePhuKienRepo.findByMaSKUPhuKienContainingIgnoreCaseWithThuocTinh(sku);
            System.out.println("🔍 Service: Tìm thấy " + phuKienList.size() + " phụ kiện (chứa keyword)");

            for (BienThePhuKien bienThe : phuKienList) {
                // Sử dụng method đã có để convert thành PhuKienBanHangTaiQuayDTO
                PhuKienBanHangTaiQuayDTO phuKienDTO = convertPhuKienToDTO(bienThe);
                if (phuKienDTO != null) {
                    // Chuyển đổi PhuKienBanHangTaiQuayDTO thành SanPhamBanHangTaiQuayDTO
                    SanPhamBanHangTaiQuayDTO dto = convertPhuKienToSanPhamDTO(phuKienDTO);
                    if (dto != null) {
                        result.add(dto);
                        System.out.println("✅ Service: Đã thêm phụ kiện: " + dto.getMaSKUPhuKien());
                    }
                }
            }

            System.out.println("✅ Service: Tổng cộng tìm thấy " + result.size() + " phụ kiện");
            return result;
        } catch (Exception e) {
            System.err.println("❌ Service: Lỗi trong timKiemPhuKienTheoMaSKU: " + e.getMessage());
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

            System.out.println("✅ Service: Đã convert PhuKienBanHangTaiQuayDTO thành SanPhamBanHangTaiQuayDTO cho SKU: " + phuKienDTO.getMaSKUPhuKien());
            return dto;
        } catch (Exception e) {
            System.err.println("❌ Service: Lỗi trong convertPhuKienToSanPhamDTO: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Chuyển đổi BienTheSanPham sang SanPhamBanHangTaiQuayDTO
    private SanPhamBanHangTaiQuayDTO convertToDTO(BienTheSanPham bienThe) {
        System.out.println("🔍 Service: convertToDTO cho SKU: " + (bienThe != null ? bienThe.getMaSKU() : "null"));
        if (bienThe == null) {
            System.out.println("❌ Service: bienThe is null");
            return null;
        }

        try {
            // Debug: Kiểm tra thuộc tính có được load không
            System.out.println("🔍 Service: Số lượng thuộc tính: " + (bienThe.getThuocTinhList() != null ? bienThe.getThuocTinhList().size() : "null"));
            if (bienThe.getThuocTinhList() != null && !bienThe.getThuocTinhList().isEmpty()) {
                System.out.println("🔍 Service: Thuộc tính đầu tiên: " + bienThe.getThuocTinhList().get(0).getTenThuocTinh() + " = " + bienThe.getThuocTinhList().get(0).getTenThuocTinhBienThe());
                // Debug: In tất cả thuộc tính
                for (int i = 0; i < bienThe.getThuocTinhList().size(); i++) {
                    var tt = bienThe.getThuocTinhList().get(i);
                    System.out.println("🔍 Service: Thuộc tính " + i + ": '" + tt.getTenThuocTinh() + "' = '" + tt.getTenThuocTinhBienThe() + "'");
                }
            } else {
                System.out.println("❌ Service: Không có thuộc tính nào được load!");
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

            System.out.println("🔍 Service: Chuỗi thuộc tính tạo được: '" + thuocTinhString + "'");

            long soLuong = imeiRepo.countByBienTheSanPham_MaSKU(bienThe.getMaSKU());
            System.out.println("🔍 Service: SKU " + bienThe.getMaSKU() + " có " + soLuong + " IMEI");

            SanPhamBanHangTaiQuayDTO dto = SanPhamBanHangTaiQuayDTO.builder()
                    .maSKU(bienThe.getMaSKU())
                    .tenSanPham(bienThe.getSanPham().getTenSanPham())
                    .gia(bienThe.getGia())
                    .thuocTinh(thuocTinhString)
                    .soLuong((int) soLuong)
                    .loai("Sản phẩm chính")
                    .trangThai(bienThe.getTrangThai())
                    .build();

            System.out.println("✅ Service: Đã convert DTO thành công cho SKU: " + dto.getMaSKU());
            return dto;
        } catch (Exception e) {
            System.err.println("❌ Service: Lỗi trong convertToDTO cho SKU " + bienThe.getMaSKU() + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Convert BienThePhuKien to PhuKienBanHangTaiQuayDTO
    private PhuKienBanHangTaiQuayDTO convertPhuKienToDTO(BienThePhuKien bienThePhuKien) {
        try {
            System.out.println("🔍 Service: Convert phụ kiện - SKU: " + bienThePhuKien.getMaSKUPhuKien());

            // Debug: Kiểm tra thuộc tính phụ kiện
            System.out.println("🔍 Service: Số lượng thuộc tính phụ kiện: " + (bienThePhuKien.getThuocTinhPhuKienList() != null ? bienThePhuKien.getThuocTinhPhuKienList().size() : "null"));
            if (bienThePhuKien.getThuocTinhPhuKienList() != null && !bienThePhuKien.getThuocTinhPhuKienList().isEmpty()) {
                System.out.println("🔍 Service: Thuộc tính phụ kiện đầu tiên: " + bienThePhuKien.getThuocTinhPhuKienList().get(0).getTenThuocTinh() + " = " + bienThePhuKien.getThuocTinhPhuKienList().get(0).getGiaTriThuocTinh());
            }

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

            System.out.println("🔍 Service: Chuỗi thuộc tính phụ kiện: '" + thuocTinhString + "'");

            // Lấy số lượng IMEI cho phụ kiện
            long soLuongImei = imeiRepo.countByBienThePhuKien_MaSKUPhuKien(bienThePhuKien.getMaSKUPhuKien());
            System.out.println("🔍 Service: Phụ kiện " + bienThePhuKien.getMaSKUPhuKien() + " có " + soLuongImei + " IMEI");

            PhuKienBanHangTaiQuayDTO dto = PhuKienBanHangTaiQuayDTO.builder()
                    .maSKUPhuKien(bienThePhuKien.getMaSKUPhuKien())
                    .tenPhuKien(bienThePhuKien.getPhuKien().getTenPhuKien())
                    .gia(bienThePhuKien.getGia())
                    .thuocTinh(thuocTinhString)
                    .soLuong((int) soLuongImei)
                    .loai("Phụ kiện")
                    .trangThai(bienThePhuKien.getTrangThai())
                    .build();

            System.out.println("✅ Service: Đã convert phụ kiện DTO thành công cho SKU: " + dto.getMaSKUPhuKien());
            return dto;
        } catch (Exception e) {
            System.err.println("❌ Service: Lỗi trong convertPhuKienToDTO cho SKU " + bienThePhuKien.getMaSKUPhuKien() + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
