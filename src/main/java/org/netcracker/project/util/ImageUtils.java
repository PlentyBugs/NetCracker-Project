package org.netcracker.project.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class ImageUtils {

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * Метод для сохранения файлов
     * @param file - Файл
     * @return - Имя файла
     * @throws IOException - Возможное исключение при неудачном взаимодействии с файловой системой
     */
    public String saveFile(MultipartFile file) throws IOException {
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
