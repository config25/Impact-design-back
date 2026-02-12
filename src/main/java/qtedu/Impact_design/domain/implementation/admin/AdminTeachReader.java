package qtedu.Impact_design.domain.implementation.admin;

import lombok.RequiredArgsConstructor;
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

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminTeachReader {

    private final UserinfoRepository userinfoRepository;
    private final GameRepository gameRepository;

    public List<ClassInfoResponse> getAllClassList(Long userId) {
        validateAdmin(userId);
        return toResponseList(gameRepository.findAllClassListByStatus(10));
    }

    public TeachListResponse getAllTeachList(Long userId) {
        validateAdmin(userId);

        List<ClassInfoResponse> inProgress = toResponseList(gameRepository.findAllClassListByStatus(10));
        List<ClassInfoResponse> setting = toResponseList(gameRepository.findAllClassListByStatus(1));
        List<ClassInfoResponse> completed = toResponseList(gameRepository.findAllClassListByStatus(100));
        List<ClassInfoResponse> etc = toResponseList(gameRepository.findAllClassListByStatus(0));

        return TeachListResponse.builder()
                .inProgress(inProgress)
                .setting(setting)
                .completed(completed)
                .etc(etc)
                .build();
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
                .map(c -> ClassInfoResponse.from(c, null))
                .collect(Collectors.toList());
    }
}
