package com.qualitymanagementsystemfc.qualitymanagementsystem.repository;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.flowchart.FlowChartDO;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FlowChartRepository extends MongoRepository<FlowChartDO, String> {
}
