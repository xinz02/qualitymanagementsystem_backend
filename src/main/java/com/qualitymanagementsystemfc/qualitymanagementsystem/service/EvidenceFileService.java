package com.qualitymanagementsystemfc.qualitymanagementsystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter.EvidenceFileConverter;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.procedure.EvidenceFileDTO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EvidenceFileService {

    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFsOperations gridFsOperations;
    @Autowired
    private EvidenceFileConverter evidenceFileConverter;

    /**
     * Upload evidence file
     *
     * @param evidenceInfoData
     * @param evidenceFile
     * @return
     * @throws IOException
     */
    public EvidenceFileVO uploadEvidence(String evidenceInfoData, MultipartFile evidenceFile) throws IOException {
        if (evidenceInfoData == null || evidenceInfoData.isEmpty() || evidenceFile == null || evidenceFile.isEmpty()) {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();

        EvidenceFileDTO evidenceFileDTO = objectMapper.readValue(evidenceInfoData, EvidenceFileDTO.class);

        Document metadata = new Document();
        metadata.put("contentType", evidenceFile.getContentType());
        metadata.put("originalName", evidenceFile.getOriginalFilename());
        metadata.put("flowChartId", evidenceFileDTO.getFlowChartId());
        metadata.put("nodeId", evidenceFileDTO.getNodeId());
        metadata.put("flowChart", evidenceFileDTO.getFlowChart());
        metadata.put("personInCharge", evidenceFileDTO.getPersonInCharge());
        metadata.put("semester", evidenceFileDTO.getSemester());

        String fileId = gridFsTemplate.store(
                evidenceFile.getInputStream(),
                evidenceFile.getOriginalFilename(),
                evidenceFile.getContentType(),
                metadata
        ).toString(); // returns fileId as string

        return evidenceFileConverter.convertToVO(fileId);
    }

    /**
     * Edit existing evidence file info - person in charge & semester
     *
     * @param fileId
     * @param evidenceInfoData
     * @return
     * @throws IOException
     */
    public EvidenceFileVO editEvidence(String fileId, String evidenceInfoData) throws IOException {
        if (evidenceInfoData == null || evidenceInfoData.isEmpty() || fileId == null || fileId.isEmpty()) {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();

        EvidenceFileDTO evidenceFileDTO = objectMapper.readValue(evidenceInfoData, EvidenceFileDTO.class);

        GridFSFile originalFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(new ObjectId(fileId))));
        if (originalFile == null) {
            throw new FileNotFoundException("File not found");
        }

        GridFsResource resource = gridFsTemplate.getResource(originalFile);

        byte[] fileContent = StreamUtils.copyToByteArray(resource.getInputStream());

        gridFsTemplate.delete(Query.query(Criteria.where("_id").is(new ObjectId(fileId))));

        Document metadata = new Document();
        metadata.put("contentType", originalFile.getMetadata().get("contentType"));
        metadata.put("originalName", originalFile.getFilename());
        metadata.put("flowChartId", evidenceFileDTO.getFlowChartId());
        metadata.put("nodeId", evidenceFileDTO.getNodeId());
        metadata.put("flowChart", evidenceFileDTO.getFlowChart());
        metadata.put("personInCharge", evidenceFileDTO.getPersonInCharge());
        metadata.put("semester", evidenceFileDTO.getSemester());

        InputStream inputStream = new ByteArrayInputStream(fileContent);
        String newFileId = gridFsTemplate.store(
                inputStream,
                originalFile.getFilename(),
                originalFile.getMetadata().get("contentType").toString(),
                metadata
        ).toString();

        return evidenceFileConverter.convertToVO(newFileId);
    }

//    public EvidenceFileVO editEvidence(String fileId, String evidenceInfoData) throws IOException {
//        if (evidenceInfoData == null || evidenceInfoData.isEmpty() || fileId == null || fileId.isEmpty()) {
//            return null;
//        }
//
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        EvidenceFileDTO evidenceFileDTO = objectMapper.readValue(evidenceInfoData, EvidenceFileDTO.class);
//
//        GridFSFile originalFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(new ObjectId(fileId))));
//
//        if (originalFile == null) {
//            throw new FileNotFoundException("File not found");
//        }
//
//        // Get the file's input stream
//        GridFsResource resource = gridFsTemplate.getResource(originalFile);
//        InputStream inputStream = resource.getInputStream();
//
//        // Delete the old file
//        gridFsTemplate.delete(Query.query(Criteria.where("_id").is(new ObjectId(fileId))));
//
//        // Prepare new metadata
//        Document metadata = new Document();
//        metadata.put("contentType", originalFile.getMetadata().get("contentType")); // preserve original
//        metadata.put("originalName", originalFile.getFilename()); // preserve original
//        metadata.put("flowChartId", evidenceFileDTO.getFlowChartId());
//        metadata.put("nodeId", evidenceFileDTO.getNodeId());
//        metadata.put("flowChart", evidenceFileDTO.getFlowChart());
//        metadata.put("personInCharge", evidenceFileDTO.getPersonInCharge());
//        metadata.put("semester", evidenceFileDTO.getSemester());
//
//        // Store the file again with updated metadata
//        String newFileId = gridFsTemplate.store(
//                inputStream,
//                originalFile.getFilename(),
//                originalFile.getMetadata().get("contentType").toString(),
//                metadata
//        ).toString();
//
//        return evidenceFileConverter.convertToVO(newFileId);
//    }

    /**
     * Get all evidence file by flow chart id
     *
     * @param flowChartId
     * @return
     */
    public FlowChartEvidenceFileListVO getEvidenceFileByFlowChartId(String flowChartId) {
        FlowChartEvidenceFileListVO evidenceFileListVO = new FlowChartEvidenceFileListVO();

        List<EvidenceFileVO> evidenceFileVOS = new ArrayList<>();

        // Query GridFS for files with matching flowChartId in metadata
        Query query = Query.query(Criteria.where("metadata.flowChartId").is(flowChartId));
        GridFSFindIterable foundFiles = gridFsTemplate.find(query);

        // Map<flowchartId, Map<nodeId, List<files>>>
        Map<String, Map<String, List<EvidenceFileInfoVO>>> groupedByFlowChartAndNode = new HashMap<>();

        for (GridFSFile file : foundFiles) {
            String flowChartType = (String) file.getMetadata().get("flowChart");
            String nodeId = (String) file.getMetadata().get("nodeId");
            EvidenceFileInfoVO vo = evidenceFileConverter.convertToInfoVO(file);

            groupedByFlowChartAndNode
                    .computeIfAbsent(flowChartType, k -> new HashMap<>()) // map for each flowchart
                    .computeIfAbsent(nodeId, k -> new ArrayList<>()) // list for each node
                    .add(vo);
        }

        long mainCount = groupedByFlowChartAndNode.keySet().stream()
                .filter(key -> key.trim().equalsIgnoreCase("mainflow"))
                .count();

        if (mainCount > 1) {
            throw new IllegalStateException("Duplicate 'mainFlowChart' entries found in flowchart evidence.");
        }

        FlowChartEvidenceFileVO mainFlowChart = new FlowChartEvidenceFileVO();
        List<FlowChartEvidenceFileVO> subFlowCharts = new ArrayList<>();

        for (Map.Entry<String, Map<String, List<EvidenceFileInfoVO>>> entry : groupedByFlowChartAndNode.entrySet()) {
            String flowChartType = entry.getKey();
            Map<String, List<EvidenceFileInfoVO>> nodeGroup = entry.getValue();

            List<NodeEvidenceFileInfoVO> nodeFiles = nodeGroup.entrySet().stream()
                    .map(nodeEntry -> {
                        NodeEvidenceFileInfoVO nodeVO = new NodeEvidenceFileInfoVO();
                        nodeVO.setNodeId(nodeEntry.getKey());
                        nodeVO.setNodeFiles(nodeEntry.getValue());
                        return nodeVO;
                    })
                    .collect(Collectors.toList());

            FlowChartEvidenceFileVO flowChartVO = new FlowChartEvidenceFileVO();
            flowChartVO.setFlowChart(flowChartType);
            flowChartVO.setNodeFiles(nodeFiles);

            if (flowChartType.trim().equalsIgnoreCase("mainflow")) {
                mainFlowChart = flowChartVO;
            } else {
                subFlowCharts.add(flowChartVO);
            }
        }

        evidenceFileListVO.setFlowChartId(flowChartId);
        evidenceFileListVO.setMainFlowChartEvidenceFile(mainFlowChart);
        evidenceFileListVO.setSubFlowChartsEvidenceFile(subFlowCharts);

        return evidenceFileListVO;
    }

    /**
     * Get all evidence files under specific node
     *
     * @param nodeId
     * @return
     */
    public EvidenceFileListVO getEvidenceFileByNodeId(String nodeId) {
        EvidenceFileListVO evidenceFileListVO = new EvidenceFileListVO();

        List<EvidenceFileVO> evidenceFileVOS = new ArrayList<>();

        // Query GridFS for files with matching nodeId in metadata
        Query query = Query.query(Criteria.where("metadata.nodeId").is(nodeId));
        GridFSFindIterable foundFiles = gridFsTemplate.find(query);

        for (GridFSFile file : foundFiles) {
            evidenceFileVOS.add(evidenceFileConverter.convertToVO(file.getObjectId().toString()));
        }

        evidenceFileListVO.setEvidenceFileListVOList(evidenceFileVOS);

        return evidenceFileListVO;
    }

    /**
     * Delete evidence file
     *
     * @param nodeId
     * @param fileId
     */
    @Transactional
    public void deleteEvidenceFile(String nodeId, String fileId) {

        if (nodeId == null && nodeId.isBlank() && fileId == null && fileId.isBlank()) {
            throw new IllegalArgumentException("Node or File is blank.");
        }

        GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));

        if (file == null) {
            throw new IllegalArgumentException("File not exists.");
        }

        // Delete the file from GridFS
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is(fileId)));
    }

    /**
     * Delete evidence file by flowchart id
     *
     * @param flowChartId
     */
    @Transactional
    public void deleteEvidenceFileByFlowChartId(String flowChartId) {
        if (flowChartId == null || flowChartId.isBlank()) {
            throw new IllegalArgumentException("FlowChart Id is blank.");
        }

        Query query = Query.query(Criteria.where("metadata.flowChartId").is(flowChartId));
        GridFSFindIterable foundFiles = gridFsTemplate.find(query);

        for (GridFSFile file : foundFiles) {
            ObjectId fileId = file.getObjectId();
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(fileId)));
        }
    }
}
