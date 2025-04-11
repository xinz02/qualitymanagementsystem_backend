package com.qualitymanagementsystemfc.qualitymanagementsystem.service;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.PasswordResetToken;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.UserDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.repository.PasswordResetTokenRepository;
import com.qualitymanagementsystemfc.qualitymanagementsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class PasswordResetTokenService {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public String createToken(UserDO user) {
        PasswordResetToken existedtoken = passwordResetTokenRepository.findByUser_UserId(user.getUserId());

        if(existedtoken != null) {
            return null;
        }

        // Generate a random token
        String token = UUID.randomUUID().toString();

        // Create expiry date (1 hour)
        Date expiryDate = new Date(System.currentTimeMillis() + 3600000);

        // Save token to database
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(expiryDate);
        passwordResetTokenRepository.save(resetToken);

        return token;
    }

    public boolean isTokenValid(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        return resetToken != null && resetToken.getExpiryDate().after(new Date());
    }

    public PasswordResetToken getPasswordTokenByToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    public void deleteToken(String token) {
        passwordResetTokenRepository.deleteById(token);
    }

}
