package com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.user.User;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure.EvidenceFileInfoVO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure.EvidenceFileVO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Component;

@Component
public class EvidenceFileConverter {

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private UserService userService;

    @Autowired
    private GridFsOperations gridFsOperations;

    public EvidenceFileVO convertToVO(String fileId) {
        EvidenceFileVO evidenceFileVO = new EvidenceFileVO();

        GridFSFile savedFile = gridFsOperations.findOne(Query.query(Criteria.where("_id").is(fileId)));

        if (savedFile != null) {
            evidenceFileVO.setFileName(savedFile.getFilename());
            evidenceFileVO.setFileSize(savedFile.getLength());
            evidenceFileVO.setFileDownloadUrl("/procedure/file/" + savedFile.getObjectId());


            evidenceFileVO.setFileId(savedFile.getObjectId().toString());

            String fileType = savedFile.getMetadata() != null ? savedFile.getMetadata().getString("contentType") : null;
            evidenceFileVO.setFileType(fileType);

            String savedNodeId = savedFile.getMetadata() != null ? savedFile.getMetadata().getString("nodeId") : null;
            evidenceFileVO.setNodeId(savedNodeId);

            String savedFlowChartId = savedFile.getMetadata() != null ? savedFile.getMetadata().getString("flowChartId") : null;
            evidenceFileVO.setFlowChartId(savedFlowChartId);

            String savedFlowChart = savedFile.getMetadata() != null ? savedFile.getMetadata().getString("flowChart") : null;
            evidenceFileVO.setFlowChart(savedFlowChart);

            String savedPersonInCharge = savedFile.getMetadata() != null ? savedFile.getMetadata().getString("personInCharge") : null;
            User personInCharge = userConverter.convertDOToModel(userService.findByUserId(savedPersonInCharge));
            evidenceFileVO.setPersonInCharge(personInCharge);

            String semester = savedFile.getMetadata() != null ? savedFile.getMetadata().getString("semester") : null;
            evidenceFileVO.setSemester(semester);

        } else {
            throw new IllegalStateException("File stored but not found in GridFS.");
        }

        return evidenceFileVO;
    }

    public EvidenceFileInfoVO convertToInfoVO(GridFSFile file) {
        EvidenceFileInfoVO evidenceFileInfoVO = new EvidenceFileInfoVO();

        evidenceFileInfoVO.setFileId(file.getObjectId().toString());
        evidenceFileInfoVO.setFileName(file.getFilename());

        String fileType = file.getMetadata() != null ? file.getMetadata().getString("contentType") : null;
        evidenceFileInfoVO.setFileType(fileType);

        evidenceFileInfoVO.setFileDownloadUrl("/procedure/file/" + file.getObjectId());
        evidenceFileInfoVO.setFileSize(file.getLength());

        String savedPersonInCharge = file.getMetadata() != null ? file.getMetadata().getString("personInCharge") : null;
        User personInCharge = userConverter.convertDOToModel(userService.findByUserId(savedPersonInCharge));
        evidenceFileInfoVO.setPersonInCharge(personInCharge);

        String semester = file.getMetadata() != null ? file.getMetadata().getString("semester") : null;
        evidenceFileInfoVO.setSemester(semester);

        return evidenceFileInfoVO;
    }
}
