package com.example.datn.Controller;



import com.example.datn.Service.KhuyenMaiService;
import com.example.datn.Service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupDataUpdater implements ApplicationRunner {

    private final KhuyenMaiService khuyenMaiService;
    private final VoucherService voucherService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("====== Chạy cập nhật trạng thái khuyến mãi khi khởi động ======");
        khuyenMaiService.capNhatTrangThai();

        voucherService.capNhatTrangThaiVoucher();
        System.out.println("================== Cập nhật khi khởi động hoàn tất ==================");
    }




}