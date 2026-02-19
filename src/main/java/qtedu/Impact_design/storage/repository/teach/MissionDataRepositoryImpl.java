package qtedu.Impact_design.storage.repository.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.team.TbMissionDataModel;
import qtedu.Impact_design.domain.repository.teach.MissionDataRepository;
import qtedu.Impact_design.storage.jpaentity.teach.TbMissionData;
import qtedu.Impact_design.storage.jparepository.teach.TbMissionDataJpaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MissionDataRepositoryImpl implements MissionDataRepository {

    private final TbMissionDataJpaRepository tbMissionDataJpaRepository;

    @Override
    public List<TbMissionDataModel> findByMissionId(Integer missionId) {
        return tbMissionDataJpaRepository.findByMissionId(missionId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void saveAll(Integer missionId, List<Integer> teamIds) {
        List<TbMissionData> entities = teamIds.stream()
                .map(teamId -> TbMissionData.builder()
                        .missionId(missionId)
                        .teamId(teamId)
                        .build())
                .collect(Collectors.toList());
        tbMissionDataJpaRepository.saveAll(entities);
    }

    private TbMissionDataModel toModel(TbMissionData entity) {
        return TbMissionDataModel.builder()
                .missionDataId(entity.getMissionDataId())
                .statusCeo(entity.getStatusCeo())
                .statusMar(entity.getStatusMar())
                .statusPro(entity.getStatusPro())
                .statusFin(entity.getStatusFin())
                .statusCho(entity.getStatusCho())
                .teamId(entity.getTeamId())
                .missionId(entity.getMissionId())
                .build();
    }
}
