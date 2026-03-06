package qtedu.Impact_design.domain.implementation.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import qtedu.Impact_design.api.dto.request.teach.ClassSaveRequest;
import qtedu.Impact_design.api.dto.request.teach.ClassUpdateRequest;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.external.ExternalFileClient;
import qtedu.Impact_design.domain.model.media.FileData;
import qtedu.Impact_design.domain.model.media.Media;
import qtedu.Impact_design.domain.model.media.MediaType;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.model.team.TbMissionModel;
import qtedu.Impact_design.domain.repository.teach.GameTeamRepository;
import qtedu.Impact_design.domain.repository.teach.MissionDataRepository;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;
import qtedu.Impact_design.domain.repository.teach.TbMissionRepository;

import qtedu.Impact_design.common.error.BadRequestException;
import qtedu.Impact_design.common.error.ErrorCode;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TeachUpdater {

    private final TbGameRepository tbGameRepository;
    private final TbMissionRepository tbMissionRepository;
    private final GameTeamRepository gameTeamRepository;
    private final MissionDataRepository missionDataRepository;
    private final ExternalFileClient fileClient;

    @Value("${file.base-url:}")
    private String fileBaseUrl;

    @Transactional
    public Integer updateClass(Integer gameId, ClassSaveRequest request, MultipartFile image) {
        if (request.getProjectDate() != null) {
            validateDate(request.getProjectDate());
        }

        TbGameModel game = findGame(gameId);

        TbGameModel updatedGame = TbGameModel.builder()
                .gameId(game.getGameId())
                .name(request.getName() != null ? request.getName() : game.getName())
                .code(game.getCode())
                .num(game.getNum())
                .numTeam(request.getNumTeam() != null ? request.getNumTeam() : game.getNumTeam())
                .numMember(request.getNumMember() != null ? String.valueOf(request.getNumMember()) : game.getNumMember())
                .createdAt(game.getCreatedAt())
                .endedAt(game.getEndedAt())
                .status(game.getStatus())
                .eStatus(game.getEStatus())
                .summary(request.getSummary() != null ? request.getSummary() : game.getSummary())
                .totalDd(game.getTotalDd())
                .lang(game.getLang())
                .worldType(request.getWorldType() != null ? request.getWorldType() : game.getWorldType())
                .step(game.getStep())
                .classType(request.getClassType() != null ? request.getClassType() : game.getClassType())
                .isDoing(game.getIsDoing())
                .regDate(game.getRegDate())
                .popupId(request.getPopupId() != null ? request.getPopupId() : game.getPopupId())
                .imageUrl(game.getImageUrl())
                .target(request.getTarget() != null ? request.getTarget() : game.getTarget())
                .projectDate(request.getProjectDate() != null ? request.getProjectDate() : game.getProjectDate())
                .build();

        tbGameRepository.save(updatedGame);

        if (image != null && !image.isEmpty()) {
            // 기존 이미지 삭제
            if (game.getImageUrl() != null) {
                Media oldMedia = Media.forClass(fileBaseUrl, gameId, game.getImageUrl(), MediaType.IMAGE_PNG);
                try { fileClient.removeFile(oldMedia); } catch (Exception ignored) {}
            }
            // 새 이미지 저장
            String imageUrl = saveClassImage(gameId, image);
            tbGameRepository.updateImageUrl(gameId, imageUrl);
        }

        if (request.getEnddate() != null && !request.getEnddate().isBlank()) {
            tbMissionRepository.findLatestByGameId(gameId)
                    .ifPresent(mission -> {
                        LocalDateTime newEnddate = LocalDateTime.parse(request.getEnddate(),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        TbMissionModel updated = TbMissionModel.builder()
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
                        tbMissionRepository.save(updated);
                    });
        }

        return gameId;
    }

    @Transactional
    public Integer updateClass(Integer gameId, ClassUpdateRequest request) {
        TbGameModel game = findGame(gameId);

        TbGameModel updatedGame = TbGameModel.builder()
                .gameId(game.getGameId())
                .name(request.getName() != null ? request.getName() : game.getName())
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
                .step(game.getStep())
                .classType(game.getClassType())
                .isDoing(game.getIsDoing())
                .regDate(game.getRegDate())
                .popupId(game.getPopupId())
                .imageUrl(game.getImageUrl())
                .target(game.getTarget())
                .projectDate(game.getProjectDate())
                .build();

        tbGameRepository.save(updatedGame);

        if (request.getEnddate() != null && !request.getEnddate().isBlank()) {
            tbMissionRepository.findLatestByGameId(gameId)
                    .ifPresent(mission -> {
                        LocalDateTime newEnddate = LocalDateTime.parse(request.getEnddate(),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        TbMissionModel updated = TbMissionModel.builder()
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
                        tbMissionRepository.save(updated);
                    });
        }

        return gameId;
    }

    @Transactional
    public void startClass(Integer gameId, String enddate) {
        validateDate(enddate);

        TbGameModel game = findGame(gameId);

        // 첫 미션 생성 (year=1, term=1, sequence=1)
        LocalDateTime enddateTime = LocalDateTime.parse(enddate,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        TbMissionModel newMission = TbMissionModel.builder()
                .sequence(1)
                .startdate(LocalDateTime.now())
                .enddate(enddateTime)
                .ddYear(1)
                .ddTerm(1)
                .gameId(gameId)
                .build();

        TbMissionModel savedMission = tbMissionRepository.save(newMission);

        // 모든 팀 연결
        List<Integer> teamIds = gameTeamRepository.findTeamIdsByGameId(gameId);
        if (!teamIds.isEmpty()) {
            missionDataRepository.saveAll(savedMission.getMissionId(), teamIds);
        }

        // status: 1 → 10
        updateGameStatus(game, 10, 0, null);
    }

    @Transactional
    public void endClass(Integer gameId) {
        TbGameModel game = findGame(gameId);
        updateGameStatus(game, 100, 0, LocalDateTime.now());
    }

    @Transactional
    public void restoreClass(Integer gameId, String enddate) {
        validateDate(enddate);

        TbGameModel game = findGame(gameId);

        // 최신 미션의 제출기한 갱신
        LocalDateTime enddateTime = LocalDateTime.parse(enddate,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        Optional<TbMissionModel> latestMission = tbMissionRepository.findLatestByGameId(gameId);
        if (latestMission.isPresent()) {
            tbMissionRepository.updateEnddate(latestMission.get().getMissionId(), enddateTime);
        } else {
            // 미션이 없으면 새로 생성
            TbMissionModel newMission = TbMissionModel.builder()
                    .sequence(1)
                    .startdate(LocalDateTime.now())
                    .enddate(enddateTime)
                    .ddYear(1)
                    .ddTerm(1)
                    .gameId(gameId)
                    .build();

            TbMissionModel savedMission = tbMissionRepository.save(newMission);

            List<Integer> teamIds = gameTeamRepository.findTeamIdsByGameId(gameId);
            if (!teamIds.isEmpty()) {
                missionDataRepository.saveAll(savedMission.getMissionId(), teamIds);
            }
        }

        // 복원 시 ended_at을 null로 초기화
        TbGameModel updated = TbGameModel.builder()
                .gameId(game.getGameId())
                .name(game.getName())
                .code(game.getCode())
                .num(game.getNum())
                .numTeam(game.getNumTeam())
                .numMember(game.getNumMember())
                .createdAt(game.getCreatedAt())
                .endedAt(null)
                .status(10)
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
                .imageUrl(game.getImageUrl())
                .target(game.getTarget())
                .projectDate(game.getProjectDate())
                .build();

        tbGameRepository.save(updated);
    }

    @Transactional
    public void startNextStage(Integer gameId, String enddate) {
        TbGameModel game = findGame(gameId);

        // 1. 현재 최신 미션 조회
        Optional<TbMissionModel> latestMission = tbMissionRepository.findLatestByGameId(gameId);

        // 2. 다음 dd_year, dd_term, sequence 계산
        int nextYear;
        int nextTerm;
        int nextSequence;

        if (latestMission.isPresent()) {
            TbMissionModel current = latestMission.get();
            nextSequence = current.getSequence() + 1;
            if (current.getDdTerm() < 4) {
                nextYear = current.getDdYear();
                nextTerm = current.getDdTerm() + 1;
            } else {
                nextYear = current.getDdYear() + 1;
                nextTerm = 1;
            }
        } else {
            nextYear = 1;
            nextTerm = 1;
            nextSequence = 1;
        }

        // 3. 새 미션 INSERT
        LocalDateTime enddateTime = LocalDateTime.parse(enddate,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        TbMissionModel newMission = TbMissionModel.builder()
                .sequence(nextSequence)
                .startdate(LocalDateTime.now())
                .enddate(enddateTime)
                .ddYear(nextYear)
                .ddTerm(nextTerm)
                .gameId(gameId)
                .build();

        TbMissionModel savedMission = tbMissionRepository.save(newMission);

        // 4. 새 미션에 모든 팀 연결 (tbmissiondata INSERT)
        List<Integer> teamIds = gameTeamRepository.findTeamIdsByGameId(gameId);
        if (!teamIds.isEmpty()) {
            missionDataRepository.saveAll(savedMission.getMissionId(), teamIds);
        }

        // 5. 게임 상태 업데이트 (status=10, eStatus=0)
        updateGameStatus(game, 10, 0, null);
    }

    private String saveClassImage(Integer gameId, MultipartFile image) {
        try {
            String originalName = image.getOriginalFilename();
            MediaType mediaType = MediaType.fromType(image.getContentType());
            if (mediaType == null) mediaType = MediaType.IMAGE_PNG;

            Media media = Media.forClass(fileBaseUrl, gameId, originalName, mediaType);
            FileData fileData = FileData.of(image.getInputStream(), mediaType, originalName, image.getSize());
            fileClient.uploadFile(fileData, media);

            return media.getPath();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save class image", e);
        }
    }

    private TbGameModel findGame(Integer gameId) {
        return tbGameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));
    }

    private void updateGameStatus(TbGameModel game, Integer status, Integer eStatus, LocalDateTime endedAt) {
        TbGameModel updated = TbGameModel.builder()
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
                .imageUrl(game.getImageUrl())
                .target(game.getTarget())
                .projectDate(game.getProjectDate())
                .build();

        tbGameRepository.save(updated);
    }

    private void validateDate(String date) {
        LocalDateTime parsed;
        try {
            parsed = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException e) {
            throw new BadRequestException(ErrorCode.INVALID_DATE);
        }
        if (parsed.isBefore(LocalDateTime.now())) {
            throw new BadRequestException(ErrorCode.DATE_IN_PAST);
        }
    }

}
