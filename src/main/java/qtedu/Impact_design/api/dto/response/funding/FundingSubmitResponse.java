package qtedu.Impact_design.api.dto.response.funding;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FundingSubmitResponse {
    private final Boolean submitted;
}
