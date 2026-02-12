package qtedu.Impact_design.domain.implementation.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.team.TbTeamModel;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TeachTeamRemover {

    private final TbGameRepository tbGameRepository;
    private final TbTeamRepository tbTeamRepository;
    private final TeamUserRepository teamUserRepository;
    private final UserinfoRepository userinfoRepository;

    @Transactional
    public void deleteTeam(Integer teamId, Integer gameId) {
        TbTeamModel team = tbTeamRepository.findByTeamId(teamId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        TbTeamModel updated = TbTeamModel.builder()
                .teamId(team.getTeamId())
                .name(team.getName())
                .sequence(team.getSequence())
                .status(-1)
                .aiPlay(team.getAiPlay())
                .code(team.getCode())
                .isDoing(team.getIsDoing())
                .teamGubun(team.getTeamGubun())
                .numUser(team.getNumUser())
                .build();

        tbTeamRepository.save(updated);
        tbGameRepository.decrementNumTeam(gameId);
    }

    @Transactional
    public void deleteTeamMembers(List<Long> userIds) {
        for (Long userId : userIds) {
            userinfoRepository.findByUserId(userId).ifPresent(user -> {
                if ("1".equals(user.getWriter())) {
                    // writer가 삭제되면 같은 팀의 남는 멤버에게 writer 이전
                    teamUserRepository.findByUserId(userId).ifPresent(teamUser -> {
                        List<Long> remainingMemberIds = teamUserRepository.findByTeamId(teamUser.getTeamId())
                                .stream()
                                .map(TeamUserModel::getUserId)
                                .filter(id -> !userIds.contains(id))
                                .toList();

                        if (!remainingMemberIds.isEmpty()) {
                            userinfoRepository.setWriter(remainingMemberIds.get(0));
                        }
                    });
                    // 삭제되는 유저의 writer 해제
                    userinfoRepository.clearWriterByUserIds(List.of(userId));
                }
            });
            teamUserRepository.deleteByUserId(userId);
        }
    }
}
