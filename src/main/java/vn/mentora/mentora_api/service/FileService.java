package vn.mentora.mentora_api.service;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    // private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg",
    // "jpeg", "png", "webp");

    public String store(MultipartFile file, String folder) throws IdInvalidException, IOException {
        if (file == null || file.isEmpty()) {
            throw new IdInvalidException("File không được để trống!");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);

        if (folder.equals("avatars")) {
            List<String> allowedImages = Arrays.asList("jpg", "jpeg", "png", "webp");
            if (!allowedImages.contains(extension.toLowerCase())) {
                throw new IdInvalidException("Chỉ chấp nhận file ảnh: jpg, jpeg, png, webp");
            }
        } else if (folder.equals("cvs")) {
            List<String> allowedCvs = Arrays.asList("pdf", "doc", "docx");
            if (!allowedCvs.contains(extension.toLowerCase())) {
                throw new IdInvalidException("Chỉ chấp nhận file cv: pdf, doc, docx");
            }
        }

        String uniqueFilename = UUID.randomUUID().toString() + "." + extension;
        Path uploadDir = Paths.get(URI.create(baseUri)).resolve(folder);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path targetPath = uploadDir.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetPath);

        return folder + "/" + uniqueFilename;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

}
