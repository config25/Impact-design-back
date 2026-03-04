package qtedu.Impact_design.domain.repository.auth;

import qtedu.Impact_design.domain.model.team.TeamUserModel;

import java.util.List;
import java.util.Optional;

public interface TeamUserRepository {
    TeamUserModel save(TeamUserModel teamUserModel);
    List<TeamUserModel> findByTeamId(Integer teamId);
    List<TeamUserModel> findByTeamIdIn(List<Integer> teamIds);
    Optional<TeamUserModel> findByUserId(Long userId);
    Optional<String> findTeamNameByUserId(Long userId);
    void deleteByUserId(Long userId);
    void updateTeamId(Long userId, Integer teamId);
    int countByTeamId(Integer teamId);
}
