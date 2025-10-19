//package com.example.datn.Service;
//
//
//
//
//import com.example.datn.Config.NotFoundException;
//import com.example.datn.DTO.CRUDthongso.LoaiThongSoDetailDTO;
//import com.example.datn.Model.DanhMuc;
//import com.example.datn.Model.LoaiThongSo;
//import com.example.datn.Repository.DanhMucRepository;
//import com.example.datn.Repository.LoaiThongSoRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class LoaiThongSoService {
//
//    private final LoaiThongSoRepository loaiThongSoRepo;
//    private final DanhMucRepository danhMucRepo;
//
//    @Transactional(readOnly = true)
//    public List<LoaiThongSoDetailDTO> getAll() {
//        return loaiThongSoRepo.findAll().stream()
//                .map(this::mapToDetailDTO)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional
//    public LoaiThongSo create(LoaiThongSoDetailDTO dto) {
//        DanhMuc danhMuc = danhMucRepo.findById(dto.getMaDanhMuc())
//                .orElseThrow(() -> new NotFoundException("Không tìm thấy Danh mục với ID: " + dto.getMaDanhMuc()));
//
//        LoaiThongSo newLTS = new LoaiThongSo();
//        newLTS.setTenLoaiThongSo(dto.getTenLoaiThongSo());
//        newLTS.setDanhMuc(danhMuc);
//
//        return loaiThongSoRepo.save(newLTS);
//    }
//
//    @Transactional
//    public LoaiThongSo update(Integer id, LoaiThongSoDetailDTO dto) {
//        LoaiThongSo existingLTS = loaiThongSoRepo.findById(id)
//                .orElseThrow(() -> new NotFoundException("Không tìm thấy Loại thông số với ID: " + id));
//
//        DanhMuc danhMuc = danhMucRepo.findById(dto.getMaDanhMuc())
//                .orElseThrow(() -> new NotFoundException("Không tìm thấy Danh mục với ID: " + dto.getMaDanhMuc()));
//
//        existingLTS.setTenLoaiThongSo(dto.getTenLoaiThongSo());
//        existingLTS.setDanhMuc(danhMuc);
//
//        return loaiThongSoRepo.save(existingLTS);
//    }
//
//    @Transactional
//    public void delete(Integer id) {
//        // Cẩn trọng: Nên có kiểm tra xem LoaiThongSo này đã được sử dụng ở
//        // bảng ThongSoKyThuat chưa trước khi xóa để tránh lỗi khóa ngoại.
//        if (!loaiThongSoRepo.existsById(id)) {
//            throw new NotFoundException("Không tìm thấy Loại thông số với ID: " + id);
//        }
//        loaiThongSoRepo.deleteById(id);
//    }
//
//    private LoaiThongSoDetailDTO mapToDetailDTO(LoaiThongSo lts) {
//        return new LoaiThongSoDetailDTO(
//                lts.getLoaiThongSoId(),
//                lts.getTenLoaiThongSo(),
//                lts.getDanhMuc() != null ? lts.getDanhMuc().getMaDanhMuc() : null,
//                lts.getDanhMuc() != null ? lts.getDanhMuc().getTenDanhMuc() : "N/A"
//        );
//    }
//}
package com.example.datn.Service.ThongSoAdmin;

import com.example.datn.Config.NotFoundException;
import com.example.datn.DTO.CRUDthongso.LoaiThongSoDetailDTO;
import com.example.datn.Model.DanhMuc;
import com.example.datn.Model.DanhMucPhuKien;
import com.example.datn.Model.LoaiThongSo;
import com.example.datn.Repository.DanhMucPhuKienRepository;
import com.example.datn.Repository.DanhMucRepository;
import com.example.datn.Repository.LoaiThongSoRepository;
import com.example.datn.Repository.ThongSoKyThuatRepository; // Thêm import này
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoaiThongSoService {

    private final LoaiThongSoRepository loaiThongSoRepo;
    private final DanhMucRepository danhMucRepo;
    private final DanhMucPhuKienRepository danhMucPhuKienRepo;
    private final ThongSoKyThuatRepository thongSoKyThuatRepo; // Thêm repo này để kiểm tra ràng buộc

    @Transactional(readOnly = true)
    public List<LoaiThongSoDetailDTO> getAll() {
        return loaiThongSoRepo.findAll().stream()
                .map(this::mapToDetailDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public LoaiThongSo create(LoaiThongSoDetailDTO dto) {
        LoaiThongSo newLTS = new LoaiThongSo();
        newLTS.setTenLoaiThongSo(dto.getTenLoaiThongSo());

        // Liên kết với Danh Mục Sản Phẩm hoặc Danh Mục Phụ Kiện
        if (dto.getMaDanhMuc() != null) {
            DanhMuc danhMuc = danhMucRepo.findById(dto.getMaDanhMuc())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy Danh mục với ID: " + dto.getMaDanhMuc()));
            newLTS.setDanhMuc(danhMuc);
        } else if (dto.getMaDanhMucPhuKien() != null) {
            DanhMucPhuKien dmpk = danhMucPhuKienRepo.findById(dto.getMaDanhMucPhuKien())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy Danh mục Phụ kiện với ID: " + dto.getMaDanhMucPhuKien()));
            newLTS.setDanhMucPhuKien(dmpk);
        } else {
            throw new IllegalArgumentException("Phải cung cấp mã danh mục sản phẩm hoặc mã danh mục phụ kiện.");
        }

        return loaiThongSoRepo.save(newLTS);
    }

    /**
     * Phương thức UPDATE đã có sẵn
     */
    @Transactional
    public LoaiThongSo update(Integer id, LoaiThongSoDetailDTO dto) {
        LoaiThongSo existingLTS = loaiThongSoRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy Loại thông số với ID: " + id));

        existingLTS.setTenLoaiThongSo(dto.getTenLoaiThongSo());
        // Logic cập nhật danh mục tương tự như hàm create
        if (dto.getMaDanhMuc() != null) {
            DanhMuc danhMuc = danhMucRepo.findById(dto.getMaDanhMuc())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy Danh mục với ID: " + dto.getMaDanhMuc()));
            existingLTS.setDanhMuc(danhMuc);
            existingLTS.setDanhMucPhuKien(null); // Đảm bảo chỉ có 1 liên kết
        } else if (dto.getMaDanhMucPhuKien() != null) {
            DanhMucPhuKien dmpk = danhMucPhuKienRepo.findById(dto.getMaDanhMucPhuKien())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy Danh mục Phụ kiện với ID: " + dto.getMaDanhMucPhuKien()));
            existingLTS.setDanhMucPhuKien(dmpk);
            existingLTS.setDanhMuc(null); // Đảm bảo chỉ có 1 liên kết
        }

        return loaiThongSoRepo.save(existingLTS);
    }

    /**
     * Phương thức DELETE đã được thêm vào và hoàn thiện
     */
    @Transactional
    public void delete(Integer id) {
        // 1. Kiểm tra xem LoaiThongSo có tồn tại không
        if (!loaiThongSoRepo.existsById(id)) {
            throw new NotFoundException("Không tìm thấy Loại thông số với ID: " + id);
        }

        // 2. Kiểm tra xem LoaiThongSo này có đang được sử dụng ở đâu không
        boolean isUsed = thongSoKyThuatRepo.existsByLoaiThongSo_LoaiThongSoId(id);
        if (isUsed) {
            // Nếu đã được sử dụng, không cho phép xóa và báo lỗi
            throw new IllegalStateException("Không thể xóa. Loại thông số này đã được sử dụng.");
        }

        // 3. Nếu không được sử dụng, tiến hành xóa
        loaiThongSoRepo.deleteById(id);
    }

    private LoaiThongSoDetailDTO mapToDetailDTO(LoaiThongSo lts) {
        LoaiThongSoDetailDTO dto = new LoaiThongSoDetailDTO();
        dto.setLoaiThongSoId(lts.getLoaiThongSoId());
        dto.setTenLoaiThongSo(lts.getTenLoaiThongSo());

        if (lts.getDanhMuc() != null) {
            dto.setMaDanhMuc(lts.getDanhMuc().getMaDanhMuc());
            dto.setTenDanhMuc(lts.getDanhMuc().getTenDanhMuc());
        }
        if (lts.getDanhMucPhuKien() != null) {
            dto.setMaDanhMucPhuKien(lts.getDanhMucPhuKien().getMaDanhMucPhuKien());
            dto.setTenDanhMucPhuKien(lts.getDanhMucPhuKien().getTenDanhMucPhuKien());
        }
        return dto;
    }
}