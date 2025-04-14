package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.UploadedFile;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
}
