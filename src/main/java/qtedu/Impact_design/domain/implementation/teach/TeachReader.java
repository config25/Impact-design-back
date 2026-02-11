package qtedu.Impact_design.domain.implementation.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.response.teach.*;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.team.TbMissionDataModel;
import qtedu.Impact_design.domain.repository.teach.GameRepository;
import qtedu.Impact_design.domain.repository.teach.MissionDataRepository;
import qtedu.Impact_design.storage.jpaentity.teach.Contents;
import qtedu.Impact_design.storage.jpaentity.teach.TbGame;
import qtedu.Impact_design.storage.jpaentity.teach.TbMission;
import qtedu.Impact_design.storage.jparepository.teach.*;
import qtedu.Impact_design.storage.jparepository.user.UserinfoJpaRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeachReader {

    private final GameRepository gameRepository;
    private final MissionDataRepository missionDataRepository;
    private final TbGameJpaRepository tbGameJpaRepository;
    private final TbMissionJpaRepository tbMissionJpaRepository;
    private final TbTeamJpaRepository tbTeamJpaRepository;
    private final GameTeamJpaRepository gameTeamJpaRepository;
    private final TeamUserJpaRepository teamUserJpaRepository;
    private final ContentsJpaRepository contentsJpaRepository;
    private final TeamSubmitStatusChecker submitStatusChecker;
    private final UserinfoJpaRepository userinfoJpaRepository;

    public List<ClassInfoResponse> getTeachIndex(Long userId) {
        List<ClassInfoProjection> classList = gameRepository.findClassList(userId, 10);

        return classList.stream().map(c -> {
            Integer statusCeo = calculateMinStatusCeo(c.getMissionId());
            return ClassInfoResponse.from(c, statusCeo);
        }).collect(Collectors.toList());
    }

    public TeachListResponse getTeachList(Long userId) {
        List<ClassInfoResponse> inProgress = toResponseList(gameRepository.findClassList(userId, 10));
        List<ClassInfoResponse> setting = toResponseList(gameRepository.findClassList(userId, 1));
        List<ClassInfoResponse> completed = toResponseList(gameRepository.findClassList(userId, 100));
        List<ClassInfoResponse> etc = toResponseList(gameRepository.findClassList(userId, 0));

        return TeachListResponse.builder()
                .inProgress(inProgress)
                .setting(setting)
                .completed(completed)
                .etc(etc)
                .build();
    }

    public TeachDetailResponse getTeachDetail(Integer gameId) {
        TbGame game = tbGameJpaRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        TeachDetailResponse.MissionInfo missionInfo = tbMissionJpaRepository
                .findFirstByGameIdOrderByMissionIdDesc(gameId.longValue())
                .map(this::toMissionInfo)
                .orElse(null);

        List<TeachDetailResponse.TeamInfo> teams = getTeamInfoList(gameId);

        TeachDetailResponse.GameLogoInfo gameLogo = contentsJpaRepository
                .findFirstByStTpAndWorldId(1, gameId)
                .map(this::toGameLogoInfo)
                .orElse(null);

        List<String> stepArr = parseStepArr(game.getStep());

        return TeachDetailResponse.builder()
                .gameId(game.getGameId())
                .name(game.getName())
                .numTeam(game.getNumTeam())
                .totalDd(game.getTotalDd())
                .status(game.getStatus())
                .summary(game.getSummary())
                .code(game.getCode())
                .step(game.getStep())
                .stepArr(stepArr)
                .mission(missionInfo)
                .teams(teams)
                .gameLogo(gameLogo)
                .build();
    }

    public TeachDetail2Response getTeachDetail2(Long userId, Integer gameId) {
        if (gameId == null) {
            List<ClassInfoProjection> classList = gameRepository.findClassList(userId, 10);
            if (!classList.isEmpty()) {
                gameId = classList.get(0).getGameId();
            }
        }

        if (gameId == null) {
            throw new NotFoundException(ErrorCode.GAME_NOT_FOUND);
        }

        TbGame game = tbGameJpaRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        TeachDetailResponse.MissionInfo missionInfo = tbMissionJpaRepository
                .findFirstByGameIdOrderByMissionIdDesc(gameId.longValue())
                .map(this::toMissionInfo)
                .orElse(null);

        List<TeachDetailResponse.TeamInfo> teams = getTeamInfoList(gameId);

        TeachDetailResponse.GameLogoInfo gameLogo = contentsJpaRepository
                .findFirstByStTpAndWorldId(1, gameId)
                .map(this::toGameLogoInfo)
                .orElse(null);

        List<String> stepArr = parseStepArr(game.getStep());

        List<ClassInfoResponse> classList = toResponseList(gameRepository.findClassList(userId, 10));

        return TeachDetail2Response.builder()
                .gameId(game.getGameId())
                .name(game.getName())
                .numTeam(game.getNumTeam())
                .totalDd(game.getTotalDd())
                .status(game.getStatus())
                .summary(game.getSummary())
                .code(game.getCode())
                .step(game.getStep())
                .stepArr(stepArr)
                .mission(missionInfo)
                .teams(teams)
                .gameLogo(gameLogo)
                .classList(classList)
                .build();
    }

    public StudentListResponse getStudentList(Integer gameId) {
        TbGame game = tbGameJpaRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        String code = game.getCode();

        // 게임 정보
        StudentListResponse.GameInfo gameInfo = StudentListResponse.GameInfo.builder()
                .gameId(game.getGameId())
                .name(game.getName())
                .numTeam(game.getNumTeam())
                .totalDd(game.getTotalDd())
                .status(game.getStatus())
                .summary(game.getSummary())
                .code(game.getCode())
                .step(game.getStep())
                .build();

        // 팀 목록
        List<StudentListResponse.TeamInfo> teamList = tbTeamJpaRepository.findByCode(code).stream()
                .map(team -> StudentListResponse.TeamInfo.builder()
                        .teamId(team.getTeamId())
                        .name(team.getName())
                        .sequence(team.getSequence())
                        .numUser(team.getNumUser())
                        .build())
                .collect(Collectors.toList());

        // 학생 목록 (팀 이름 포함)
        List<StudentListResponse.StudentInfo> studentList = userinfoJpaRepository
                .findStudentsWithTeamByCode(code).stream()
                .map(s -> StudentListResponse.StudentInfo.builder()
                        .userId(s.getUserId())
                        .loginId(s.getLoginId())
                        .name(s.getUserName())
                        .teamId(s.getTeamId())
                        .teamName(s.getTeamName())
                        .build())
                .collect(Collectors.toList());

        return StudentListResponse.builder()
                .game(gameInfo)
                .teamList(teamList)
                .studentList(studentList)
                .build();
    }

    private List<TeachDetailResponse.TeamInfo> getTeamInfoList(Integer gameId) {
        List<Integer> teamIds = gameTeamJpaRepository.findTeamIdsByGameId(gameId);

        return teamIds.stream()
                .map(teamId -> tbTeamJpaRepository.findByTeamId(teamId).orElse(null))
                .filter(team -> team != null && (team.getStatus() == null || team.getStatus() != -1))
                .map(team -> {
                    int numUser = teamUserJpaRepository.countByTeamId(team.getTeamId());
                    Long writerUserId = submitStatusChecker.findWriterUserId(team.getTeamId());

                    return TeachDetailResponse.TeamInfo.builder()
                            .teamId(team.getTeamId())
                            .teamName(team.getName())
                            .sequence(team.getSequence())
                            .status(team.getStatus())
                            .numUser(numUser)
                            .submitA(submitStatusChecker.checkSubmitA(writerUserId))
                            .submitB(submitStatusChecker.checkSubmitB(writerUserId))
                            .submitC(submitStatusChecker.checkSubmitC(writerUserId))
                            .submitD(submitStatusChecker.checkSubmitD(writerUserId))
                            .submitE(submitStatusChecker.checkSubmitE(writerUserId))
                            .submitF(submitStatusChecker.checkSubmitF(writerUserId))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private TeachDetailResponse.MissionInfo toMissionInfo(TbMission mission) {
        return TeachDetailResponse.MissionInfo.builder()
                .missionId(mission.getMissionId())
                .sequence(mission.getSequence())
                .subject(mission.getSubject())
                .summary(mission.getSummary())
                .startdate(mission.getStartdate())
                .enddate(mission.getEnddate())
                .ddYear(mission.getDdYear())
                .ddTerm(mission.getDdTerm())
                .build();
    }

    private TeachDetailResponse.GameLogoInfo toGameLogoInfo(Contents contents) {
        return TeachDetailResponse.GameLogoInfo.builder()
                .idx(contents.getIdx())
                .subject(contents.getSubject())
                .extDir(contents.getExtDir())
                .orgFilenm(contents.getOrgFilenm())
                .newFilenm(contents.getNewFilenm())
                .build();
    }

    private List<String> parseStepArr(String step) {
        if (step == null || step.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.asList(step.split(","));
    }

    private Integer calculateMinStatusCeo(Integer missionId) {
        if (missionId == null) {
            return 20;
        }
        List<TbMissionDataModel> dataList = missionDataRepository.findByMissionId(missionId.longValue());
        int minStatus = 20;
        for (TbMissionDataModel data : dataList) {
            if (data.getStatusCeo() != null && data.getStatusCeo() < minStatus) {
                minStatus = data.getStatusCeo();
            }
        }
        return minStatus;
    }

    private List<ClassInfoResponse> toResponseList(List<ClassInfoProjection> projections) {
        return projections.stream()
                .map(c -> ClassInfoResponse.from(c, null))
                .collect(Collectors.toList());
    }
}
