package com.example.datn.Service.banhangtaiquay;

import com.example.datn.DTO.dtoBanHangTaiQuay.ImeiBanHangTaiQuayDTO;
import com.example.datn.Model.IMEI;
import com.example.datn.Repository.BanHangTaiQuay.IMEIBanHangTaiQuayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImeiBanHangTaiQuayService {

    @Autowired
    private IMEIBanHangTaiQuayRepository imeiRepo;

    // Lấy list IMEI theo mã SKU (có thể filter theo IMEI) - Hỗ trợ cả sản phẩm chính và phụ kiện
    public List<ImeiBanHangTaiQuayDTO> getImeiListBySkuMaBT(String skuMaBT, String imei) {
        System.out.println("🔍 Service: Tìm IMEI cho SKU: " + skuMaBT + ", IMEI filter: " + imei);
        List<Object[]> results = new ArrayList<>();

        try {
            // ✅ THỬ TÌM SẢN PHẨM CHÍNH TRƯỚC
            if (imei == null || imei.trim().isEmpty()) {
                System.out.println("🔍 Service: Gọi findImeisBySkuMaBT với SKU: " + skuMaBT);
                results = imeiRepo.findImeisBySkuMaBT(skuMaBT);
                System.out.println("🔍 Service: Kết quả sản phẩm chính từ DB: " + (results != null ? results.size() : "null") + " records");
            } else {
                System.out.println("🔍 Service: Gọi findImeisBySkuMaBTAndImei với SKU: " + skuMaBT + ", IMEI: " + imei);
                results = imeiRepo.findImeisBySkuMaBTAndImei(skuMaBT, imei);
                System.out.println("🔍 Service: Kết quả sản phẩm chính từ DB: " + (results != null ? results.size() : "null") + " records");
            }

            // ✅ NẾU KHÔNG TÌM THẤY SẢN PHẨM CHÍNH, THỬ TÌM PHỤ KIỆN
            if (results.isEmpty()) {
                System.out.println("🔍 Service: Không tìm thấy sản phẩm chính, thử tìm phụ kiện...");
                if (imei == null || imei.trim().isEmpty()) {
                    results = imeiRepo.findImeisBySkuPhuKien(skuMaBT);
                    System.out.println("🔍 Service: Kết quả phụ kiện từ DB: " + (results != null ? results.size() : "null") + " records");
                } else {
                    // Tìm phụ kiện với filter IMEI
                    System.out.println("🔍 Service: Tìm phụ kiện với filter IMEI: " + imei);
                    results = imeiRepo.findImeisBySkuPhuKienAndImei(skuMaBT, imei);
                    System.out.println("🔍 Service: Kết quả phụ kiện với filter từ DB: " + (results != null ? results.size() : "null") + " records");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Service: Lỗi khi tìm IMEI: " + e.getMessage());
            results = new ArrayList<>();
        }

        List<ImeiBanHangTaiQuayDTO> dtos = results.stream().map(obj -> {
            ImeiBanHangTaiQuayDTO dto = new ImeiBanHangTaiQuayDTO();
            dto.setId((Integer) obj[0]);

            // ✅ PHÂN BIỆT SẢN PHẨM CHÍNH VÀ PHỤ KIỆN
            String skuValue = (String) obj[1];
            if (skuValue.equals(skuMaBT)) {
                // Nếu SKU khớp với input, có thể là sản phẩm chính hoặc phụ kiện
                // Cần kiểm tra thêm để phân biệt
                dto.setMaSKU(skuValue);
                dto.setMaSKUPhuKien(skuValue);
            } else {
                // Fallback
                dto.setMaSKU(skuValue);
                dto.setMaSKUPhuKien(skuValue);
            }

            dto.setImei((String) obj[2]);
            dto.setTrangThai((Integer) obj[3]);
            System.out.println("🔍 Service: Tạo DTO - ID: " + dto.getId() + ", SKU: " + dto.getMaSKU() + ", IMEI: " + dto.getImei() + ", Status: " + dto.getTrangThai());
            return dto;
        }).toList();

        System.out.println("🔍 Service: Trả về " + dtos.size() + " IMEI DTOs");
        return dtos;
    }

    // Tìm sản phẩm theo IMEI chính xác
    public ImeiBanHangTaiQuayDTO timSanPhamTheoImei(String imei) {
        System.out.println("🔍 Service: timSanPhamTheoImei được gọi với IMEI: " + imei);
        List<ImeiBanHangTaiQuayDTO> results = getImeiByExactImei(imei);
        System.out.println("🔍 Service: getImeiByExactImei trả về " + (results != null ? results.size() : "null") + " results");

        if (results.isEmpty()) {
            System.out.println("❌ Service: Không tìm thấy IMEI: " + imei);
            return null;
        }

        ImeiBanHangTaiQuayDTO dto = results.get(0);
        System.out.println("🔍 Service: DTO - SKU: " + dto.getMaSKU() + ", IMEI: " + dto.getImei() + ", TenSP: " + dto.getTenSanPham());

        ImeiBanHangTaiQuayDTO response = new ImeiBanHangTaiQuayDTO();
        response.setImei(dto.getImei());
        response.setTrangThai(dto.getTrangThai());

        // ✅ Xử lý cả sản phẩm chính và phụ kiện
        if (dto.getMaSKU() != null) {
            // Sản phẩm chính
            response.setMaSKU(dto.getMaSKU());
            response.setTenSanPham(dto.getTenSanPham());
            System.out.println("🔍 Service: Xử lý sản phẩm chính - SKU: " + dto.getMaSKU());
        } else if (dto.getMaSKUPhuKien() != null) {
            // Phụ kiện
            response.setMaSKUPhuKien(dto.getMaSKUPhuKien());
            response.setTenPhuKien(dto.getTenPhuKien());
            System.out.println("🔍 Service: Xử lý phụ kiện - SKU: " + dto.getMaSKUPhuKien());
        }

        // Set thông tin sản phẩm/phụ kiện
        if (dto.getSanPham() != null) {
            response.setSanPham(dto.getSanPham());
            System.out.println("🔍 Service: Sử dụng sanPham từ DTO");
        } else {
            // Tạo object sản phẩm từ thông tin IMEI với dữ liệu đầy đủ
            var sanPhamObject = new Object() {
                public String maSKU = dto.getMaSKU();
                public String maSKUPhuKien = dto.getMaSKUPhuKien();
                public String tenSanPham = dto.getTenSanPham();
                public String tenPhuKien = dto.getTenPhuKien();
                public java.math.BigDecimal gia = java.math.BigDecimal.ZERO;
                public String thuocTinh = "";
                public Integer soLuong = 1;
            };

            // ✅ LẤY THÔNG TIN ĐẦY ĐỦ TỪ DATABASE
            try {
                if (dto.getMaSKU() != null) {
                    // Sản phẩm chính - lấy thông tin từ BienTheSanPham
                    System.out.println("🔍 Service: Lấy thông tin sản phẩm chính từ DB cho SKU: " + dto.getMaSKU());
                    // TODO: Implement logic to get full product info
                } else if (dto.getMaSKUPhuKien() != null) {
                    // Phụ kiện - lấy thông tin từ BienThePhuKien
                    System.out.println("🔍 Service: Lấy thông tin phụ kiện từ DB cho SKU: " + dto.getMaSKUPhuKien());
                    // TODO: Implement logic to get full accessory info
                }
            } catch (Exception e) {
                System.err.println("❌ Service: Lỗi khi lấy thông tin đầy đủ: " + e.getMessage());
            }

            response.setSanPham(sanPhamObject);
            System.out.println("🔍 Service: Tạo sanPham object từ DTO với thông tin cơ bản");
        }

        System.out.println("🔍 Service: Trả về ImeiResponseDTO với IMEI: " + response.getImei());
        System.out.println("🔍 Service: Chi tiết response - maSKU: " + response.getMaSKU() +
                ", maSKUPhuKien: " + response.getMaSKUPhuKien() +
                ", tenSanPham: " + response.getTenSanPham() +
                ", tenPhuKien: " + response.getTenPhuKien() +
                ", gia: " + (response.getSanPham() != null ? "có object" : "null"));
        return response;
    }

    // Lấy IMEI khả dụng theo SKU
    public List<ImeiBanHangTaiQuayDTO> getImeiKhachDung(String maSKU) {
        System.out.println("🔍 Service: getImeiKhachDung được gọi với SKU: " + maSKU);
        List<ImeiBanHangTaiQuayDTO> result = getImeiListBySkuMaBT(maSKU, null);
        System.out.println("🔍 Service: getImeiKhachDung trả về " + (result != null ? result.size() : "null") + " items");
        return result;
    }

    // Tìm kiếm IMEI chính xác trong toàn bộ hệ thống
    private List<ImeiBanHangTaiQuayDTO> getImeiByExactImei(String imei) {
        System.out.println("🔍 Service: Tìm kiếm IMEI: " + imei);
        // Tìm IMEI entity để lấy thông tin đầy đủ
        var imeiEntity = imeiRepo.findByImei(imei);
        if (imeiEntity.isEmpty()) {
            System.out.println("❌ Service: Không tìm thấy IMEI: " + imei);
            return new ArrayList<>();
        }
        System.out.println("✅ Service: Tìm thấy IMEI entity");

        IMEI imeiObj = imeiEntity.get();
        ImeiBanHangTaiQuayDTO dto = new ImeiBanHangTaiQuayDTO();
        dto.setImei(imeiObj.getImei());
        dto.setTrangThai(imeiObj.getTrangThai());

        // Lấy thông tin sản phẩm/phụ kiện - xử lý lazy loading
        try {
            if (imeiObj.getBienTheSanPham() != null) {
                // ✅ Xử lý sản phẩm chính
                var bienThe = imeiObj.getBienTheSanPham();
                var sanPham = bienThe.getSanPham();

                dto.setMaSKU(bienThe.getMaSKU());
                dto.setTenSanPham(sanPham.getTenSanPham());

                // Tạo object sản phẩm đầy đủ
                final String thuocTinhString;
                if (bienThe.getThuocTinhList() != null && !bienThe.getThuocTinhList().isEmpty()) {
                    thuocTinhString = bienThe.getThuocTinhList().stream()
                            .map(tt -> tt.getTenThuocTinh() + ": " + tt.getTenThuocTinhBienThe())
                            .collect(java.util.stream.Collectors.joining(", "));
                } else {
                    thuocTinhString = "";
                }

                var sanPhamDTO = new Object() {
                    public String maSKU = bienThe.getMaSKU();
                    public String tenSanPham = sanPham.getTenSanPham();
                    public java.math.BigDecimal gia = bienThe.getGia();
                    public String thuocTinh = thuocTinhString;
                    public Integer soLuong = 1;
                };

                dto.setSanPham(sanPhamDTO);
                System.out.println("✅ Service: Xử lý sản phẩm chính - SKU: " + bienThe.getMaSKU());
                System.out.println("🔍 Service: Chi tiết sản phẩm - Gia: " + bienThe.getGia() + ", ThuocTinh: " + thuocTinhString);
            } else if (imeiObj.getBienThePhuKien() != null) {
                // ✅ Xử lý phụ kiện
                var bienThePhuKien = imeiObj.getBienThePhuKien();
                var phuKien = bienThePhuKien.getPhuKien();

                dto.setMaSKUPhuKien(bienThePhuKien.getMaSKUPhuKien());
                dto.setTenPhuKien(phuKien.getTenPhuKien());

                // Tạo object phụ kiện đầy đủ
                final String thuocTinhString;
                if (bienThePhuKien.getThuocTinhPhuKienList() != null && !bienThePhuKien.getThuocTinhPhuKienList().isEmpty()) {
                    thuocTinhString = bienThePhuKien.getThuocTinhPhuKienList().stream()
                            .map(tt -> tt.getTenThuocTinh() + ": " + tt.getGiaTriThuocTinh())
                            .collect(java.util.stream.Collectors.joining(", "));
                } else {
                    thuocTinhString = "";
                }

                var phuKienDTO = new Object() {
                    public String maSKU = null; // Sản phẩm chính = null
                    public String maSKUPhuKien = bienThePhuKien.getMaSKUPhuKien();
                    public String tenSanPham = null; // Sản phẩm chính = null
                    public String tenPhuKien = phuKien.getTenPhuKien();
                    public java.math.BigDecimal gia = bienThePhuKien.getGia();
                    public String thuocTinh = thuocTinhString;
                    public Integer soLuong = 1;
                };

                dto.setSanPham(phuKienDTO);
                System.out.println("✅ Service: Xử lý phụ kiện - SKU: " + bienThePhuKien.getMaSKUPhuKien());
            } else {
                // Xử lý trường hợp không có sản phẩm/phụ kiện
                dto.setMaSKU("UNKNOWN");
                dto.setTenSanPham("Sản phẩm không xác định");

                var sanPhamDTO = new Object() {
                    public String maSKU = "UNKNOWN";
                    public String tenSanPham = "Sản phẩm không xác định";
                    public java.math.BigDecimal gia = java.math.BigDecimal.ZERO;
                    public String thuocTinh = "";
                    public Integer soLuong = 0;
                };

                dto.setSanPham(sanPhamDTO);
                System.out.println("❌ Service: Không tìm thấy sản phẩm/phụ kiện cho IMEI");
            }
        } catch (Exception e) {
            // Xử lý lỗi lazy loading
            System.err.println("Lỗi khi load relationship: " + e.getMessage());
            dto.setMaSKU("ERROR");
            dto.setTenSanPham("Lỗi khi tải thông tin sản phẩm");
        }

        return List.of(dto);
    }

    // Lookup IMEI theo SKU (hỗ trợ cả sản phẩm và phụ kiện)
    public List<ImeiBanHangTaiQuayDTO> lookupImeisBySku(String sku) {
        System.out.println("🔍 Service: lookupImeisBySku được gọi với SKU: " + sku);

        try {
            // Tìm IMEI theo SKU sản phẩm chính
            List<ImeiBanHangTaiQuayDTO> sanPhamImeis = getImeiListBySkuMaBT(sku, null);
            System.out.println("🔍 Service: Tìm thấy " + sanPhamImeis.size() + " IMEI cho sản phẩm chính");

            if (!sanPhamImeis.isEmpty()) {
                return sanPhamImeis;
            }

            // Nếu không tìm thấy sản phẩm chính, tìm phụ kiện
            System.out.println("🔍 Service: Không tìm thấy sản phẩm chính, tìm kiếm phụ kiện");

            // Tìm IMEI theo SKU phụ kiện
            List<Object[]> phuKienResults = imeiRepo.findImeisBySkuPhuKien(sku);
            System.out.println("🔍 Service: Tìm thấy " + (phuKienResults != null ? phuKienResults.size() : "null") + " IMEI cho phụ kiện");

            if (phuKienResults != null && !phuKienResults.isEmpty()) {
                List<ImeiBanHangTaiQuayDTO> phuKienImeis = phuKienResults.stream().map(obj -> {
                    ImeiBanHangTaiQuayDTO dto = new ImeiBanHangTaiQuayDTO();
                    dto.setId((Integer) obj[0]); // ✅ SỬA: obj[0] là id
                    dto.setMaSKUPhuKien((String) obj[1]); // ✅ SỬA: obj[1] là maSKUPhuKien
                    dto.setImei((String) obj[2]); // ✅ SỬA: obj[2] là imei
                    dto.setTrangThai((Integer) obj[3]); // ✅ SỬA: obj[3] là trangThai
                    System.out.println("🔍 Service: Tạo DTO phụ kiện - ID: " + dto.getId() + ", SKU: " + dto.getMaSKUPhuKien() + ", IMEI: " + dto.getImei() + ", Status: " + dto.getTrangThai());
                    return dto;
                }).toList();

                System.out.println("✅ Service: Trả về " + phuKienImeis.size() + " IMEI phụ kiện");
                return phuKienImeis;
            }

            System.out.println("❌ Service: Không tìm thấy IMEI cho SKU: " + sku);
            return new ArrayList<>();

        } catch (Exception e) {
            System.err.println("❌ Service: Lỗi trong lookupImeisBySku: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ===== OPTIMIZED BATCH UPDATE METHODS =====

    // Cập nhật trạng thái một IMEI (tối ưu hóa)
    @Transactional
    public boolean capNhatTrangThaiImei(String imei, int status) {
        System.out.println("🔄 Service: Cập nhật trạng thái IMEI " + imei + " → " + status);
        try {
            int updated = imeiRepo.capNhatTrangThaiMotImei(imei, status);
            if (updated > 0) {
                System.out.println("✅ Service: Đã cập nhật IMEI " + imei + " → trạng thái " + status);
                return true;
            } else {
                System.out.println("❌ Service: Không tìm thấy IMEI: " + imei);
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Service: Lỗi cập nhật trạng thái IMEI: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật trạng thái nhiều IMEI cùng lúc (tối ưu hóa)
    @Transactional
    public boolean capNhatTrangThaiNhieuImei(List<String> imeiList, int status) {
        System.out.println("🔄 Service: Cập nhật trạng thái " + imeiList.size() + " IMEI → " + status);
        try {
            int updated = imeiRepo.capNhatTrangThaiNhieuImei(imeiList, status);
            System.out.println("✅ Service: Đã cập nhật " + updated + "/" + imeiList.size() + " IMEI → trạng thái " + status);
            return updated > 0;
        } catch (Exception e) {
            System.err.println("❌ Service: Lỗi cập nhật trạng thái IMEI hàng loạt: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ===== LEGACY METHODS (Backward Compatibility) =====

    // Cập nhật trạng thái IMEI thành 5 (đã thêm vào giỏ hàng)
    @Transactional
    public boolean capNhatTrangThaiImeiThanh5(String imei) {
        return capNhatTrangThaiImei(imei, 5);
    }

    // Cập nhật trạng thái nhiều IMEI thành 5
    @Transactional
    public boolean capNhatTrangThaiNhieuImeiThanh5(List<String> imeiList) {
        return capNhatTrangThaiNhieuImei(imeiList, 5);
    }

    // Cập nhật trạng thái IMEI về 1 (trạng thái ban đầu)
    @Transactional
    public boolean capNhatTrangThaiImeiVe1(String imei) {
        return capNhatTrangThaiImei(imei, 1);
    }

    // Cập nhật trạng thái nhiều IMEI về 1
    @Transactional
    public boolean capNhatTrangThaiNhieuImeiVe1(List<String> imeiList) {
        return capNhatTrangThaiNhieuImei(imeiList, 1);
    }
}