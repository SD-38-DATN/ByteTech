package com.example.datn.Service;

import com.example.datn.DTO.CRUDphukien.PhuKienDTO;
import com.example.datn.DTO.PhuKienDTO.PhuKienChiTietDTO;
import com.example.datn.DTO.TrangMuaHang.TrangChuPhuKien;
import com.example.datn.Model.PhuKien;
import com.example.datn.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhuKienService {

    private final PhuKienRepository phuKienRepo;
    private final BienThePhuKienRepository bienThePhuKienRepo;
    private final ThuocTinhPhuKienRepository thuocTinhPhuKienRepo;

    private final LoaiThongSoRepository loaiThongSoRepository ;

    private final FeedBackRepository feedBackRepository   ;



    public List<TrangChuPhuKien> getAllPhuKien() {
        return phuKienRepo.findAll().stream().map(pk -> {
            TrangChuPhuKien dto = new TrangChuPhuKien();
            dto.setMaPhuKien(pk.getMaPhuKien());
            dto.setTenPhuKien(pk.getTenPhuKien());
            dto.setMoTa(pk.getMoTa());
            dto.setSoLuong(pk.getSoLuong());
            dto.setGia(pk.getGia());

            if (pk.getDanhMucPhuKien() != null) {
                dto.setTenDanhMucPhuKien(pk.getDanhMucPhuKien().getTenDanhMucPhuKien());
            }
            return dto;
        }).collect(Collectors.toList());
    }

//    public PhuKienChiTietDTO getPhuKienChiTiet(int id) {
//        PhuKien pk = phuKienRepo.findById(id).orElseThrow();
//
//        // Lấy biến thể theo mã phụ kiện
//        List<BienThePhuKien> bienTheList = bienThePhuKienRepo.findByPhuKien_MaPhuKien(id);
//
//        // Lọc biến thể trạng thái = 1
//        List<BienThePhuKienDTO> bienTheDTOs = bienTheList.stream()
//                .filter(bt -> bt.getTrangThai() != null && bt.getTrangThai() == 1)
//                .map(bt -> {
//                    // Lấy thuộc tính theo mã SKU phụ kiện
//                    List<ThuocTinhPhuKien> ttList = thuocTinhPhuKienRepo.findByBienThePhuKien_MaSKUPhuKien(bt.getMaSKUPhuKien());
//
//                    Map<String, String> thuocTinhMap = ttList.stream()
//                            .collect(Collectors.toMap(
//                                    ThuocTinhPhuKien::getTenThuocTinh,
//                                    ThuocTinhPhuKien::getGiaTriThuocTinh
//                            ));
//
//                    return new BienThePhuKienDTO(
//                            bt.getMaSKUPhuKien(),
//                            bt.getGia(),
//                            bt.getSoLuong(),
//                            bt.getTrangThai(),
//                            thuocTinhMap
//                    );
//                })
//                .toList();
//
//        return new PhuKienChiTietDTO(
//                pk.getMaPhuKien(),
//                pk.getTenPhuKien(),
//                pk.getMoTa(),
//                pk.getDanhMucPhuKien().getTenDanhMucPhuKien(),
//                bienTheDTOs
//        );
//    }

    public PhuKienChiTietDTO getPhuKienChiTiet(int id) {
        // 1. Lấy phụ kiện gốc từ database
        PhuKien pk = phuKienRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phụ kiện với ID: " + id));

        // 2. Map danh sách biến thể phụ kiện (BienThePhuKien -> BienThePhuKienDTO)
        List<PhuKienChiTietDTO.BienThePhuKienDTO> bienTheDTOs = pk.getBienThePhuKienList().stream()
                .filter(bt -> bt.getTrangThai() != null && bt.getTrangThai() == 1) // Chỉ lấy biến thể đang hoạt động
                .map(bt -> {
                    // <<< SỬA LẠI: Chuyển đổi từ List<ThuocTinhPhuKien> sang List<ThuocTinhPhuKienDTO>
                    List<PhuKienChiTietDTO.ThuocTinhPhuKienDTO> thuocTinhDTOs = bt.getThuocTinhPhuKienList().stream()
                            .map(tt -> new PhuKienChiTietDTO.ThuocTinhPhuKienDTO(
                                    tt.getTenThuocTinh(),
                                    tt.getGiaTriThuocTinh()
                            ))
                            .collect(Collectors.toList());

                    return new PhuKienChiTietDTO.BienThePhuKienDTO(
                            bt.getMaSKUPhuKien(),
                            bt.getGia(),
                            bt.getSoLuong(),
                            bt.getTrangThai(),
                            thuocTinhDTOs // <<< SỬA LẠI: Truyền vào List DTO
                    );
                })
                .collect(Collectors.toList());

        // 3. Lấy và lọc thông số kỹ thuật theo đúng phụ kiện hiện tại
        Integer maDanhMucPhuKien = (pk.getDanhMucPhuKien() != null) ? pk.getDanhMucPhuKien().getMaDanhMucPhuKien() : null;
        List<PhuKienChiTietDTO.LoaiThongSoDTO> loaiThongSoDTOs = Collections.emptyList();

        if (maDanhMucPhuKien != null) {
            loaiThongSoDTOs = loaiThongSoRepository.findByDanhMucPhuKien_MaDanhMucPhuKien(maDanhMucPhuKien)
                    .stream()
                    .map(loai -> {
                        // Lọc thông số kỹ thuật CHỈ thuộc về phụ kiện đang xem
                        List<PhuKienChiTietDTO.ThongSoKyThuatDTO> thongSoDTOs =
                                loai.getThongSoKyThuatList().stream()
                                        .filter(ts -> ts.getPhuKien() != null && ts.getPhuKien().getMaPhuKien() == id)
                                        .map(ts -> new PhuKienChiTietDTO.ThongSoKyThuatDTO(
                                                ts.getTenThongSo(),
                                                ts.getGiaTriThongSo(),
                                                ts.getTrangThai()
                                        ))
                                        .collect(Collectors.toList());

                        // Chỉ trả về loại thông số nếu nó có thông số liên quan đến phụ kiện này
                        if (!thongSoDTOs.isEmpty()) {
                            return new PhuKienChiTietDTO.LoaiThongSoDTO(loai.getTenLoaiThongSo(), thongSoDTOs);
                        }
                        return null;
                    })
                    .filter(dto -> dto != null) // Loại bỏ các nhóm thông số rỗng
                    .collect(Collectors.toList());
        }

        // 4. Lấy danh sách feedback cho phụ kiện
        List<PhuKienChiTietDTO.FeedbackDTO> feedbackDTOs = feedBackRepository.findByPhuKien_MaPhuKien(id).stream()
                .map(fb -> new PhuKienChiTietDTO.FeedbackDTO(
                        fb.getUser().getId(),
                        fb.getNoiDung(),
                        fb.getDanhGia(),
                        fb.getNgayDanhGia()
                ))
                .collect(Collectors.toList());


        // 5. Build DTO cuối cùng để trả về
        PhuKienChiTietDTO chiTietDTO = new PhuKienChiTietDTO();
        chiTietDTO.setMaPhuKien(pk.getMaPhuKien());
        chiTietDTO.setTenPhuKien(pk.getTenPhuKien());
        chiTietDTO.setMoTa(pk.getMoTa());
        chiTietDTO.setMaDanhMucPhuKien(maDanhMucPhuKien);
        chiTietDTO.setTenDanhMucPhuKien(pk.getDanhMucPhuKien() != null ? pk.getDanhMucPhuKien().getTenDanhMucPhuKien() : null);
        chiTietDTO.setBienTheList(bienTheDTOs);
        chiTietDTO.setLoaiThongSoList(loaiThongSoDTOs); // ✅ Bổ sung
        chiTietDTO.setFeedbackList(feedbackDTOs);     // ✅ Bổ sung

        return chiTietDTO;
    }


    public List<PhuKienDTO> getPhuKienByDanhMucId(Integer maDanhMucPhuKien) {
        return phuKienRepo.getPhuKienByDanhMucId(maDanhMucPhuKien);
    }








}
