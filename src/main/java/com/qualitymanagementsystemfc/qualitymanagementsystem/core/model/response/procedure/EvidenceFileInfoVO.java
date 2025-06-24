package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.user.User;
import lombok.Data;

@Data
public class EvidenceFileInfoVO {

    private String fileId;

    private String fileName;

    private String fileType;

    private String fileDownloadUrl;

    private long fileSize;

    private User personInCharge;

    private String semester;
}
