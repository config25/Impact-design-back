package qtedu.Impact_design.api.dto.response.ai;

import lombok.Builder;
import lombok.Getter;
import qtedu.Impact_design.domain.model.ai.AiModel;
import qtedu.Impact_design.domain.model.ai.AiResponse;

@Getter
@Builder
public class AiChatResponse {
    private String content;
    private String model;
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer totalTokens;

    public static AiChatResponse from(AiResponse response) {
        return AiChatResponse.builder()
                .content(response.getContent())
                .model(response.getModel().getModelId())
                .promptTokens(response.getPromptTokens())
                .completionTokens(response.getCompletionTokens())
                .totalTokens(response.getTotalTokens())
                .build();
    }
}
