package com.qualitymanagementsystemfc.qualitymanagementsystem.web;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.enums.UserRole;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.module.AddModuleRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.module.DeleteModuleRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.module.EditModuleRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.module.GetModuleRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.module.ModuleVO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.ModuleService;
import com.qualitymanagementsystemfc.qualitymanagementsystem.utils.CommonApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/module")
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    @GetMapping("/getAllModule")
    public ResponseEntity<CommonApiResult<List<ModuleVO>>> getAllModule() {
        CommonApiResult<List<ModuleVO>> res = new CommonApiResult<>();
        List<ModuleVO> allModuleWithCategories = moduleService.getAllModules();
        res.setData(allModuleWithCategories);

        return ResponseEntity.ok(res);
    }

    @PostMapping("/getAllAccessibleModule")
    public ResponseEntity<CommonApiResult<List<ModuleVO>>> getAllAccessibleModule(@RequestBody GetModuleRequest request) {
        CommonApiResult<List<ModuleVO>> res = new CommonApiResult<>();

        if (request == null || request.getRole().isBlank()) {
            res.setMessage("Request or role is null.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        try {

            List<ModuleVO> allModuleWithCategories = new ArrayList<>();

            String role = request.getRole();

            if (role.equals(UserRole.ADMIN.getCode()) || role.equals(UserRole.SPK_MANAGER.getCode())) {
                allModuleWithCategories = moduleService.getAllModules();
            } else {
                allModuleWithCategories =  moduleService.getAllModulesByRole(role);
            }

            res.setData(allModuleWithCategories);

            return ResponseEntity.ok(res);

        } catch (Exception e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }

    }

    @PostMapping("/addModule")
    public ResponseEntity<CommonApiResult<ModuleVO>> addNewModule(@RequestBody AddModuleRequest request) {
        CommonApiResult<ModuleVO> res = new CommonApiResult<>();

        if (request == null) {
            res.setMessage("Request is null.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        try {
            ModuleVO addedModule = moduleService.addModule(request);

            res.setData(addedModule);
            res.setMessage("Module added successfully!");

            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

        } catch (Exception e) {
            res.setMessage("Fail to add new module. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }

    }

    @PostMapping("/editModule")
    public ResponseEntity<CommonApiResult<ModuleVO>> editModule(@RequestBody EditModuleRequest request) {
        CommonApiResult<ModuleVO> res = new CommonApiResult<>();

        if (request == null) {
            res.setMessage("Request is null.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        try {
            ModuleVO editedModule = moduleService.editModule(request);

            res.setData(editedModule);
            res.setMessage("Module edited successfully!");

            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

        } catch (Exception e) {
            res.setMessage("Fail to add new module. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }

    }

    @DeleteMapping("/deleteModule")
    public ResponseEntity<CommonApiResult<Void>> deleteModule(@RequestBody DeleteModuleRequest request) {
        CommonApiResult<Void> res = new CommonApiResult<>();

        if (request == null) {
            res.setMessage("Request is null.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        try {
            boolean success = moduleService.deleteModule(request.getModuleId());

            if (!success) {
                res.setMessage("Unable to delete module. Module still contains category.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
            }

            res.setMessage("Module deleted successfully!");
            return ResponseEntity.ok(res);

        } catch (IllegalArgumentException | IllegalStateException e) {
            res.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

        } catch (Exception e) {
            res.setMessage("Fail to delete module. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }

    }
}
