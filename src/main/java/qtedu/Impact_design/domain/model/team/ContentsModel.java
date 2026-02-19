package qtedu.Impact_design.domain.model.team;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContentsModel {
    private final Integer contentsId;
    private final String subject;
    private final String extDir;
    private final String orgFilename;
    private final String newFilename;
}
