package com.qualitymanagementsystemfc.qualitymanagementsystem.repository;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.UserDO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserDO, String> {

    public Optional<UserDO> findByUsername(String username);

    public UserDO findByEmail(String email);

    public boolean existsByEmail(String email);

    public boolean existsByUsername(String username);

}
