package qtedu.Impact_design.domain.implementation.media;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.domain.external.ExternalFileClient;
import qtedu.Impact_design.domain.model.media.FileData;
import qtedu.Impact_design.domain.model.media.Media;

@Component
@RequiredArgsConstructor
public class FileAppender {

    private final ExternalFileClient externalFileClient;

    public void appendFile(FileData file, Media media) {
        externalFileClient.uploadFile(file, media);
    }
}
