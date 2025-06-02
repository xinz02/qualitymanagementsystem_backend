package com.qualitymanagementsystemfc.qualitymanagementsystem.web;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure.ProcedureVO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.security.JwtUtil;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.ProcedureService;
import com.qualitymanagementsystemfc.qualitymanagementsystem.utils.CommonApiResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
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

    /**
     * Get procedure by id
     *
     * @param id
     * @return
     */
    @GetMapping("/getProcedure/{id}")
    public ResponseEntity<CommonApiResult<ProcedureVO>> getProcedureById(@PathVariable String id) {

        CommonApiResult<ProcedureVO> res = new CommonApiResult<>();

        ProcedureVO procedureVO = procedureService.getProcedureById(id);

        res.setData(procedureVO);

        return ResponseEntity.ok(res);

    }

    /**
     * Get all accessible procedures
     *
     * @param request
     * @return
     */
    @GetMapping("/getProcedure")
    public ResponseEntity<CommonApiResult<List<ProcedureVO>>> getAllProcedures(HttpServletRequest request) {

        CommonApiResult<List<ProcedureVO>> res = new CommonApiResult<>();

        try {
            String role = null;
            String userId = null;

            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                role = jwtUtil.getUserRoleFromJwtToken(token);
                userId = jwtUtil.getUserIdFromJwtToken(token);
            }

            List<ProcedureVO> procedureVOs = procedureService.getAllProcedures(role, userId);

            res.setData(procedureVOs);

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
                                                                        @RequestPart(value = "procedureTemplateData", required = false) String procedureTemplateData,
                                                                        @RequestPart(value = "file", required = false) MultipartFile file) {
        CommonApiResult<ProcedureVO> res = new CommonApiResult<>();

        if (procedureData == null) {
            res.setMessage("Request is null.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        try {
            ProcedureVO procedureVO = procedureService.addProcedure(procedureData, procedureTemplateData, file);

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
                                                                      @RequestPart(value = "procedureTemplateData", required = false) String procedureTemplateData,
                                                                      @RequestPart(value = "file", required = false) MultipartFile file,
                                                                      @PathVariable String id) {
        CommonApiResult<ProcedureVO> res = new CommonApiResult<>();

        if (procedureData == null || id == null) {
            res.setMessage("Invalid Request. Please refresh and try again.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        try {
            ProcedureVO procedureVO = procedureService.editProcedure(id, procedureData, procedureTemplateData, file);

            res.setData(procedureVO);
            res.setMessage("Procedure edited successfully!");

            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

        } catch (Exception e) {
            res.setMessage("Fail to edit procedure. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

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


    /**
     * Download uploaded file
     *
     * @param fileId
     * @return
     * @throws IOException
     */
    @GetMapping("/file/{fileId}")
    public ResponseEntity<Resource> downloadProcedureFile(@PathVariable String fileId) throws IOException {

        try {
            GridFsResource resource = procedureService.downloadFile(fileId);

            GridFSFile file = resource.getGridFSFile();
            if (file == null || file.getMetadata() == null) {
                return ResponseEntity.notFound().build();
            }

            String contentType = file.getMetadata().getString("contentType");
            String originalFileName = file.getMetadata().getString("originalName");

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFileName + "\"")
                    .body(resource);

        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
