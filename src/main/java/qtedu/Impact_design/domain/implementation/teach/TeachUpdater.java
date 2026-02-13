package qtedu.Impact_design.domain.implementation.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import qtedu.Impact_design.api.dto.request.teach.ClassSaveRequest;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.external.ExternalFileClient;
import qtedu.Impact_design.domain.model.media.FileData;
import qtedu.Impact_design.domain.model.media.Media;
import qtedu.Impact_design.domain.model.media.MediaType;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.model.team.TbMissionModel;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;
import qtedu.Impact_design.domain.repository.teach.TbMissionRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class TeachUpdater {

    private final TbGameRepository tbGameRepository;
    private final TbMissionRepository tbMissionRepository;
    private final ExternalFileClient fileClient;

    @Value("${file.base-url:}")
    private String fileBaseUrl;

    @Transactional
    public Integer updateClass(Integer gameId, ClassSaveRequest request, MultipartFile image) {
        TbGameModel game = findGame(gameId);

        TbGameModel updatedGame = TbGameModel.builder()
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
            tbMissionRepository.findLatestByGameId(gameId.longValue())
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
    public void startClass(Integer gameId) {
        TbGameModel game = findGame(gameId);
        updateGameStatus(game, 10, null, null);
    }

    @Transactional
    public void endClass(Integer gameId) {
        TbGameModel game = findGame(gameId);
        updateGameStatus(game, 100, 0, LocalDateTime.now());
    }

    @Transactional
    public void restoreClass(Integer gameId) {
        TbGameModel game = findGame(gameId);
        updateGameStatus(game, 10, null, null);
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

}
