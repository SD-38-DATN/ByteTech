package com.example.datn.Repository.spec;

import com.example.datn.Model.Role;
import com.example.datn.Model.Users;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class KhachHangSpecs {

    public static Specification<Users> searchLike(String q) {
        if (q == null || q.isBlank()) return null;
        String kw = "%" + q.trim().toLowerCase() + "%";
        return (root, cq, cb) -> cb.or(
                cb.like(cb.lower(root.get("email")), kw),
                cb.like(cb.lower(root.get("hoTen")), kw),
                cb.like(cb.lower(root.get("tenHienThi")), kw),
                cb.like(cb.lower(root.get("soDienThoai")), kw),
                cb.like(cb.lower(root.get("username")), kw)
        );
    }

    /**
     * gender: "1"=Nam, "0"=Nữ, "2"=Khác (null hoặc không 0/1)
     */
    public static Specification<Users> genderEq(String gender) {
        if (gender == null || gender.isBlank()) return null;
        return (root, cq, cb) -> {
            if ("1".equals(gender) || "0".equals(gender)) {
                return cb.equal(root.get("gioiTinh"), Integer.valueOf(gender));
            }
            // "2" => khác 0/1 hoặc null
            return cb.or(
                    cb.isNull(root.get("gioiTinh")),
                    cb.not(root.get("gioiTinh").in(0, 1))
            );
        };
    }

    public static Specification<Users> statusEq(String status) {
        if (status == null || status.isBlank()) return null;
        return (root, cq, cb) -> cb.equal(root.get("trangThai"), status.trim().toUpperCase());
    }

    // Dùng LEFT JOIN để không nổ khi role null, và lọc roleName = 'USER'
    public static Specification<Users> roleIsUser() {
        return (root, cq, cb) -> {
            Join<Users, Role> j = root.join("role", JoinType.LEFT);
            return cb.equal(j.get("roleName"), "USER");
        };
    }
}
