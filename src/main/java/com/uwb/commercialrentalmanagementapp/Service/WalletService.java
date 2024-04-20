package com.uwb.commercialrentalmanagementapp.Service;

import com.uwb.commercialrentalmanagementapp.Model.User;
import com.uwb.commercialrentalmanagementapp.Repository.UserRepository;
import com.uwb.commercialrentalmanagementapp.Repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    @Autowired
    public WalletService(WalletRepository walletRepository, UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    public BigDecimal getBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return user.getWallet();
    }

    public void deductFromBalance(User user, BigDecimal amount) {
        if (user.getWallet().compareTo(amount) >= 0) {
            user.setWallet(user.getWallet().subtract(amount));
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Insufficient funds in the wallet.");
        }
    }
}
