package qtedu.Impact_design.storage.repository.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.storage.jpaentity.teach.TeamUser;
import qtedu.Impact_design.storage.jparepository.teach.TeamUserJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TeamUserRepositoryImpl implements TeamUserRepository {

    private final TeamUserJpaRepository teamUserJpaRepository;

    @Override
    public TeamUserModel save(TeamUserModel model) {
        TeamUser entity = TeamUser.builder()
                .teamUserId(model.getTeamUserId())
                .userId(model.getUserId())
                .teamId(model.getTeamId())
                .userlevel(model.getUserlevel())
                .isDoing(model.getIsDoing())
                .build();
        TeamUser saved = teamUserJpaRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public List<TeamUserModel> findByTeamId(Integer teamId) {
        return teamUserJpaRepository.findByTeamId(teamId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamUserModel> findByTeamIdIn(List<Integer> teamIds) {
        return teamUserJpaRepository.findByTeamIdIn(teamIds).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TeamUserModel> findByUserId(Long userId) {
        return teamUserJpaRepository.findByUserId(userId)
                .map(this::toModel);
    }

    @Override
    public void deleteByUserId(Long userId) {
        teamUserJpaRepository.deleteByUserId(userId);
    }

    @Override
    public void updateTeamId(Long userId, Integer teamId) {
        teamUserJpaRepository.updateTeamIdByUserId(userId, teamId);
    }

    @Override
    public int countByTeamId(Integer teamId) {
        return teamUserJpaRepository.countByTeamId(teamId);
    }

    private TeamUserModel toModel(TeamUser entity) {
        return TeamUserModel.builder()
                .teamUserId(entity.getTeamUserId())
                .userId(entity.getUserId())
                .teamId(entity.getTeamId())
                .userlevel(entity.getUserlevel())
                .isDoing(entity.getIsDoing())
                .build();
    }
}
