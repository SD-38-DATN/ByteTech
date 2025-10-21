package com.example.datn.Repository;

import com.example.datn.Model.GioHang;
import com.example.datn.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByUsername(String username);

    Optional<Users> findByEmail(String email);




    @Query("SELECT u FROM Users u WHERE u.role.roleName = :roleName")
    List<Users> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT u FROM Users u WHERE u.role.roleName = 'USER'")
    List<Users> findAllUsers();

    @Query("SELECT u FROM Users u WHERE u.role.roleName = 'ADMIN'")
    List<Users> findAllAdmins();


    @Query("SELECT u FROM Users u WHERE u.role.id = 3")
    List<Users> findAllNhanVien();

    @Query("SELECT u FROM Users u WHERE u.role.id = 3 AND (u.hoTen LIKE %:keyword% OR u.username LIKE %:keyword%)")
    List<Users> searchNhanVien(@Param("keyword") String keyword);
    //check trùng dùng khi thêm mới
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsBySoDienThoai(String soDienThoai);
    boolean existsByTenHienThi(String tenHienThi);
    //check trùng vời ng khác
    boolean existsByUsernameAndIdNot(String username, Integer id);
    boolean existsByEmailAndIdNot(String email, Integer id);
    boolean existsBySoDienThoaiAndIdNot(String soDienThoai, Integer id);
    boolean existsByTenHienThiAndIdNot(String tenHienThi, Integer id);


}
