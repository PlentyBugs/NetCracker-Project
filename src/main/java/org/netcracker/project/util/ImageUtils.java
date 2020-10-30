package org.netcracker.project.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ImageUtils {

    @Value("${upload.path}")
    private static String uploadPath;

    public static String saveFile(MultipartFile file) throws IOException {
        if (file != null && file.getOriginalFilename() != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);

            if (uploadDir.exists()) uploadDir.mkdir();

            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFilename));

            return resultFilename;
        }
        return "";
    }
}
