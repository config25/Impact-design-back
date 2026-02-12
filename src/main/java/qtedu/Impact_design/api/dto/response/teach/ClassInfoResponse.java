package qtedu.Impact_design.api.dto.response.teach;

import lombok.Builder;
import lombok.Getter;
import qtedu.Impact_design.domain.model.team.ClassInfoProjection;

import java.time.LocalDateTime;

@Getter
@Builder
public class ClassInfoResponse {
    private Integer gameId;
    private String name;
    private Integer numTeam;
    private Integer totalDd;
    private Integer missionId;
    private Integer sequence;
    private String subject;
    private String summary;
    private LocalDateTime startdate;
    private LocalDateTime enddate;
    private Integer ddYear;
    private Integer ddTerm;
    private Integer statusCeo;
    private String imageUrl;

    public static ClassInfoResponse from(ClassInfoProjection projection, Integer statusCeo, String imageUrl) {
        return ClassInfoResponse.builder()
                .gameId(projection.getGameId())
                .name(projection.getName())
                .numTeam(projection.getNumTeam())
                .totalDd(projection.getTotalDd())
                .missionId(projection.getMissionId())
                .sequence(projection.getSequence())
                .subject(projection.getSubject())
                .summary(projection.getSummary())
                .startdate(projection.getStartdate())
                .enddate(projection.getEnddate())
                .ddYear(projection.getDdYear())
                .ddTerm(projection.getDdTerm())
                .statusCeo(statusCeo)
                .imageUrl(imageUrl)
                .build();
    }
}
