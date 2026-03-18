package qtedu.Impact_design.domain.implementation.team;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamReader {

    private final TeamUserRepository teamUserRepository;

    public List<Long> readTeamMemberUserIds(Long userId) {
        TeamUserModel currentUser = teamUserRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        return teamUserRepository.findByTeamId(currentUser.getTeamId()).stream()
                .map(TeamUserModel::getUserId)
                .collect(Collectors.toList());
    }

    public List<Long> readTeamMemberUserIdsByTeamId(Integer teamId) {
        return teamUserRepository.findByTeamId(teamId).stream()
                .map(TeamUserModel::getUserId)
                .collect(Collectors.toList());
    }
}
