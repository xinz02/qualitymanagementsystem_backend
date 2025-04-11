package com.qualitymanagementsystemfc.qualitymanagementsystem.repository;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {

    public PasswordResetToken findByToken(String token);

//    public PasswordResetToken findByUser_Id(String userId);
public PasswordResetToken findByUser_UserId(String userId);

    public void deleteById(String token);
}

