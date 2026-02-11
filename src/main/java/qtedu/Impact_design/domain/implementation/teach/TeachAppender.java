package qtedu.Impact_design.domain.implementation.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.request.teach.ClassSaveRequest;
import qtedu.Impact_design.storage.jpaentity.teach.GameAdmin;
import qtedu.Impact_design.storage.jpaentity.teach.GameTeam;
import qtedu.Impact_design.storage.jpaentity.teach.TbGame;
import qtedu.Impact_design.storage.jpaentity.teach.TbTeam;
import qtedu.Impact_design.storage.jparepository.teach.GameAdminJpaRepository;
import qtedu.Impact_design.storage.jparepository.teach.GameTeamJpaRepository;
import qtedu.Impact_design.storage.jparepository.teach.TbGameJpaRepository;
import qtedu.Impact_design.storage.jparepository.teach.TbTeamJpaRepository;

import java.time.LocalDateTime;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class TeachAppender {

    private final TbGameJpaRepository tbGameJpaRepository;
    private final TbTeamJpaRepository tbTeamJpaRepository;
    private final GameAdminJpaRepository gameAdminJpaRepository;
    private final GameTeamJpaRepository gameTeamJpaRepository;

    @Transactional
    public Integer createClass(Long userId, ClassSaveRequest request) {
        String code = generateCode();

        TbGame game = TbGame.builder()
                .name(request.getName())
                .code(code)
                .numTeam(request.getNumTeam())
                .numMember(request.getNumMember() != null ? String.valueOf(request.getNumMember()) : null)
                .worldType(request.getWorldType())
                .popupId(request.getPopupId() != null ? request.getPopupId() : 0)
                .summary(request.getSummary())
                .classType(request.getClassType())
                .totalDd(16)
                .status(1)
                .isDoing(1)
                .lang(1)
                .regDate(LocalDateTime.now())
                .build();

        TbGame savedGame = tbGameJpaRepository.save(game);
        Integer gameId = savedGame.getGameId();

        // GameAdmin 연결
        GameAdmin gameAdmin = GameAdmin.builder()
                .gameId(gameId)
                .userId(userId)
                .build();
        gameAdminJpaRepository.save(gameAdmin);

        // 팀 생성
        createTeams(gameId, request.getNumTeam(), code);

        return gameId;
    }

    private void createTeams(Integer gameId, Integer numTeam, String code) {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";

        for (int i = 1; i <= numTeam; i++) {
            String teamName = String.valueOf(alphabet.charAt((i - 1) % 26)) + "팀";

            TbTeam team = TbTeam.builder()
                    .name(teamName)
                    .sequence(i)
                    .code(code)
                    .status(0)
                    .isDoing(1)
                    .build();

            TbTeam savedTeam = tbTeamJpaRepository.save(team);

            GameTeam gameTeam = GameTeam.builder()
                    .gameId(gameId)
                    .teamId(savedTeam.getTeamId())
                    .build();
            gameTeamJpaRepository.save(gameTeam);
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
