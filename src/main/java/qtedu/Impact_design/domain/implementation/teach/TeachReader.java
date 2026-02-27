package qtedu.Impact_design.domain.implementation.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.response.teach.*;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.team.ContentsModel;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.model.team.TbMissionModel;
import qtedu.Impact_design.domain.model.team.TbMissionDataModel;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.teach.*;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;
import qtedu.Impact_design.domain.model.team.ClassInfoProjection;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeachReader {

    private final GameRepository gameRepository;
    private final MissionDataRepository missionDataRepository;
    private final TbGameRepository tbGameRepository;
    private final TbMissionRepository tbMissionRepository;
    private final TbTeamRepository tbTeamRepository;
    private final GameTeamRepository gameTeamRepository;
    private final TeamUserRepository teamUserRepository;
    private final ContentsRepository contentsRepository;
    private final TeamSubmitStatusChecker submitStatusChecker;
    private final UserinfoRepository userinfoRepository;

    @Value("${file.base-url:}")
    private String fileBaseUrl;

    public List<ClassInfoResponse> getTeachIndex(Long userId) {
        List<ClassInfoProjection> classList = gameRepository.findClassList(userId, 10);

        return classList.stream().map(c -> {
            Integer statusCeo = calculateMinStatusCeo(c.getMissionId());
            return ClassInfoResponse.from(c, statusCeo, resolveImageUrl(c.getImageUrl()));
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
        TbGameModel game = tbGameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        TeachDetailResponse.MissionInfo missionInfo = tbMissionRepository
                .findLatestByGameId(gameId)
                .map(this::toMissionInfo)
                .orElse(null);

        List<TeachDetailResponse.TeamInfo> teams = getTeamInfoList(gameId);

        TeachDetailResponse.GameLogoInfo gameLogo = contentsRepository
                .findGameLogo(gameId)
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
                .imageUrl(resolveImageUrl(game.getImageUrl()))
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

        TbGameModel game = tbGameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        TeachDetailResponse.MissionInfo missionInfo = tbMissionRepository
                .findLatestByGameId(gameId)
                .map(this::toMissionInfo)
                .orElse(null);

        List<TeachDetailResponse.TeamInfo> teams = getTeamInfoList(gameId);

        TeachDetailResponse.GameLogoInfo gameLogo = contentsRepository
                .findGameLogo(gameId)
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
                .imageUrl(resolveImageUrl(game.getImageUrl()))
                .classList(classList)
                .build();
    }

    public StudentListResponse getStudentList(Integer gameId) {
        TbGameModel game = tbGameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        String code = game.getCode();

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

        List<StudentListResponse.TeamInfo> teamList = tbTeamRepository.findByCode(code).stream()
                .map(team -> StudentListResponse.TeamInfo.builder()
                        .teamId(team.getTeamId())
                        .name(team.getName())
                        .sequence(team.getSequence())
                        .numUser(team.getNumUser())
                        .build())
                .collect(Collectors.toList());

        List<StudentListResponse.StudentInfo> studentList = userinfoRepository
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
        List<Integer> teamIds = gameTeamRepository.findTeamIdsByGameId(gameId);
        if (teamIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. 팀 정보 배치 조회
        Map<Integer, qtedu.Impact_design.domain.model.team.TbTeamModel> teamMap =
                tbTeamRepository.findByTeamIdIn(teamIds).stream()
                        .collect(Collectors.toMap(
                                qtedu.Impact_design.domain.model.team.TbTeamModel::getTeamId,
                                Function.identity()));

        // 2. 팀원 정보 배치 조회 → 팀별 userIds + 팀별 인원수
        List<qtedu.Impact_design.domain.model.team.TeamUserModel> allTeamUsers =
                teamUserRepository.findByTeamIdIn(teamIds);
        Map<Integer, List<Long>> teamUserIdsMap = allTeamUsers.stream()
                .collect(Collectors.groupingBy(
                        qtedu.Impact_design.domain.model.team.TeamUserModel::getTeamId,
                        Collectors.mapping(
                                qtedu.Impact_design.domain.model.team.TeamUserModel::getUserId,
                                Collectors.toList())));

        // 3. 제출 상태 배치 조회
        Map<Integer, TeamSubmitStatusChecker.TeamSubmitResult> submitMap =
                submitStatusChecker.checkAllSubmitStatuses(teamUserIdsMap);

        // 4. 결과 조합
        return teamIds.stream()
                .map(teamMap::get)
                .filter(team -> team != null && (team.getStatus() == null || team.getStatus() != -1))
                .map(team -> {
                    int numUser = teamUserIdsMap.getOrDefault(team.getTeamId(), Collections.emptyList()).size();
                    TeamSubmitStatusChecker.TeamSubmitResult submit =
                            submitMap.getOrDefault(team.getTeamId(), TeamSubmitStatusChecker.TeamSubmitResult.allNotSubmitted());

                    return TeachDetailResponse.TeamInfo.builder()
                            .teamId(team.getTeamId())
                            .teamName(team.getName())
                            .sequence(team.getSequence())
                            .status(team.getStatus())
                            .numUser(numUser)
                            .submitA(submit.getSubmitA())
                            .submitB(submit.getSubmitB())
                            .submitC(submit.getSubmitC())
                            .submitD(submit.getSubmitD())
                            .submitE(submit.getSubmitE())
                            .submitF(submit.getSubmitF())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private TeachDetailResponse.MissionInfo toMissionInfo(TbMissionModel mission) {
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

    private TeachDetailResponse.GameLogoInfo toGameLogoInfo(ContentsModel contents) {
        return TeachDetailResponse.GameLogoInfo.builder()
                .contentsId(contents.getContentsId())
                .subject(contents.getSubject())
                .extDir(contents.getExtDir())
                .orgFilename(contents.getOrgFilename())
                .newFilename(contents.getNewFilename())
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
        List<TbMissionDataModel> dataList = missionDataRepository.findByMissionId(missionId);
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
                .map(c -> ClassInfoResponse.from(c, null, resolveImageUrl(c.getImageUrl())))
                .collect(Collectors.toList());
    }

    private String resolveImageUrl(String path) {
        if (path == null || path.isBlank()) return null;
        return fileBaseUrl + "/" + path;
    }
}
