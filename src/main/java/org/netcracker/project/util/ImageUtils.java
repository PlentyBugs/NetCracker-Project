package org.netcracker.project.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            String resultFilename = createFile() + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFilename));

            return resultFilename;
        }
        return "";
    }

    /**
     * Метод для сохранения файлов
     * @param file - Файл
     * @param x - X координата начала обрезки
     * @param y - Y координата начала обрезки
     * @param width - Ширина обрезанного изображения
     * @param height - Высота обрезанного изображения
     * @return - Имя файла
     * @throws IOException - Возможное исключение при неудачном взаимодействии с файловой системой
     */
    public String cropAndSaveImage(MultipartFile file, Integer x, Integer y, Integer width, Integer height) throws IOException {
        if (file != null && file.getOriginalFilename() != null && !file.getOriginalFilename().isEmpty()) {
            BufferedImage image = ImageIO.read(file.getInputStream());
            BufferedImage newImage = image.getSubimage(x, y, width, height);

            String resultFilename = createFile() + "." + file.getOriginalFilename();

            Matcher formatFinder = Pattern.compile(".+\\.(.+)").matcher(resultFilename);
            if (formatFinder.find()) {
                ImageIO.write(newImage, formatFinder.group(1), new File(uploadPath + "/" + resultFilename));
            }

            return resultFilename;
        }
        return "";
    }

    private String createFile() {
        File uploadDir = new File(uploadPath);

        if (uploadDir.exists()) uploadDir.mkdir();

        String uuidFile = UUID.randomUUID().toString();

        return uuidFile;
    }
}
