package qtedu.Impact_design.domain.implementation.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.request.teach.ClassSaveRequest;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.storage.jpaentity.teach.TbGame;
import qtedu.Impact_design.storage.jpaentity.teach.TbMission;
import qtedu.Impact_design.storage.jparepository.teach.TbGameJpaRepository;
import qtedu.Impact_design.storage.jparepository.teach.TbMissionJpaRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class TeachUpdater {

    private final TbGameJpaRepository tbGameJpaRepository;
    private final TbMissionJpaRepository tbMissionJpaRepository;

    @Transactional
    public Integer updateClass(Integer gameId, ClassSaveRequest request) {
        TbGame game = findGame(gameId);

        TbGame updatedGame = TbGame.builder()
                .gameId(game.getGameId())
                .name(request.getName())
                .code(game.getCode())
                .num(game.getNum())
                .numTeam(request.getNumTeam())
                .numMember(request.getNumMember() != null ? String.valueOf(request.getNumMember()) : game.getNumMember())
                .createdAt(game.getCreatedAt())
                .endedAt(game.getEndedAt())
                .status(game.getStatus())
                .eStatus(game.getEStatus())
                .summary(request.getSummary())
                .totalDd(game.getTotalDd())
                .lang(game.getLang())
                .worldType(request.getWorldType() != null ? request.getWorldType() : game.getWorldType())
                .step(game.getStep())
                .classType(request.getClassType() != null ? request.getClassType() : game.getClassType())
                .isDoing(game.getIsDoing())
                .regDate(game.getRegDate())
                .popupId(request.getPopupId() != null ? request.getPopupId() : game.getPopupId())
                .build();

        tbGameJpaRepository.save(updatedGame);

        // mission enddate 업데이트
        if (request.getEnddate() != null && !request.getEnddate().isBlank()) {
            tbMissionJpaRepository.findFirstByGameIdOrderByMissionIdDesc(gameId.longValue())
                    .ifPresent(mission -> {
                        LocalDateTime newEnddate = LocalDateTime.parse(request.getEnddate(),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        TbMission updated = TbMission.builder()
                                .missionId(mission.getMissionId())
                                .sequence(mission.getSequence())
                                .subject(mission.getSubject())
                                .summary(mission.getSummary())
                                .startdate(mission.getStartdate())
                                .enddate(newEnddate)
                                .ddYear(mission.getDdYear())
                                .ddTerm(mission.getDdTerm())
                                .mlevel(mission.getMlevel())
                                .gameId(mission.getGameId())
                                .toinform(mission.getToinform())
                                .build();
                        tbMissionJpaRepository.save(updated);
                    });
        }

        return gameId;
    }

    @Transactional
    public void startClass(Integer gameId) {
        TbGame game = findGame(gameId);
        updateGameStatus(game, 10, null, null);
    }

    @Transactional
    public void endClass(Integer gameId) {
        TbGame game = findGame(gameId);
        updateGameStatus(game, 100, 0, LocalDateTime.now());
    }

    @Transactional
    public void restoreClass(Integer gameId) {
        TbGame game = findGame(gameId);
        updateGameStatus(game, 10, null, null);
    }

    private TbGame findGame(Integer gameId) {
        return tbGameJpaRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));
    }

    private void updateGameStatus(TbGame game, Integer status, Integer eStatus, LocalDateTime endedAt) {
        TbGame updated = TbGame.builder()
                .gameId(game.getGameId())
                .name(game.getName())
                .code(game.getCode())
                .num(game.getNum())
                .numTeam(game.getNumTeam())
                .numMember(game.getNumMember())
                .createdAt(game.getCreatedAt())
                .endedAt(endedAt != null ? endedAt : game.getEndedAt())
                .status(status)
                .eStatus(eStatus != null ? eStatus : game.getEStatus())
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
}
