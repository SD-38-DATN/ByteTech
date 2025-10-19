package com.example.datn.Service;



import com.example.datn.Model.FeedBack;
import com.example.datn.Model.PhuKien;
import com.example.datn.Model.SanPham;
import com.example.datn.Model.Users;
import com.example.datn.Repository.FeedBackRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedBackService {

    private final FeedBackRepository feedBackRepository;

    public FeedBack saveFeedback(FeedBack feedback) {
        return feedBackRepository.save(feedback);
    }

    public List<FeedBack> getAllFeedbacks() {
        return feedBackRepository.findAll();
    }

    public List<FeedBack> getFeedbacksBySanPham(SanPham sanPham) {
        return feedBackRepository.findBySanPham(sanPham);
    }

    public List<FeedBack> getFeedbacksByPhuKien(PhuKien phuKien) {
        // Gọi đến phương thức đã khai báo trong repository
        return feedBackRepository.findByPhuKien(phuKien);
    }

    public List<FeedBack> getFeedbacksByUser(Users user) {
        return feedBackRepository.findByUser(user);
    }

    public void deleteFeedback(Integer id) {
        feedBackRepository.deleteById(id);
    }


    public Page<FeedBack> getFeedbacksBySanPham(Integer sanPhamId, Pageable pageable) {
        return feedBackRepository.findBySanPham_MaSanPham(sanPhamId, pageable);
    }

    public double getAverageRating(Integer sanPhamId) {
        Double avg = feedBackRepository.findAverageRatingBySanPhamId(sanPhamId);
        return avg != null ? avg : 0.0;
    }
}
