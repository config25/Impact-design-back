package qtedu.Impact_design.domain.implementation.media;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.domain.model.UserId;
import qtedu.Impact_design.domain.model.media.FileCategory;
import qtedu.Impact_design.domain.model.media.FileData;
import qtedu.Impact_design.domain.model.media.Media;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FileGenerator {

    @Value("${file.base-url:${cloud.aws.s3.base-url:}}")
    private String baseUrl;

    public Media generateMedia(FileData file, UserId userId, FileCategory category) {
        return Media.upload(baseUrl, "", category, userId, file.getName(), file.getContentType());
    }

    public List<Map.Entry<FileData, Media>> generateMedias(List<FileData> files, UserId userId, FileCategory category) {
        return files.stream()
                .map(file -> new AbstractMap.SimpleEntry<>(
                        file,
                        Media.upload(baseUrl, "", category, userId, file.getName(), file.getContentType())
                ))
                .collect(Collectors.toList());
    }
}
