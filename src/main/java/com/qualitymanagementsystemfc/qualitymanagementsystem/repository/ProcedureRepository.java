package com.qualitymanagementsystemfc.qualitymanagementsystem.repository;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.UserDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.ProcedureDO;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProcedureRepository extends MongoRepository<ProcedureDO, String> {

    public boolean existsByProcedureNumber(String procedureNumber);

    public List<ProcedureDO> findByViewPrivilegeContaining(String role);

    //    public List<ProcedureDO> findByAssignToContaining(String userId);

    List<ProcedureDO> findByPindaanDokumenList_AssignToContaining(UserDO userDO);


//    public List<ProcedureDO> findByApprover_UserId(String userId);

    public boolean existsByCategory_CategoryId(String categoryId);

}
