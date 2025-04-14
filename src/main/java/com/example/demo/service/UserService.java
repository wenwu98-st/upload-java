package com.example.demo.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.UploadedFile;
import com.example.demo.entity.User;
import com.example.demo.repository.UploadedFileRepository;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UploadedFileRepository uploadedFileRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 邮箱注册
    public User registerByEmail(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        if (existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("邮箱已被注册");
        }

        user.setCreatedAt(LocalDateTime.now()); // 设置创建时间
        return userRepository.save(user); // 返回保存后的 user，带有 id
    }

    // 手机号注册
    public User registerByPhoneNumber(User user) {
        if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        if (existsByPhoneNumber(user.getPhoneNumber())) {
            throw new IllegalArgumentException("手机号已被注册");
        }

        user.setCreatedAt(LocalDateTime.now()); // 设置创建时间
        return userRepository.save(user); // 返回保存后的 user，带有 id
    }

    public String login(String identifier, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(identifier);

        if (optionalUser.isEmpty()) {
            optionalUser = userRepository.findByEmail(identifier);
        }

        if (optionalUser.isEmpty()) {
            optionalUser = userRepository.findByPhoneNumber(identifier);
        }

        if (optionalUser.isPresent() && optionalUser.get().getPassword().equals(password)) {
            return "Login successful";
        }

        return "用户名或密码错误";
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    public UploadedFile saveUploadedFile(MultipartFile file, String uploadedBy) throws IOException {
        // 保存文件到磁盘
        String filePath = uploadDir + File.separator + file.getOriginalFilename();
        File dest = new File(filePath);
        file.transferTo(dest);

        // 保存记录到数据库
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileName(file.getOriginalFilename());
        uploadedFile.setUploadedBy(uploadedBy);
        uploadedFile.setUploadTime(LocalDateTime.now());
        uploadedFile.setFilePath(filePath);

        return uploadedFileRepository.save(uploadedFile);
    }
}
