package com.devloop.common.Validator;

import com.devloop.attachment.enums.FileFormat;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import org.springframework.stereotype.Component;

@Component
public class FileValidator {
    public FileFormat mapStringToFileFormat(String fileType) {
        if (fileType.equalsIgnoreCase("image/png")) {
            return FileFormat.PNG;
        } else if (fileType.equalsIgnoreCase("image/jpeg")) {
            return FileFormat.JPEG;
        } else if (fileType.equalsIgnoreCase("image/jpg")) {
            return FileFormat.JPG;
        }
        throw new ApiException(ErrorStatus._UNSUPPORTED_FILE_TYPE);
    }
}
