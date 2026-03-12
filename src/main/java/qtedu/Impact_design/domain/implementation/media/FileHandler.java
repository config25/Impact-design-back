package qtedu.Impact_design.domain.implementation.media;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.common.error.BadRequestException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.domain.model.UserId;
import qtedu.Impact_design.domain.model.media.FileCategory;
import qtedu.Impact_design.domain.model.media.FileData;
import qtedu.Impact_design.domain.model.media.Media;
import qtedu.Impact_design.domain.model.media.MediaType;
import qtedu.Impact_design.domain.util.AsyncJobExecutor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
public class FileHandler {

    private final FileAppender fileAppender;
    private final FileRemover fileRemover;
    private final FileGenerator fileGenerator;
    private final FileValidator fileValidator;
    private final AsyncJobExecutor asyncJobExecutor;

    public List<Media> handleNewFiles(UserId userId, List<FileData> files, FileCategory category) {
        fileValidator.validateFilesNameCorrect(files);
        List<Map.Entry<FileData, Media>> mediaWithFiles = fileGenerator.generateMedias(files, userId, category);
        try {
            asyncJobExecutor.executeAsyncJobs(mediaWithFiles, pair ->
                    fileAppender.appendFile(pair.getKey(), pair.getValue()));
        } catch (Exception e) {
            log.error("파일 업로드 실패 - userId: {}, fileCount: {}", userId.getId(), files.size(), e);
            throw new BadRequestException(ErrorCode.FILE_UPLOAD_FAILED);
        }
        return mediaWithFiles.stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public Media handleNewFile(UserId userId, FileData file, FileCategory category) {
        fileValidator.validateFileNameCorrect(file);
        Media media = fileGenerator.generateMedia(file, userId, category);
        try {
            asyncJobExecutor.executeAsyncJob(media, item -> fileAppender.appendFile(file, media));
        } catch (Exception e) {
            log.error("파일 업로드 실패 - userId: {}, fileName: {}", userId.getId(), file.getName(), e);
            throw new BadRequestException(ErrorCode.FILE_UPLOAD_FAILED);
        }
        return media;
    }

    public void handleOldFile(Media media) {
        if (!MediaType.IMAGE_BASIC.equals(media.getType())) {
            try {
                asyncJobExecutor.executeAsyncJob(media, fileRemover::removeFile);
            } catch (Exception e) {
                log.error("파일 삭제 실패 - path: {}", media.getPath(), e);
                throw new BadRequestException(ErrorCode.FILE_DELETE_FAILED);
            }
        }
    }

    public void handleOldFiles(List<Media> medias) {
        try {
            asyncJobExecutor.executeAsyncJobs(medias, fileRemover::removeFile);
        } catch (Exception e) {
            log.error("파일 일괄삭제 실패 - count: {}", medias.size(), e);
            throw new BadRequestException(ErrorCode.FILE_DELETE_FAILED);
        }
    }
}
