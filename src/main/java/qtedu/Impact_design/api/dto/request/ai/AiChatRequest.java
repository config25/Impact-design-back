package qtedu.Impact_design.api.dto.request.ai;

import lombok.Getter;
import lombok.NoArgsConstructor;
import qtedu.Impact_design.domain.model.ai.AiModel;

@Getter
@NoArgsConstructor
public class AiChatRequest {
    private AiModel model;
    private String systemPrompt;
    private String userPrompt;
}
