package qtedu.Impact_design.domain.implementation.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.domain.model.ai.AiModel;
import qtedu.Impact_design.domain.model.ai.AiRequest;
import qtedu.Impact_design.domain.model.ai.AiResponse;
import qtedu.Impact_design.external.ai.AiClient;
import qtedu.Impact_design.external.ai.AiClientFactory;

@Component
@RequiredArgsConstructor
public class AiImplementation {

    private final AiClientFactory aiClientFactory;

    public AiResponse chat(AiModel model, String systemPrompt, String userPrompt) {
        AiClient client = aiClientFactory.getClient(model);
        AiRequest request = AiRequest.of(model, systemPrompt, userPrompt);
        return client.chat(request);
    }

    public AiResponse chatWithOptions(AiModel model, String systemPrompt, String userPrompt,
                                      Double temperature, Integer maxTokens) {
        AiClient client = aiClientFactory.getClient(model);
        AiRequest request = AiRequest.withOptions(model, systemPrompt, userPrompt, temperature, maxTokens);
        return client.chat(request);
    }
}
