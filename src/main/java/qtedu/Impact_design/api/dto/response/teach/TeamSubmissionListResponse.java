package qtedu.Impact_design.api.dto.response.teach;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamSubmissionListResponse {
    private Integer teamId;
    private String teamName;
    private Integer sequence;
    private Integer numUser;
    private String submitA;  // 성과관리 현황진단
    private String submitB;  // 정체성 설계
    private String submitC;  // 성과경로 설계
    private String submitD;  // 전술적 실행과제
    private String submitE;  // 전략적 실행과제
    private String submitF;  // 실행과제 검증
}
