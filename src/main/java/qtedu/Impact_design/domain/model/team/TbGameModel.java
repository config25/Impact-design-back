package qtedu.Impact_design.domain.model.team;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TbGameModel {
    private final Integer gameId;
    private final String name;
    private final String code;
    private final Integer num;
    private final Integer numTeam;
    private final String numMember;
    private final LocalDateTime createdAt;
    private final LocalDateTime endedAt;
    private final Integer status;
    private final Integer eStatus;
    private final String summary;
    private final Integer totalDd;
    private final Integer lang;
    private final Integer worldType;
    private final String step;
    private final String classType;
    private final Integer isDoing;
    private final LocalDateTime regDate;
    private final Integer popupId;
    private final String imageUrl;
    private final String target;
    private final String projectDate;
}
