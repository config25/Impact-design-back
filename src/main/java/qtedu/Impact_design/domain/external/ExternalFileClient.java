package qtedu.Impact_design.domain.external;

import qtedu.Impact_design.domain.model.media.FileData;
import qtedu.Impact_design.domain.model.media.Media;

public interface ExternalFileClient {

    void uploadFile(FileData file, Media media);

    void removeFile(Media media);
}