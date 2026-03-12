package qtedu.Impact_design.domain.implementation.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.response.teach.ClassInfoResponse;
import qtedu.Impact_design.api.dto.response.teach.TeachListResponse;
import qtedu.Impact_design.common.error.AuthorizationException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.en.UserRole;
import qtedu.Impact_design.domain.model.user.UserinfoModel;
import qtedu.Impact_design.domain.repository.teach.GameRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;
import qtedu.Impact_design.domain.model.team.ClassInfoProjection;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminTeachReader {

    private final UserinfoRepository userinfoRepository;
    private final GameRepository gameRepository;

    @Value("${file.base-url:}")
    private String fileBaseUrl;

    public List<ClassInfoResponse> getAllClassList(Long userId) {
        validateAdmin(userId);
        Map<Integer, List<ClassInfoResponse>> grouped = groupByStatus();
        return grouped.getOrDefault(10, Collections.emptyList());
    }

    public TeachListResponse getAllTeachList(Long userId) {
        validateAdmin(userId);
        Map<Integer, List<ClassInfoResponse>> grouped = groupByStatus();

        return TeachListResponse.builder()
                .inProgress(grouped.getOrDefault(10, Collections.emptyList()))
                .setting(grouped.getOrDefault(1, Collections.emptyList()))
                .completed(grouped.getOrDefault(100, Collections.emptyList()))
                .etc(grouped.getOrDefault(0, Collections.emptyList()))
                .build();
    }

    private Map<Integer, List<ClassInfoResponse>> groupByStatus() {
        return gameRepository.findAllActiveClassList().stream()
                .collect(Collectors.groupingBy(
                        ClassInfoProjection::getStatus,
                        Collectors.mapping(
                                c -> ClassInfoResponse.from(c, null, resolveImageUrl(c.getImageUrl())),
                                Collectors.toList()
                        )
                ));
    }

    private void validateAdmin(Long userId) {
        UserinfoModel user = userinfoRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        if (user.getUserRole() != UserRole.ADMIN) {
            throw new AuthorizationException(ErrorCode.ACCESS_DENIED);
        }
    }

    private List<ClassInfoResponse> toResponseList(List<ClassInfoProjection> projections) {
        return projections.stream()
                .map(c -> ClassInfoResponse.from(c, null, resolveImageUrl(c.getImageUrl())))
                .collect(Collectors.toList());
    }

    private String resolveImageUrl(String path) {
        if (path == null || path.isBlank()) return null;
        return fileBaseUrl + "/" + path;
    }
}
