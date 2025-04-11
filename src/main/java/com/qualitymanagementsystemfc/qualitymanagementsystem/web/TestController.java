package com.qualitymanagementsystemfc.qualitymanagementsystem.web;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.enums.UserRole;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.UserDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.auth.LoginRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.utils.CommonApiResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    @PostMapping("/login")
    public CommonApiResult<UserDO> testLogin(@RequestBody LoginRequest request) {
        CommonApiResult<UserDO> result = new CommonApiResult<>();
        System.out.println("Called login");
        System.out.println(request.getUsername());
        System.out.println(request.getPassword());

        UserDO user = new UserDO();
        user.setName("Amy");
        user.setRole(UserRole.STUDENT.getCode());
        user.setPassword("123");

        result.setData(user);
//        result.setStatus(401);
        result.setMessage("Login unsuccessfully");



        return result;
//        Map<String, String> response = new HashMap<>();
//        response.put("error", "Login failed: Invalid username or password");
//
//        return ResponseEntity.status(401).body(response); // Returns JSON response
    }

    @GetMapping("/hi")
    public String test(@RequestBody LoginRequest request) {


        return "Hi";
    }
}
