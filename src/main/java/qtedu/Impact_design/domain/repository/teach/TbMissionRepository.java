package qtedu.Impact_design.domain.repository.teach;

import qtedu.Impact_design.domain.model.team.TbMissionModel;

import java.util.Optional;

public interface TbMissionRepository {
    Optional<TbMissionModel> findLatestByGameId(Integer gameId);
    TbMissionModel save(TbMissionModel model);
}
