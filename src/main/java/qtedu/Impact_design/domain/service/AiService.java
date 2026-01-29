package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qtedu.Impact_design.domain.implementation.ai.AiImplementation;
import qtedu.Impact_design.domain.model.ai.AiModel;
import qtedu.Impact_design.domain.model.ai.AiResponse;

@Service
@RequiredArgsConstructor
public class AiService {

    private final AiImplementation aiImplementation;

    public AiResponse chat(AiModel model, String systemPrompt, String userPrompt) {
        return aiImplementation.chat(model, systemPrompt, userPrompt);
    }

    public AiResponse chatWithOptions(AiModel model, String systemPrompt, String userPrompt,
                                      Double temperature, Integer maxTokens) {
        return aiImplementation.chatWithOptions(model, systemPrompt, userPrompt, temperature, maxTokens);
    }
}
