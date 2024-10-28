package com.devloop.common.Validator;

import com.devloop.attachment.enums.FileFormat;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.community.entity.Community;
import com.devloop.party.entity.Party;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class FileValidator {
    public FileFormat mapStringToFileFormat(String fileType) {
        log.info("::: fileType :::" + fileType);
        if (fileType.equalsIgnoreCase("image/png")) {
            return FileFormat.PNG;
        } else if (fileType.equalsIgnoreCase("image/jpeg")) {
            return FileFormat.JPEG;
        } else if (fileType.equalsIgnoreCase("image/jpg")) {
            return FileFormat.JPG;
        } else if (fileType.equalsIgnoreCase("text/plain")) {
            return FileFormat.TXT;
        } else if (fileType.equalsIgnoreCase("application/pdf")) {
            return FileFormat.PDF;
        } else if (fileType.contains("gif")) {
            return FileFormat.GIF;
        }
        throw new ApiException(ErrorStatus._UNSUPPORTED_FILE_TYPE);
    }

    public <T> void fileTypeValidator(MultipartFile file, T object) {

        List<String> acceptedTypes = List.of();
        if (object instanceof Party) {
            acceptedTypes = Arrays.asList("jpg", "png", "pdf", "jpeg");
        } else if (object instanceof Community) {
            acceptedTypes = Arrays.asList("jpg", "png", "pdf", "jpeg");
        } else if (object instanceof User) {
            acceptedTypes = Arrays.asList("jpg", "png", "jpeg");
        } else if (object instanceof ProjectWithTutor) {
            acceptedTypes = Arrays.asList("jpg", "png", "jpeg", "gif", "pdf");
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || !acceptedTypes.contains(fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase())) {
            throw new ApiException(ErrorStatus._UNSUPPORTED_FILE_TYPE);
        }
    }

    public void fileSizeValidator(MultipartFile file, Long size) {
        log.info(String.valueOf(file.getSize()));
        if (file.getSize() > size) {
            throw new IllegalArgumentException("파일 크기가 너무 큽니다." + file.getSize());
        }
    }
}
