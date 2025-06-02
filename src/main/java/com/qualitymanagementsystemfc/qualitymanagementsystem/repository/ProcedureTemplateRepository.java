package com.qualitymanagementsystemfc.qualitymanagementsystem.repository;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.ProcedureTemplateDO;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProcedureTemplateRepository extends MongoRepository<ProcedureTemplateDO, String> {
}
