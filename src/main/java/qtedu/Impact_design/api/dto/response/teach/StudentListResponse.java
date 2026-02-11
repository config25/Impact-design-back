package qtedu.Impact_design.api.dto.response.teach;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StudentListResponse {

    private GameInfo game;
    private List<TeamInfo> teamList;
    private List<StudentInfo> studentList;

    @Getter
    @Builder
    public static class GameInfo {
        private Integer gameId;
        private String name;
        private Integer numTeam;
        private Integer totalDd;
        private Integer status;
        private String summary;
        private String code;
        private String step;
    }

    @Getter
    @Builder
    public static class TeamInfo {
        private Integer teamId;
        private String name;
        private Integer sequence;
        private Integer numUser;
    }

    @Getter
    @Builder
    public static class StudentInfo {
        private Long userId;
        private String loginId;
        private String name;
        private String mail;
        private Integer teamId;
        private String teamName;
    }
}
