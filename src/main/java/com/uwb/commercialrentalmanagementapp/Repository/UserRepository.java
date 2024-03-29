package com.uwb.commercialrentalmanagementapp.Repository;

import com.uwb.commercialrentalmanagementapp.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    @Query("SELECT u.role FROM User u WHERE u.Id = :userId")
    String findRole(@Param("userId") Long userId);

    List<User> findAll();

    @Query("SELECT u.wallet FROM User u WHERE u.Id = :userId")
    BigDecimal findWalletBalanceByUserId(@Param("userId") Long userId);

}
