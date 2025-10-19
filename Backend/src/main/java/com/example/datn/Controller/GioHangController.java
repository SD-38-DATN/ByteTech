//package com.example.datn.Controller;
//
//
//
//import com.example.datn.DTO.TrangMuaHang.AddToCartRequestDTO;
//
//import com.example.datn.DTO.TrangMuaHang.BienTheDTO;
//import com.example.datn.DTO.TrangMuaHang.GioHangChiTietDTO;
//
//import com.example.datn.DTO.TrangMuaHang.GioHangDTO;
//import com.example.datn.Service.GioHangService;
//import lombok.RequiredArgsConstructor;
//import java.util.Map;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/cart")
//@RequiredArgsConstructor
//public class GioHangController {
//
//    private final GioHangService gioHangService;
////
////     Lấy giỏ hàng theo token trong header Authorization
//    @GetMapping
//    public ResponseEntity<GioHangDTO> getCart(@RequestHeader("Authorization") String authHeader) {
//        try {
//            // Lấy token từ header "Bearer <token>"
//            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
//
//            // Gọi service để lấy giỏ hàng
//            GioHangDTO gioHangDTO = gioHangService.getCartByToken(token);
//
//            return ResponseEntity.ok(gioHangDTO);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(null);
//        }
//    }
//
//    @PutMapping("/update")
//    public ResponseEntity<?> updateSoLuong(@RequestBody List<GioHangChiTietDTO> updates) {
//        try {
//            gioHangService.updateSoLuong(updates);
//            return ResponseEntity.ok("Cập nhật số lượng thành công");
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    @DeleteMapping("/{chiTietId}")
//    public ResponseEntity<?> deleteChiTiet(@PathVariable Integer chiTietId) {
//        try {
//            gioHangService.deleteChiTiet(chiTietId);
//            return ResponseEntity.ok("Xóa chi tiết giỏ hàng thành công");
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//    @PostMapping("/add")
//    public ResponseEntity<?> addToCart(@RequestHeader("Authorization") String authHeader,
//                                       @RequestBody BienTheDTO bienTheDTO) {
//        try {
//            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
//            GioHangChiTietDTO chiTietDTO = gioHangService.addToCart(token, bienTheDTO);
//            return ResponseEntity.ok(chiTietDTO);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//
//
//
//
//}
package com.example.datn.Controller;



import com.example.datn.DTO.TrangMuaHang.BienTheDTO;
import com.example.datn.DTO.TrangMuaHang.GioHangChiTietDTO;

import com.example.datn.DTO.TrangMuaHang.GioHangDTO;
import com.example.datn.Service.GioHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class GioHangController {

    private final GioHangService gioHangService;
    //
//     Lấy giỏ hàng theo token trong header Authorization
    @GetMapping
    public ResponseEntity<GioHangDTO> getCart(@RequestHeader("Authorization") String authHeader) {
        try {
            // Lấy token từ header "Bearer <token>"
            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

            // Gọi service để lấy giỏ hàng
            GioHangDTO gioHangDTO = gioHangService.getCartByToken(token);

            return ResponseEntity.ok(gioHangDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateSoLuong(@RequestBody List<GioHangChiTietDTO> updates) {
        try {
            gioHangService.updateSoLuong(updates);
            return ResponseEntity.ok("Cập nhật số lượng thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{chiTietId}")
    public ResponseEntity<?> deleteChiTiet(@PathVariable Integer chiTietId) {
        try {
            gioHangService.deleteChiTiet(chiTietId);
            return ResponseEntity.ok("Xóa chi tiết giỏ hàng thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestHeader("Authorization") String authHeader,
                                       @RequestBody BienTheDTO bienTheDTO) {
        try {
            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
            GioHangChiTietDTO chiTietDTO = gioHangService.addToCart(token, bienTheDTO);
            return ResponseEntity.ok(chiTietDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }





}
