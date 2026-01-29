package qtedu.Impact_design.domain.model.ai;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AiModel {

    GPT_4_1("gpt-4.1", "openai"),
    GPT_4_1_MINI("gpt-4.1-mini", "openai"),
    // 추후 확장
    // GEMINI_PRO("gemini-pro", "google"),
    ;

    private final String modelId;
    private final String provider;
}
