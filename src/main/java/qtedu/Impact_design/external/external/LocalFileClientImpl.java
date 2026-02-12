package qtedu.Impact_design.external.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.domain.external.ExternalFileClient;
import qtedu.Impact_design.domain.model.media.FileData;
import qtedu.Impact_design.domain.model.media.Media;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Primary
@Component
public class LocalFileClientImpl implements ExternalFileClient {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void uploadFile(FileData file, Media media) {
        try {
            Path filePath = Paths.get(uploadDir, media.getPath());
            Files.createDirectories(filePath.getParent());

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new ConflictException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public void removeFile(Media media) {
        try {
            Path filePath = Paths.get(uploadDir, media.getPath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new ConflictException(ErrorCode.FILE_DELETE_FAILED);
        }
    }
}
