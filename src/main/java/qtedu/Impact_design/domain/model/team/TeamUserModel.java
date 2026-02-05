package qtedu.Impact_design.domain.model.team;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamUserModel {
    private final Integer idx;
    private final Long userId;
    private final Integer teamId;
    private final Integer userlevel;
    private final Integer isDoing;

    public static TeamUserModel createStudent(Long userId, Integer teamId) {
        return TeamUserModel.builder()
                .userId(userId)
                .teamId(teamId)
                .userlevel(0)
                .isDoing(1)
                .build();
    }
}
