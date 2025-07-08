package com.qualitymanagementsystemfc.qualitymanagementsystem.web;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.form.FormVO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.security.JwtUtil;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.FormService;
import com.qualitymanagementsystemfc.qualitymanagementsystem.utils.CommonApiResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/form")
public class FormController {

    @Autowired
    private FormService formService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/getForm/{id}")
    public ResponseEntity<CommonApiResult<FormVO>> getFormById(@PathVariable String id) {

        CommonApiResult<FormVO> res = new CommonApiResult<>();

        if (id == null) {
            res.setMessage("Request is null.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        try {
            FormVO formVO = formService.getFormById(id);

            res.setData(formVO);

            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

        } catch (Exception e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }

    }

    @GetMapping("/getForms")
    public ResponseEntity<CommonApiResult<List<FormVO>>> getAllForms(HttpServletRequest request) {

        CommonApiResult<List<FormVO>> res = new CommonApiResult<>();

        try {
            String role = null;
            String userId = null;

            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                role = jwtUtil.getUserRoleFromJwtToken(token);
                userId = jwtUtil.getUserIdFromJwtToken(token);
            }

            List<FormVO> formVOs = formService.getAllForms(role, userId);

            res.setData(formVOs);

            return ResponseEntity.ok(res);

        } catch (Exception e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);

        }
    }

    @PostMapping(value = "/addForm", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonApiResult<FormVO>> addNewForm(@RequestPart("formData") String formData,
                                                              @RequestPart(value = "form", required = false) MultipartFile form) {
        CommonApiResult<FormVO> res = new CommonApiResult<>();

        if (formData == null || formData.isEmpty() || form.isEmpty()) {
            res.setMessage("Request is null.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        try {
            FormVO formVO = formService.addForm(formData, form);

            res.setData(formVO);
            res.setMessage("Form added successfully!");

            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

        } catch (Exception e) {
            String message = !e.getMessage().isBlank() ? e.getMessage() :  "Fail to add new form. Please try again later.";
            res.setMessage(message);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @PutMapping(value = "/editForm/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonApiResult<FormVO>> editForm(@RequestPart("formData") String formData,
                                                                 @RequestPart(value = "form", required = false) MultipartFile form,
                                                                 @PathVariable String id) {
        CommonApiResult<FormVO> res = new CommonApiResult<>();

        if (formData == null || formData.isEmpty() || id == null || id.isEmpty()) {
            res.setMessage("Request is null.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        try {
            FormVO formVO = formService.editForm(id, formData, form);

            res.setData(formVO);
            res.setMessage("Form edited successfully!");

            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

        } catch (Exception e) {
            res.setMessage("Fail to edit form. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @DeleteMapping("/deleteForm/{id}")
    public ResponseEntity<CommonApiResult<Void>> deleteForm(@PathVariable String id) {
        CommonApiResult<Void> res = new CommonApiResult<>();

        if (id == null) {
            res.setMessage("Invalid Request. Please try again.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        try {
            boolean success = formService.deleteForm(id);

            if (!success) {
                res.setMessage("Fail to delete form. Please try again later.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
            }

            res.setMessage("Form deleted successfully!");

            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

        } catch (Exception e) {
            res.setMessage("Fail to delete form. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

}
