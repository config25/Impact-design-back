package qtedu.Impact_design.domain.implementation.ai;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.FrequencyAnalysis;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.FrequencyItem;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.GoalAnalysis;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.VisionMissionValue;
import qtedu.Impact_design.domain.implementation.ai.prompt.ReportPromptBuilder;
import qtedu.Impact_design.domain.model.ai.AiModel;
import qtedu.Impact_design.domain.model.ai.AiRequest;
import qtedu.Impact_design.domain.model.ai.AiResponse;
import qtedu.Impact_design.external.ai.AiClient;
import qtedu.Impact_design.external.ai.AiClientFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiImplementation {

    private final AiClientFactory aiClientFactory;
    private final ObjectMapper objectMapper;

    private static final String AI_JSON_SYSTEM_PROMPT = "반드시 JSON 형식으로만 응답하세요. 다른 텍스트 없이 순수 JSON만 출력하세요.";
    private static final int MIN_DATA_FOR_AI = 3;

    // ── 범용 AI 호출 ──

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

    // ── 리포트 AI 분석 ──

    public FrequencyAnalysis analyzeFrequency(List<String> data, String category) {
        FrequencyAnalysis fallback = FrequencyAnalysis.builder()
                .allData(data)
                .top4(Collections.emptyList())
                .keywords(Collections.emptyList())
                .aiSummary(Collections.emptyList())
                .build();

        if (data.size() <= MIN_DATA_FOR_AI) {
            return fallback;
        }

        try {
            JsonNode root = requestJson(ReportPromptBuilder.frequency(data, category));

            return FrequencyAnalysis.builder()
                    .allData(data)
                    .top4(parseFrequencyItems(root, "top4"))
                    .keywords(parseStringList(root, "keywords"))
                    .aiSummary(parseStringList(root, "summary"))
                    .build();
        } catch (Exception e) {
            log.warn("AI 빈도 분석 실패 [{}]: {}", category, e.getMessage());
            return fallback;
        }
    }

    public VisionMissionValue analyzeVisionMissionValue(List<String> visions, List<String> missions, List<String> values) {
        VisionMissionValue fallback = VisionMissionValue.builder()
                .visionTop4(Collections.emptyList())
                .missionTop4(Collections.emptyList())
                .valueTop4(Collections.emptyList())
                .aiVision("").aiMission("").aiValue("")
                .build();

        int totalCount = visions.size() + missions.size() + values.size();
        if (totalCount <= MIN_DATA_FOR_AI) {
            return fallback;
        }

        try {
            JsonNode root = requestJson(ReportPromptBuilder.visionMissionValue(visions, missions, values));

            return VisionMissionValue.builder()
                    .visionTop4(parseFrequencyItems(root, "visionTop4"))
                    .missionTop4(parseFrequencyItems(root, "missionTop4"))
                    .valueTop4(parseFrequencyItems(root, "valueTop4"))
                    .aiVision(root.has("aiVision") ? root.get("aiVision").asText() : "")
                    .aiMission(root.has("aiMission") ? root.get("aiMission").asText() : "")
                    .aiValue(root.has("aiValue") ? root.get("aiValue").asText() : "")
                    .build();
        } catch (Exception e) {
            log.warn("AI 비전/미션/가치 분석 실패: {}", e.getMessage());
            return fallback;
        }
    }

    public GoalAnalysis analyzeGoals(List<String> goalData) {
        GoalAnalysis fallback = GoalAnalysis.builder()
                .goals(Collections.emptyList())
                .keywords(Collections.emptyList())
                .build();

        if (goalData.size() <= MIN_DATA_FOR_AI) {
            return fallback;
        }

        try {
            JsonNode root = requestJson(ReportPromptBuilder.goals(goalData));

            return GoalAnalysis.builder()
                    .goals(parseFrequencyItems(root, "goals"))
                    .keywords(parseStringList(root, "keywords"))
                    .build();
        } catch (Exception e) {
            log.warn("AI 목표 분석 실패: {}", e.getMessage());
            return fallback;
        }
    }

    public List<FrequencyItem> analyzeSimpleFrequency(List<String> data, String category) {
        if (data.isEmpty()) {
            return Collections.emptyList();
        }

        List<FrequencyItem> fallback = data.stream()
                .map(d -> FrequencyItem.builder().content(d).count(1).build())
                .collect(Collectors.toList());

        if (data.size() <= MIN_DATA_FOR_AI) {
            return fallback;
        }

        try {
            JsonNode root = requestJson(ReportPromptBuilder.simpleFrequency(data, category));
            return parseFrequencyItems(root, "items");
        } catch (Exception e) {
            log.warn("AI 단순 빈도 분석 실패 [{}]: {}", category, e.getMessage());
            return fallback;
        }
    }

    // ── 공통 유틸 ──

    private JsonNode requestJson(String userPrompt) throws Exception {
        AiResponse response = chat(AiModel.GPT_4_1_MINI, AI_JSON_SYSTEM_PROMPT, userPrompt);
        return objectMapper.readTree(extractJson(response.getContent()));
    }

    private String extractJson(String content) {
        int start = content.indexOf("{");
        int end = content.lastIndexOf("}");
        if (start >= 0 && end > start) {
            return content.substring(start, end + 1);
        }
        return content;
    }

    private List<FrequencyItem> parseFrequencyItems(JsonNode root, String fieldName) {
        List<FrequencyItem> items = new ArrayList<>();
        if (root.has(fieldName) && root.get(fieldName).isArray()) {
            for (JsonNode item : root.get(fieldName)) {
                items.add(FrequencyItem.builder()
                        .content(item.has("content") ? item.get("content").asText() : "")
                        .count(item.has("count") ? item.get("count").asInt() : 0)
                        .build());
            }
        }
        return items;
    }

    private List<String> parseStringList(JsonNode root, String fieldName) {
        List<String> list = new ArrayList<>();
        if (root.has(fieldName) && root.get(fieldName).isArray()) {
            for (JsonNode node : root.get(fieldName)) {
                list.add(node.asText());
            }
        }
        return list;
    }
}
