package vn.mentora.mentora_api.service;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import vn.mentora.mentora_api.util.error.IdInvalidException;

@Service
public class FileService {

    @Value("${app.upload-file.base-uri}")
    private String baseUri;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "webp");

    public String store(MultipartFile file) throws IdInvalidException, IOException {
        if (file == null || file.isEmpty()) {
            throw new IdInvalidException("File không được để trống!");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IdInvalidException("Chỉ chấp nhận file ảnh: jpg, jpeg, png, webp");
        }

        String uniqueFilename = UUID.randomUUID().toString() + "." + extension;

        // file:///D:/pet_project/uploads/mentora-uploads/ ->
        // D:/pet_project/uploads/mentora-uploads
        Path uploadDir = Paths.get(URI.create(baseUri));
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // ghép đường dẫn
        Path targetPath = uploadDir.resolve(uniqueFilename);
        // copy stream dữ liệu vào file mới tạo (UUID đảm bảo không trùng tên)
        Files.copy(file.getInputStream(), targetPath);
        return uniqueFilename;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

}
