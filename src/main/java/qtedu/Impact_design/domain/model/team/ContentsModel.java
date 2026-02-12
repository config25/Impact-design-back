package qtedu.Impact_design.domain.model.team;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContentsModel {
    private final Integer idx;
    private final String subject;
    private final String extDir;
    private final String orgFilenm;
    private final String newFilenm;
}
