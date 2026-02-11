package qtedu.Impact_design.api.dto.response.teach;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TeamInfoResponse {
    private Integer teamId;
    private String teamName;
    private Integer sequence;
    private Integer isDoing;
    private Integer teamGubun;
    private Integer numUser;
    private List<MemberInfo> members;

    @Getter
    @Builder
    public static class MemberInfo {
        private Long userId;
        private String loginId;
        private String name;
        private String mail;
        private String writer;
    }
}
