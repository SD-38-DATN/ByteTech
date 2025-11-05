package com.example.datn.Service.banhangtaiquay;

import com.example.datn.DTO.banhangtaiquay.ImeiBanHangTaiQuayDTO;
import com.example.datn.Model.IMEI;
import com.example.datn.Repository.banhangtaiquay.IMEIBanHangTaiQuayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImeiBanHangTaiQuayService {

    @Autowired
    private IMEIBanHangTaiQuayRepository imeiRepo;

    // Lấy list IMEI theo mã SKU (có thể filter theo IMEI) - Hỗ trợ cả sản phẩm chính và phụ kiện (đã kiểm tra)
    public List<ImeiBanHangTaiQuayDTO> getImeiListBySkuMaBT(String skuMaBT, String imei) {

        List<Object[]> results = new ArrayList<>();

        try {
            //  THỬ TÌM SẢN PHẨM CHÍNH TRƯỚC
            if (imei == null || imei.trim().isEmpty()) {
                results = imeiRepo.findImeisBySkuMaBT(skuMaBT);
            } else {
                results = imeiRepo.findImeisBySkuMaBTAndImei(skuMaBT, imei);
            }
            //  NẾU KHÔNG TÌM THẤY SẢN PHẨM CHÍNH, THỬ TÌM PHỤ KIỆN
            if (results.isEmpty()) {
                if (imei == null || imei.trim().isEmpty()) {
                    results = imeiRepo.findImeisBySkuPhuKien(skuMaBT);
                } else {
                    // Tìm phụ kiện với filter IMEI
                    results = imeiRepo.findImeisBySkuPhuKienAndImei(skuMaBT, imei);
                }
            }
        } catch (Exception e) {
            results = new ArrayList<>();
        }

        List<ImeiBanHangTaiQuayDTO> dtos = results.stream().map(obj -> {
            ImeiBanHangTaiQuayDTO dto = new ImeiBanHangTaiQuayDTO();
            dto.setId((Integer) obj[0]);

            //  PHÂN BIỆT SẢN PHẨM CHÍNH VÀ PHỤ KIỆN
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
            return dto;
        }).toList();

        return dtos;
    }

    // Tìm sản phẩm theo IMEI chính xác (đã kiiem tra)
    public ImeiBanHangTaiQuayDTO timSanPhamTheoImei(String imei) {

        var imeiEntity = imeiRepo.findByImei(imei);
        if (imeiEntity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Không tìm thấy sản phẩm với IMEI: " + imei);
        }
        imeiEntity = imeiRepo.findByImeiDK(imei);

        if (imeiEntity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "IMEI này đã được bán, không thể thêm vào giỏ hàng.");
        }
        List<ImeiBanHangTaiQuayDTO> results = getImeiByExactImei(imei);

        if (results.isEmpty()) {
            return null;
        }

        ImeiBanHangTaiQuayDTO dto = results.get(0);

        ImeiBanHangTaiQuayDTO response = new ImeiBanHangTaiQuayDTO();
        response.setImei(dto.getImei());
        response.setTrangThai(dto.getTrangThai());

        //  Xử lý cả sản phẩm chính và phụ kiện
        if (dto.getMaSKU() != null) {
            // Sản phẩm chính
            response.setMaSKU(dto.getMaSKU());
            response.setTenSanPham(dto.getTenSanPham());
        } else if (dto.getMaSKUPhuKien() != null) {
            // Phụ kiện
            response.setMaSKUPhuKien(dto.getMaSKUPhuKien());
            response.setTenPhuKien(dto.getTenPhuKien());
        }

        // Set thông tin sản phẩm/phụ kiện
        if (dto.getSanPham() != null) {
            response.setSanPham(dto.getSanPham());

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
            response.setSanPham(sanPhamObject);
        }
        return response;
    }

    // Lấy IMEI khả dụng theo SKU
    public List<ImeiBanHangTaiQuayDTO> getImeiKhachDung(String maSKU) {
        List<ImeiBanHangTaiQuayDTO> result = getImeiListBySkuMaBT(maSKU, null);
        return result;
    }

    // Tìm kiếm IMEI chính xác trong toàn bộ hệ thống
    private List<ImeiBanHangTaiQuayDTO> getImeiByExactImei(String imei) {

        // Tìm IMEI entity để lấy thông tin đầy đủ
        var imeiEntity = imeiRepo.findByImei(imei);
        if (imeiEntity.isEmpty()) {
            return new ArrayList<>();
        }

        IMEI imeiObj = imeiEntity.get();
        ImeiBanHangTaiQuayDTO dto = new ImeiBanHangTaiQuayDTO();
        dto.setImei(imeiObj.getImei());
        dto.setTrangThai(imeiObj.getTrangThai());

        // Lấy thông tin sản phẩm/phụ kiện - xử lý lazy loading
        try {
            if (imeiObj.getBienTheSanPham() != null) {
                //  Xử lý sản phẩm chính
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

            } else if (imeiObj.getBienThePhuKien() != null) {
                //  Xử lý phụ kiện
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
            }
        } catch (Exception e) {
            // Xử lý lỗi lazy loading
            dto.setMaSKU("ERROR");
            dto.setTenSanPham("Lỗi khi tải thông tin sản phẩm");
        }
        return List.of(dto);
    }

    // tim theo  IMEI hoạc SKU (hỗ trợ cả sản phẩm và phụ kiện) hiên tại không dùng đến (hàm getImeiListBySkuMaBT đã có )
    public List<ImeiBanHangTaiQuayDTO> lookupImeisBySku(String sku) {
        try {
            // Tìm IMEI theo SKU sản phẩm chính
            List<ImeiBanHangTaiQuayDTO> sanPhamImeis = getImeiListBySkuMaBT(sku, null);

            if (!sanPhamImeis.isEmpty()) {
                return sanPhamImeis;
            }
            // Tìm IMEI theo SKU phụ kiện
            List<Object[]> phuKienResults = imeiRepo.findImeisBySkuPhuKien(sku);
            if (phuKienResults != null && !phuKienResults.isEmpty()) {
                List<ImeiBanHangTaiQuayDTO> phuKienImeis = phuKienResults.stream().map(obj -> {
                    ImeiBanHangTaiQuayDTO dto = new ImeiBanHangTaiQuayDTO();
                    dto.setId((Integer) obj[0]); // SỬA: obj[0] là id
                    dto.setMaSKUPhuKien((String) obj[1]); //  SỬA: obj[1] là maSKUPhuKien
                    dto.setImei((String) obj[2]); // ✅ SỬA: obj[2] là imei
                    dto.setTrangThai((Integer) obj[3]); //  SỬA: obj[3] là trangThai
                    return dto;
                }).toList();
                return phuKienImeis;
            }
            return new ArrayList<>();
        } catch (Exception e) {

            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ===== OPTIMIZED BATCH UPDATE METHODS =====

    // Cập nhật trạng thái một IMEI (tối ưu hóa)
    @Transactional
    public boolean capNhatTrangThaiImei(String imei, int status) {
        try {
            int updated = imeiRepo.capNhatTrangThaiMotImei(imei, status);
            if (updated > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật trạng thái nhiều IMEI cùng lúc (tối ưu hóa)
    @Transactional
    public boolean capNhatTrangThaiNhieuImei(List<String> imeiList, int status) {
        try {
            int updated = imeiRepo.capNhatTrangThaiNhieuImei(imeiList, status);
            return updated > 0;
        } catch (Exception e) {

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