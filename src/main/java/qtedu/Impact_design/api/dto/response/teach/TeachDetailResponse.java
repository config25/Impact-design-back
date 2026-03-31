package qtedu.Impact_design.api.dto.response.teach;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class TeachDetailResponse {

    // Game 정보
    private Integer gameId;
    private String name;
    private Integer numTeam;
    private Integer totalDd;
    private Integer status;
    private String summary;
    private String code;
    private String step;
    private List<String> stepArr;
    private String target;

    // Mission 정보 (최신 1건)
    private MissionInfo mission;

    // Team 목록
    private List<TeamInfo> teams;

    // GameLogo
    private GameLogoInfo gameLogo;

    // 강의실 이미지
    private String imageUrl;

    @Getter
    @Builder
    public static class MissionInfo {
        private Integer missionId;
        private Integer sequence;
        private String subject;
        private String summary;
        private LocalDateTime startdate;
        private LocalDateTime enddate;
        private Integer ddYear;
        private Integer ddTerm;
    }

    @Getter
    @Builder
    public static class TeamInfo {
        private Integer teamId;
        private String teamName;
        private Integer sequence;
        private Integer status;
        private Integer numUser;
        private String submitA;  // 성과관리 현황진단
        private String submitB;  // 정체성 설계
        private String submitC;  // 성과경로 설계
        private String submitD;  // 전술적 실행과제 (QUICK)
        private String submitE;  // 전략적 실행과제 (BUILD)
        private String submitF;  // 실행과제 검증
    }

    @Getter
    @Builder
    public static class GameLogoInfo {
        private Integer contentsId;
        private String subject;
        private String extDir;
        private String orgFilename;
        private String newFilename;
    }
}
