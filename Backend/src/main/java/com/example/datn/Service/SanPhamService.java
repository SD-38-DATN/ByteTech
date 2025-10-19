package com.example.datn.Service;

import com.example.datn.DTO.TrangMuaHang.LocThongSo.ThongSoKyThuatDTO;
import com.example.datn.DTO.TrangMuaHang.LocThuocTinhDTO;
import com.example.datn.DTO.TrangMuaHang.SanPhamChiTietDTO;
import com.example.datn.DTO.TrangMuaHang.TrangChuSanPham;
import com.example.datn.Model.SanPham;
import com.example.datn.Repository.BienTheSanPhamRepository;
import com.example.datn.Repository.LoaiThongSoRepository;
import com.example.datn.Repository.SanPhamRepository;
import com.example.datn.Repository.ThuocTinhRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SanPhamService {
    private final SanPhamRepository sanPhamRepo;


    private final BienTheSanPhamRepository bienTheRepo;
    private final ThuocTinhRepository thuocTinhRepo;

//    public List<TrangChuSanPham> getAllSanPham() {
//        return sanPhamRepo.findAll().stream().map(sp -> {
//            TrangChuSanPham dto = new TrangChuSanPham();
//            dto.setMaSanPham(sp.getMaSanPham());
//            dto.setTenSanPham(sp.getTenSanPham());
//            dto.setMoTa(sp.getMoTa());
//            dto.setSoLuong(sp.getSoLuong());
//            dto.setGia(sp.getGia());
//            if (sp.getDanhMuc() != null) {
//                dto.setTenDanhMuc(sp.getDanhMuc().getTenDanhMuc());
//            }
//
//            return dto;
//        }).collect(Collectors.toList());
//    }


//    public List<TrangChuSanPham> getAllSanPham() {
//        List<Object[]> rows = sanPhamRepo.findAllWithPromoCheck();
//        List<TrangChuSanPham> result = new ArrayList<>();
//
//        for (Object[] r : rows) {
//            TrangChuSanPham dto = new TrangChuSanPham();
//            dto.setMaSanPham(((Number) r[0]).intValue());
//            dto.setTenSanPham((String) r[1]);
//            dto.setMoTa((String) r[2]);
//            dto.setSoLuong(r[3] != null ? ((Number) r[3]).intValue() : null);
//            dto.setMaDanhMuc(r[4] != null ? ((Number) r[4]).intValue() : null);
//            dto.setTenDanhMuc((String) r[5]);
//            dto.setGia((BigDecimal) r[6]);
//
//            // nếu có khuyến mãi thì mấy field dưới != null
//            dto.setMaSKU((String) r[7]);
//            dto.setGiaKhongKhuyenMaiBienThe((BigDecimal) r[8]);
//            dto.setGiaBienThe((BigDecimal) r[9]);
//            dto.setGiaTriGiamKhuyenMai((BigDecimal) r[10]);
//            dto.setLoaiGiam(r[11] != null ? ((Number) r[11]).intValue() : null);
//
//            result.add(dto);
//        }
//        return result;
//    }
public List<TrangChuSanPham> getSanPhamByTen(String tenSanPham) {
    // Gọi phương thức repository mới và truyền vào tenSanPham
    List<Object[]> rows = sanPhamRepo.findWithPromoCheckByTenSanPham(tenSanPham);
    List<TrangChuSanPham> result = new ArrayList<>();

    for (Object[] r : rows) {
//        TrangChuSanPham dto = new TrangChuSanPham();
//        dto.setMaSanPham(((Number) r[0]).intValue());
//        dto.setTenSanPham((String) r[1]);
//        dto.setMoTa((String) r[2]);
//        dto.setSoLuong(r[3] != null ? ((Number) r[3]).intValue() : null);
//        dto.setMaDanhMuc(r[4] != null ? ((Number) r[4]).intValue() : null);
//        dto.setTenDanhMuc((String) r[5]);
//
//        // Thông tin của biến thể có giá thấp nhất
//        dto.setMaSKU((String) r[6]);
//        dto.setGiaKhongKhuyenMaiBienThe((BigDecimal) r[7]);
//        dto.setGiaBienThe((BigDecimal) r[8]);  // giá hiển thị = giá sau KM của biến thể min
//        dto.setGiaTriGiamKhuyenMai((BigDecimal) r[9]);
//        dto.setLoaiGiam(r[10] != null ? ((Number) r[10]).intValue() : null);
        TrangChuSanPham dto = new TrangChuSanPham();
        dto.setMaSanPham(((Number) r[0]).intValue());
        dto.setTenSanPham((String) r[1]);
        dto.setMoTa((String) r[2]);

        // ===== DÒNG MỚI THÊM VÀO =====
        dto.setThuongHieu((String) r[3]);

        // ===== CẬP NHẬT LẠI CHỈ SỐ (INDEX) CHO CÁC DÒNG SAU (tăng lên 1) =====
        dto.setSoLuong(r[4] != null ? ((Number) r[4]).intValue() : null);
        dto.setMaDanhMuc(r[5] != null ? ((Number) r[5]).intValue() : null);
        dto.setTenDanhMuc((String) r[6]);

        // biến thể min
        dto.setMaSKU((String) r[7]);
        dto.setGiaKhongKhuyenMaiBienThe((BigDecimal) r[8]);
        dto.setGiaBienThe((BigDecimal) r[9]);  // giá hiển thị = giá sau KM của biến thể min
        dto.setGiaTriGiamKhuyenMai((BigDecimal) r[10]);
        dto.setLoaiGiam(r[11] != null ? ((Number) r[11]).intValue() : null);


        // Gán giá hiển thị chung cho sản phẩm
        dto.setGia(dto.getGiaBienThe());

        result.add(dto);
    }
    return result;
}


//    public List<TrangChuSanPham> getSanPhamByDanhMuc(Integer maDanhMuc) {
//        // Gọi phương thức repository mới và truyền vào maDanhMuc
//        List<Object[]> rows = sanPhamRepo.findWithPromoCheckByDanhMuc(maDanhMuc);
//        List<TrangChuSanPham> result = new ArrayList<>();
//
//        for (Object[] r : rows) {
//
//
//
//            TrangChuSanPham dto = new TrangChuSanPham();
//            dto.setMaSanPham(((Number) r[0]).intValue());
//            dto.setTenSanPham((String) r[1]);
//            dto.setMoTa((String) r[2]);
//
//            // ===== DÒNG MỚI THÊM VÀO =====
//            dto.setThuongHieu((String) r[3]);
//
//            // ===== CẬP NHẬT LẠI CHỈ SỐ (INDEX) CHO CÁC DÒNG SAU (tăng lên 1) =====
//            dto.setSoLuong(r[4] != null ? ((Number) r[4]).intValue() : null);
//            dto.setMaDanhMuc(r[5] != null ? ((Number) r[5]).intValue() : null);
//            dto.setTenDanhMuc((String) r[6]);
//
//            // biến thể min
//            dto.setMaSKU((String) r[7]);
//            dto.setGiaKhongKhuyenMaiBienThe((BigDecimal) r[8]);
//            dto.setGiaBienThe((BigDecimal) r[9]);  // giá hiển thị = giá sau KM của biến thể min
//            dto.setGiaTriGiamKhuyenMai((BigDecimal) r[10]);
//            dto.setLoaiGiam(r[11] != null ? ((Number) r[11]).intValue() : null);
//
//            // Gán giá hiển thị chung cho sản phẩm
//            dto.setGia(dto.getGiaBienThe());
//
//            result.add(dto);
//        }
//        return result;
//    }



    // File: SanPhamService.java

//    public List<TrangChuSanPham> getSanPhamByDanhMuc(Integer maDanhMuc) {
//        List<Object[]> rows = sanPhamRepo.findWithPromoCheckByDanhMuc(maDanhMuc);
//        Map<Integer, TrangChuSanPham> productMap = new LinkedHashMap<>();
//
//        for (Object[] r : rows) {
//            Integer currentMaSanPham = ((Number) r[0]).intValue();
//            TrangChuSanPham dto = productMap.get(currentMaSanPham);
//
//            if (dto == null) {
//                dto = new TrangChuSanPham();
//                dto.setMaSanPham(currentMaSanPham);
//                dto.setTenSanPham((String) r[1]);
//                dto.setMoTa((String) r[2]);
//                dto.setThuongHieu((String) r[3]);
//                dto.setSoLuong(r[4] != null ? ((Number) r[4]).intValue() : null);
//                dto.setMaDanhMuc(r[5] != null ? ((Number) r[5]).intValue() : null);
//                dto.setTenDanhMuc((String) r[6]);
//                dto.setMaSKU((String) r[7]);
//                dto.setGiaKhongKhuyenMaiBienThe((BigDecimal) r[8]);
//                dto.setGiaBienThe((BigDecimal) r[9]);
//                dto.setGiaTriGiamKhuyenMai((BigDecimal) r[10]);
//                dto.setLoaiGiam(r[11] != null ? ((Number) r[11]).intValue() : null);
//                dto.setGia(dto.getGiaBienThe());
//                dto.setChiTietThuocTinh(new ArrayList<>());
//                productMap.put(currentMaSanPham, dto);
//            }
//
//            String tenThuocTinh = (String) r[12];
//            // ✅ SỬA TÊN BIẾN CHO CHÍNH XÁC VỚI CSDL
//            String tenThuocTinhBienThe = (String) r[13];
//
//            if (tenThuocTinh != null && tenThuocTinhBienThe != null) {
//                // ✅ SỬ DỤNG BIẾN ĐÃ ĐỔI TÊN
//                dto.getChiTietThuocTinh().add(new LocThuocTinhDTO(tenThuocTinh, tenThuocTinhBienThe));
//            }
//        }
//        return new ArrayList<>(productMap.values());
//    }




    @PersistenceContext
    private EntityManager entityManager;

    // ✅ 1. Đơn giản hóa lại hàm, chỉ cần nhận vào maDanhMuc
    public List<TrangChuSanPham> getSanPhamByDanhMuc(Integer maDanhMuc) {

        // ✅ 2. Bỏ code xử lý "specs", thay bằng một câu query tĩnh đơn giản
        String sqlQuery = """
        WITH CheapestVariantDetails AS (
            SELECT
                bt.maSanPham, bt.maSKU, bt.giaKhongKhuyenMai, km.giaTriGiam, km.loaiGiam,
                CASE WHEN km.maKhuyenMai IS NOT NULL AND km.trangThai = 1 AND CONVERT(date, GETDATE()) BETWEEN km.ngayBatDau AND km.ngayKetThuc THEN CASE WHEN km.loaiGiam = 1 THEN bt.giaKhongKhuyenMai * (1.0 - IIF(km.giaTriGiam > 1, km.giaTriGiam/100.0, km.giaTriGiam)) ELSE bt.giaKhongKhuyenMai - ISNULL(km.giaTriGiam, 0) END ELSE bt.giaKhongKhuyenMai END AS giaSauKM,
                ROW_NUMBER() OVER(PARTITION BY bt.maSanPham ORDER BY CASE WHEN km.maKhuyenMai IS NOT NULL AND km.trangThai = 1 AND CONVERT(date, GETDATE()) BETWEEN km.ngayBatDau AND km.ngayKetThuc THEN CASE WHEN km.loaiGiam = 1 THEN bt.giaKhongKhuyenMai * (1.0 - IIF(km.giaTriGiam > 1, km.giaTriGiam/100.0, km.giaTriGiam)) ELSE bt.giaKhongKhuyenMai - ISNULL(km.giaTriGiam, 0) END ELSE bt.giaKhongKhuyenMai END ASC, bt.maSKU ASC) as rn
            FROM BienTheSanPham bt LEFT JOIN KhuyenMai km ON bt.maKhuyenMai = km.maKhuyenMai
        ),
        AllProductAttributes AS (
            SELECT DISTINCT bt.maSanPham, tt.tenThuocTinh, tt.tenThuocTinhBienThe
            FROM BienTheSanPham bt JOIN ThuocTinh tt ON bt.maSKU = tt.maSKU
        )
        SELECT
            sp.maSanPham, sp.tenSanPham, sp.moTa, sp.thuongHieu, sp.soLuong,
            dm.maDanhMuc, dm.tenDanhMuc,
            cvd.maSKU, cvd.giaKhongKhuyenMai, cvd.giaSauKM,
            cvd.giaTriGiam, cvd.loaiGiam,
            apa.tenThuocTinh, apa.tenThuocTinhBienThe,
            lts.tenLoaiThongSo, tsk.giaTriThongSo
        FROM SanPham sp
        JOIN DanhMuc dm ON sp.maDanhMuc = dm.maDanhMuc
        LEFT JOIN ThongSoKyThuat tsk ON sp.maSanPham = tsk.maSanPham
        LEFT JOIN LoaiThongSo lts ON tsk.loaiThongSoId = lts.loaiThongSoId
        LEFT JOIN CheapestVariantDetails cvd ON sp.maSanPham = cvd.maSanPham AND cvd.rn = 1 
        LEFT JOIN AllProductAttributes apa ON sp.maSanPham = apa.maSanPham 
        WHERE sp.maDanhMuc = :maDanhMuc
    """;

        Query query = entityManager.createNativeQuery(sqlQuery);
        query.setParameter("maDanhMuc", maDanhMuc);
        List<Object[]> rows = query.getResultList();

        // ✅ 3. Phần mapping dữ liệu này đã đúng và được giữ nguyên
        Map<Integer, TrangChuSanPham> productMap = new LinkedHashMap<>();
        for (Object[] r : rows) {
            Integer currentMaSanPham = ((Number) r[0]).intValue();
            TrangChuSanPham dto = productMap.get(currentMaSanPham);

            if (dto == null) {
                dto = new TrangChuSanPham();
                dto.setMaSanPham(currentMaSanPham);
                dto.setTenSanPham((String) r[1]);
                dto.setMoTa((String) r[2]);
                dto.setThuongHieu((String) r[3]);
                dto.setSoLuong(r[4] != null ? ((Number) r[4]).intValue() : null);
                dto.setMaDanhMuc(r[5] != null ? ((Number) r[5]).intValue() : null);
                dto.setTenDanhMuc((String) r[6]);
                dto.setMaSKU((String) r[7]);
                dto.setGiaKhongKhuyenMaiBienThe((BigDecimal) r[8]);
                dto.setGiaBienThe((BigDecimal) r[9]);
                dto.setGiaTriGiamKhuyenMai((BigDecimal) r[10]);
                dto.setLoaiGiam(r[11] != null ? ((Number) r[11]).intValue() : null);
                dto.setGia(dto.getGiaBienThe());
                dto.setChiTietThuocTinh(new ArrayList<>());
                dto.setChiTietThongSo(new ArrayList<>());
                productMap.put(currentMaSanPham, dto);
            }

            String tenThuocTinh = (String) r[12];
            String tenThuocTinhBienThe = (String) r[13];
            if (tenThuocTinh != null && tenThuocTinhBienThe != null) {
                LocThuocTinhDTO thuocTinhDTO = new LocThuocTinhDTO(tenThuocTinh, tenThuocTinhBienThe);
                if (!dto.getChiTietThuocTinh().contains(thuocTinhDTO)) {
                    dto.getChiTietThuocTinh().add(thuocTinhDTO);
                }
            }

            String tenLoaiThongSo = (String) r[14];
            String giaTriThongSo = (String) r[15];
            if (tenLoaiThongSo != null && giaTriThongSo != null) {
                ThongSoKyThuatDTO thongSoDTO = new ThongSoKyThuatDTO(tenLoaiThongSo, giaTriThongSo);
                if (!dto.getChiTietThongSo().contains(thongSoDTO)) {
                    dto.getChiTietThongSo().add(thongSoDTO);
                }
            }
        }
        return new ArrayList<>(productMap.values());
    }



//    public List<TrangChuSanPham> getAllSanPham() {
//        List<Object[]> rows = sanPhamRepo.findAllWithPromoCheck();
//        List<TrangChuSanPham> result = new ArrayList<>();
//
//        for (Object[] r : rows) {
//            TrangChuSanPham dto = new TrangChuSanPham();
//            dto.setMaSanPham(((Number) r[0]).intValue());
//            dto.setTenSanPham((String) r[1]);
//            dto.setMoTa((String) r[2]);
//            dto.setSoLuong(r[3] != null ? ((Number) r[3]).intValue() : null);
//            dto.setMaDanhMuc(r[4] != null ? ((Number) r[4]).intValue() : null);
//            dto.setTenDanhMuc((String) r[5]);
//
//            // biến thể min
//            dto.setMaSKU((String) r[6]);
//            dto.setGiaKhongKhuyenMaiBienThe((BigDecimal) r[7]);
//            dto.setGiaBienThe((BigDecimal) r[8]);  // giá hiển thị = giá sau KM của biến thể min
//            dto.setGiaTriGiamKhuyenMai((BigDecimal) r[9]);
//            dto.setLoaiGiam(r[10] != null ? ((Number) r[10]).intValue() : null);
//
//            // Nếu bạn vẫn muốn giữ field "gia" tổng quát
//            dto.setGia(dto.getGiaBienThe());
//
//            result.add(dto);
//        }
//        return result;
//    }
public List<TrangChuSanPham> getAllSanPham() {
    List<Object[]> rows = sanPhamRepo.findAllWithPromoCheck();
    List<TrangChuSanPham> result = new ArrayList<>();

    for (Object[] r : rows) {
        TrangChuSanPham dto = new TrangChuSanPham();
        dto.setMaSanPham(((Number) r[0]).intValue());
        dto.setTenSanPham((String) r[1]);
        dto.setMoTa((String) r[2]);

        // ===== DÒNG MỚI THÊM VÀO =====
        dto.setThuongHieu((String) r[3]);

        // ===== CẬP NHẬT LẠI CHỈ SỐ (INDEX) CHO CÁC DÒNG SAU (tăng lên 1) =====
        dto.setSoLuong(r[4] != null ? ((Number) r[4]).intValue() : null);
        dto.setMaDanhMuc(r[5] != null ? ((Number) r[5]).intValue() : null);
        dto.setTenDanhMuc((String) r[6]);

        // biến thể min
        dto.setMaSKU((String) r[7]);
        dto.setGiaKhongKhuyenMaiBienThe((BigDecimal) r[8]);
        dto.setGiaBienThe((BigDecimal) r[9]);  // giá hiển thị = giá sau KM của biến thể min
        dto.setGiaTriGiamKhuyenMai((BigDecimal) r[10]);
        dto.setLoaiGiam(r[11] != null ? ((Number) r[11]).intValue() : null);

        // Nếu bạn vẫn muốn giữ field "gia" tổng quát
        dto.setGia(dto.getGiaBienThe());

        result.add(dto);
    }
    return result;
}




    public List<TrangChuSanPham> getAllSanPham1(Long danhMucId, BigDecimal minPrice, BigDecimal maxPrice) {
        List<Object[]> rows = sanPhamRepo.filterSanPham(danhMucId, minPrice, maxPrice);
        List<TrangChuSanPham> result = new ArrayList<>();

        for (Object[] r : rows) {
            TrangChuSanPham dto = new TrangChuSanPham();
            dto.setMaSanPham(((Number) r[0]).intValue());
            dto.setTenSanPham((String) r[1]);
            dto.setMoTa((String) r[2]);
            dto.setSoLuong(r[3] != null ? ((Number) r[3]).intValue() : null);
            dto.setMaDanhMuc(r[4] != null ? ((Number) r[4]).intValue() : null);
            dto.setTenDanhMuc((String) r[5]);

            // biến thể min
            dto.setMaSKU((String) r[6]);
            dto.setGiaKhongKhuyenMaiBienThe((BigDecimal) r[7]);
            dto.setGiaBienThe((BigDecimal) r[8]);  // giá hiển thị = giá sau KM của biến thể min
            dto.setGiaTriGiamKhuyenMai((BigDecimal) r[9]);
            dto.setLoaiGiam(r[10] != null ? ((Number) r[10]).intValue() : null);

            // field "gia" tổng quát = giá hiển thị
            dto.setGia(dto.getGiaBienThe());

            result.add(dto);
        }
        return result;
    }


    public List<TrangChuSanPham> getSanPhamLienQuan(int sanPhamId) {
        // 1. Lấy sản phẩm gốc
        SanPhamChiTietDTO sanPham = getSanPhamChiTiet(sanPhamId);
        if (sanPham == null) {
            return Collections.emptyList();
        }

        // 2. Lấy giá: nếu null thì fallback sang giá của biến thể (lấy giá min trong list biến thể)
        BigDecimal gia = sanPham.getGia();
        if (gia == null && sanPham.getBienTheList() != null && !sanPham.getBienTheList().isEmpty()) {
            gia = sanPham.getBienTheList().stream()
                    .map(SanPhamChiTietDTO.BienTheDTO::getGia)
                    .filter(Objects::nonNull)
                    .min(BigDecimal::compareTo) // lấy giá nhỏ nhất trong các biến thể
                    .orElse(BigDecimal.ZERO);
        }

        if (gia == null) {
            gia = BigDecimal.ZERO; // fallback cuối cùng để tránh NullPointerException
        }

        // 3. Lấy minPrice, maxPrice dựa trên giá sản phẩm gốc
        BigDecimal minPrice = gia.multiply(BigDecimal.valueOf(0.8)); // trong khoảng 80%
        BigDecimal maxPrice = gia.multiply(BigDecimal.valueOf(1.2)); // đến 120%

        // 4. Lấy danh mục gốc
        Long danhMucId = (long) sanPham.getMaDanhMuc();

        // 5. Lọc sản phẩm liên quan (trong cùng danh mục và tầm giá)
        List<TrangChuSanPham> result = getAllSanPham1(danhMucId, minPrice, maxPrice);

        // 6. Loại bỏ chính sản phẩm gốc
        return result.stream()
                .filter(sp -> sp.getMaSanPham() != sanPhamId)
                .collect(Collectors.toList());
    }





//    public SanPhamChiTietDTO getSanPhamChiTiet(int id) {
//        SanPham sp = sanPhamRepo.findById(id).orElseThrow();
//
//        // Lấy biến thể theo mã sản phẩm
//        List<BienTheSanPham> bienTheList = bienTheRepo.findBySanPham_MaSanPham(id);
//
//        // Lọc những biến thể trạng thái = 1
//        List<BienTheDTO> bienTheDTOs = bienTheList.stream()
//                .filter(bt -> bt.getTrangThai() != null && bt.getTrangThai() == 1) // chỉ trạng thái 1
//                .map(bt -> {
//                    // Lấy thuộc tính theo mã SKU
//                    List<ThuocTinh> ttList = thuocTinhRepo.findByBienTheSanPham_MaSKU(bt.getMaSKU());
//
//                    Map<String, String> thuocTinhMap = ttList.stream()
//                            .collect(Collectors.toMap(
//                                    ThuocTinh::getTenThuocTinh,
//                                    ThuocTinh::getTenThuocTinhBienThe
//                            ));
//
//                    return new BienTheDTO(
//                            bt.getMaSKU(),
//                            bt.getGia(),
//                            bt.getSoLuong(),
//                            bt.getTrangThai(),  // gán trangThai vào DTO
//                            thuocTinhMap
//                    );
//                })
//                .toList();
//
//        return new SanPhamChiTietDTO(
//                sp.getMaSanPham(),
//                sp.getTenSanPham(),
//                sp.getMoTa(),
//                sp.getDanhMuc().getTenDanhMuc(),
//                bienTheDTOs
//        );
//    }
//
//    public SanPhamChiTietDTO getSanPhamChiTiet(int id) {
//        // 1. Lấy sản phẩm từ database, nếu không có sẽ báo lỗi
//        SanPham sp = sanPhamRepo.findById(id)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
//
//        // 2. Lấy danh sách biến thể của sản phẩm
//        List<BienTheSanPham> bienTheList = bienTheRepo.findBySanPham_MaSanPham(id);
//
//        // 3. Chuyển đổi danh sách biến thể sang DTO, chỉ lấy những biến thể có trạng thái = 1
//        List<BienTheDTO> bienTheDTOs = bienTheList.stream()
//                .filter(bt -> bt.getTrangThai() != null && bt.getTrangThai() == 1) // chỉ trạng thái 1
//                .map(bt -> {
//                    // Với mỗi biến thể, lấy ra các thuộc tính tương ứng
//                    List<ThuocTinh> ttList = thuocTinhRepo.findByBienTheSanPham_MaSKU(bt.getMaSKU());
//                    Map<String, String> thuocTinhMap = ttList.stream()
//                            .collect(Collectors.toMap(
//                                    ThuocTinh::getTenThuocTinh,
//                                    ThuocTinh::getTenThuocTinhBienThe
//                            ));
//
//                    return new BienTheDTO(
//                            bt.getMaSKU(),
//                            bt.getGia(),
//                            bt.getSoLuong(),
//                            bt.getTrangThai(),
//                            thuocTinhMap
//                    );
//                })
//                .toList();
//
//        // ======================== PHẦN BỔ SUNG ========================
//        // 4. Lấy danh sách thông số kỹ thuật và chuyển đổi sang DTO
//        List<SanPhamChiTietDTO.ThongSoKyThuatDTO> thongSoDTOs;
//        if (sp.getThongSoKyThuatList() != null && !sp.getThongSoKyThuatList().isEmpty()) {
//            thongSoDTOs = sp.getThongSoKyThuatList().stream()
//                    .map(ts -> new SanPhamChiTietDTO.ThongSoKyThuatDTO(ts.getTenThongSo(), ts.getGiaTriThongSo()))
//                    .collect(Collectors.toList());
//        } else {
//            // Nếu sản phẩm không có thông số nào, trả về một danh sách rỗng
//            thongSoDTOs = Collections.emptyList();
//        }
//        // ===============================================================
//
//        // 5. Tạo DTO cuối cùng để trả về
//        SanPhamChiTietDTO chiTietDTO = new SanPhamChiTietDTO();
//        chiTietDTO.setMaSanPham(sp.getMaSanPham());
//        chiTietDTO.setTenSanPham(sp.getTenSanPham());
//        chiTietDTO.setMoTa(sp.getMoTa());
//        chiTietDTO.setTenDanhMuc(sp.getDanhMuc() != null ? sp.getDanhMuc().getTenDanhMuc() : null);
//        chiTietDTO.setBienTheList(bienTheDTOs);
//        chiTietDTO.setThongSoList(thongSoDTOs); // Gán danh sách thông số đã lấy được
//
//        return chiTietDTO;
//    }


//    public SanPhamChiTietDTO getSanPhamChiTiet(int id) {
//        // 1. Lấy sản phẩm từ database, nếu không có sẽ báo lỗi
//        SanPham sp = sanPhamRepo.findById(id)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
//
//        // 2. Lấy và chuyển đổi danh sách biến thể của sản phẩm chính
//        List<BienTheDTO> bienTheDTOs = sp.getBienTheSanPhamList().stream()
//                .filter(bt -> bt.getTrangThai() != null && bt.getTrangThai() == 1)
//                .map(bt -> {
//                    Map<String, String> thuocTinhMap = bt.getThuocTinhList().stream()
//                            .collect(Collectors.toMap(
//                                    ThuocTinh::getTenThuocTinh,
//                                    ThuocTinh::getTenThuocTinhBienThe
//                            ));
//                    return new BienTheDTO(bt.getMaSKU(), bt.getGia(), bt.getSoLuong(), bt.getTrangThai(), thuocTinhMap);
//                })
//                .collect(Collectors.toList());
//
//        // 3. Lấy và chuyển đổi danh sách thông số kỹ thuật
//        List<SanPhamChiTietDTO.ThongSoKyThuatDTO> thongSoDTOs = sp.getThongSoKyThuatList().stream()
//                .map(ts -> new SanPhamChiTietDTO.ThongSoKyThuatDTO(ts.getTenThongSo(), ts.getGiaTriThongSo()))
//                .collect(Collectors.toList());
//
//        // ======================== PHẦN BỔ SUNG CHO PHỤ KIỆN ========================
//        // 4. Lấy và chuyển đổi danh sách phụ kiện và các cấp con của nó
//        List<SanPhamChiTietDTO.PhuKienDTO> phuKienDTOs;
//        if (sp.getPhuKienList() != null && !sp.getPhuKienList().isEmpty()) {
//            phuKienDTOs = sp.getPhuKienList().stream()
//                    .map(phuKien -> { // Mỗi 'phuKien' là một đối tượng PhuKien Model
//                        // Chuyển đổi danh sách biến thể của phụ kiện
//                        List<SanPhamChiTietDTO.BienThePhuKienDTO> bienThePKDTOs = phuKien.getBienThePhuKienList().stream()
//                                .map(bienThePK -> { // Mỗi 'bienThePK' là một BienThePhuKien Model
//                                    // Chuyển đổi danh sách thuộc tính của biến thể phụ kiện
//                                    List<SanPhamChiTietDTO.ThuocTinhPhuKienDTO> thuocTinhPKDTOs = bienThePK.getThuocTinhPhuKienList().stream()
//                                            .map(thuocTinhPK -> new SanPhamChiTietDTO.ThuocTinhPhuKienDTO(
//                                                    thuocTinhPK.getTenThuocTinh(),
//                                                    thuocTinhPK.getGiaTriThuocTinh()
//                                            ))
//                                            .collect(Collectors.toList());
//
//                                    return new SanPhamChiTietDTO.BienThePhuKienDTO(
//                                            bienThePK.getMaSKUPhuKien(),
//                                            bienThePK.getGia(),
//                                            bienThePK.getSoLuong(),
//                                            thuocTinhPKDTOs
//                                    );
//                                })
//                                .collect(Collectors.toList());
//
//                        return new SanPhamChiTietDTO.PhuKienDTO(
//                                phuKien.getMaPhuKien(),
//                                phuKien.getTenPhuKien(),
//                                phuKien.getMoTa(),
//                                bienThePKDTOs
//                        );
//                    })
//                    .collect(Collectors.toList());
//        } else {
//            phuKienDTOs = Collections.emptyList(); // Trả về danh sách rỗng nếu không có
//        }
//        // ===========================================================================
//
//        // 5. Tạo DTO cuối cùng để trả về
//        SanPhamChiTietDTO chiTietDTO = new SanPhamChiTietDTO();
//        chiTietDTO.setMaSanPham(sp.getMaSanPham());
//        chiTietDTO.setTenSanPham(sp.getTenSanPham());
//        chiTietDTO.setMoTa(sp.getMoTa());
//        chiTietDTO.setTenDanhMuc(sp.getDanhMuc() != null ? sp.getDanhMuc().getTenDanhMuc() : null);
//        chiTietDTO.setBienTheList(bienTheDTOs);
//        chiTietDTO.setThongSoList(thongSoDTOs);
//        chiTietDTO.setPhuKienList(phuKienDTOs); // Gán danh sách phụ kiện đã xử lý
//
//        return chiTietDTO;
//    }


    private final LoaiThongSoRepository loaiThongSoRepository ;
//    public SanPhamChiTietDTO getSanPhamChiTiet(int id) {
//        // 1. Lấy sản phẩm từ database, nếu không có sẽ báo lỗi
//        SanPham sp = sanPhamRepo.findById(id)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
//
//        // 2. Lấy và chuyển đổi danh sách biến thể của sản phẩm chính
//        List<BienTheDTO> bienTheDTOs = sp.getBienTheSanPhamList().stream()
//                .filter(bt -> bt.getTrangThai() != null && bt.getTrangThai() == 1)
//                .map(bt -> {
//                    Map<String, String> thuocTinhMap = bt.getThuocTinhList().stream()
//                            .collect(Collectors.toMap(
//                                    ThuocTinh::getTenThuocTinh,
//                                    ThuocTinh::getTenThuocTinhBienThe
//                            ));
//                    return new BienTheDTO(
//                            bt.getMaSKU(),
//                            bt.getGia(),
//                            bt.getSoLuong(),
//                            bt.getTrangThai(),
//                            thuocTinhMap
//                    );
//                })
//                .collect(Collectors.toList());
//
//        // 3. Lấy và chuyển đổi danh sách loại thông số + thông số kỹ thuật
//
////        sp.getLoaiThongSoList().stream()
//        List<SanPhamChiTietDTO.LoaiThongSoDTO> loaiThongSoDTOs = loaiThongSoRepository.findBySanPham_MaSanPham(sp.getMaSanPham())
//                .stream()
//                .map(loai -> {
//                    List<SanPhamChiTietDTO.ThongSoKyThuatDTO> thongSoDTOs = loai.getThongSoKyThuatList().stream()
//                            .map(ts -> new SanPhamChiTietDTO.ThongSoKyThuatDTO(
//                                    ts.getTenThongSo(),
//                                    ts.getGiaTriThongSo(),
//                                    ts.getTrangThai()  // ✅ giữ nguyên trạng thái
//                            ))
//                            .collect(Collectors.toList());
//
//                    return new SanPhamChiTietDTO.LoaiThongSoDTO(
//                            loai.getTenLoaiThongSo(),
//                            thongSoDTOs
//                    );
//                })
//                .collect(Collectors.toList());
//
//        // 4. Lấy và chuyển đổi danh sách phụ kiện và các cấp con của nó
//        List<SanPhamChiTietDTO.PhuKienDTO> phuKienDTOs;
//        if (sp.getPhuKienList() != null && !sp.getPhuKienList().isEmpty()) {
//            phuKienDTOs = sp.getPhuKienList().stream()
//                    .map(phuKien -> {
//                        // Chuyển đổi danh sách biến thể của phụ kiện
//                        List<SanPhamChiTietDTO.BienThePhuKienDTO> bienThePKDTOs = phuKien.getBienThePhuKienList().stream()
//                                .map(bienThePK -> {
//                                    // Chuyển đổi danh sách thuộc tính của biến thể phụ kiện
//                                    List<SanPhamChiTietDTO.ThuocTinhPhuKienDTO> thuocTinhPKDTOs =
//                                            bienThePK.getThuocTinhPhuKienList().stream()
//                                                    .map(thuocTinhPK -> new SanPhamChiTietDTO.ThuocTinhPhuKienDTO(
//                                                            thuocTinhPK.getTenThuocTinh(),
//                                                            thuocTinhPK.getGiaTriThuocTinh()
//                                                    ))
//                                                    .collect(Collectors.toList());
//
//                                    return new SanPhamChiTietDTO.BienThePhuKienDTO(
//                                            bienThePK.getMaSKUPhuKien(),
//                                            bienThePK.getGia(),
//                                            bienThePK.getSoLuong(),
//                                            thuocTinhPKDTOs
//                                    );
//                                })
//                                .collect(Collectors.toList());
//
//                        return new SanPhamChiTietDTO.PhuKienDTO(
//                                phuKien.getMaPhuKien(),
//                                phuKien.getTenPhuKien(),
//                                phuKien.getMoTa(),
//                                bienThePKDTOs
//                        );
//                    })
//                    .collect(Collectors.toList());
//        } else {
//            phuKienDTOs = Collections.emptyList();
//        }
//
//        // 5. Tạo DTO cuối cùng để trả về
//        SanPhamChiTietDTO chiTietDTO = new SanPhamChiTietDTO();
//        chiTietDTO.setMaSanPham(sp.getMaSanPham());
//        chiTietDTO.setTenSanPham(sp.getTenSanPham());
//        chiTietDTO.setMoTa(sp.getMoTa());
//        chiTietDTO.setTenDanhMuc(sp.getDanhMuc() != null ? sp.getDanhMuc().getTenDanhMuc() : null);
//        chiTietDTO.setBienTheList(bienTheDTOs);
//        chiTietDTO.setLoaiThongSoList(loaiThongSoDTOs); // ✅ gán danh sách loại thông số
//        chiTietDTO.setPhuKienList(phuKienDTOs);
//
//        return chiTietDTO;
//    }



//    public SanPhamChiTietDTO getSanPhamChiTiet(int id) {
//        // 1. Lấy sản phẩm từ database
//        SanPham sp = sanPhamRepo.findById(id)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
//
//        // 2. Lấy danh sách biến thể (giữ nguyên)
//        List<BienTheDTO> bienTheDTOs = sp.getBienTheSanPhamList().stream()
//                .filter(bt -> bt.getTrangThai() != null && bt.getTrangThai() == 1)
//                .map(bt -> {
//                    Map<String, String> thuocTinhMap = bt.getThuocTinhList().stream()
//                            .collect(Collectors.toMap(
//                                    ThuocTinh::getTenThuocTinh,
//                                    ThuocTinh::getTenThuocTinhBienThe
//                            ));
//                    return new BienTheDTO(
//                            bt.getMaSKU(),
//                            bt.getGia(),
//                            bt.getSoLuong(),
//                            bt.getTrangThai(),
//                            thuocTinhMap
//                    );
//                })
//                .collect(Collectors.toList());
//
//        // 3. Lấy loại thông số theo mã danh mục của sản phẩm ✅
//        Integer maDanhMuc = (sp.getDanhMuc() != null) ? sp.getDanhMuc().getMaDanhMuc() : null;
//        List<SanPhamChiTietDTO.LoaiThongSoDTO> loaiThongSoDTOs = Collections.emptyList();
//
//        if (maDanhMuc != null) {
//            loaiThongSoDTOs = loaiThongSoRepository.findBySanPham_DanhMuc_MaDanhMuc(maDanhMuc)
//                    .stream()
//                    .map(loai -> {
//                        List<SanPhamChiTietDTO.ThongSoKyThuatDTO> thongSoDTOs =
//                                loai.getThongSoKyThuatList().stream()
//                                        .map(ts -> new SanPhamChiTietDTO.ThongSoKyThuatDTO(
//                                                ts.getTenThongSo(),
//                                                ts.getGiaTriThongSo(),
//                                                sp.getMaSanPham(),     // ✅ gắn mã sản phẩm
//                                                ts.getTrangThai()
//                                        ))
//                                        .collect(Collectors.toList());
//
//                        return new SanPhamChiTietDTO.LoaiThongSoDTO(
//                                loai.getTenLoaiThongSo(),
//                                thongSoDTOs
//                        );
//                    })
//                    .collect(Collectors.toList());
//        }
//
//        // 4. Lấy phụ kiện (giữ nguyên)
//        List<SanPhamChiTietDTO.PhuKienDTO> phuKienDTOs;
//        if (sp.getPhuKienList() != null && !sp.getPhuKienList().isEmpty()) {
//            phuKienDTOs = sp.getPhuKienList().stream()
//                    .map(phuKien -> {
//                        List<SanPhamChiTietDTO.BienThePhuKienDTO> bienThePKDTOs =
//                                phuKien.getBienThePhuKienList().stream()
//                                        .map(bienThePK -> {
//                                            List<SanPhamChiTietDTO.ThuocTinhPhuKienDTO> thuocTinhPKDTOs =
//                                                    bienThePK.getThuocTinhPhuKienList().stream()
//                                                            .map(thuocTinhPK -> new SanPhamChiTietDTO.ThuocTinhPhuKienDTO(
//                                                                    thuocTinhPK.getTenThuocTinh(),
//                                                                    thuocTinhPK.getGiaTriThuocTinh()
//                                                            ))
//                                                            .collect(Collectors.toList());
//
//                                            return new SanPhamChiTietDTO.BienThePhuKienDTO(
//                                                    bienThePK.getMaSKUPhuKien(),
//                                                    bienThePK.getGia(),
//                                                    bienThePK.getSoLuong(),
//                                                    thuocTinhPKDTOs
//                                            );
//                                        })
//                                        .collect(Collectors.toList());
//
//                        return new SanPhamChiTietDTO.PhuKienDTO(
//                                phuKien.getMaPhuKien(),
//                                phuKien.getTenPhuKien(),
//                                phuKien.getMoTa(),
//                                bienThePKDTOs
//                        );
//                    })
//                    .collect(Collectors.toList());
//        } else {
//            phuKienDTOs = Collections.emptyList();
//        }
//
//        // 5. Build DTO cuối cùng
//        SanPhamChiTietDTO chiTietDTO = new SanPhamChiTietDTO();
//        chiTietDTO.setMaSanPham(sp.getMaSanPham());
//        chiTietDTO.setTenSanPham(sp.getTenSanPham());
//        chiTietDTO.setMoTa(sp.getMoTa());
//        chiTietDTO.setTenDanhMuc(sp.getDanhMuc() != null ? sp.getDanhMuc().getTenDanhMuc() : null);
//        chiTietDTO.setBienTheList(bienTheDTOs);
//        chiTietDTO.setLoaiThongSoList(loaiThongSoDTOs); // ✅ theo mã danh mục
//        chiTietDTO.setPhuKienList(phuKienDTOs);
//
//        return chiTietDTO;
//    }



        public SanPhamChiTietDTO getSanPhamChiTiet(int id) {
            // 1. Lấy sản phẩm gốc từ database
            SanPham sp = sanPhamRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));

            // 2. Map danh sách biến thể sản phẩm (BienTheSanPham -> BienTheDTO)
            List<SanPhamChiTietDTO.BienTheDTO> bienTheDTOs = sp.getBienTheSanPhamList().stream()
                    .filter(bt -> bt.getTrangThai() != null && bt.getTrangThai() == 1) // Chỉ lấy biến thể đang hoạt động
                    .map(bt -> {
                        // <<< SỬA LẠI: Chuyển đổi từ List<ThuocTinh> sang List<ThuocTinhDTO> thay vì Map
                        List<SanPhamChiTietDTO.ThuocTinhDTO> thuocTinhDTOs = bt.getThuocTinhList().stream()
                                .map(tt -> new SanPhamChiTietDTO.ThuocTinhDTO(
                                        tt.getTenThuocTinh(),
                                        tt.getTenThuocTinhBienThe() // Trong DTO đã đổi tên thành giaTriThuocTinh
                                ))
                                .collect(Collectors.toList());

                        // Lấy mã khuyến mãi một cách an toàn
                        Integer maKhuyenMai = (bt.getKhuyenMai() != null) ? bt.getKhuyenMai().getMaKhuyenMai() : null;

                        return new SanPhamChiTietDTO.BienTheDTO(
                                bt.getMaSKU(),
                                bt.getGia(),
                                bt.getGiaKhongKhuyenMai(),  // ✅ bổ sung giá gốc
                                bt.getSoLuong(),
                                bt.getTrangThai(),
                                maKhuyenMai, // <<< THÊM MỚI: Bổ sung mã khuyến mãi
                                thuocTinhDTOs // <<< SỬA LẠI: Truyền vào List DTO thay vì Map
                        );
                    })
                    .collect(Collectors.toList());

            // 3. Lấy và lọc thông số kỹ thuật theo đúng sản phẩm hiện tại
            Integer maDanhMuc = (sp.getDanhMuc() != null) ? sp.getDanhMuc().getMaDanhMuc() : null;
            List<SanPhamChiTietDTO.LoaiThongSoDTO> loaiThongSoDTOs = Collections.emptyList();

            if (maDanhMuc != null) {
                // Lấy tất cả các loại thông số của danh mục
                loaiThongSoDTOs = loaiThongSoRepository.findByDanhMuc_MaDanhMuc(maDanhMuc)
                        .stream()
                        .map(loai -> {
                            // <<< SỬA LẠI LOGIC: Lọc thông số kỹ thuật CHỈ thuộc về sản phẩm đang xem
                            // Sửa đoạn code bị lỗi trong service của bạn thành như sau:

                            List<SanPhamChiTietDTO.ThongSoKyThuatDTO> thongSoDTOs =
                                    // 👇 SỬA Ở ĐÂY: Thêm "get" và sửa lại cách viết hoa cho đúng
                                    loai.getThongSoKyThuatList().stream()
                                            .filter(ts -> ts.getSanPham() != null && ts.getSanPham().getMaSanPham() == id)
                                            .map(ts -> new SanPhamChiTietDTO.ThongSoKyThuatDTO(
                                                    ts.getTenThongSo(),
                                                    ts.getGiaTriThongSo(),
                                                    sp.getMaSanPham(),
                                                    ts.getTrangThai()
                                            ))
                                            .collect(Collectors.toList());

                            // Chỉ trả về loại thông số nếu nó có thông số liên quan đến sản phẩm này
                            if (!thongSoDTOs.isEmpty()) {
                                return new SanPhamChiTietDTO.LoaiThongSoDTO(loai.getTenLoaiThongSo(), thongSoDTOs);
                            }
                            return null;
                        })
                        .filter(dto -> dto != null) // Loại bỏ các nhóm thông số rỗng
                        .collect(Collectors.toList());
            }

            // 4. Map danh sách phụ kiện (PhuKien -> PhuKienDTO)
            List<SanPhamChiTietDTO.PhuKienDTO> phuKienDTOs = sp.getPhuKienList().stream()
                    .map(phuKien -> {
                        List<SanPhamChiTietDTO.BienThePhuKienDTO> bienThePKDTOs =
                                phuKien.getBienThePhuKienList().stream()
                                        // <<< THÊM MỚI: Chỉ lấy biến thể phụ kiện đang hoạt động
                                        .filter(btpk -> btpk.getTrangThai() != null && btpk.getTrangThai() == 1)
                                        .map(bienThePK -> {
                                            List<SanPhamChiTietDTO.ThuocTinhPhuKienDTO> thuocTinhPKDTOs =
                                                    bienThePK.getThuocTinhPhuKienList().stream()
                                                            .map(thuocTinhPK -> new SanPhamChiTietDTO.ThuocTinhPhuKienDTO(
                                                                    thuocTinhPK.getTenThuocTinh(),
                                                                    thuocTinhPK.getGiaTriThuocTinh()
                                                            ))
                                                            .collect(Collectors.toList());

                                            return new SanPhamChiTietDTO.BienThePhuKienDTO(
                                                    bienThePK.getMaSKUPhuKien(),
                                                    bienThePK.getGia(),
                                                    bienThePK.getSoLuong(),
                                                    thuocTinhPKDTOs
                                                    // Lưu ý: Nếu DTO BienThePhuKienDTO có 'trangThai', bạn cần thêm vào đây
                                            );
                                        })
                                        .collect(Collectors.toList());

                        return new SanPhamChiTietDTO.PhuKienDTO(
                                phuKien.getMaPhuKien(),
                                phuKien.getTenPhuKien(),
                                phuKien.getMoTa(),
                                bienThePKDTOs
                        );
                    })
                    .collect(Collectors.toList());


            // 5. Build DTO cuối cùng để trả về
            SanPhamChiTietDTO chiTietDTO = new SanPhamChiTietDTO();
            chiTietDTO.setMaSanPham(sp.getMaSanPham());
            chiTietDTO.setTenSanPham(sp.getTenSanPham());
            chiTietDTO.setMoTa(sp.getMoTa());
            chiTietDTO.setMaDanhMuc(maDanhMuc); // <<< THÊM MỚI: Bổ sung mã danh mục
            chiTietDTO.setTenDanhMuc(sp.getDanhMuc() != null ? sp.getDanhMuc().getTenDanhMuc() : null);
            chiTietDTO.setBienTheList(bienTheDTOs);
            chiTietDTO.setLoaiThongSoList(loaiThongSoDTOs);
            chiTietDTO.setPhuKienList(phuKienDTOs);

            return chiTietDTO;
        }
    }







