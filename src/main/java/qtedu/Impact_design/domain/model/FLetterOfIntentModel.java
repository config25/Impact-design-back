package qtedu.Impact_design.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FLetterOfIntentModel {
    private final Integer intentIndex;
    private final Integer stdntNo;
    private final String courseCd;
    private final String investmentTarget;
    private final String investmentPrice;
    private final Integer score1;
    private final Integer score2;
    private final Integer score3;
    private final Integer score4;
    private final Integer score5;
    private final Integer score6;
    private final Integer score7;
    private final Integer score8;
    private final Integer score9;
    private final Integer score10;
    private final String opinion;
    private final String delYn;
    private final String regId;
    private final LocalDateTime regDt;
    private final String mdfcnId;
    private final LocalDateTime mdfcnDt;
    private final String commitYn;
    private final Integer teamId;
    private final Integer gameId;
    private final String categoryCd;
    private final Long canvasId;
}
