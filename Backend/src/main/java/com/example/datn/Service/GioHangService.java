//package com.example.datn.Service;
//
//
//
//import com.example.datn.Config.JwtUtil;
//import com.example.datn.DTO.TrangMuaHang.AddToCartRequestDTO;
//import com.example.datn.DTO.TrangMuaHang.BienTheDTO;
//import com.example.datn.DTO.TrangMuaHang.GioHangDTO;
//import com.example.datn.DTO.TrangMuaHang.GioHangChiTietDTO;
//import com.example.datn.Model.*;
//import com.example.datn.Repository.*;
//
//import io.jsonwebtoken.Claims;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@RequiredArgsConstructor
//@Service
//public class GioHangService {
//
//    private final GioHangRepository gioHangRepository;
//    private final BienTheSanPhamRepository bienTheSanPhamRepository;
//
//    private final BienThePhuKienRepository bienThePhuKienRepository ;
//
//    private final GioHangChiTietRepository gioHangChiTietRepository;
//    private final UsersRepository usersRepository;
//    private final JwtUtil jwtUtil;
//
//    // ------------------ Mapping ------------------
//    private GioHangDTO toDTO(GioHang e) {
//        return new GioHangDTO(
//                e.getMaGioHang(),
//                e.getUser().getId(),
//                e.getTongTien(),
//                e.getNgayTao(),
//                e.getChiTietList().stream().map(this::toChiTietDTO).toList()
//        );
//    }
//
//    private GioHangChiTietDTO toChiTietDTO(GioHangChiTiet ct) {
//        BienTheSanPham bienThe = ct.getBienThe();
//        SanPham sp = bienThe.getSanPham();
//
//        // Chuyển danh sách ThuocTinh thành Map<tenThuocTinh, tenThuocTinhBienThe>
//        Map<String, String> thuocTinhMap = bienThe.getThuocTinhList().stream()
//                .collect(Collectors.toMap(
//                        t -> t.getTenThuocTinh(),          // key
//                        t -> t.getTenThuocTinhBienThe()    // value
//                ));
//
//        return new GioHangChiTietDTO(
//                ct.getId(),
//                bienThe.getMaSKU(),
//                sp.getTenSanPham(),
//                ct.getSoLuong(),
//                ct.getGia(),
//                bienThe.getSoLuong(), // gán số lượng tồn kho
//                thuocTinhMap
//        );
//    }
//
//
//    // ------------------ Business ------------------
//
//    public GioHangDTO getCartByToken(String token) {
//        // Giải mã token để lấy username
//        Claims claims = jwtUtil.extractAllClaims(token);
//        String username = claims.getSubject();
//
//        Users user = usersRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy user = " + username));
//
//        GioHang gioHang = gioHangRepository.findByUser(user)
//                .orElseThrow(() -> new RuntimeException("Người dùng chưa có giỏ hàng"));
//
//        return toDTO(gioHang);
//    }
//
//    // ------------------ Update số lượng ------------------
//
//    // ------------------ Update số lượng ------------------
//    @Transactional
//    public void updateSoLuong(List<GioHangChiTietDTO> updates) {
//        for (GioHangChiTietDTO dto : updates) {
//            GioHangChiTiet chiTiet = gioHangChiTietRepository.findById(dto.getId())
//                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết giỏ hàng id=" + dto.getId()));
//
//            // Cập nhật số lượng
//            chiTiet.setSoLuong(dto.getSoLuong());
//
//            // Lưu lại chi tiết giỏ hàng
//            gioHangChiTietRepository.save(chiTiet);
//        }
//    }
//
//    @Transactional
//    public void deleteChiTiet(Integer chiTietId) {
//        GioHangChiTiet chiTiet = gioHangChiTietRepository.findById(chiTietId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết giỏ hàng id = " + chiTietId));
//        gioHangChiTietRepository.delete(chiTiet);
//    }
//
//
//    @Transactional
//    public GioHangChiTietDTO addToCart(String token, BienTheDTO bienTheDTO) {
//        // 1. Lấy user từ token
//        Claims claims = jwtUtil.extractAllClaims(token);
//        String username = claims.getSubject();
//        Users user = usersRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy user = " + username));
//
//        // 2. Lấy giỏ hàng của user, nếu chưa có thì tạo mới
//        GioHang gioHang = gioHangRepository.findByUser(user)
//                .orElseGet(() -> {
//                    GioHang newCart = new GioHang();
//                    newCart.setUser(user);
//                    newCart.setTongTien(BigDecimal.ZERO);
//                    newCart.setNgayTao(java.time.LocalDateTime.now());
//                    return gioHangRepository.save(newCart);
//                });
//
//        // 3. Lấy biến thể từ DB
//        BienTheSanPham bienThe = bienTheSanPhamRepository.findByMaSKU(bienTheDTO.getMaSKU())
//                .orElseThrow(() -> new RuntimeException("Biến thể không tồn tại"));
//
//
//
//
//        int addQty = bienTheDTO.getSoLuong(); // số lượng muốn thêm
//
//        // 4. Kiểm tra xem biến thể đã có trong giỏ chưa
//        GioHangChiTiet exist = gioHangChiTietRepository
//                .findByGioHangAndBienThe_MaSKU(gioHang, bienTheDTO.getMaSKU())
//                .orElse(null);
//
//        if (exist != null) {
//            // Nếu đã có, tăng số lượng lên addQty
//            int newQty = exist.getSoLuong() + addQty;
//            if (newQty > bienThe.getSoLuong()) {
//                throw new RuntimeException("Số lượng vượt quá tồn kho");
//            }
//            exist.setSoLuong(newQty);
//            gioHangChiTietRepository.save(exist);
//            return toChiTietDTO(exist);
//        }
//
//        // 5. Nếu chưa có, tạo mới chi tiết giỏ hàng với số lượng addQty
//        if (addQty > bienThe.getSoLuong()) {
//            throw new RuntimeException("Số lượng vượt quá tồn kho");
//        }
//
//        GioHangChiTiet chiTiet = new GioHangChiTiet();
//        chiTiet.setGioHang(gioHang);
//        chiTiet.setBienThe(bienThe);
//        chiTiet.setGia(bienThe.getGia());
//        chiTiet.setSoLuong(addQty);
//        gioHangChiTietRepository.save(chiTiet);
//
//        return toChiTietDTO(chiTiet);
//    }
//
//}


package com.example.datn.Service;

import com.example.datn.Config.JwtUtil;
import com.example.datn.DTO.TrangMuaHang.BienTheDTO;
import com.example.datn.DTO.TrangMuaHang.GioHangDTO;
import com.example.datn.DTO.TrangMuaHang.GioHangChiTietDTO;
import com.example.datn.Model.*;
import com.example.datn.Repository.*;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GioHangService {

    private final GioHangRepository gioHangRepository;
    private final BienTheSanPhamRepository bienTheSanPhamRepository;
    private final BienThePhuKienRepository bienThePhuKienRepository; // Đã có sẵn
    private final GioHangChiTietRepository gioHangChiTietRepository;
    private final UsersRepository usersRepository;
    private final JwtUtil jwtUtil;

    // ------------------ Mapping ------------------
    private GioHangDTO toDTO(GioHang e) {
        return new GioHangDTO(
                e.getMaGioHang(),
                e.getUser().getId(),
                e.getTongTien(),
                e.getNgayTao(),
                e.getChiTietList().stream().map(this::toChiTietDTO).collect(Collectors.toList())
        );
    }

    // THAY ĐỔI: Cập nhật hàm toChiTietDTO để xử lý cả sản phẩm và phụ kiện
    private GioHangChiTietDTO toChiTietDTO(GioHangChiTiet ct) {
        // Trường hợp là Biến thể Sản phẩm
        if (ct.getBienThe() != null) {
            BienTheSanPham bienThe = ct.getBienThe();
            SanPham sp = bienThe.getSanPham();

            Map<String, String> thuocTinhMap = bienThe.getThuocTinhList().stream()
                    .collect(Collectors.toMap(
                            t -> t.getTenThuocTinh(),
                            t -> t.getTenThuocTinhBienThe()
                    ));

            return new GioHangChiTietDTO(
                    ct.getId(),
                    bienThe.getMaSKU(),
                    sp.getTenSanPham(),
                    ct.getSoLuong(),
                    ct.getGia(),
                    bienThe.getSoLuong(), // số lượng tồn kho
                    thuocTinhMap
            );
        }
        // Trường hợp là Biến thể Phụ kiện
        else if (ct.getBienThePhuKien() != null) {
            BienThePhuKien bienThePK = ct.getBienThePhuKien();
            PhuKien pk = bienThePK.getPhuKien(); // Giả sử có quan hệ này

            // Lấy thuộc tính của phụ kiện (giả sử có bảng ThuocTinhPhuKien)
            Map<String, String> thuocTinhMap = bienThePK.getThuocTinhPhuKienList().stream()
                    .collect(Collectors.toMap(
                            ThuocTinhPhuKien::getTenThuocTinh,
                            ThuocTinhPhuKien::getGiaTriThuocTinh
                    ));

            return new GioHangChiTietDTO(
                    ct.getId(),
                    bienThePK.getMaSKUPhuKien(),
                    pk.getTenPhuKien(), // Lấy tên phụ kiện
                    ct.getSoLuong(),
                    ct.getGia(),
                    bienThePK.getSoLuong(), // số lượng tồn kho
                    thuocTinhMap
            );
        }
        // Trường hợp lỗi
        else {
            throw new RuntimeException("Chi tiết giỏ hàng không hợp lệ: " + ct.getId());
        }
    }


    // ------------------ Business ------------------

    public GioHangDTO getCartByToken(String token) {
        Claims claims = jwtUtil.extractAllClaims(token);
        String username = claims.getSubject();
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user = " + username));
        GioHang gioHang = gioHangRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Người dùng chưa có giỏ hàng"));
        return toDTO(gioHang);
    }

    @Transactional
    public void updateSoLuong(List<GioHangChiTietDTO> updates) {
        for (GioHangChiTietDTO dto : updates) {
            GioHangChiTiet chiTiet = gioHangChiTietRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết giỏ hàng id=" + dto.getId()));
            chiTiet.setSoLuong(dto.getSoLuong());
            gioHangChiTietRepository.save(chiTiet);
        }
    }

    @Transactional
    public void deleteChiTiet(Integer chiTietId) {
        GioHangChiTiet chiTiet = gioHangChiTietRepository.findById(chiTietId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết giỏ hàng id = " + chiTietId));
        gioHangChiTietRepository.delete(chiTiet);
    }

    // THAY ĐỔI TOÀN BỘ PHƯƠNG THỨC NÀY
    @Transactional
    public GioHangChiTietDTO addToCart(String token, BienTheDTO bienTheDTO) {
        // 1. Lấy user và giỏ hàng (giữ nguyên)
        Claims claims = jwtUtil.extractAllClaims(token);
        String username = claims.getSubject();
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user = " + username));

        GioHang gioHang = gioHangRepository.findByUser(user)
                .orElseGet(() -> {
                    GioHang newCart = new GioHang();
                    newCart.setUser(user);
                    newCart.setTongTien(BigDecimal.ZERO);
                    newCart.setNgayTao(LocalDateTime.now());
                    return gioHangRepository.save(newCart);
                });

        int addQty = bienTheDTO.getSoLuong();
        String maSKU = bienTheDTO.getMaSKU();

        // 2. Tìm kiếm biến thể trong bảng sản phẩm trước
        Optional<BienTheSanPham> bienTheSanPhamOpt = bienTheSanPhamRepository.findByMaSKU(maSKU);

        if (bienTheSanPhamOpt.isPresent()) {
            // --- LOGIC XỬ LÝ BIẾN THỂ SẢN PHẨM ---
            BienTheSanPham bienThe = bienTheSanPhamOpt.get();

            GioHangChiTiet exist = gioHangChiTietRepository
                    .findByGioHangAndBienThe_MaSKU(gioHang, maSKU)
                    .orElse(null);

            if (exist != null) {
                int newQty = exist.getSoLuong() + addQty;
                if (newQty > bienThe.getSoLuong()) {
                    throw new RuntimeException("Số lượng sản phẩm vượt quá tồn kho");
                }
                exist.setSoLuong(newQty);
                gioHangChiTietRepository.save(exist);
                return toChiTietDTO(exist);
            } else {
                if (addQty > bienThe.getSoLuong()) {
                    throw new RuntimeException("Số lượng sản phẩm vượt quá tồn kho");
                }
                GioHangChiTiet chiTiet = new GioHangChiTiet();
                chiTiet.setGioHang(gioHang);
                chiTiet.setBienThe(bienThe); // Gán biến thể sản phẩm
                chiTiet.setBienThePhuKien(null); // Đảm bảo trường còn lại là null
                chiTiet.setGia(bienThe.getGia());
                chiTiet.setSoLuong(addQty);
                gioHangChiTietRepository.save(chiTiet);
                return toChiTietDTO(chiTiet);
            }
        } else {
            // 3. Nếu không phải sản phẩm, tìm kiếm trong bảng phụ kiện
            Optional<BienThePhuKien> bienThePhuKienOpt = bienThePhuKienRepository.findById(maSKU);

            if (bienThePhuKienOpt.isPresent()) {
                // --- LOGIC XỬ LÝ BIẾN THỂ PHỤ KIỆN ---
                BienThePhuKien bienThePK = bienThePhuKienOpt.get();

                GioHangChiTiet exist = gioHangChiTietRepository
                        .findByGioHangAndBienThePhuKien_MaSKUPhuKien(gioHang, maSKU)
                        .orElse(null);

                if (exist != null) {
                    int newQty = exist.getSoLuong() + addQty;
                    if (newQty > bienThePK.getSoLuong()) {
                        throw new RuntimeException("Số lượng phụ kiện vượt quá tồn kho");
                    }
                    exist.setSoLuong(newQty);
                    gioHangChiTietRepository.save(exist);
                    return toChiTietDTO(exist);
                } else {
                    if (addQty > bienThePK.getSoLuong()) {
                        throw new RuntimeException("Số lượng phụ kiện vượt quá tồn kho");
                    }
                    GioHangChiTiet chiTiet = new GioHangChiTiet();
                    chiTiet.setGioHang(gioHang);
                    chiTiet.setBienThePhuKien(bienThePK); // Gán biến thể phụ kiện
                    chiTiet.setBienThe(null); // Đảm bảo trường còn lại là null
                    chiTiet.setGia(bienThePK.getGia());
                    chiTiet.setSoLuong(addQty);
                    gioHangChiTietRepository.save(chiTiet);
                    return toChiTietDTO(chiTiet);
                }
            } else {
                // 4. Nếu không tìm thấy ở đâu, báo lỗi
                throw new RuntimeException("Sản phẩm hoặc phụ kiện với SKU '" + maSKU + "' không tồn tại");
            }
        }
    }
}