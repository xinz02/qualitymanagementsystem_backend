package com.qualitymanagementsystemfc.qualitymanagementsystem.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.result.DeleteResult;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter.FlowChartConverter;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.flowchart.FlowChartDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.procedure.flowchart.Edge;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.procedure.flowchart.FlowChartData;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.procedure.flowchart.Node;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.procedure.FlowChartDTO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.repository.FlowChartRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class FlowChartService {

    @Autowired
    private FlowChartRepository flowChartRepository;

    @Autowired
    private FlowChartConverter flowChartConverter;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations gridFsOperations;

    @Autowired
    private EvidenceFileService evidenceFileService;

    /**
     * Add new flowchart
     * @param flowChartDTO
     * @return
     */
    @Transactional
    public FlowChartDO addFlowChart(FlowChartDTO flowChartDTO) {
        if(flowChartDTO == null) {

            return flowChartRepository.save(new FlowChartDO());
        }

        FlowChartDO flowChartDO = flowChartConverter.convertDTOToDO(flowChartDTO);

        return flowChartRepository.save(flowChartDO);
    }

    /**
     * Edit existing flow chart
     * @param flowChartId
     * @param editFlowChartDTO
     * @return
     */
    @Transactional
    public FlowChartDO editFlowChart(String flowChartId, FlowChartDTO editFlowChartDTO) {

        FlowChartDO existingFlowChartDO = flowChartRepository.findById(flowChartId).orElseThrow(() -> new IllegalArgumentException("Procedure Template not found."));

        existingFlowChartDO = flowChartConverter.convertExistingDOFromDTO(existingFlowChartDO, editFlowChartDTO);

        return flowChartRepository.save(existingFlowChartDO);
    }

    /**
     * Delete flow chart
     * @param flowChartId
     * @return
     */
    @Transactional
    public boolean deleteFlowChart(String flowChartId) {
        FlowChartDO existingFlowChartDO = flowChartRepository.findById(flowChartId).orElseThrow(() -> new IllegalArgumentException("Procedure Template not found."));

        evidenceFileService.deleteEvidenceFileByFlowChartId(flowChartId);
//        flowChartRepository.delete(existingFlowChartDO);
//
//        return flowChartRepository.findById(flowChartId).isEmpty();

        DeleteResult result = mongoTemplate.remove(Query.query(Criteria.where("_id").is(existingFlowChartDO.getFlowChartId())), FlowChartDO.class);

        return result.getDeletedCount() > 0;
    }

    public FlowChartDO createNewFlowChartVersion(FlowChartDO latestVersion) {
        FlowChartDO flowChartDO = new FlowChartDO();
        String flowChartId = "flowchart_" + UUID.randomUUID().toString();
        flowChartDO.setFlowChartId(flowChartId);


        FlowChartData newMainFlow = createNewFlowChartData(latestVersion.getMainFlowChart(), flowChartId);
        flowChartDO.setMainFlowChart(newMainFlow);

        List<FlowChartData> newSubFlow = latestVersion.getSubFlowCharts().stream()
                .map(flowchart -> createNewFlowChartData(flowchart, flowChartId))
                .toList();
        flowChartDO.setSubFlowCharts(newSubFlow);

        return flowChartRepository.save(flowChartDO);
    }

    private FlowChartData createNewFlowChartData(FlowChartData original, String flowChartId) {
        FlowChartData newFlowChartData = new FlowChartData();
        newFlowChartData.setId(original.getId());
        newFlowChartData.setTitle(original.getTitle());

        // 1. Map old node IDs to new node IDs
        Map<String, String> nodeIdMap = new HashMap<>();

        List<Node> newNodes = new ArrayList<>();

        for (Node oldNode : original.getNodes()) {
            String oldNodeId = oldNode.getId();
            String newNodeId = "node_" + UUID.randomUUID();
            nodeIdMap.put(oldNodeId, newNodeId);

            Node newNode = new Node();
            newNode.setId(newNodeId);
            newNode.setType(oldNode.getType());
            newNode.setPosition(oldNode.getPosition());
            newNode.setMeasured(oldNode.getMeasured());

            Map<String, String> newData = new HashMap<>(oldNode.getData());
            newData.put("flowChartId", flowChartId);
            newNode.setData(newData);

            newNodes.add(newNode);

            try {
                duplicateNodeFiles(oldNodeId, newNodeId, flowChartId);
            } catch (IOException e) {
                // Handle cleanly or rethrow with context
                throw new RuntimeException("Failed to duplicate evidence files for node: " + oldNodeId, e);
            }
        }

        // 2. Duplicate nodes
//        List<Node> newNodes = original.getNodes().stream().map(oldNode -> {
//            String oldNodeId = oldNode.getId();
//            String newNodeId = "node_" + UUID.randomUUID();
//            nodeIdMap.put(oldNodeId, newNodeId);
//
//            Node newNode = new Node();
//            newNode.setId(newNodeId);
//            newNode.setType(oldNode.getType());
//            newNode.setPosition(oldNode.getPosition());
//            newNode.setMeasured(oldNode.getMeasured());
//
//            // Update flowChartId inside data
//            Map<String, String> newData = new HashMap<>(oldNode.getData());
//            newData.put("flowChartId", flowChartId);
//            newNode.setData(newData);
//
//            try {
//                duplicateNodeFiles(oldNodeId, newNodeId, flowChartId);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//            return newNode;
//        }).collect(Collectors.toList());

        // 3. Duplicate edges and update references
        List<Edge> newEdges = original.getEdges().stream().map(oldEdge -> {
            Edge newEdge = new Edge();

            // Replace source and target node IDs
            String newSource = nodeIdMap.getOrDefault(oldEdge.getSource(), oldEdge.getSource());
            String newTarget = nodeIdMap.getOrDefault(oldEdge.getTarget(), oldEdge.getTarget());

            newEdge.setSource(newSource);
            newEdge.setTarget(newTarget);

            // Replace inside sourceHandle and targetHandle
            newEdge.setSourceHandle(replaceNodeIdInHandle(oldEdge.getSourceHandle(), nodeIdMap));
            newEdge.setTargetHandle(replaceNodeIdInHandle(oldEdge.getTargetHandle(), nodeIdMap));

            // Optional: regenerate edge ID
            newEdge.setId("edge_" + UUID.randomUUID());

            // Copy other properties
            newEdge.setType(oldEdge.getType());
            newEdge.setMarkerEnd(oldEdge.getMarkerEnd());
            newEdge.setStyle(oldEdge.getStyle());

            return newEdge;
        }).collect(Collectors.toList());

        newFlowChartData.setNodes(newNodes);
        newFlowChartData.setEdges(newEdges);

        return newFlowChartData;
    }

    private String replaceNodeIdInHandle(String handle, Map<String, String> nodeIdMap) {
        if (handle == null) return null;

        for (Map.Entry<String, String> entry : nodeIdMap.entrySet()) {
            String oldId = entry.getKey();
            String newId = entry.getValue();

            if (handle.contains(oldId)) {
                return handle.replace(oldId, newId);
            }
        }

        return handle;
    }

    public void duplicateNodeFiles(String oldNodeId, String newNodeId, String newFlowChartId) throws IOException {
        // Find all files in GridFS that are linked to the old node ID
        Query query = new Query(Criteria.where("metadata.nodeId").is(oldNodeId));
        List<GridFSFile> files = new ArrayList<>();
        gridFsTemplate.find(query).into(files);

        for (GridFSFile oldFile : files) {
            GridFsResource resource = gridFsOperations.getResource(oldFile);
            InputStream inputStream = resource.getInputStream();

            // Copy metadata and update nodeId and flowChartId
            Document oldMetadata = oldFile.getMetadata();
            Document newMetadata = new Document(oldMetadata); // clone
            newMetadata.put("nodeId", newNodeId);
            newMetadata.put("flowChartId", newFlowChartId);

            // Optionally update filename to avoid confusion
            String filename = oldFile.getFilename();
            String contentType = (String) oldMetadata.get("contentType");

            // Store copied file
            gridFsTemplate.store(inputStream, filename, contentType, newMetadata);
        }
    }
}
