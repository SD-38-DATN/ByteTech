package com.example.datn.Service;

import com.example.datn.DTO.TrangMuaHang.VoucherDTO;
import com.example.datn.Repository.VoucherRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;

    /**
     * Phương thức này chứa logic chính để gọi repository
     * và cập nhật trạng thái của tất cả voucher.
     */
    public void capNhatTrangThaiVoucher() {
        int rows = voucherRepository.capNhatTrangThaiTheoNgayVoucher();
        System.out.println("Đã quét và cập nhật trạng thái cho " + rows + " voucher.");
    }

    /**
     * Scheduler sẽ tự động chạy vào lúc 00:05 (12 giờ 5 phút đêm) hàng ngày
     * để gọi phương thức capNhatTrangThaiVoucher() ở trên.
     */
    @Scheduled(cron = "0 5 0 * * ?")
    public void autoUpdateTrangThaiVoucher() {
        System.out.println("Scheduler đang chạy để cập nhật trạng thái voucher...");
        this.capNhatTrangThaiVoucher(); // Gọi phương thức cập nhật trong cùng lớp
    }


    public List<VoucherDTO> getActiveVouchers() {
        // Gọi phương thức mới trong repository và truyền vào trạng thái 1 (hoạt động)
        return voucherRepository.findActiveVouchersAsDTO(1);
    }

    public List<VoucherDTO> getUserVouchers(int userId) {
        return voucherRepository.findUserVouchers(userId);
    }

}
