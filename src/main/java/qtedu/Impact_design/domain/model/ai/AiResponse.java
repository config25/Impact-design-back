package qtedu.Impact_design.domain.model.ai;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiResponse {

    private final String content;
    private final AiModel model;
    private final Integer promptTokens;
    private final Integer completionTokens;
    private final Integer totalTokens;

    public static AiResponse of(String content, AiModel model) {
        return AiResponse.builder()
                .content(content)
                .model(model)
                .build();
    }

    public static AiResponse withUsage(String content, AiModel model,
                                       Integer promptTokens, Integer completionTokens) {
        return AiResponse.builder()
                .content(content)
                .model(model)
                .promptTokens(promptTokens)
                .completionTokens(completionTokens)
                .totalTokens(promptTokens + completionTokens)
                .build();
    }
}
