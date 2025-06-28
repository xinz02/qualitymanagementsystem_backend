package com.qualitymanagementsystemfc.qualitymanagementsystem.web;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.enums.UserRole;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.form.ProcedureInfo;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.procedure.AddVersionRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.procedure.ApproveProcedureDTO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure.*;
import com.qualitymanagementsystemfc.qualitymanagementsystem.security.JwtUtil;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.EvidenceFileService;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.FileService;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.ProcedureService;
import com.qualitymanagementsystemfc.qualitymanagementsystem.utils.CommonApiResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/procedure")
public class ProcedureController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ProcedureService procedureService;

    @Autowired
    private FileService fileService;

    @Autowired
    private EvidenceFileService evidenceFileService;

    /**
     * Get procedure by id
     *
     * @param id
     * @return
     */
    @GetMapping("/getProcedure/{id}")
    public ResponseEntity<CommonApiResult<ProcedureVO>> getProcedureById(HttpServletRequest request, @PathVariable String id) {

        CommonApiResult<ProcedureVO> res = new CommonApiResult<>();

        if (id == null) {
            res.setMessage("Request is null.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        try {
            String role = null;
            String userId = null;

            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                role = jwtUtil.getUserRoleFromJwtToken(token);
                userId = jwtUtil.getUserIdFromJwtToken(token);
            }

            ProcedureVO procedureVO = procedureService.getProcedureById(id, role, userId);

            res.setData(procedureVO);

            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

        } catch (Exception e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }

    }

    @GetMapping("/getProcedure/{id}/{version}")
    public ResponseEntity<CommonApiResult<ProcedureVersionVO>> getProcedureByIdAndVersion(HttpServletRequest request, @PathVariable String id, @PathVariable String version) {

        CommonApiResult<ProcedureVersionVO> res = new CommonApiResult<>();

        if (id == null) {
            res.setMessage("Request is null.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        try {
            String role = null;
            String userId = null;

            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                role = jwtUtil.getUserRoleFromJwtToken(token) == null ? UserRole.STUDENT.getCode() : jwtUtil.getUserRoleFromJwtToken(token);
                userId = jwtUtil.getUserIdFromJwtToken(token);
            }

            ProcedureVersionVO procedureVO = procedureService.getProcedureByIdAndVersion(id, role, userId, version);

            res.setData(procedureVO);

            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

        } catch (Exception e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }

    }

    /**
     * Get all accessible procedures
     *
     * @param request
     * @return
     */
    @GetMapping("/getProcedure")
    public ResponseEntity<CommonApiResult<ProcedureListVO>> getAllProcedures(HttpServletRequest request) {

        CommonApiResult<ProcedureListVO> res = new CommonApiResult<>();

        try {
            String role = null;
            String userId = null;

            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                role = jwtUtil.getUserRoleFromJwtToken(token);
                userId = jwtUtil.getUserIdFromJwtToken(token);
            }


            ProcedureListVO procedureListVO = procedureService.getAllProcedures(role, userId);

            res.setData(procedureListVO);

            return ResponseEntity.ok(res);

        } catch (Exception e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);

        }
    }

    @GetMapping("/getProcedureInfo")
    public ResponseEntity<CommonApiResult<List<ProcedureInfo>>> getAllProceduresInfo(HttpServletRequest request) {

        CommonApiResult<List<ProcedureInfo>> res = new CommonApiResult<>();

        try {

            List<ProcedureInfo> procedureInfos = procedureService.getAllProceduresInfo();

            res.setData(procedureInfos);

            return ResponseEntity.ok(res);

        } catch (Exception e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);

        }
    }

    /**
     * Add new procedure
     *
     * @param procedureData
     * @param procedureTemplateData
     * @param file
     * @return savedProcedure
     */
    @PostMapping(value = "/addProcedure", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonApiResult<ProcedureVO>> addNewProcedure(@RequestPart("procedureData") String procedureData,
                                                                        @RequestPart(value = "pindaanDokumen", required = false) String pindaanDokumen,
                                                                        @RequestPart(value = "procedureTemplateData", required = false) String procedureTemplateData,
                                                                        @RequestPart(value = "file", required = false) MultipartFile file) {
        CommonApiResult<ProcedureVO> res = new CommonApiResult<>();

        if (procedureData == null) {
            res.setMessage("Request is null.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        } else if(procedureTemplateData != null && !procedureData.isBlank() && file != null && !file.isEmpty()) {
            res.setMessage("Create new procedure while Upload new files are not allowed.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        try {
            ProcedureVO procedureVO = procedureService.addProcedure(procedureData, pindaanDokumen, procedureTemplateData, file);

            res.setData(procedureVO);
            res.setMessage("Procedure added successfully!");

            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

        } catch (Exception e) {
            res.setMessage("Fail to add new procedure. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @PostMapping(value = "/addNewVersion/{id}")
    public ResponseEntity<CommonApiResult<ProcedureVersionVO>> addNewProcedureVersion(@PathVariable String id, @RequestBody AddVersionRequest request) {
        CommonApiResult<ProcedureVersionVO> res = new CommonApiResult<>();

        if (id == null || id.isBlank() || request == null) {
            res.setMessage("Request is null.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

        }

        try {
            ProcedureVersionVO procedureVersionVO = procedureService.addNewProcedureVersion(id,request);

            res.setData(procedureVersionVO);
            res.setMessage("New procedure version created successfully!");

            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

        } catch (Exception e) {
            res.setMessage("Fail to create new procedure version. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    /**
     * Edit existing procedure
     *
     * @param procedureData
     * @param procedureTemplateData
     * @param file
     * @param id
     * @return
     */
    @PutMapping(value = "/editProcedure/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonApiResult<ProcedureVO>> editProcedure(@RequestPart("procedureData") String procedureData,
                                                                      @RequestPart(value = "pindaanDokumen", required = false) String pindaanDokumen,
                                                                      @RequestPart(value = "procedureTemplateData", required = false) String procedureTemplateData,
                                                                      @RequestPart(value = "file", required = false) MultipartFile file,
                                                                      @PathVariable String id) {
        CommonApiResult<ProcedureVO> res = new CommonApiResult<>();

        if (procedureData == null || id == null) {
            res.setMessage("Invalid Request. Please refresh and try again.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        try {
            ProcedureVO procedureVO = procedureService.editProcedure(id, procedureData, pindaanDokumen, procedureTemplateData, file);

            res.setData(procedureVO);
            res.setMessage("Procedure edited successfully!");

            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException | IllegalStateException e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

        } catch (Exception e) {
            res.setMessage("Fail to edit procedure. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    /**
     * Delete whole existing procedure
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteProcedure/{id}")
    public ResponseEntity<CommonApiResult<Void>> deleteProcedure(@PathVariable String id) {
        CommonApiResult<Void> res = new CommonApiResult<>();

        if (id == null) {
            res.setMessage("Invalid Request. Please try again.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        try {
            boolean success = procedureService.deleteProcedure(id);

            if (!success) {
                res.setMessage("Fail to delete procedure. Please try again later.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
            }

            res.setMessage("Procedure deleted successfully!");

            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

        } catch (Exception e) {
            res.setMessage("Fail to delete procedure. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @DeleteMapping("/deleteProcedure/{id}/{version}")
    public ResponseEntity<CommonApiResult<Void>> deleteProcedureVersion(@PathVariable String id, @PathVariable String version) {
        CommonApiResult<Void> res = new CommonApiResult<>();

        if (id == null || version == null) {
            res.setMessage("Invalid Request. Please try again.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        try {
            boolean success = procedureService.deleteProcedureVersion(id, version);

            if (!success) {
                res.setMessage("Fail to delete procedure. Please try again later.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
            }

            res.setMessage("Procedure deleted successfully!");

            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

        } catch (Exception e) {
            res.setMessage("Fail to delete procedure. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    /**
     * View pdf file and download doc/docx file
     *
     * @param fileId
     * @return
     * @throws IOException
     */
    @GetMapping("/file/{fileId}")
    public ResponseEntity<?> viewOrDownloadFile(@PathVariable String fileId) throws IOException {

        CommonApiResult<Void> res = new CommonApiResult<>();

        if (fileId == null || fileId.isBlank()) {
            res.setMessage("Invalid Request. Please try again.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        try {
            GridFsResource resource = fileService.downloadFile(fileId);

            GridFSFile file = resource.getGridFSFile();
            if (file == null || file.getMetadata() == null) {
                return ResponseEntity.notFound().build();
            }

            String contentType = file.getMetadata().getString("contentType");
            String originalFileName = file.getMetadata().getString("originalName");

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + originalFileName + "\"")
                    .body(resource);

        } catch (FileNotFoundException e) {
            res.setMessage("File not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }
    }

    /**
     * upload evidence file
     * @param evidenceInfoData
     * @param evidenceFile
     * @return
     */
    @PostMapping(value = "/uploadEvidence", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonApiResult<EvidenceFileVO>> uploadEvidence(@RequestPart("evidenceInfoData") String evidenceInfoData,
                                                                          @RequestPart(value = "evidenceFile", required = false) MultipartFile evidenceFile) {
        CommonApiResult<EvidenceFileVO> res = new CommonApiResult<>();

        if (evidenceInfoData == null || evidenceFile.isEmpty()) {
            res.setMessage("Request is null.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        try {
            EvidenceFileVO savedFile = evidenceFileService.uploadEvidence(evidenceInfoData, evidenceFile);

            res.setData(savedFile);
            res.setMessage("File uploaded successfully!");

            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException | IllegalStateException e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

        } catch (Exception e) {
            res.setMessage("Fail to upload file. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);

        }
    }

    /**
     * get all files under a flowchart
     * @param flowChartId
     * @return
     */
    @GetMapping("/evidenceFiles/flowChart/{flowChartId}")
    public ResponseEntity<CommonApiResult<FlowChartEvidenceFileListVO>> getEvidenceFileByFlowChartId(@PathVariable String flowChartId) {
        CommonApiResult<FlowChartEvidenceFileListVO> res = new CommonApiResult<>();

        if (flowChartId == null || flowChartId.isEmpty()) {
            res.setMessage("Request is null.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        try {
            FlowChartEvidenceFileListVO flowChartEvidenceFileListVO = evidenceFileService.getEvidenceFileByFlowChartId(flowChartId);

            res.setData(flowChartEvidenceFileListVO);

            return ResponseEntity.ok(res);

        } catch (IllegalStateException e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);

        } catch (Exception e) {
            res.setMessage("Fail to get evidence files. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);

        }

    }

    /**
     * get all files under specific nodes
     * @param nodeId
     * @return
     */
    @GetMapping("/evidenceFiles/node/{nodeId}")
    public ResponseEntity<CommonApiResult<EvidenceFileListVO>> getEvidenceFileByNodeId(@PathVariable String nodeId) {
        CommonApiResult<EvidenceFileListVO> res = new CommonApiResult<>();

        if (nodeId == null || nodeId.isEmpty()) {
            res.setMessage("Request is null.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        EvidenceFileListVO evidenceFileListVO = evidenceFileService.getEvidenceFileByNodeId(nodeId);

        res.setData(evidenceFileListVO);

        return ResponseEntity.ok(res);
    }

    /**
     * Delete evidence file
     *
     * @param nodeId
     * @param fileId
     * @return
     */
    @DeleteMapping("/deleteEvidence/{nodeId}/{fileId}")
    public ResponseEntity<CommonApiResult<Void>> deleteEvidenceFile(@PathVariable String nodeId, @PathVariable String fileId) {
        CommonApiResult<Void> res = new CommonApiResult<>();

        if (nodeId == null && fileId == null) {
            res.setMessage("Invalid Request. Please try again.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        try {
            evidenceFileService.deleteEvidenceFile(nodeId, fileId);

            res.setMessage("File deleted successfully!");

            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

        } catch (Exception e) {
            res.setMessage("Fail to delete file. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    /**
     * edit evidence file info - person in charge & semester
     * @param fileId
     * @param evidenceInfoData
     * @return
     */
    @PutMapping(value = "/editEvidence/{fileId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonApiResult<EvidenceFileVO>> editEvidence(@PathVariable String fileId, @RequestPart("evidenceInfoData") String evidenceInfoData) {
        CommonApiResult<EvidenceFileVO> res = new CommonApiResult<>();

        if (fileId == null || fileId.isEmpty() || evidenceInfoData == null || evidenceInfoData.isEmpty()) {
            res.setMessage("Request is null.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        try {
            EvidenceFileVO editedFile = evidenceFileService.editEvidence(fileId, evidenceInfoData);

            res.setData(editedFile);
            res.setMessage("File Info edited successfully!");

            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException | IllegalStateException | FileNotFoundException e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

        } catch (Exception e) {
            res.setMessage("Fail to edit file. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);

        }
    }

    @PatchMapping("/updateProcedureStatus/{procedureId}")
    public ResponseEntity<CommonApiResult<ProcedureVO>> updateProcedureStatus(@PathVariable String procedureId, @RequestBody ApproveProcedureDTO request) {
        CommonApiResult<ProcedureVO> res = new CommonApiResult<>();

        if (procedureId == null || procedureId.isEmpty() || request == null) {
            res.setMessage("Request is null.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        try {
            ProcedureVO updatedProcedure = procedureService.updateProcedureStatus(procedureId, request);

            res.setData(updatedProcedure);
            res.setMessage("Procedure status updated!");

            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException | IllegalStateException e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

        } catch (Exception e) {
            res.setMessage("Fail to update procedure status. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);

        }
    }
}
