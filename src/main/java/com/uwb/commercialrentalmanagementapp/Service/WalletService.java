package com.uwb.commercialrentalmanagementapp.Service;

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
        return userRepository.findWalletBalanceByUserId(userId);
    }


//    public void addToBalance(User user, BigDecimal amount) {
//        User existingUser = userRepository.findByUsername(user.getUsername());
//        if (existingUser != null) {
//            // Upewnij się, że wallet nie jest nullem
//            existingUser.setWallet(existingUser.getWallet() != null ? existingUser.getWallet() : BigDecimal.ZERO);
//            existingUser.setWallet(existingUser.getWallet().add(amount));
//            userRepository.save(existingUser);
//        } else {
//            throw new IllegalArgumentException("User not found.");
//        }
//    }


//    public void deductFromBalance(User user, BigDecimal amount) {
//        Wallet wallet = walletRepository.findByUser(user);
//        if (wallet != null && wallet.getBalance().compareTo(amount) >= 0) {
//            wallet.setBalance(wallet.getBalance().subtract(amount));
//            walletRepository.save(wallet);
//        } else {
//            throw new IllegalArgumentException("Insufficient funds in the wallet.");
//        }
//    }


}
