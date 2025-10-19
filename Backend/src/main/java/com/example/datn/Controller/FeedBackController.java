package com.example.datn.Controller;



import com.example.datn.Config.JwtUtil;
import com.example.datn.DTO.TrangMuaHang.FeedBackRequest;
import com.example.datn.DTO.TrangMuaHang.FeedBackResponseDTO;
import com.example.datn.Model.FeedBack;
import com.example.datn.Model.SanPham;
import com.example.datn.Model.Users;
import com.example.datn.Repository.SanPhamRepository;
import com.example.datn.Repository.UsersRepository;
import com.example.datn.Service.FeedBackService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedBackController {

    private final FeedBackService feedBackService;
    private final UsersRepository usersRepository;
    private final SanPhamRepository sanPhamRepository;
    private final JwtUtil jwtUtil;

    /**
     * API thêm phản hồi (user phải login, gửi kèm token)
     */
    @PostMapping
    public ResponseEntity<?> addFeedback(
            @RequestBody FeedBackRequest request,
            @RequestHeader("Authorization") String authHeader) {

        // Lấy token từ header
        String token = authHeader.replace("Bearer ", "");

        // Giải mã để lấy username
        String username = jwtUtil.extractUsername(token);

        // Tìm user trong DB
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tìm sản phẩm
        SanPham sanPham = sanPhamRepository.findById(request.getMaSanPham())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Tạo feedback mới
        FeedBack feedback = FeedBack.builder()
                .user(user)
                .sanPham(sanPham)
                .noiDung(request.getNoiDung())
                .danhGia(request.getDanhGia())
                .build();

        return ResponseEntity.ok(feedBackService.saveFeedback(feedback));
    }

    /**
     * Lấy tất cả feedback
     */
    @GetMapping
    public List<FeedBack> getAllFeedbacks() {
        return feedBackService.getAllFeedbacks();
    }

    /**
     * Lấy feedback theo sản phẩm
     */
    @GetMapping("/sanpham/{id}")
    public List<FeedBackResponseDTO> getFeedbacksBySanPham(@PathVariable("id") Integer sanPhamId) {
        SanPham sanPham = new SanPham();
        sanPham.setMaSanPham(sanPhamId);

        return feedBackService.getFeedbacksBySanPham(sanPham)
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
     * Lấy feedback theo user (từ token)
     */
    @GetMapping("/me")
    public List<FeedBack> getMyFeedbacks(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);

        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return feedBackService.getFeedbacksByUser(user);
    }

    /**
     * Xóa feedback (Admin mới có quyền)
     */
    @DeleteMapping("/{id}")
    public String deleteFeedback(@PathVariable Integer id) {
        feedBackService.deleteFeedback(id);
        return "Đã xóa feedback id = " + id;
    }



    /**
     * Lấy feedback theo sản phẩm + phân trang
     */
    @GetMapping("/sanphamfeedback/{id}")
    public ResponseEntity<?> getFeedbacksBySanPham(
            @PathVariable("id") Integer sanPhamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayDanhGia").descending());

        var feedbackPage = feedBackService.getFeedbacksBySanPham(sanPhamId, pageable)
                .map(fb -> new FeedBackResponseDTO(
                        fb.getId(),
                        fb.getNoiDung(),
                        fb.getDanhGia(),
                        fb.getNgayDanhGia(),
                        fb.getUser().getUsername(),
                        fb.getUser().getTenHienThi()
                ));

        // Tính trung bình rating
        double averageRating = feedbackPage.getContent().isEmpty()
                ? 0.0
                : feedbackPage.getContent().stream()
                .mapToInt(FeedBackResponseDTO::getDanhGia)
                .average()
                .orElse(0.0);

        return ResponseEntity.ok(
                new java.util.HashMap<>() {{
                    put("feedbacks", feedbackPage.getContent());
                    put("currentPage", feedbackPage.getNumber());
                    put("totalPages", feedbackPage.getTotalPages());
                    put("totalElements", feedbackPage.getTotalElements());
                    put("averageRating", String.format("%.1f", averageRating));
                }}
        );
    }



}
