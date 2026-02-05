package qtedu.Impact_design.domain.implementation.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.response.teach.ClassInfoResponse;
import qtedu.Impact_design.api.dto.response.teach.TeachListResponse;
import qtedu.Impact_design.domain.repository.teach.GameRepository;
import qtedu.Impact_design.domain.model.team.TbMissionDataModel;
import qtedu.Impact_design.domain.repository.teach.MissionDataRepository;
import qtedu.Impact_design.storage.jparepository.teach.ClassInfoProjection;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TeachFacade {

    private final GameRepository gameRepository;
    private final MissionDataRepository missionDataRepository;

    @Transactional(readOnly = true)
    public List<ClassInfoResponse> getTeachIndex(Long userId) {
        List<ClassInfoProjection> classList = gameRepository.findClassList(userId, 10);

        return classList.stream().map(c -> {
            Integer statusCeo = calculateMinStatusCeo(c.getMissionId());
            return ClassInfoResponse.from(c, statusCeo);
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TeachListResponse getTeachList(Long userId) {
        List<ClassInfoResponse> inProgress = toResponseList(gameRepository.findClassList(userId, 10));
        List<ClassInfoResponse> setting = toResponseList(gameRepository.findClassList(userId, 1));
        List<ClassInfoResponse> completed = toResponseList(gameRepository.findClassList(userId, 100));
        List<ClassInfoResponse> etc = toResponseList(gameRepository.findClassList(userId, 0));

        return TeachListResponse.builder()
                .inProgress(inProgress)
                .setting(setting)
                .completed(completed)
                .etc(etc)
                .build();
    }

    private Integer calculateMinStatusCeo(Integer missionId) {
        if (missionId == null) {
            return 20;
        }
        List<TbMissionDataModel> dataList = missionDataRepository.findByMissionId(missionId.longValue());
        int minStatus = 20;
        for (TbMissionDataModel data : dataList) {
            if (data.getStatusCeo() != null && data.getStatusCeo() < minStatus) {
                minStatus = data.getStatusCeo();
            }
        }
        return minStatus;
    }

    private List<ClassInfoResponse> toResponseList(List<ClassInfoProjection> projections) {
        return projections.stream()
                .map(c -> ClassInfoResponse.from(c, null))
                .collect(Collectors.toList());
    }
}
