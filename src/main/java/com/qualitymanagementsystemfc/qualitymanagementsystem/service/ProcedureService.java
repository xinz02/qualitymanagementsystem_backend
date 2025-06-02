package com.qualitymanagementsystemfc.qualitymanagementsystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.result.DeleteResult;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter.ProcedureConverter;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.enums.UserRole;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.ProcedureDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.ProcedureTemplateDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.procedure.ProcedureDTO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure.ProcedureVO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.repository.ProcedureRepository;
import com.qualitymanagementsystemfc.qualitymanagementsystem.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProcedureService {

    @Autowired
    private ProcedureConverter procedureConverter;

    @Autowired
    private ProcedureRepository procedureRepository;

    @Autowired
    private ProcedureTemplateService procedureTemplateService;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations gridFsOperations;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Get procedure by Id
     *
     * @param id
     * @return
     */
    public ProcedureVO getProcedureById(String id) {
        ProcedureDO procedureDO = procedureRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Procedure not found."));

        return procedureConverter.convertDOToVO(procedureDO);
    }

    /**
     * Get Accessible Procedures
     *
     * @param role
     * @param userId
     * @return
     */
    public List<ProcedureVO> getAllProcedures(String role, String userId) {

        if (role == null || role.isBlank()) {
            role = UserRole.STUDENT.getCode();
        }

        if (role.equals(UserRole.SPK_MANAGER.getCode()) || role.equals(UserRole.ADMIN.getCode())) {
            List<ProcedureDO> procedureDOs = procedureRepository.findAll();
            return procedureDOs.stream().map(procedureConverter::convertDOToVO).toList();

        } else {

            List<ProcedureDO> procedureDOs = new ArrayList<>(procedureRepository.findByViewPrivilegeContaining(role));

            if (!role.equals(UserRole.STUDENT.getCode()) && userId != null && !userId.isBlank()) {
                procedureDOs.addAll(procedureRepository.findByAssignToContaining(userId));

                procedureDOs.addAll(procedureRepository.findByApprover(userId));
            }

            List<ProcedureDO> distinctProcedureDOs = procedureDOs.stream().collect(
                    Collectors.collectingAndThen(
                            Collectors.toMap(
                                    ProcedureDO::getProcedureId,
                                    p -> p,
                                    (p1, p2) -> p1 // Keep first
                            ),
                            map -> new ArrayList<>(map.values())
                    ));

            return distinctProcedureDOs.stream().map(procedureConverter::convertDOToVO).toList();
        }

    }

    /**
     * Add new procedure
     *
     * @param procedureData
     * @param procedureTemplateData
     * @param file
     * @return
     */
    @Transactional
    public ProcedureVO addProcedure(String procedureData, String procedureTemplateData, MultipartFile file) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            ProcedureDTO procedureDTO = objectMapper.readValue(procedureData, ProcedureDTO.class);

            boolean procedureExisted = procedureRepository.existsByProcedureNumber(procedureDTO.getProcedureNumber());

            if (procedureExisted) {
                throw new IllegalArgumentException("Procedure with the same Procedure Number already exists.");
            }

            ProcedureDO procedureDO = procedureConverter.convertDTOToDO(procedureDTO);

            if (procedureTemplateData != null && !procedureTemplateData.isBlank()) {
                ProcedureTemplateDO procedureTemplateDO = procedureTemplateService.addProcedureTemplate(procedureTemplateData);
                procedureDO.setProcedureTemplateData(procedureTemplateDO);
            }

            String fileName = null;
            String fileType = null;
            String fileId = null;

            if (file != null && !file.isEmpty()) {
                fileId = saveFile(file);
//                fileName = file.getOriginalFilename();
//                fileType = file.getContentType();
                procedureDO.setFileId(fileId);
            }

            procedureDO.setGmt_create(new Date());
            procedureDO.setGmt_modified(new Date());

            ProcedureDO savedProcedure = procedureRepository.save(procedureDO);

            //            if (savedProcedure.getFileId() != null && !savedProcedure.getFileId().isBlank()) {
//                procedureVO.setFileName(fileName);
//                procedureVO.setFileType(fileType);
//                procedureVO.setFileDownloadUrl("/procedure/file/" + fileId);
//            }

            return procedureConverter.convertDOToVO(savedProcedure);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Edit existing procedure
     *
     * @param id
     * @param procedureData
     * @param procedureTemplateData
     * @param file
     * @return
     */
    @Transactional
    public ProcedureVO editProcedure(String id, String procedureData, String procedureTemplateData, MultipartFile file) {

        try {
            ProcedureDO existingProcedure = procedureRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Procedure does not exists."));

            ObjectMapper objectMapper = new ObjectMapper();

            ProcedureDTO editProcedureDTO = objectMapper.readValue(procedureData, ProcedureDTO.class);

            existingProcedure = procedureConverter.convertExistingDOFromDTO(existingProcedure, editProcedureDTO);
            existingProcedure.setGmt_modified(new Date());

            if (procedureTemplateData != null && !procedureTemplateData.isBlank()) {

                if (existingProcedure.getProcedureTemplateData() == null) {
                    ProcedureTemplateDO addProcedureTemplateDO = procedureTemplateService.addProcedureTemplate(procedureTemplateData);
                    existingProcedure.setProcedureTemplateData(addProcedureTemplateDO);
                } else {
                    procedureTemplateService.editProcedureTemplate(existingProcedure.getProcedureTemplateData().getTemplateId(), procedureTemplateData);
                }

            } else {
                String templateId = existingProcedure.getProcedureTemplateData() != null ? existingProcedure.getProcedureTemplateData().getTemplateId() : null;

                if(templateId != null && !templateId.isBlank()) {
                    procedureTemplateService.deleteProcedureTemplate(templateId);

                    existingProcedure.setProcedureTemplateData(null);
                }
            }

            if (file != null && !file.isEmpty()) {
                String oldFileId = existingProcedure.getFileId();

                boolean isSameFile = false;

                if (oldFileId != null && !oldFileId.isBlank()) {
                    GridFSFile existingFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(oldFileId)));

                    if (existingFile != null && existingFile.getLength() == file.getSize()) {
                        // Compare file hashes (safer than just size)
                        GridFsResource existingFileResource = gridFsTemplate.getResource(existingFile);

                        try (InputStream existingFileStream = existingFileResource.getInputStream();
                             InputStream newFileStream = file.getInputStream()) {

                            String existingFileHash = DigestUtils.md5DigestAsHex(existingFileStream);
                            String newFileHash = DigestUtils.md5DigestAsHex(newFileStream);

                            isSameFile = existingFileHash.equals(newFileHash);
                        }

                    }
                }

                if (!isSameFile) {
                    // Delete old file if different
                    if (oldFileId != null && !oldFileId.isBlank()) {
                        gridFsTemplate.delete(Query.query(Criteria.where("_id").is(oldFileId)));
                    }

                    // Save new file
                    String newFileId = saveFile(file);
                    existingProcedure.setFileId(newFileId);
                }

            } else {
                String oldFileId = existingProcedure.getFileId();

                if (oldFileId != null && !oldFileId.isBlank()) {
                    gridFsTemplate.delete(Query.query(Criteria.where("_id").is(oldFileId)));

                    existingProcedure.setFileId(null);

                }

            }

            ProcedureDO editedProcedure = procedureRepository.save(existingProcedure);

            return procedureConverter.convertDOToVO(editedProcedure);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Transactional
    public boolean deleteProcedure(String procedureId) {
        ProcedureDO procedureDO = procedureRepository.findById(procedureId).orElseThrow(() -> new IllegalArgumentException("Procedure does not exists."));

        if (procedureDO.getProcedureTemplateData() != null) {
            boolean success = procedureTemplateService.deleteProcedureTemplate(procedureDO.getProcedureTemplateData().getTemplateId());

            if (!success) {
                return false;
            }
        }

        if (procedureDO.getFileId() != null && !procedureDO.getFileId().isBlank()) {
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(procedureDO.getFileId())));
        }

//        procedureRepository.delete(procedureDO);
//
//        return procedureRepository.findById(procedureId).isEmpty();

        DeleteResult result = mongoTemplate.remove(Query.query(Criteria.where("_id").is(procedureId)), ProcedureDO.class);

        return result.getDeletedCount() > 0;
    }

    /**
     * Save uploaded procedure file into db
     *
     * @param file
     * @return
     * @throws IOException
     */
    public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Optional: Add metadata
        org.bson.Document metadata = new org.bson.Document();
        metadata.put("contentType", file.getContentType());
        metadata.put("originalName", file.getOriginalFilename());

        return gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType(),
                metadata
        ).toString(); // returns fileId as string
    }

    /**
     * Download uploaded procedure file
     *
     * @param fileId
     * @return
     * @throws IOException
     */
    public GridFsResource downloadFile(String fileId) throws IOException {
        GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));

        if (file == null) {
            throw new FileNotFoundException("File not found with id: " + fileId);
        }

        return gridFsOperations.getResource(file);
    }
}
