package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.user.User;
import lombok.Data;

@Data
public class EvidenceFileVO {

    private String flowChartId;

    private String nodeId;

    private String flowChart;

    private String fileId;

    private String fileName;

    private String fileType;

    private String fileDownloadUrl;

    private long fileSize;

    private User personInCharge;

    private String uploadedDate;

    private String semester;

}
