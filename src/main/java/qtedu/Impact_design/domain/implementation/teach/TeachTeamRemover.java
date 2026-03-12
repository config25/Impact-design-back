package qtedu.Impact_design.domain.implementation.teach;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.team.TbTeamModel;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.model.user.UserinfoModel;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
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
                .teamCategory(team.getTeamCategory())
                .numUser(team.getNumUser())
                .build();

        tbTeamRepository.save(updated);
        tbGameRepository.decrementNumTeam(gameId);

        log.info("팀 삭제 - teamId: {}, gameId: {}", teamId, gameId);
    }

    @Transactional
    public void deleteTeamMembers(List<Long> userIds) {
        // 1. 배치 조회: 삭제 대상 유저 정보 + teamUser 정보
        List<UserinfoModel> users = userinfoRepository.findAllByUserIds(userIds);
        List<TeamUserModel> teamUsers = teamUserRepository.findByUserIdIn(userIds);
        Set<Long> deleteSet = Set.copyOf(userIds);

        // 2. writer 이전 처리 (삭제 대상 중 writer인 유저)
        Map<Integer, Long> teamWriterMap = teamUsers.stream()
                .filter(tu -> users.stream()
                        .anyMatch(u -> u.getUserId().equals(tu.getUserId()) && "1".equals(u.getWriter())))
                .collect(Collectors.toMap(TeamUserModel::getTeamId, TeamUserModel::getUserId, (a, b) -> a));

        for (Map.Entry<Integer, Long> entry : teamWriterMap.entrySet()) {
            Integer teamId = entry.getKey();
            List<Long> remainingMemberIds = teamUserRepository.findByTeamId(teamId).stream()
                    .map(TeamUserModel::getUserId)
                    .filter(id -> !deleteSet.contains(id))
                    .toList();

            if (!remainingMemberIds.isEmpty()) {
                userinfoRepository.setWriter(remainingMemberIds.get(0));
            }
        }

        if (!teamWriterMap.isEmpty()) {
            userinfoRepository.clearWriterByUserIds(List.copyOf(teamWriterMap.values()));
        }

        // 3. 팀별 멤버 수 감소
        teamUsers.stream()
                .collect(Collectors.groupingBy(TeamUserModel::getTeamId, Collectors.counting()))
                .forEach((teamId, count) -> tbTeamRepository.decrementNumUserBy(teamId, count.intValue()));

        // 4. 배치 삭제
        teamUserRepository.deleteByUserIdIn(userIds);

        log.info("팀 멤버 삭제 - userIds: {}", userIds);
    }
}
