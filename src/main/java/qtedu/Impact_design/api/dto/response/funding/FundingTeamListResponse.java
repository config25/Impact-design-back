package qtedu.Impact_design.api.dto.response.funding;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FundingTeamListResponse {
    private List<TeamItem> teams;
    private Integer myTeamId;

    public static FundingTeamListResponse from(List<TeamItem> teams, Integer myTeamId) {
        return FundingTeamListResponse.builder()
                .teams(teams)
                .myTeamId(myTeamId)
                .build();
    }

    @Getter
    @Builder
    public static class TeamItem {
        private Integer teamId;
        private String teamName;
        private String businessName;

        public static TeamItem from(Integer teamId, String teamName, String businessName) {
            return TeamItem.builder()
                    .teamId(teamId)
                    .teamName(teamName)
                    .businessName(businessName)
                    .build();
        }
    }
}
