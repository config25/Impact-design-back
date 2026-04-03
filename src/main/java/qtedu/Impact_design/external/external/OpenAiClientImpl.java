package qtedu.Impact_design.external.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import qtedu.Impact_design.domain.model.ai.AiRequest;
import qtedu.Impact_design.domain.model.ai.AiResponse;
import qtedu.Impact_design.domain.repository.ai.AiClient;
import qtedu.Impact_design.external.ai.openai.OpenAiProperties;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiClientImpl implements AiClient {

    private final OpenAiProperties properties;
    private final RestTemplate restTemplate;

    private static final String PROVIDER = "openai";

    @Override
    public AiResponse chat(AiRequest request) {
        String url = properties.getBaseUrl() + "/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(properties.getApiKey());

        Map<String, Object> body = buildRequestBody(request);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            log.info("OpenAI 요청 - model: {}, promptLength: {}", request.getModel().getModelId(), request.getUserPrompt().length());
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            AiResponse aiResponse = parseResponse(response.getBody(), request);
            log.info("OpenAI 응답 - model: {}, promptTokens: {}, completionTokens: {}", request.getModel().getModelId(), aiResponse.getPromptTokens(), aiResponse.getCompletionTokens());
            return aiResponse;
        } catch (Exception e) {
            log.error("OpenAI API 호출 실패 - model: {}, error: {}", request.getModel().getModelId(), e.getMessage());
            throw new RuntimeException("AI 서비스 호출에 실패했습니다.", e);
        }
    }

    @Override
    public String getProvider() {
        return PROVIDER;
    }

    private Map<String, Object> buildRequestBody(AiRequest request) {
        return Map.of(
                "model", request.getModel().getModelId(),
                "messages", List.of(
                        Map.of("role", "system", "content", request.getSystemPrompt()),
                        Map.of("role", "user", "content", request.getUserPrompt())
                ),
                "temperature", request.getTemperature(),
                "max_tokens", request.getMaxTokens()
        );
    }

    @SuppressWarnings("unchecked")
    private AiResponse parseResponse(Map<String, Object> responseBody, AiRequest request) {
        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        String content = (String) message.get("content");

        Map<String, Object> usage = (Map<String, Object>) responseBody.get("usage");
        Integer promptTokens = (Integer) usage.get("prompt_tokens");
        Integer completionTokens = (Integer) usage.get("completion_tokens");

        return AiResponse.withUsage(content, request.getModel(), promptTokens, completionTokens);
    }
}
