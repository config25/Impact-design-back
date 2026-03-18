package qtedu.Impact_design.domain.repository.teach;

import qtedu.Impact_design.domain.model.team.TbMissionDataModel;

import java.util.List;

public interface MissionDataRepository {
    List<TbMissionDataModel> findByMissionId(Integer missionId);
    List<TbMissionDataModel> findByMissionIdIn(List<Integer> missionIds);
    void saveAll(Integer missionId, List<Integer> teamIds);
}
