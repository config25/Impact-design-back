package qtedu.Impact_design.domain.repository.auth;

import qtedu.Impact_design.domain.model.team.TbTeamModel;

import java.util.List;
import java.util.Optional;

public interface TbTeamRepository {
    List<TbTeamModel> findByCode(String code);
    List<TbTeamModel> findByCodeExcludingTeam(String code, Integer excludeTeamId);
    Optional<TbTeamModel> findByTeamId(Integer teamId);
    TbTeamModel save(TbTeamModel model);
    List<TbTeamModel> findDeletedByGameId(Integer gameId);
}
