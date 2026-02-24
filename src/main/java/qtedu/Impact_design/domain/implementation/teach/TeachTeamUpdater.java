package qtedu.Impact_design.domain.implementation.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.model.team.TbTeamModel;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.repository.FLetterOfIntent2Repository;
import qtedu.Impact_design.domain.repository.FLetterOfIntentRepository;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TeachTeamUpdater {

    private final TbGameRepository tbGameRepository;
    private final TbTeamRepository tbTeamRepository;
    private final TeamUserRepository teamUserRepository;
    private final UserinfoRepository userinfoRepository;
    private final FLetterOfIntentRepository fLetterOfIntentRepository;
    private final FLetterOfIntent2Repository fLetterOfIntent2Repository;

    @Transactional
    public void saveStep(Integer gameId, String step) {
        TbGameModel game = tbGameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        TbGameModel updated = TbGameModel.builder()
                .gameId(game.getGameId())
                .name(game.getName())
                .code(game.getCode())
                .num(game.getNum())
                .numTeam(game.getNumTeam())
                .numMember(game.getNumMember())
                .createdAt(game.getCreatedAt())
                .endedAt(game.getEndedAt())
                .status(game.getStatus())
                .eStatus(game.getEStatus())
                .summary(game.getSummary())
                .totalDd(game.getTotalDd())
                .lang(game.getLang())
                .worldType(game.getWorldType())
                .step(step)
                .classType(game.getClassType())
                .isDoing(game.getIsDoing())
                .regDate(game.getRegDate())
                .popupId(game.getPopupId())
                .imageUrl(game.getImageUrl())
                .target(game.getTarget())
                .projectDate(game.getProjectDate())
                .build();

        tbGameRepository.save(updated);
    }

    @Transactional
    public void restoreTeam(Integer teamId, Integer gameId) {
        TbTeamModel team = tbTeamRepository.findByTeamId(teamId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        // 이미 활성 상태인 팀은 복원하지 않음 (numTeam 중복 증가 방지)
        if (team.getStatus() != null && team.getStatus() != -1) {
            return;
        }

        TbTeamModel updated = TbTeamModel.builder()
                .teamId(team.getTeamId())
                .name(team.getName())
                .sequence(team.getSequence())
                .status(1)
                .aiPlay(team.getAiPlay())
                .code(team.getCode())
                .isDoing(team.getIsDoing())
                .teamCategory(team.getTeamCategory())
                .numUser(team.getNumUser())
                .build();

        tbTeamRepository.save(updated);
        tbGameRepository.incrementNumTeam(gameId);
    }

    @Transactional
    public void updateTeamMember(Long userId, Integer teamId) {
        userinfoRepository.findByUserId(userId)
                .filter(user -> user.isWriter())
                .ifPresent(user -> { throw new ConflictException(ErrorCode.WRITER_CANNOT_MOVE); });

        // 이전 팀 멤버 수 감소, 새 팀 멤버 수 증가
        teamUserRepository.findByUserId(userId).ifPresent(teamUser ->
                tbTeamRepository.decrementNumUser(teamUser.getTeamId())
        );
        tbTeamRepository.incrementNumUser(teamId);

        teamUserRepository.updateTeamId(userId, teamId);
        String teamIdStr = String.valueOf(teamId);
        fLetterOfIntentRepository.updateInvestmentTargetByCanvasOwner(userId, teamIdStr);
        fLetterOfIntent2Repository.updateInvestmentTargetByCanvasOwner(userId, teamIdStr);
    }

    @Transactional
    public void setTeamWriter(Integer teamId, Long userId) {
        List<Long> teamUserIds = teamUserRepository.findByTeamId(teamId).stream()
                .map(TeamUserModel::getUserId)
                .collect(Collectors.toList());

        // 해당 유저가 팀 소속인지 검증
        if (!teamUserIds.contains(userId)) {
            throw new NotFoundException(ErrorCode.TEAM_NOT_FOUND);
        }

        userinfoRepository.clearWriterByUserIds(teamUserIds);
        userinfoRepository.setWriter(userId);
    }

    @Transactional
    public void updateTeamInfo(Integer teamId, String teamName, Integer sequence, Integer isDoing, Integer aiPlay) {
        TbTeamModel team = tbTeamRepository.findByTeamId(teamId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        TbTeamModel updated = TbTeamModel.builder()
                .teamId(team.getTeamId())
                .name(teamName != null ? teamName : team.getName())
                .sequence(sequence != null ? sequence : team.getSequence())
                .status(team.getStatus())
                .aiPlay(aiPlay != null ? aiPlay : team.getAiPlay())
                .code(team.getCode())
                .isDoing(isDoing != null ? isDoing : team.getIsDoing())
                .teamCategory(team.getTeamCategory())
                .numUser(team.getNumUser())
                .build();

        tbTeamRepository.save(updated);
    }
}
