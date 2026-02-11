package qtedu.Impact_design.domain.implementation.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.storage.jpaentity.teach.GameTeam;
import qtedu.Impact_design.storage.jpaentity.teach.TbGame;
import qtedu.Impact_design.storage.jpaentity.teach.TbTeam;
import qtedu.Impact_design.storage.jpaentity.teach.TeamUser;
import qtedu.Impact_design.storage.jparepository.teach.GameTeamJpaRepository;
import qtedu.Impact_design.storage.jparepository.teach.TbGameJpaRepository;
import qtedu.Impact_design.storage.jparepository.teach.TbTeamJpaRepository;
import qtedu.Impact_design.storage.jparepository.teach.TeamUserJpaRepository;
import qtedu.Impact_design.storage.jparepository.user.UserinfoJpaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TeachTeamAppender {

    private final TbGameJpaRepository tbGameJpaRepository;
    private final TbTeamJpaRepository tbTeamJpaRepository;
    private final GameTeamJpaRepository gameTeamJpaRepository;
    private final TeamUserJpaRepository teamUserJpaRepository;
    private final UserinfoJpaRepository userinfoJpaRepository;

    @Transactional
    public void saveStep(Integer gameId, String step) {
        TbGame game = tbGameJpaRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        TbGame updated = TbGame.builder()
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
                .build();

        tbGameJpaRepository.save(updated);
    }

    @Transactional
    public Integer addTeam(Integer gameId) {
        TbGame game = tbGameJpaRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        int teamCount = gameTeamJpaRepository.countByGameId(gameId) + 1;
        String teamName = "팀" + teamCount;

        TbTeam team = TbTeam.builder()
                .name(teamName)
                .sequence(teamCount)
                .code(game.getCode())
                .status(0)
                .isDoing(1)
                .build();

        TbTeam savedTeam = tbTeamJpaRepository.save(team);

        GameTeam gameTeam = GameTeam.builder()
                .gameId(gameId)
                .teamId(savedTeam.getTeamId())
                .build();
        gameTeamJpaRepository.save(gameTeam);

        int currentNumTeam = game.getNumTeam() != null ? game.getNumTeam() : 0;
        updateNumTeam(game, currentNumTeam + 1);

        return savedTeam.getTeamId();
    }

    @Transactional
    public Integer addEvaluationTeam(Integer gameId) {
        TbGame game = tbGameJpaRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        int teamCount = gameTeamJpaRepository.countByGameId(gameId) + 1;

        TbTeam team = TbTeam.builder()
                .name("평가팀")
                .sequence(teamCount)
                .code(game.getCode())
                .status(0)
                .isDoing(1)
                .teamGubun(1)
                .build();

        TbTeam savedTeam = tbTeamJpaRepository.save(team);

        GameTeam gameTeam = GameTeam.builder()
                .gameId(gameId)
                .teamId(savedTeam.getTeamId())
                .build();
        gameTeamJpaRepository.save(gameTeam);

        int currentNumTeam = game.getNumTeam() != null ? game.getNumTeam() : 0;
        updateNumTeam(game, currentNumTeam + 1);

        return savedTeam.getTeamId();
    }

    @Transactional
    public void deleteTeam(Integer teamId, Integer gameId) {
        TbTeam team = tbTeamJpaRepository.findByTeamId(teamId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        TbTeam updated = TbTeam.builder()
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

        tbTeamJpaRepository.save(updated);

        TbGame game = tbGameJpaRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        int currentNumTeam = game.getNumTeam() != null ? game.getNumTeam() : 0;
        if (currentNumTeam > 0) {
            updateNumTeam(game, currentNumTeam - 1);
        }
    }

    @Transactional
    public void restoreTeam(Integer teamId, Integer gameId) {
        TbTeam team = tbTeamJpaRepository.findByTeamId(teamId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        TbTeam updated = TbTeam.builder()
                .teamId(team.getTeamId())
                .name(team.getName())
                .sequence(team.getSequence())
                .status(1)
                .aiPlay(team.getAiPlay())
                .code(team.getCode())
                .isDoing(team.getIsDoing())
                .teamGubun(team.getTeamGubun())
                .numUser(team.getNumUser())
                .build();

        tbTeamJpaRepository.save(updated);

        TbGame game = tbGameJpaRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        int currentNumTeam = game.getNumTeam() != null ? game.getNumTeam() : 0;
        updateNumTeam(game, currentNumTeam + 1);
    }

    private void updateNumTeam(TbGame game, int newNumTeam) {
        TbGame updated = TbGame.builder()
                .gameId(game.getGameId())
                .name(game.getName())
                .code(game.getCode())
                .num(game.getNum())
                .numTeam(newNumTeam)
                .numMember(game.getNumMember())
                .createdAt(game.getCreatedAt())
                .endedAt(game.getEndedAt())
                .status(game.getStatus())
                .eStatus(game.getEStatus())
                .summary(game.getSummary())
                .totalDd(game.getTotalDd())
                .lang(game.getLang())
                .worldType(game.getWorldType())
                .step(game.getStep())
                .classType(game.getClassType())
                .isDoing(game.getIsDoing())
                .regDate(game.getRegDate())
                .popupId(game.getPopupId())
                .build();

        tbGameJpaRepository.save(updated);
    }

    @Transactional
    public void updateTeamMember(Long userId, Integer teamId) {
        teamUserJpaRepository.updateTeamIdByUserId(userId, teamId);
    }

    @Transactional
    public void deleteTeamMembers(List<Long> userIds) {
        for (Long userId : userIds) {
            // writer 초기화
            userinfoJpaRepository.findById(userId).ifPresent(user -> {
                if ("1".equals(user.getWriter())) {
                    userinfoJpaRepository.setWriter(userId);
                }
            });
            // teamuser에서 삭제
            teamUserJpaRepository.deleteByUserId(userId);
        }
    }

    @Transactional
    public void setTeamWriter(Integer teamId, Long userId) {
        // 해당 팀 전원 writer 초기화
        List<Long> teamUserIds = teamUserJpaRepository.findByTeamId(teamId).stream()
                .map(TeamUser::getUserId)
                .collect(Collectors.toList());

        if (!teamUserIds.isEmpty()) {
            userinfoJpaRepository.clearWriterByUserIds(teamUserIds);
        }

        // 선택한 유저를 대표작성자로 지정
        userinfoJpaRepository.setWriter(userId);
    }

    @Transactional
    public void updateTeamInfo(Integer teamId, String teamName, Integer sequence, Integer isDoing, Integer aiPlay) {
        TbTeam team = tbTeamJpaRepository.findByTeamId(teamId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        TbTeam updated = TbTeam.builder()
                .teamId(team.getTeamId())
                .name(teamName != null ? teamName : team.getName())
                .sequence(sequence != null ? sequence : team.getSequence())
                .status(team.getStatus())
                .aiPlay(aiPlay != null ? aiPlay : team.getAiPlay())
                .code(team.getCode())
                .isDoing(isDoing != null ? isDoing : team.getIsDoing())
                .teamGubun(team.getTeamGubun())
                .numUser(team.getNumUser())
                .build();

        tbTeamJpaRepository.save(updated);
    }
}
