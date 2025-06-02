package com.qualitymanagementsystemfc.qualitymanagementsystem.web;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.CategoryDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.CategoryService;
import com.qualitymanagementsystemfc.qualitymanagementsystem.utils.CommonApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/getAllCategories")
    public ResponseEntity<CommonApiResult<List<CategoryDO>>> getAllCategories() {
        CommonApiResult<List<CategoryDO>> res = new CommonApiResult<>();

        List<CategoryDO> categories = categoryService.getAllCategory();
        res.setData(categories);
        return ResponseEntity.ok(res);
    }

//    @GetMapping("/getCategoriesByModule/{moduleId}")
//    public ResponseEntity<CommonApiResult<List<Category>>> getCategoriesByModule(@PathVariable String moduleId) {
//        CommonApiResult<Category> res = new CommonApiResult<>();
//
//        List<Category> categories = new ArrayList<>();
//        res.setData(categories);
//        return ResponseEntity.ok(res);
//    }

//    @PostMapping("/addCategory")
//    public ResponseEntity<CommonApiResult<CategoryDO>> addNewCategoryForExistingModule(@RequestBody AddCategoryRequest request) {
//        CommonApiResult<CategoryDO> res = new CommonApiResult<>();
//
//        try {
//            CategoryDO addedCategory = categoryService.addCategory(request.getCategory());
//
//            res.setData(addedCategory);
//            res.setMessage("Category added successfully!");
//
//            return ResponseEntity.ok(res);
//
//        } catch (IllegalArgumentException e) {
//            res.setMessage(e.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
//
//        } catch (Exception e) {
//            res.setMessage("Fail to add new category. Please try again later.");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
//        }
//    }
}
