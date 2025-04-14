package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.UploadedFile;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/register/email")
    public Map<String, Object> registerByEmail(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", System.currentTimeMillis());

        try {
            logger.info("Attempting to register user with email: {}", user.getEmail());

            // 自动生成用户名（如果前端没传）
            if (user.getUsername() == null || user.getUsername().isEmpty()) {
                user.setUsername("user_" + UUID.randomUUID().toString().substring(0, 8));
            }

            User registeredUser = userService.registerByEmail(user);

            response.put("code", 200);
            response.put("message", "邮箱注册成功");
            response.put("data", Map.of(
                    "user", Map.of(
                            "id", registeredUser.getId(),
                            "username", registeredUser.getUsername()
                    )
            ));
        } catch (IllegalArgumentException e) {
            logger.warn("Validation error during email registration: {}", e.getMessage());
            response.put("code", 400);
            response.put("message", "邮箱注册失败: " + e.getMessage());
            response.put("data", null);
        } catch (Exception e) {
            logger.error("Unexpected error during email registration", e);
            response.put("code", 500);
            response.put("message", "邮箱注册失败: " + (e.getMessage() != null ? e.getMessage() : "未知错误"));
            response.put("data", null);
        }

        return response;
    }

    @PostMapping("/register/phone")
    public Map<String, Object> registerByPhoneNumber(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", System.currentTimeMillis());

        try {
            logger.info("Attempting to register user with phone number: {}", user.getPhoneNumber());

            // 自动生成用户名（如果前端没传）
            if (user.getUsername() == null || user.getUsername().isEmpty()) {
                user.setUsername("user_" + UUID.randomUUID().toString().substring(0, 8));
            }

            User registeredUser = userService.registerByPhoneNumber(user);

            response.put("code", 200);
            response.put("message", "手机号注册成功");
            response.put("data", Map.of(
                    "user", Map.of(
                            "id", registeredUser.getId(),
                            "username", registeredUser.getUsername()
                    )
            ));
        } catch (IllegalArgumentException e) {
            logger.warn("Validation error during phone registration: {}", e.getMessage());
            response.put("code", 400);
            response.put("message", "手机号注册失败: " + e.getMessage());
            response.put("data", null);
        } catch (Exception e) {
            logger.error("Unexpected error during phone registration", e);
            response.put("code", 500);
            response.put("message", "手机号注册失败: " + (e.getMessage() != null ? e.getMessage() : "未知错误"));
            response.put("data", null);
        }

        return response;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginRequest) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", System.currentTimeMillis());

        try {
            String identifier = loginRequest.get("identifier");
            String password = loginRequest.get("password");

            if (identifier == null || identifier.isEmpty() || password == null || password.isEmpty()) {
                throw new IllegalArgumentException("用户名/邮箱/手机号和密码不能为空");
            }

            String result = userService.login(identifier, password);

            if ("Login successful".equals(result)) {
                response.put("code", 200);
                response.put("message", result);
            } else {
                response.put("code", 401);
                response.put("message", result);
            }
        } catch (Exception e) {
            logger.error("Error occurred during login", e);
            response.put("code", 500);
            response.put("message", "登录失败: " + (e.getMessage() != null ? e.getMessage() : "未知错误"));
        }

        return response;
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/upload")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("uploadedBy") String uploadedBy) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", System.currentTimeMillis());

        try {
            logger.info("Received file upload request. Uploaded by: {}, File name: {}", uploadedBy, file.getOriginalFilename());

            if (file.isEmpty()) {
                throw new IllegalArgumentException("文件不能为空");
            }

            // 保存文件并记录上传信息
            UploadedFile uploadedFile = userService.saveUploadedFile(file, uploadedBy);

            response.put("code", 200);
            response.put("message", "文件上传成功");
            response.put("data", Map.of(
                    "fileName", uploadedFile.getFileName(),
                    "uploadedBy", uploadedFile.getUploadedBy(),
                    "uploadTime", uploadedFile.getUploadTime(),
                    "filePath", uploadedFile.getFilePath()
            ));
        } catch (IllegalArgumentException e) {
            logger.warn("Validation error during file upload: {}", e.getMessage());
            response.put("code", 400);
            response.put("message", "文件上传失败: " + e.getMessage());
            response.put("data", null);
        } catch (Exception e) {
            logger.error("Unexpected error during file upload", e);
            response.put("code", 500);
            response.put("message", "文件上传失败: " + (e.getMessage() != null ? e.getMessage() : "未知错误"));
            response.put("data", null);
        }

        return response;
    }
}
