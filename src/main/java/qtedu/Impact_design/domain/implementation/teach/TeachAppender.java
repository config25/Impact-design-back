package qtedu.Impact_design.domain.implementation.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import qtedu.Impact_design.api.dto.request.teach.ClassSaveRequest;
import qtedu.Impact_design.domain.external.ExternalFileClient;
import qtedu.Impact_design.domain.model.media.FileData;
import qtedu.Impact_design.domain.model.media.Media;
import qtedu.Impact_design.domain.model.media.MediaType;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.model.team.TbTeamModel;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;
import qtedu.Impact_design.domain.repository.teach.GameAdminRepository;
import qtedu.Impact_design.domain.repository.teach.GameTeamRepository;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class TeachAppender {

    private final TbGameRepository tbGameRepository;
    private final TbTeamRepository tbTeamRepository;
    private final GameAdminRepository gameAdminRepository;
    private final GameTeamRepository gameTeamRepository;
    private final ExternalFileClient fileClient;

    @Value("${file.base-url:}")
    private String fileBaseUrl;

    @Transactional
    public Integer createClass(Long userId, ClassSaveRequest request, MultipartFile image) {
        String code = generateCode();

        TbGameModel game = TbGameModel.builder()
                .name(request.getName())
                .code(code)
                .numTeam(request.getNumTeam())
                .numMember(request.getNumMember() != null ? String.valueOf(request.getNumMember()) : null)
                .worldType(request.getWorldType())
                .popupId(request.getPopupId() != null ? request.getPopupId() : 0)
                .summary(request.getSummary())
                .classType(request.getClassType())
                .target(request.getTarget())
                .projectDate(request.getProjectDate())
                .totalDd(16)
                .status(1)
                .isDoing(1)
                .lang(1)
                .regDate(LocalDateTime.now())
                .build();

        TbGameModel savedGame = tbGameRepository.save(game);
        Integer gameId = savedGame.getGameId();

        gameAdminRepository.save(gameId, userId);
        createTeams(gameId, request.getNumTeam(), code);

        if (image != null && !image.isEmpty()) {
            String imageUrl = saveClassImage(gameId, image);
            tbGameRepository.updateImageUrl(gameId, imageUrl);
        }

        return gameId;
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

    private void createTeams(Integer gameId, Integer numTeam, String code) {
        for (int i = 1; i <= numTeam; i++) {
            String teamName = "팀" + i;

            TbTeamModel team = TbTeamModel.builder()
                    .name(teamName)
                    .sequence(i)
                    .code(code)
                    .status(0)
                    .isDoing(1)
                    .build();

            TbTeamModel savedTeam = tbTeamRepository.save(team);
            gameTeamRepository.save(gameId, savedTeam.getTeamId());
        }
    }

    private String generateCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int n = random.nextInt(36);
            if (n > 25) {
                sb.append(n - 25);
            } else {
                sb.append((char) (n + 65));
            }
        }

        return sb.toString();
    }

}
