package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure;

import lombok.Data;

import java.util.List;

@Data
public class NodeEvidenceFileInfoVO {

    private String nodeId;

    private List<EvidenceFileInfoVO> nodeFiles;
}
