package qtedu.Impact_design.domain.implementation.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.response.teach.DeletedTeamResponse;
import qtedu.Impact_design.api.dto.response.teach.TeamInfoResponse;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.team.TbTeamModel;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeachTeamReader {

    private final TbTeamRepository tbTeamRepository;
    private final TeamUserRepository teamUserRepository;
    private final UserinfoRepository userinfoRepository;

    public List<DeletedTeamResponse> getDeletedTeams(Integer gameId) {
        return tbTeamRepository.findDeletedByGameId(gameId).stream()
                .map(team -> DeletedTeamResponse.builder()
                        .teamId(team.getTeamId())
                        .teamName(team.getName())
                        .sequence(team.getSequence())
                        .build())
                .collect(Collectors.toList());
    }

    public TeamInfoResponse getTeamInfo(Integer teamId) {
        TbTeamModel team = tbTeamRepository.findByTeamId(teamId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        List<Long> userIds = teamUserRepository.findByTeamId(teamId).stream()
                .map(TeamUserModel::getUserId)
                .collect(Collectors.toList());

        List<TeamInfoResponse.MemberInfo> members = userIds.isEmpty()
                ? List.of()
                : userinfoRepository.findAllByUserIds(userIds).stream()
                        .map(user -> TeamInfoResponse.MemberInfo.builder()
                                .userId(user.getUserId())
                                .loginId(user.getLoginId())
                                .name(user.getUserName())
                                .writer(user.getWriter())
                                .build())
                        .collect(Collectors.toList());

        return TeamInfoResponse.builder()
                .teamId(team.getTeamId())
                .teamName(team.getName())
                .sequence(team.getSequence())
                .isDoing(team.getIsDoing())
                .teamCategory(team.getTeamCategory())
                .numUser(members.size())
                .members(members)
                .build();
    }
}
