package com.example.datn.Controller;

import com.example.datn.Config.JwtUtil;
import com.example.datn.DTO.TrangMuaHang.FeedBackPhuKienRequest;
import com.example.datn.DTO.TrangMuaHang.FeedBackResponseDTO;
import com.example.datn.Model.FeedBack;
import com.example.datn.Model.PhuKien;
import com.example.datn.Model.Users;
import com.example.datn.Repository.PhuKienRepository;
import com.example.datn.Repository.UsersRepository;
import com.example.datn.Service.FeedBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/feedback-phukien") // Đổi đường dẫn API để tránh trùng lặp
@CrossOrigin(origins = "*") // Cho phép gọi từ frontend
public class FeedbackPhuKienController {

    @Autowired
    private FeedBackService feedBackService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PhuKienRepository phuKienRepository; // Dùng repository của Phụ Kiện

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * API thêm một feedback mới cho phụ kiện
     */
    @PostMapping
    public ResponseEntity<?> addFeedback(
            @RequestBody FeedBackPhuKienRequest request, // Dùng request mới
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);

        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tìm phụ kiện thay vì sản phẩm
        PhuKien phuKien = phuKienRepository.findById(request.getMaPhuKien())
                .orElseThrow(() -> new RuntimeException("Accessory not found"));

        // Tạo feedback mới
        FeedBack feedback = FeedBack.builder()
                .user(user)
                .phuKien(phuKien) // Set phụ kiện thay vì sản phẩm
                .noiDung(request.getNoiDung())
                .danhGia(request.getDanhGia())
                .build();

        return ResponseEntity.ok(feedBackService.saveFeedback(feedback));
    }

    /**
     * API lấy tất cả feedback của một phụ kiện
     */
    @GetMapping("/phukien/{id}")
    public List<FeedBackResponseDTO> getFeedbacksByPhuKien(@PathVariable("id") Integer phuKienId) {
        PhuKien phuKien = new PhuKien();
        phuKien.setMaPhuKien(phuKienId);

        // Cần thêm phương thức getFeedbacksByPhuKien trong Service
        return feedBackService.getFeedbacksByPhuKien(phuKien)
                .stream()
                .map(fb -> new FeedBackResponseDTO(
                        fb.getId(),
                        fb.getNoiDung(),
                        fb.getDanhGia(),
                        fb.getNgayDanhGia(),
                        fb.getUser().getUsername(),
                        fb.getUser().getTenHienThi()
                ))
                .toList();
    }

    /**
     * API xóa một feedback theo ID
     * (Logic này có thể dùng chung với feedback sản phẩm nếu gộp chung controller)
     */
    @DeleteMapping("/{id}")
    public String deleteFeedback(@PathVariable Integer id) {
        feedBackService.deleteFeedback(id);
        return "Đã xóa feedback id = " + id;
    }
}
