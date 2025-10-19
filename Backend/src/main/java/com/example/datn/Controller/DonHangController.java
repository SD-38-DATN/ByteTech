package com.example.datn.Controller;


import com.example.datn.Config.JwtUtil;
import com.example.datn.DTO.DonHang.ImeiResponseDTO;
import com.example.datn.DTO.DonHang.XacNhanDonHangRequestDTO;
import com.example.datn.DTO.TrangMuaHang.DonHangRequestDTO;
import com.example.datn.DTO.TrangMuaHang.DonHangResponseDTO;

import com.example.datn.Repository.UsersRepository;
import com.example.datn.Service.DonHangService;
import io.jsonwebtoken.Jwts;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donhang")
@RequiredArgsConstructor
public class DonHangController {

    private final DonHangService donHangService;

    private final UsersRepository usersRepository;

    // Tạo đơn hàng từ giỏ hàng của user
    @PostMapping("/create")
    public ResponseEntity<DonHangResponseDTO> taoDonHang(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody DonHangRequestDTO request) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        DonHangResponseDTO response = donHangService.taoDonHang(token, request);
        return ResponseEntity.ok(response);
    }
    private  final JwtUtil jwtUtil ;
    // 🔹 Hàm tiện ích: lấy username từ token
    private String getUsernameFromToken(String authorizationHeader) {
        String token = authorizationHeader.startsWith("Bearer ")
                ? authorizationHeader.substring(7)
                : authorizationHeader;

        return Jwts.parserBuilder()
                .setSigningKey(jwtUtil.getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // ================== API cho User ==================



    // Trong DonHangController.java

    // Lấy danh sách đơn hàng CỦA USER ĐANG ĐĂNG NHẬP theo trạng thái
    @GetMapping("/me/trang-thai/{trangThai}")
    public ResponseEntity<List<DonHangResponseDTO>> layDonHangCuaToiTheoTrangThai(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("trangThai") Integer trangThai) {

        String username = getUsernameFromToken(authorizationHeader);
        List<DonHangResponseDTO> donHangList =
                donHangService.getDonHangCuaUserTheoTrangThai(username, trangThai);

        if (donHangList == null || donHangList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(donHangList);
    }

    @GetMapping("/me/cho-xac-nhan")
    public ResponseEntity<List<DonHangResponseDTO>> layDonHangChoXacNhanCuaToi(
            @RequestHeader("Authorization") String authorizationHeader) {

        String username = getUsernameFromToken(authorizationHeader);
        List<DonHangResponseDTO> donHangList =
                donHangService.getDonHangCuaUserTheoTrangThai(username, 0);

        if (donHangList == null || donHangList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(donHangList);
    }

    @PutMapping("/{maDonHang}/xac-nhan")

    public ResponseEntity<String> xacNhanDonHang(
            @PathVariable Integer maDonHang,
            @RequestBody XacNhanDonHangRequestDTO request) {

        donHangService.xacNhanDonHang(maDonHang, request.getItems());

        return ResponseEntity.ok("Đơn hàng #" + maDonHang + " đã được xác nhận và chuyển sang trạng thái Đang giao hàng.");
    }

    @GetMapping("/imei-lookup")
    public ResponseEntity<List<ImeiResponseDTO>> lookupImeisBySku(@RequestParam("sku") String sku) {
        List<ImeiResponseDTO> imeiList = donHangService.findAvailableImeisBySku(sku);
        if (imeiList == null || imeiList.isEmpty()) {
            return ResponseEntity.noContent().build(); // Trả về 204 No Content nếu không tìm thấy
        }
        return ResponseEntity.ok(imeiList);
    }



    @GetMapping("/trang-thai/{trangThai}")
    public ResponseEntity<List<DonHangResponseDTO>> layDonHangTheoTrangThai(
            @PathVariable("trangThai") Integer trangThai) {

        List<DonHangResponseDTO> donHangList = donHangService.getDonHangTheoTrangThai(trangThai);

        if (donHangList == null || donHangList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(donHangList);
    }




















    // ================== CÁC API MỚI ĐƯỢC BỔ SUNG ==================

    /**
     * Endpoint để đánh dấu một đơn hàng là "Hoàn thành".
     * Yêu cầu đơn hàng phải đang ở trạng thái "Đang giao hàng" (1).
     */
    @PutMapping("/{maDonHang}/hoan-thanh")
    public ResponseEntity<String> hoanThanhDonHang(@PathVariable Integer maDonHang) {
        try {
            donHangService.hoanThanhDonHang(maDonHang);
            return ResponseEntity.ok("Đơn hàng #" + maDonHang + " đã được cập nhật thành công sang trạng thái 'Hoàn thành'.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint để hủy một đơn hàng khi đang giao.
     * Yêu cầu đơn hàng phải đang ở trạng thái "Đang giao hàng" (1).
     */
    @PutMapping("/{maDonHang}/huy-khi-giao")
    public ResponseEntity<String> huyDonHangKhiDangGiao(@PathVariable Integer maDonHang) {
        try {
            donHangService.huyDonHangKhiDangGiao(maDonHang);
            return ResponseEntity.ok("Đơn hàng #" + maDonHang + " đã được hủy thành công.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint để hủy một đơn hàng đang "Chờ xác nhận".
     * Yêu cầu đơn hàng phải đang ở trạng thái 0.
     */
    @PutMapping("/{maDonHang}/huy-cho-xac-nhan")
    public ResponseEntity<String> huyDonHangChoXacNhan(@PathVariable Integer maDonHang) {
        try {
            donHangService.huyDonHangChoXacNhan(maDonHang);
            return ResponseEntity.ok("Đơn hàng #" + maDonHang + " đã được hủy thành công khi đang chờ xác nhận.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

