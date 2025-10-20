package com.example.datn.Repository;

import com.example.datn.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface KhachHangRepository extends JpaRepository<Users, Integer>, JpaSpecificationExecutor<Users> {
}
