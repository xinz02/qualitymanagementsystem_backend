package com.qualitymanagementsystemfc.qualitymanagementsystem.repository;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.form.FormDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.ProcedureDO;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FormRepository extends MongoRepository<FormDO, String> {

    public boolean existsByFormNumber(String formNumber);

    public List<FormDO> findByViewPrivilegeContaining(String role);

    public List<FormDO> findByPersonInCharge_UserId(String userId);
}
