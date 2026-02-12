package qtedu.Impact_design.api.dto.response.teach;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class TeachDetail2Response {

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

    // Mission 정보 (최신 1건)
    private TeachDetailResponse.MissionInfo mission;

    // Team 목록
    private List<TeachDetailResponse.TeamInfo> teams;

    // GameLogo
    private TeachDetailResponse.GameLogoInfo gameLogo;

    // 강의실 이미지
    private String imageUrl;

    // 교사의 진행중인 클래스 목록 (teach_detail2 전용)
    private List<ClassInfoResponse> classList;
}
