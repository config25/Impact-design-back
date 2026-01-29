package qtedu.Impact_design.domain.implementation.media;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.domain.external.ExternalFileClient;
import qtedu.Impact_design.domain.model.media.Media;

@Component
@RequiredArgsConstructor
public class FileRemover {

    private final ExternalFileClient externalFileClient;

    public void removeFile(Media media) {
        externalFileClient.removeFile(media);
    }
}
