package qtedu.Impact_design.domain.model.ai;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiRequest {

    private final AiModel model;
    private final String systemPrompt;
    private final String userPrompt;
    private final Double temperature;
    private final Integer maxTokens;

    public static AiRequest of(AiModel model, String systemPrompt, String userPrompt) {
        return AiRequest.builder()
                .model(model)
                .systemPrompt(systemPrompt)
                .userPrompt(userPrompt)
                .temperature(0.7)
                .maxTokens(2000)
                .build();
    }

    public static AiRequest withOptions(AiModel model, String systemPrompt, String userPrompt,
                                        Double temperature, Integer maxTokens) {
        return AiRequest.builder()
                .model(model)
                .systemPrompt(systemPrompt)
                .userPrompt(userPrompt)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .build();
    }
}
