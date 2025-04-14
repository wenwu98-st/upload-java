CREATE TABLE uploaded_files (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    uploaded_by VARCHAR(255) NOT NULL,
    upload_time DATETIME NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL
);
