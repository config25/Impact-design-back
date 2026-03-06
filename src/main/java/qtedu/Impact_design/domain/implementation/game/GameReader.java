package qtedu.Impact_design.domain.implementation.game;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.model.user.UserinfoModel;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.teach.GameTeamRepository;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameReader {

    private final TbGameRepository tbGameRepository;
    private final TeamUserRepository teamUserRepository;
    private final UserinfoRepository userinfoRepository;
    private final GameTeamRepository gameTeamRepository;

    @Value("${file.base-url:}")
    private String fileBaseUrl;

    public String readStepByUserId(Long userId) {
        return tbGameRepository.findStepByUserId(userId);
    }

    public TbGameModel readGameByUserId(Long userId) {
        return tbGameRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));
    }

    public String readTeamNameByUserId(Long userId) {
        return teamUserRepository.findTeamNameByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));
    }

    public UserinfoModel readUserinfoByUserId(Long userId) {
        return userinfoRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    public Optional<TbGameModel> findById(Integer gameId) {
        return tbGameRepository.findById(gameId);
    }

    public Optional<TbGameModel> findByTeamId(Integer teamId) {
        return tbGameRepository.findByTeamId(teamId);
    }

    public List<Integer> findTeamIdsByGameId(Integer gameId) {
        return gameTeamRepository.findTeamIdsByGameId(gameId);
    }

    public String resolveImageUrl(String path) {
        if (path == null || path.isBlank()) return null;
        return fileBaseUrl + "/" + path;
    }
}
