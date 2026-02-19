package qtedu.Impact_design.domain.implementation.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.model.team.TbTeamModel;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.model.user.UserinfoModel;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.teach.GameTeamRepository;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;

@Component
@RequiredArgsConstructor
public class TeachTeamAppender {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    private final TbGameRepository tbGameRepository;
    private final TbTeamRepository tbTeamRepository;
    private final GameTeamRepository gameTeamRepository;
    private final UserinfoRepository userinfoRepository;
    private final TeamUserRepository teamUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Integer addTeam(Integer gameId) {
        TbGameModel game = tbGameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        int nextSequence = tbTeamRepository.findMaxSequenceByGameId(gameId) + 1;
        String teamName = "팀" + nextSequence;

        TbTeamModel team = TbTeamModel.builder()
                .name(teamName)
                .sequence(nextSequence)
                .code(game.getCode())
                .status(0)
                .isDoing(1)
                .build();

        TbTeamModel savedTeam = tbTeamRepository.save(team);
        gameTeamRepository.save(gameId, savedTeam.getTeamId());
        tbGameRepository.incrementNumTeam(gameId);

        return savedTeam.getTeamId();
    }

    @Transactional
    public Integer addEvaluationTeam(Integer gameId) {
        TbGameModel game = tbGameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        int nextSequence = tbTeamRepository.findMaxSequenceByGameId(gameId) + 1;

        TbTeamModel team = TbTeamModel.builder()
                .name("평가팀")
                .sequence(nextSequence)
                .code(game.getCode())
                .status(0)
                .isDoing(1)
                .teamCategory(1)
                .build();

        TbTeamModel savedTeam = tbTeamRepository.save(team);
        gameTeamRepository.save(gameId, savedTeam.getTeamId());
        tbGameRepository.incrementNumTeam(gameId);

        return savedTeam.getTeamId();
    }

    @Transactional
    public String addTeamMember(Integer teamId, Integer gameId) {
        TbTeamModel team = tbTeamRepository.findByTeamId(teamId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        TbGameModel game = tbGameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        // 1. loginId 생성: [letter][gameId][number]
        String loginId = generateLoginId(team.getSequence(), gameId);

        // 2. 비밀번호 = 아이디 (BCrypt 인코딩)
        String encodedPassword = passwordEncoder.encode(loginId);

        // 3. userinfo 저장
        UserinfoModel userinfo = UserinfoModel.createStudent(loginId, encodedPassword, game.getCode());
        UserinfoModel savedUser = userinfoRepository.save(userinfo);

        // 4. teamuser 저장
        TeamUserModel teamUser = TeamUserModel.createStudent(savedUser.getUserId(), teamId);
        teamUserRepository.save(teamUser);

        // 5. 팀에 writer가 없으면 자동으로 writer 지정
        boolean hasWriter = teamUserRepository.findByTeamId(teamId).stream()
                .map(TeamUserModel::getUserId)
                .anyMatch(uid -> userinfoRepository.findByUserId(uid)
                        .map(UserinfoModel::isWriter)
                        .orElse(false));

        if (!hasWriter) {
            userinfoRepository.setWriter(savedUser.getUserId());
        }

        return loginId;
    }

    private String generateLoginId(Integer teamSequence, Integer gameId) {
        int letterIndex = Math.min((teamSequence != null ? teamSequence : 1) - 1, ALPHABET.length() - 1);
        char letter = ALPHABET.charAt(Math.max(letterIndex, 0));
        String prefix = letter + String.valueOf(gameId);

        // 기존 loginId 중 같은 prefix의 마지막 번호 조회
        int nextNum = 1;
        String lastLoginId = userinfoRepository.findLastLoginIdByPattern(prefix + "%").orElse(null);
        if (lastLoginId != null) {
            String numPart = lastLoginId.substring(prefix.length());
            try {
                nextNum = Integer.parseInt(numPart) + 1;
            } catch (NumberFormatException e) {
                nextNum = 1;
            }
        }

        return prefix + nextNum;
    }
}
