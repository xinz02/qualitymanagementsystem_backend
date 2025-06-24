package com.qualitymanagementsystemfc.qualitymanagementsystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.result.DeleteResult;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter.FormConverter;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.enums.UserRole;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.form.FormDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.ProcedureDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.form.FormDTO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.form.FormVO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure.ProcedureVO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.repository.FormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FormService {

    @Autowired
    private FormConverter formConverter;

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    public FormVO getFormById(String id) {
        FormDO formDO = formRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Form not exists."));

        return formConverter.convertDOToVO(formDO);
    }

    public List<FormVO> getAllForms(String role, String userId) {

        if (role == null || role.isBlank()) {
            role = UserRole.STUDENT.getCode();
        }

        if (role.equals(UserRole.SPK_MANAGER.getCode()) || role.equals(UserRole.ADMIN.getCode())) {
            List<FormDO> formDOs = formRepository.findAll();
            return formDOs.stream().map(formConverter::convertDOToVO).toList();

        } else {

            List<FormDO> formDOs = new ArrayList<>(formRepository.findByViewPrivilegeContaining(role));

            if (!role.equals(UserRole.STUDENT.getCode()) && userId != null && !userId.isBlank()) {
                formDOs.addAll(formRepository.findByPersonInCharge_UserId(userId));
            }

            List<FormDO> distinctFormDOs = formDOs.stream().collect(
                    Collectors.collectingAndThen(
                            Collectors.toMap(
                                    FormDO::getFormId,
                                    p -> p,
                                    (p1, p2) -> p1 // Keep first
                            ),
                            map -> new ArrayList<>(map.values())
                    ));

            return distinctFormDOs.stream()
                    .sorted(Comparator.comparing(FormDO::getGmt_create))
                    .map(formConverter::convertDOToVO).toList();
        }

    }

    public FormVO addForm(String formData, MultipartFile file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            FormDTO formDTO = objectMapper.readValue(formData, FormDTO.class);

            boolean formExisted = formRepository.existsByFormNumber(formDTO.getFormNumber());

            if (formExisted) {
                throw new IllegalArgumentException("Form with the same Form Number already exists.");
            }

            FormDO formDO = formConverter.convertDTOToDO(formDTO);

            String fileId = null;

            if (file != null && !file.isEmpty()) {
                fileId = fileService.saveFile(file);
                formDO.setFileId(fileId);
            }

            formDO.setGmt_create(new Date());
            formDO.setGmt_modified(new Date());

            FormDO savedForm = formRepository.save(formDO);

            return formConverter.convertDOToVO(savedForm);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public FormVO editForm(String id, String formData, MultipartFile file) {

        try {
            FormDO existingForm = formRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Form does not exists."));

            ObjectMapper objectMapper = new ObjectMapper();

            FormDTO editFormDTO = objectMapper.readValue(formData, FormDTO.class);

            existingForm = formConverter.convertExistingDOFromDTO(existingForm, editFormDTO);
            existingForm.setGmt_modified(new Date());

            if (file != null) {
                String oldFileId = existingForm.getFileId();

                boolean isSameFile = false;

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

                if (!isSameFile) {
                    // Delete old file if different
                    if (oldFileId != null && !oldFileId.isBlank()) {
                        gridFsTemplate.delete(Query.query(Criteria.where("_id").is(oldFileId)));
                    }

                    // Save new file
                    String newFileId = fileService.saveFile(file);
                    existingForm.setFileId(newFileId);
                }
            }

            FormDO editedForm = formRepository.save(existingForm);

            return formConverter.convertDOToVO(editedForm);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Transactional
    public boolean deleteForm(String formId) {
        FormDO formDO = formRepository.findById(formId).orElseThrow(() -> new IllegalArgumentException("Form does not exists."));

        if (formDO.getFileId() != null && !formDO.getFileId().isBlank()) {
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(formDO.getFileId())));
        }

        DeleteResult result = mongoTemplate.remove(Query.query(Criteria.where("_id").is(formDO.getFormId())), FormDO.class);

        return result.getDeletedCount() > 0;
    }
}
