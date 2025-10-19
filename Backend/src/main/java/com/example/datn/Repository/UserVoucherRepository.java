package com.example.datn.Repository;

import com.example.datn.Model.UserVoucher;
import com.example.datn.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserVoucherRepository extends JpaRepository<UserVoucher, Integer> {


    List<UserVoucher> findByUserIdAndTrangThai(Integer userId, Integer trangThai);

    List<UserVoucher> findByUserIdAndTrangThaiIn(Integer userId, Collection<Integer> trangThais);

    boolean existsByUserIdAndVoucherId(Integer userId, Integer voucherId);
}

