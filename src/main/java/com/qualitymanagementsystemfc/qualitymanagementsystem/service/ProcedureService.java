package com.qualitymanagementsystemfc.qualitymanagementsystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.result.DeleteResult;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter.EvidenceFileConverter;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter.PindaanDokumenConverter;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter.ProcedureConverter;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.enums.ApproveStatus;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.enums.UserRole;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.form.ProcedureInfo;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.ProcedureDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.ProcedureTemplateDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.procedure.PindaanDokumen;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.procedure.AddVersionRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.procedure.ApproveProcedureDTO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.procedure.EvidenceFileDTO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.procedure.ProcedureDTO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure.*;
import com.qualitymanagementsystemfc.qualitymanagementsystem.repository.ProcedureRepository;
import com.qualitymanagementsystemfc.qualitymanagementsystem.security.JwtUtil;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProcedureService {

    @Autowired
    private ProcedureConverter procedureConverter;
    @Autowired
    private PindaanDokumenConverter pindaanDokumenConverter;
    @Autowired
    private ProcedureRepository procedureRepository;
    @Autowired
    private ProcedureTemplateService procedureTemplateService;
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private FileService fileService;
    @Autowired
    private PindaanDokumenService pindaanDokumenService;
    @Autowired
    private UserService userService;

    /**
     * Get procedure by Id
     *
     * @param id
     * @param role
     * @param userId
     * @return
     */
    public ProcedureVO getProcedureById(String id, String role, String userId) {
        ProcedureDO procedureDO = procedureRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Procedure not exists."));

        ProcedureVO procedureVO = null;

        if (role.equals(UserRole.STUDENT.getCode())) {
            throw new AccessDeniedException("Please login to view this procedure.");
        }

        if (procedureDO.getFileId() != null) {
            return procedureVO = procedureConverter.convertDOToVO(procedureDO);
        }

        List<PindaanDokumen> pindaanList = new ArrayList<>(procedureDO.getPindaanDokumenList());

        // Get approved versions
        List<PindaanDokumen> approvedList = pindaanList.stream()
                .filter(p -> ApproveStatus.APPROVE.getCode().equalsIgnoreCase(p.getApproveStatus()))
                .toList();

        int maxApprovedVersion = approvedList.stream()
                .map(p -> Integer.parseInt(p.getVersi()))
                .max(Integer::compareTo)
                .orElse(0);

        if (UserRole.ACADEMIC_STAFF.getCode().equals(role) ||
                UserRole.NON_ACADEMIC_STAFF.getCode().equals(role)) {

            // Check assigned versions
            List<PindaanDokumen> assignedVersions = pindaanList.stream()
                    .filter(p -> p.getAssignTo() != null &&
                            p.getAssignTo().stream().anyMatch(u -> u.getUserId().equals(userId)))
                    .toList();

            int maxAssignedVersion = assignedVersions.stream()
                    .map(p -> Integer.parseInt(p.getVersi()))
                    .max(Integer::compareTo)
                    .orElse(0);

            if (assignedVersions.isEmpty()) {
                // No assignment → show approved only
                procedureDO.setPindaanDokumenList(approvedList);
            } else {
                // Assigned user
                if (maxApprovedVersion > maxAssignedVersion) {
                    // Latest approved is newer than assigned version → show approved only
                    procedureDO.setPindaanDokumenList(approvedList);
                } else {
                    // Assigned version is up-to-date → show all versions up to max assigned
                    List<PindaanDokumen> allVisible = pindaanList.stream()
                            .filter(p -> {
                                int versionNum = Integer.parseInt(p.getVersi());
                                return versionNum <= maxAssignedVersion;
                            })
                            .toList();

                    // Deduplicate by versi
                    Map<String, PindaanDokumen> uniqueByVersi = new LinkedHashMap<>();
                    for (PindaanDokumen p : allVisible) {
                        uniqueByVersi.put(p.getVersi(), p); // last one wins
                    }

                    // Sort by version number ascending
                    List<PindaanDokumen> sorted = uniqueByVersi.values().stream()
                            .sorted(Comparator.comparingInt(p -> Integer.parseInt(p.getVersi())))
                            .toList();

                    procedureDO.setPindaanDokumenList(sorted);
                }
            }

            procedureVO = procedureConverter.convertDOToVO(procedureDO);

        } else {
            procedureVO = procedureConverter.convertDOToVO(procedureDO);
        }

        return procedureVO;
    }

    public ProcedureVersionVO getProcedureByIdAndVersion(String id, String role, String userId, String version) {
        ProcedureDO procedureDO = procedureRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Procedure not exists."));

        if (role.equals(UserRole.STUDENT.getCode())) {
            throw new AccessDeniedException("Please login to view this procedure.");
        }

        return procedureConverter.convertDOToVersionVO(procedureDO);
    }

    /**
     * Get ProcedureDO By Id
     *
     * @param id
     * @return
     */
    public ProcedureDO getProcedureDOById(String id) {
        return procedureRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Procedure not exists."));
    }

    public List<ProcedureInfo> getAllProceduresInfo() {

        List<ProcedureDO> procedureDOs = procedureRepository.findAll();
        return procedureDOs.stream().map(procedureConverter::convertDOToInfo).toList();
    }

    /**
     * Get All Accessible Procedures
     *
     * @param role
     * @param userId
     * @return
     */
    public ProcedureListVO getAllProcedures(String role, String userId) {

        if (role == null || role.isBlank()) {
            role = UserRole.STUDENT.getCode();
        }

        ProcedureListVO procedureListVO = new ProcedureListVO();

        if (role.equals(UserRole.SPK_MANAGER.getCode()) || role.equals(UserRole.ADMIN.getCode()) || role.equals(UserRole.APPROVER.getCode())) {
            List<ProcedureDO> procedureDOs = procedureRepository.findAll();
            procedureListVO.setAccessibleProcedures(procedureDOs.stream().map(procedureConverter::convertDOToListInfo).toList());

//        } else if (role.equals(UserRole.APPROVER.getCode())) {
//            List<ProcedureDO> procedureDOs = new ArrayList<>(procedureRepository.findByApprover_UserId(userId));
//            procedureListVO.setAssignedProcedures(procedureDOs.stream().map(procedureConverter::convertDOToListInfo).toList());

        } else {
//            List<ProcedureDO> procedureDOs = new ArrayList<>(procedureRepository.findByViewPrivilegeContaining(role));
//            procedureDOs = procedureDOs.stream().filter(p -> {
//                List<PindaanDokumen> approvedList = p.getPindaanDokumenList().stream()
//                        .filter(pd -> ApproveStatus.APPROVE.getCode().equalsIgnoreCase(pd.getApproveStatus()))
//                        .toList();
//            }).toList();
//            procedureListVO.setAccessibleProcedures(procedureDOs.stream().map(procedureConverter::convertDOToListInfo).toList());

            List<ProcedureDO> procedureDOs = new ArrayList<>(procedureRepository.findByViewPrivilegeContaining(role));

            procedureDOs = procedureDOs.stream()
                    .map(p -> {
                        List<PindaanDokumen> approvedList = p.getPindaanDokumenList().stream()
                                .filter(pd -> ApproveStatus.APPROVE.getCode().equalsIgnoreCase(pd.getApproveStatus()))
                                .toList();
                        if (!approvedList.isEmpty()) {
                            p.setPindaanDokumenList(approvedList); // keep only approved ones
                            return p;
                        }
                        return null; // filter later
                    })
                    .filter(Objects::nonNull) // remove nulls (procedures with no approved PindaanDokumen)
                    .toList();

            procedureListVO.setAccessibleProcedures(
                    procedureDOs.stream().map(procedureConverter::convertDOToListInfo).toList()
            );

            if (!role.equals(UserRole.STUDENT.getCode()) && userId != null && !userId.isBlank()) {
                List<ProcedureDO> assignedProcedureDOs = new ArrayList<>(procedureRepository.findByPindaanDokumenList_AssignToContaining(userService.findByUserId(userId)));
                procedureListVO.setAssignedProcedures(assignedProcedureDOs.stream().map(procedureConverter::convertDOToListInfo).toList());

            }

//            List<ProcedureDO> distinctProcedureDOs = procedureDOs.stream().collect(
//                    Collectors.collectingAndThen(
//                            Collectors.toMap(
//                                    ProcedureDO::getProcedureId,
//                                    p -> p,
//                                    (p1, p2) -> p1 // Keep first
//                            ),
//                            map -> new ArrayList<>(map.values())
//                    ));
//
//            return distinctProcedureDOs.stream()
//                    .sorted(Comparator.comparing(ProcedureDO::getGmt_create))
//                    .map(procedureConverter::convertDOToVO).toList();
        }

        return procedureListVO;

    }

    /**
     * Add new procedure
     *
     * @param procedureData
     * @param pindaanDokumen
     * @param procedureTemplateData
     * @param file
     * @return
     */
    @Transactional
    public ProcedureVO addProcedure(String procedureData, String pindaanDokumen, String procedureTemplateData, MultipartFile file) {

        try {
            if (pindaanDokumen != null && file != null) {
                throw new IllegalStateException("Not allowed to upload both template and file in one procedure");
            }

            ObjectMapper objectMapper = new ObjectMapper();

            ProcedureDTO procedureDTO = objectMapper.readValue(procedureData, ProcedureDTO.class);

            boolean procedureExisted = procedureRepository.existsByProcedureNumber(procedureDTO.getProcedureNumber());

            if (procedureExisted) {
                throw new IllegalArgumentException("Procedure with the same Procedure Number already exists.");
            }

            ProcedureDO procedureDO = procedureConverter.convertDTOToDO(procedureDTO);

            // Add pindaan
            // If pindaan not null, setValues
            // else, create new pindaan
            if (file == null) {
                PindaanDokumen pindaanDokumenDO = pindaanDokumenService.addPindaanDokumen(pindaanDokumen);
                // Initialize a template data
                ProcedureTemplateDO procedureTemplateDO = procedureTemplateService.addProcedureTemplate(procedureTemplateData, procedureDO);
                pindaanDokumenDO.setProcedureTemplateData(procedureTemplateDO);


                // Attach the constructed PindaanDokumen to ProcedureDO if it exists
                if (pindaanDokumenDO != null) {
                    List<PindaanDokumen> pindaanDokumenList = new ArrayList<>();
                    pindaanDokumenList.add(pindaanDokumenDO);
                    procedureDO.setPindaanDokumenList(pindaanDokumenList);
                } else {
                    throw new IllegalStateException("Version must be created first.");
                }
            }


//            if (procedureTemplateData != null && !procedureTemplateData.isBlank()) {
//                ProcedureTemplateDO procedureTemplateDO = procedureTemplateService.addProcedureTemplate(procedureTemplateData);
//                pindaanDokumenDO.setProcedureTemplateData(procedureTemplateDO);
//            }


            String fileName = null;
            String fileType = null;
            String fileId = null;

            if (file != null && !file.isEmpty()) {
                fileId = fileService.saveFile(file);
                procedureDO.setFileId(fileId);
            }

            procedureDO.setGmt_create(new Date());
            procedureDO.setGmt_modified(new Date());

            ProcedureDO savedProcedure = procedureRepository.save(procedureDO);

            return procedureConverter.convertDOToVO(savedProcedure);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public ProcedureVersionVO addNewProcedureVersion(String id, AddVersionRequest request) {

        ProcedureDO existingProcedure = procedureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Procedure does not exist."));

        PindaanDokumen latestVersion = existingProcedure.getPindaanDokumenList().stream()
                .max(Comparator.comparingInt(p -> Integer.parseInt(p.getVersi())))
                .orElseThrow(() -> new IllegalStateException("No pindaan versions found"));

        if (!latestVersion.getApproveStatus().equals(ApproveStatus.APPROVE.getCode())) {
            throw new IllegalArgumentException("Previous procedure not approved! Cannot create new version.");
        }

        PindaanDokumen pindaanDokumen = pindaanDokumenService.createNewVersion(latestVersion, request);

        existingProcedure.getPindaanDokumenList().add(pindaanDokumen);


        return procedureConverter.convertDOToVersionVO(procedureRepository.save(existingProcedure));


    }

    /**
     * Edit existing procedure
     *
     * @param id
     * @param procedureData
     * @param pindaanDokumenJson
     * @param procedureTemplateDataJson
     * @param file
     * @return
     */
//    @Transactional
//    public ProcedureVO editProcedure(String id, String procedureData, String pindaanDokumen, String procedureTemplateData, MultipartFile file) {
//
//        try {
//            ProcedureDO existingProcedure = procedureRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Procedure does not exists."));
//
//            ObjectMapper objectMapper = new ObjectMapper();
//
//            ProcedureDTO editProcedureDTO = objectMapper.readValue(procedureData, ProcedureDTO.class);
//
//            existingProcedure = procedureConverter.convertExistingDOFromDTO(existingProcedure, editProcedureDTO);
//            existingProcedure.setGmt_modified(new Date());
//
//            List<PindaanDokumen> existingPindaanList = existingProcedure.getPindaanDokumenList();
//
//            PindaanDokumen latestVersion = null;
//
//            if (existingPindaanList == null || existingPindaanList.isEmpty()) {
//                // No versions exist — just add a new one
//                PindaanDokumen pindaanDokumenDO = pindaanDokumenService.addPindaanDokumen(pindaanDokumen);
//
//                latestVersion = pindaanDokumenDO;
//
//                if (pindaanDokumenDO != null) {
//                    List<PindaanDokumen> pindaanDokumenList = new ArrayList<>();
//                    pindaanDokumenList.add(pindaanDokumenDO);
//                    existingProcedure.setPindaanDokumenList(pindaanDokumenList);
//                }
//
//            } else {
//                if (pindaanDokumen != null && !pindaanDokumen.isBlank()) {
//                    // Get the latest version and edit
//                    latestVersion = existingPindaanList.stream()
//                            .max(Comparator.comparingInt(p -> Integer.parseInt(p.getVersi())))
//                            .orElseThrow(() -> new IllegalStateException("No pindaan versions found"));
//
//                    PindaanDokumen pindaanDokumenDO = pindaanDokumenService.editPindaanDokumen(pindaanDokumen, latestVersion);
//
//                    if (pindaanDokumenDO != null) {
//                        String updatedVersion = pindaanDokumenDO.getVersi();
//
//                        existingProcedure.getPindaanDokumenList()
//                                .removeIf(p -> p.getVersi().equals(updatedVersion));
//
//                        existingProcedure.getPindaanDokumenList().add(pindaanDokumenDO);
//                    }
//                }
//            }
//
//            if (latestVersion == null) {
//                throw new IllegalStateException("Procedure has no version.");
//            }
//
//            if (procedureTemplateData != null && !procedureTemplateData.isBlank()) {
//
//                if (latestVersion.getProcedureTemplateData() == null) {
//                    ProcedureTemplateDO addProcedureTemplateDO = procedureTemplateService.addProcedureTemplate(procedureTemplateData);
//                    latestVersion.setProcedureTemplateData(addProcedureTemplateDO);
//                } else {
//                    procedureTemplateService.editProcedureTemplate(latestVersion.getProcedureTemplateData().getTemplateId(), procedureTemplateData);
//                }
//
//            } else {
//                String templateId = latestVersion.getProcedureTemplateData() != null ? latestVersion.getProcedureTemplateData().getTemplateId() : null;
//
//                if (templateId != null && !templateId.isBlank()) {
//                    procedureTemplateService.deleteProcedureTemplate(templateId);
//
//                    latestVersion.setProcedureTemplateData(null);
//                }
//            }
//
//            if (file != null && !file.isEmpty()) {
//                String oldFileId = existingProcedure.getFileId();
//
//                boolean isSameFile = false;
//
//                if (oldFileId != null && !oldFileId.isBlank()) {
//                    GridFSFile existingFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(oldFileId)));
//
//                    if (existingFile != null && existingFile.getLength() == file.getSize()) {
//                        // Compare file hashes (safer than just size)
//                        GridFsResource existingFileResource = gridFsTemplate.getResource(existingFile);
//
//                        try (InputStream existingFileStream = existingFileResource.getInputStream();
//                             InputStream newFileStream = file.getInputStream()) {
//
//                            String existingFileHash = DigestUtils.md5DigestAsHex(existingFileStream);
//                            String newFileHash = DigestUtils.md5DigestAsHex(newFileStream);
//
//                            isSameFile = existingFileHash.equals(newFileHash);
//                        }
//
//                    }
//                }
//
//                if (!isSameFile) {
//                    // Delete old file if different
//                    if (oldFileId != null && !oldFileId.isBlank()) {
//                        gridFsTemplate.delete(Query.query(Criteria.where("_id").is(oldFileId)));
//                    }
//
//                    // Save new file
//                    String newFileId = fileService.saveFile(file);
//                    existingProcedure.setFileId(newFileId);
//                }
//
//            } else {
//                String oldFileId = existingProcedure.getFileId();
//
//                if (oldFileId != null && !oldFileId.isBlank()) {
//                    gridFsTemplate.delete(Query.query(Criteria.where("_id").is(oldFileId)));
//
//                    existingProcedure.setFileId(null);
//
//                }
//
//            }
//
//            ProcedureDO editedProcedure = procedureRepository.save(existingProcedure);
//
//            return procedureConverter.convertDOToVO(editedProcedure);
//        } catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }
//
//    }
    @Transactional
    public ProcedureVO editProcedure(String id, String procedureData, String pindaanDokumenJson, String procedureTemplateDataJson, MultipartFile file) {

        try {
            ProcedureDO existingProcedure = procedureRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Procedure does not exist."));

            // Parse and update base fields
            ObjectMapper objectMapper = new ObjectMapper();
            ProcedureDTO editProcedureDTO = objectMapper.readValue(procedureData, ProcedureDTO.class);
            existingProcedure = procedureConverter.convertExistingDOFromDTO(existingProcedure, editProcedureDTO);
            existingProcedure.setGmt_modified(new Date());

            if (file == null) {
                List<PindaanDokumen> existingPindaanList = existingProcedure.getPindaanDokumenList();

                // Update PindaanDokumen
                PindaanDokumen latestVersion = updatePindaanDokumen(existingPindaanList, existingProcedure, pindaanDokumenJson);

                // Update template
                updateProcedureTemplate(existingPindaanList, latestVersion, procedureTemplateDataJson, existingProcedure);
            }

            // Update file
            updateProcedureFile(existingProcedure, file);

            // Save and return
            ProcedureDO saved = procedureRepository.save(existingProcedure);
            return procedureConverter.convertDOToVO(saved);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private PindaanDokumen updatePindaanDokumen(List<PindaanDokumen> existingPindaanList, ProcedureDO existingProcedure, String pindaanDokumenJson) throws IOException {
//        List<PindaanDokumen> existingPindaanList = existingProcedure.getPindaanDokumenList();
        PindaanDokumen latestVersion;

        if (existingPindaanList == null || existingPindaanList.isEmpty()) {
            // No versions exist — add new version
            latestVersion = pindaanDokumenService.addPindaanDokumen(pindaanDokumenJson);
            existingProcedure.setPindaanDokumenList(new ArrayList<>(List.of(latestVersion)));

        } else if (pindaanDokumenJson != null && !pindaanDokumenJson.isBlank()) {
            // Edit existing version if not approved
            latestVersion = existingPindaanList.stream()
                    .max(Comparator.comparingInt(p -> Integer.parseInt(p.getVersi())))
                    .orElseThrow(() -> new IllegalStateException("No pindaan versions found"));

            PindaanDokumen updated = pindaanDokumenService.editPindaanDokumen(pindaanDokumenJson, latestVersion);

            existingProcedure.getPindaanDokumenList().removeIf(p -> p.getVersi().equals(updated.getVersi()));
            existingProcedure.getPindaanDokumenList().add(updated);
            latestVersion = updated;

        } else {
            // No pindaanDokumen input — just return latest
            latestVersion = existingPindaanList.stream()
                    .max(Comparator.comparingInt(p -> Integer.parseInt(p.getVersi())))
                    .orElseThrow(() -> new IllegalStateException("No pindaan versions found"));
        }

        return latestVersion;
    }

    private void updateProcedureTemplate(List<PindaanDokumen> existingPindaanList, PindaanDokumen latestVersion, String procedureTemplateDataJson, ProcedureDO existingProcedure) throws IOException {
        if (procedureTemplateDataJson != null && !procedureTemplateDataJson.isBlank()) {
            if (latestVersion.getProcedureTemplateData() == null) {
                // Add new template
                ProcedureTemplateDO template = procedureTemplateService.addProcedureTemplate(procedureTemplateDataJson, existingProcedure);
                latestVersion.setProcedureTemplateData(template);
            } else {
                // Edit existing template
                procedureTemplateService.editProcedureTemplate(
                        latestVersion.getProcedureTemplateData().getTemplateId(),
                        procedureTemplateDataJson
                );
            }
        } else {
            // Delete existing template if present (replace with older version data)
            // like "reset"
            if (latestVersion.getProcedureTemplateData() != null) {

                Optional<PindaanDokumen> previousVersion = existingPindaanList.stream().filter(p -> {
                    return Integer.parseInt(p.getVersi()) == (Integer.parseInt(p.getVersi()) - 1);
                }).findFirst();

                if (previousVersion.isPresent()) {
                    latestVersion.setProcedureTemplateData(previousVersion.get().getProcedureTemplateData());
                } else {
                    latestVersion.setProcedureTemplateData(new ProcedureTemplateDO());
                }

//                String templateId = latestVersion.getProcedureTemplateData().getTemplateId();
//                if (templateId != null && !templateId.isBlank()) {
//                    procedureTemplateService.deleteProcedureTemplate(templateId);
//                    latestVersion.setProcedureTemplateData(null);
//                }
            }
        }
    }

    private void updateProcedureFile(ProcedureDO existingProcedure, MultipartFile file) throws IOException {
        String oldFileId = existingProcedure.getFileId();

        if (file != null && !file.isEmpty()) {
            boolean isSameFile = false;

            if (oldFileId != null && !oldFileId.isBlank()) {
                GridFSFile existingFile = gridFsTemplate.findOne(
                        new Query(Criteria.where("_id").is(oldFileId))
                );

                if (existingFile != null && existingFile.getLength() == file.getSize()) {
                    try (InputStream existingFileStream = gridFsTemplate.getResource(existingFile).getInputStream();
                         InputStream newFileStream = file.getInputStream()) {
                        String existingFileHash = DigestUtils.md5DigestAsHex(existingFileStream);
                        String newFileHash = DigestUtils.md5DigestAsHex(newFileStream);
                        isSameFile = existingFileHash.equals(newFileHash);
                    }
                }
            }

            if (!isSameFile) {
                // Remove old file
                if (oldFileId != null && !oldFileId.isBlank()) {
                    gridFsTemplate.delete(new Query(Criteria.where("_id").is(oldFileId)));
                }
                // Save new file
                String newFileId = fileService.saveFile(file);
                existingProcedure.setFileId(newFileId);
            }

        } else {
            // No file uploaded — delete existing
            if (oldFileId != null && !oldFileId.isBlank()) {
                gridFsTemplate.delete(new Query(Criteria.where("_id").is(oldFileId)));
                existingProcedure.setFileId(null);
            }
        }
    }

    /**
     * Delete whole existing procedure
     *
     * @param procedureId
     * @return
     */
    @Transactional
    public boolean deleteProcedure(String procedureId) {
        ProcedureDO procedureDO = procedureRepository.findById(procedureId).orElseThrow(() -> new IllegalArgumentException("Procedure does not exists."));

        if (procedureDO.getPindaanDokumenList() != null && !procedureDO.getPindaanDokumenList().isEmpty()) {
            procedureDO.getPindaanDokumenList().forEach((pindaanDokumen -> {
                if (pindaanDokumen.getProcedureTemplateData() != null) {
                    procedureTemplateService.deleteProcedureTemplate(pindaanDokumen.getProcedureTemplateData().getTemplateId());
                }
            }));
        }

//        if (procedureDO.getProcedureTemplateData() != null) {
//            boolean success = procedureTemplateService.deleteProcedureTemplate(procedureDO.getProcedureTemplateData().getTemplateId());
//
//            if (!success) {
//                return false;
//            }
//        }

        if (procedureDO.getFileId() != null && !procedureDO.getFileId().isBlank()) {
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(procedureDO.getFileId())));
        }

        DeleteResult result = mongoTemplate.remove(Query.query(Criteria.where("_id").is(procedureDO.getProcedureId())), ProcedureDO.class);

        return result.getDeletedCount() > 0;
    }

    @Transactional
    public boolean deleteProcedureVersion(String procedureId, String version) {
        ProcedureDO procedureDO = procedureRepository.findById(procedureId)
                .orElseThrow(() -> new IllegalArgumentException("Procedure does not exist."));

        List<PindaanDokumen> pindaanList = procedureDO.getPindaanDokumenList();
        if (pindaanList == null || pindaanList.isEmpty()) {
            throw new IllegalStateException("This procedure has no created version.");
        }

        // Get latest version
        PindaanDokumen latestVersion = pindaanList.stream()
                .max(Comparator.comparingInt(p -> Integer.parseInt(p.getVersi())))
                .orElseThrow(() -> new IllegalStateException("No pindaan versions found"));

        // Check if the version matches the latest version
        if (Integer.parseInt(latestVersion.getVersi()) != Integer.parseInt(version)) {
            throw new IllegalArgumentException("Only the latest procedure version is allowed to be deleted.");
        }

        if (latestVersion.getApproveStatus().equals(ApproveStatus.APPROVE.getCode())) {
            throw new IllegalArgumentException("This procedure has been approved. Not allow to delete.");
        }

        boolean success = pindaanDokumenService.deleteProcedureTemplateByVersion(latestVersion.getProcedureTemplateData().getTemplateId());


        if (!success) {
            return false;
        }

        // Remove that version from the list
        pindaanList.removeIf(p -> p.getVersi().equals(version));
//        procedureDO.setPindaanDokumenList(pindaanList);  // optional, already modifying the list

        // Save the updated procedure
        procedureRepository.save(procedureDO);

        return true;
    }





//    public EvidenceFileListVO getEvidenceFile(String flowChartId, String nodeId) {
//        EvidenceFileListVO evidenceFileListVO = new EvidenceFileListVO();
//
//        List<EvidenceFileVO> evidenceFileVOS = new ArrayList<>();
//
//        Query query = null;
//
//        if (flowChartId != null && !flowChartId.isBlank() && nodeId == null) {
//            query = Query.query(Criteria.where("metadata.flowChartId").is(flowChartId));
//        } else if (nodeId != null && !nodeId.isBlank() && flowChartId == null) {
//            query = Query.query(Criteria.where("metadata.nodeId").is(nodeId));
//        } else {

    /// /            is this correct?
//            throw new IllegalArgumentException("Illegal Request. Please try again");
//        }
//
//        GridFSFindIterable foundFiles = gridFsTemplate.find(query);
//
//        for (GridFSFile file : foundFiles) {
//            evidenceFileVOS.add(evidenceFileConverter.convertToVO(file.getObjectId().toString()));
//        }
//
//        evidenceFileListVO.setEvidenceFileListVOList(evidenceFileVOS);
//
//        return evidenceFileListVO;
//    }
    @Transactional
    public ProcedureVO updateProcedureStatus(String procedureId, ApproveProcedureDTO request) {
        ProcedureDO existingProcedure = procedureRepository.findById(procedureId)
                .orElseThrow(() -> new IllegalArgumentException("Procedure does not exist."));

        PindaanDokumen latestVersion = existingProcedure.getPindaanDokumenList().stream()
                .max(Comparator.comparingInt(p -> Integer.parseInt(p.getVersi())))
                .orElseThrow(() -> new IllegalStateException("No pindaan versions found"));

        if (Integer.parseInt(latestVersion.getVersi()) != Integer.parseInt(request.getVersion())) {
            throw new IllegalArgumentException("Version inconsistent. Cannot update history version.");
        }

        if (ApproveStatus.APPROVE.getCode().equals(latestVersion.getApproveStatus())) {
            throw new IllegalStateException("Procedure version " + latestVersion.getVersi() + " is already approved.");
        }

        // Find the pindaanDokumen with the same version and update
        existingProcedure.getPindaanDokumenList().stream()
                .filter(p -> p.getVersi().equals(latestVersion.getVersi()))  // Match the version
                .findFirst() // Get the first match
                .ifPresent(p -> {
                    p.setApproveStatus(request.getStatus());
                    p.setDescription(request.getDescription());
                    if (request.getStatus().equals(ApproveStatus.APPROVE.getCode())) {
                        p.setDiluluskan(userService.findByUserId(request.getApproverId()));
                    }
                });

        ProcedureDO updatedProcedure = procedureRepository.save(existingProcedure);

        return procedureConverter.convertDOToVO(updatedProcedure);
    }

    public boolean existsInCategory(String categoryId) {
        return procedureRepository.existsByCategory(categoryId);
    }


}


