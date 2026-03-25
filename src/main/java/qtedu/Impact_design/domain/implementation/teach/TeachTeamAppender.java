package qtedu.Impact_design.domain.implementation.teach;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.common.error.ConflictException;
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

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeachTeamAppender {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final int MAX_TEAMS_PER_GAME = 6;
    private static final int MAX_MEMBERS_PER_TEAM = 10;
    private static final int MAX_LOGIN_ID_RETRY = 5;

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

        int currentTeamCount = gameTeamRepository.countByGameId(gameId);
        if (currentTeamCount >= MAX_TEAMS_PER_GAME) {
            throw new ConflictException(ErrorCode.MAX_TEAM_EXCEEDED);
        }

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

        log.info("팀 생성 - gameId: {}, teamId: {}, teamName: {}", gameId, savedTeam.getTeamId(), teamName);
        return savedTeam.getTeamId();
    }

    @Transactional
    public Integer addEvaluationTeam(Integer gameId) {
        TbGameModel game = tbGameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        int currentTeamCount = gameTeamRepository.countByGameId(gameId);
        if (currentTeamCount >= MAX_TEAMS_PER_GAME) {
            throw new ConflictException(ErrorCode.MAX_TEAM_EXCEEDED);
        }

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
        // 비관적 락으로 동시 멤버 추가 방지
        TbTeamModel team = tbTeamRepository.findByTeamIdForUpdate(teamId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        TbGameModel game = tbGameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        int currentMemberCount = teamUserRepository.countByTeamId(teamId);
        if (currentMemberCount >= MAX_MEMBERS_PER_TEAM) {
            throw new ConflictException(ErrorCode.MAX_MEMBER_EXCEEDED);
        }

        // 1. loginId 생성 + userinfo 저장 (중복 시 retry)
        String loginId = generateLoginId(team.getSequence(), gameId);
        UserinfoModel savedUser = saveUserWithRetry(loginId, game.getCode(), team.getSequence(), gameId);

        // 4. teamuser 저장
        TeamUserModel teamUser = TeamUserModel.createStudent(savedUser.getUserId(), teamId);
        teamUserRepository.save(teamUser);

        // 5. 팀 멤버 수 증가
        tbTeamRepository.incrementNumUser(teamId);

        // 6. 팀에 writer가 없으면 자동으로 writer 지정
        List<Long> teamMemberIds = teamUserRepository.findByTeamId(teamId).stream()
                .map(TeamUserModel::getUserId)
                .toList();

        if (userinfoRepository.findWriterByUserIds(teamMemberIds).isEmpty()) {
            userinfoRepository.setWriter(savedUser.getUserId());
        }

        log.info("팀 멤버 추가 - teamId: {}, userId: {}, loginId: {}", teamId, savedUser.getUserId(), savedUser.getLoginId());
        return savedUser.getLoginId();
    }

    private UserinfoModel saveUserWithRetry(String loginId, String code, Integer teamSequence, Integer gameId) {
        for (int attempt = 0; attempt < MAX_LOGIN_ID_RETRY; attempt++) {
            try {
                String encodedPassword = passwordEncoder.encode(loginId);
                UserinfoModel userinfo = UserinfoModel.createStudent(loginId, encodedPassword, code);
                return userinfoRepository.save(userinfo);
            } catch (DataIntegrityViolationException e) {
                log.warn("loginId 중복 발생, 재시도 - loginId: {}, attempt: {}", loginId, attempt + 1);
                loginId = generateLoginId(teamSequence, gameId);
            }
        }
        throw new ConflictException(ErrorCode.LOGIN_ID_DUPLICATE);
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

        // 동시 요청으로 이미 존재하는 경우 다음 번호로 증가
        String loginId = prefix + nextNum;
        while (userinfoRepository.existsByLoginId(loginId)) {
            nextNum++;
            loginId = prefix + nextNum;
        }

        return loginId;
    }
}
