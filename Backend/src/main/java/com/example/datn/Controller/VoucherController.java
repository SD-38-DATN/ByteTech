package com.example.datn.Controller;

import com.example.datn.Config.JwtUtil;
import com.example.datn.DTO.TrangMuaHang.UserVoucherDTO;
import com.example.datn.DTO.TrangMuaHang.VoucherDTO;
import com.example.datn.Model.Users;
import com.example.datn.Repository.UsersRepository;
import com.example.datn.Service.UserVoucherService;
import com.example.datn.Service.VoucherService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/voucher")
public class VoucherController {

    private final UserVoucherService service;

    private final VoucherService voucherService ;
    private final UsersRepository userRepo;
    private final JwtUtil jwtUtil;



//    @GetMapping("/me")
//    public ResponseEntity<List<UserVoucherDTO>> getMyVouchers(HttpServletRequest request) {
//        // Lấy token từ header
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return ResponseEntity.status(401).build();
//        }
//        String token = authHeader.substring(7);
//
//        // Lấy username từ token
//        String username = jwtUtil.extractUsername(token);
//
//        // Lấy userId từ DB
//        Users user = userRepo.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // Query vouchers
//        List<UserVoucherDTO> result = service.getActiveVouchersByUser(user.getId())
//                .stream()
//                .map(uv -> UserVoucherDTO.builder()
//                        .id(uv.getId())
//                        .userId(uv.getUser().getId())
//                        .soLanSuDung(uv.getSoLanSuDung())
//                        .ngayNhan(uv.getNgayNhan())
//                        .trangThai(uv.getTrangThai())
//                        .voucher(VoucherDTO.builder()
//                                .id(uv.getVoucher().getId())
//                                .codeVoucher(uv.getVoucher().getCodeVoucher())
//                                .tenVoucher(uv.getVoucher().getTenVoucher())
//                                .soLanSuDung(uv.getVoucher().getSoLanSuDung())
//                                .moTa(uv.getVoucher().getMoTa())
//                                .loaiGiam(uv.getVoucher().getLoaiGiam())
//                                .giaTriGiam(uv.getVoucher().getGiaTriGiam())
//                                .dieuKienGiam(uv.getVoucher().getDieuKienGiam())
//                                .giamToiDa(uv.getVoucher().getGiamToiDa())
//                                .ngayBatDau(uv.getVoucher().getNgayBatDau())
//                                .ngayKetThuc(uv.getVoucher().getNgayKetThuc())
//                                .build())
//                        .build())
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(result);
//    }


    @GetMapping("/me")
    public ResponseEntity<List<UserVoucherDTO>> getMyVouchers(HttpServletRequest request) {
        // Lấy token từ header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }
        String token = authHeader.substring(7);

        // Lấy username từ token
        String username = jwtUtil.extractUsername(token);

        // Lấy userId từ DB
        Users user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // === THAY ĐỔI Ở ĐÂY ===
        // Gọi phương thức mới để lấy voucher với các trạng thái 1 và 2
        List<UserVoucherDTO> result = service.getVouchersByUserWithStatuses(user.getId())
                .stream()
                .map(uv -> UserVoucherDTO.builder()
                        .id(uv.getId())
                        .userId(uv.getUser().getId())
                        .soLanSuDung(uv.getSoLanSuDung())
                        .ngayNhan(uv.getNgayNhan())
                        .trangThai(uv.getTrangThai())
                        .voucher(VoucherDTO.builder()
                                .id(uv.getVoucher().getId())
                                .codeVoucher(uv.getVoucher().getCodeVoucher())
                                .tenVoucher(uv.getVoucher().getTenVoucher())
                                .soLanSuDung(uv.getVoucher().getSoLanSuDung())
                                .moTa(uv.getVoucher().getMoTa())
                                .loaiGiam(uv.getVoucher().getLoaiGiam())
                                .giaTriGiam(uv.getVoucher().getGiaTriGiam())
                                .dieuKienGiam(uv.getVoucher().getDieuKienGiam())
                                .giamToiDa(uv.getVoucher().getGiamToiDa())
                                .ngayBatDau(uv.getVoucher().getNgayBatDau())
                                .ngayKetThuc(uv.getVoucher().getNgayKetThuc())
                                .build())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
    @GetMapping("/active")
    public ResponseEntity<List<VoucherDTO>> getAllActiveVouchers() {
        // Gọi service, giờ đây nó sẽ trả về một List<VoucherDTO>
        List<VoucherDTO> activeVouchers = voucherService.getActiveVouchers();
        return ResponseEntity.ok(activeVouchers);
    }

    @GetMapping("/my-vouchers")
    public ResponseEntity<List<VoucherDTO>> getVoucherForMe(HttpServletRequest request) {
        // 1️⃣ Lấy token từ header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }
        String token = authHeader.substring(7);

        // 2️⃣ Lấy username từ token
        String username = jwtUtil.extractUsername(token);

        // 3️⃣ Tìm user trong DB
        Users user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 4️⃣ Lấy danh sách voucher theo userId (trangThai = 0 hoặc 1)
        List<VoucherDTO> vouchers = voucherService.getUserVouchers(user.getId());

        // 5️⃣ Trả về response
        return ResponseEntity.ok(vouchers);
    }



}
