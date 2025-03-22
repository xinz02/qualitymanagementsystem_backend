package com.qualitymanagementsystemfc.qualitymanagementsystem.repository;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.UserDO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<UserDO, String> {

    public UserDO findByName(String name);
}
