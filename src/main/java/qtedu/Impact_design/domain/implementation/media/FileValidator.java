package qtedu.Impact_design.domain.implementation.media;

import org.springframework.stereotype.Component;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.domain.model.media.FileData;

import java.util.List;

@Component
public class FileValidator {

    public void validateFilesNameCorrect(List<FileData> files) {
        for (FileData file : files) {
            if (file.getName() == null || file.getName().isEmpty()) {
                throw new ConflictException(ErrorCode.FILE_NAME_INCORRECT);
            }
        }
    }

    public void validateFileNameCorrect(FileData file) {
        System.out.println("파일 이름: " + file.getName());
        if (file.getName() == null || file.getName().isEmpty()) {
            throw new ConflictException(ErrorCode.FILE_NAME_INCORRECT);
        }
    }
}