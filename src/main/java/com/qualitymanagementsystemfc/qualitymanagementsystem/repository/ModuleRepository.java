package com.qualitymanagementsystemfc.qualitymanagementsystem.repository;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.ModuleDO;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ModuleRepository extends MongoRepository<ModuleDO, String> {

    public ModuleDO findByModuleName(String moduleName);

    public List<ModuleDO> findByViewPrivilegeContaining(String role);

    public boolean existsByModuleName(String moduleName);
}
