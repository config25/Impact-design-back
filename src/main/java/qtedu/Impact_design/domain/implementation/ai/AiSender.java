package qtedu.Impact_design.domain.implementation.ai;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.FrequencyAnalysis;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.FrequencyItem;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.VisionMissionValue;
import qtedu.Impact_design.domain.implementation.ai.prompt.ReportPromptGenerator;
import qtedu.Impact_design.domain.model.ai.AiModel;
import qtedu.Impact_design.domain.model.ai.AiRequest;
import qtedu.Impact_design.domain.model.ai.AiResponse;
import qtedu.Impact_design.domain.repository.ai.AiClient;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiSender {

    private final AiClientProvider aiClientProvider;
    private final ObjectMapper objectMapper;

    private static final String AI_JSON_SYSTEM_PROMPT = "반드시 JSON 형식으로만 응답하세요. 다른 텍스트 없이 순수 JSON만 출력하세요.";
    private static final int MIN_DATA_FOR_AI = 3;

    // ── 범용 AI 호출 ──
    public AiResponse chatWithOptions(AiModel model, String systemPrompt, String userPrompt,
                                      Double temperature, Integer maxTokens) {
        AiClient client = aiClientProvider.getClient(model);
        AiRequest request = AiRequest.withOptions(model, systemPrompt, userPrompt, temperature, maxTokens);
        return client.chat(request);
    }

    // ── 리포트 통합 AI 분석 (1회 호출) ──

    public FullReportAiResult analyzeFullReport(
            List<String> externalThreats,
            List<String> internalLimitations,
            List<String> visions,
            List<String> missions,
            List<String> values,
            List<String> goalTitles,
            List<String> tacticalPairs,
            List<String> strategicActivityPairs
    ) {
        int totalData = externalThreats.size() + internalLimitations.size() +
                visions.size() + missions.size() + values.size() +
                goalTitles.size() + tacticalPairs.size() + strategicActivityPairs.size();

        if (totalData <= MIN_DATA_FOR_AI) {
            return buildEmptyResult(externalThreats, internalLimitations,
                    tacticalPairs.size(), strategicActivityPairs.size());
        }

        try {
            String prompt = ReportPromptGenerator.fullReport(
                    externalThreats, internalLimitations,
                    visions, missions, values,
                    goalTitles, tacticalPairs, strategicActivityPairs
            );

            // max_tokens 4096으로 충분한 응답 공간 확보
            AiResponse response = chatWithOptions(AiModel.GPT_4_1_MINI, AI_JSON_SYSTEM_PROMPT, prompt, 0.3, 4096);
            String rawContent = response.getContent();

            JsonNode root = objectMapper.readTree(extractJson(rawContent));

            return FullReportAiResult.builder()
                    .externalThreats(parseFrequencyAnalysis(root, "externalThreats", externalThreats))
                    .internalLimitations(parseFrequencyAnalysis(root, "internalLimitations", internalLimitations))
                    .visionMissionValue(parseVisionMissionValue(root))
                    .goalKeywords(parseStringList(root, "goalKeywords"))
                    .tacticalOrder(parseIntList(root, "tacticalOrder", tacticalPairs.size()))
                    .strategicActivityOrder(parseIntList(root, "strategicActivityOrder", strategicActivityPairs.size()))
                    .build();

        } catch (Exception e) {
            log.warn("AI 통합 분석 실패: {}", e.getMessage());
            return buildEmptyResult(externalThreats, internalLimitations,
                    tacticalPairs.size(), strategicActivityPairs.size());
        }
    }

    private FullReportAiResult buildEmptyResult(List<String> externalThreats, List<String> internalLimitations,
                                                 int tacticalSize, int strategicActivitySize) {
        return FullReportAiResult.builder()
                .externalThreats(FrequencyAnalysis.builder()
                        .allData(externalThreats)
                        .totalCount(externalThreats.size())
                        .top4(Collections.emptyList())
                        .keywords(Collections.emptyList())
                        .aiSummary(Collections.emptyList())
                        .build())
                .internalLimitations(FrequencyAnalysis.builder()
                        .allData(internalLimitations)
                        .totalCount(internalLimitations.size())
                        .top4(Collections.emptyList())
                        .keywords(Collections.emptyList())
                        .aiSummary(Collections.emptyList())
                        .build())
                .visionMissionValue(VisionMissionValue.builder()
                        .visionTop4(Collections.emptyList())
                        .missionTop4(Collections.emptyList())
                        .valueTop4(Collections.emptyList())
                        .aiVision("").aiMission("").aiValue("")
                        .build())
                .goalKeywords(Collections.emptyList())
                .tacticalOrder(defaultOrder(tacticalSize))
                .strategicActivityOrder(defaultOrder(strategicActivitySize))
                .build();
    }

    private FrequencyAnalysis parseFrequencyAnalysis(JsonNode root, String fieldName, List<String> allData) {
        JsonNode node = root.get(fieldName);
        if (node == null) {
            return FrequencyAnalysis.builder()
                    .allData(allData)
                    .totalCount(allData.size())
                    .top4(Collections.emptyList())
                    .keywords(Collections.emptyList())
                    .aiSummary(Collections.emptyList())
                    .build();
        }

        return FrequencyAnalysis.builder()
                .allData(allData)
                .totalCount(allData.size())
                .top4(parseFrequencyItems(node, "top4"))
                .keywords(parseStringList(node, "keywords"))
                .aiSummary(parseStringList(node, "aiSummary"))
                .build();
    }

    private VisionMissionValue parseVisionMissionValue(JsonNode root) {
        JsonNode node = root.get("visionMissionValue");
        if (node == null) {
            return VisionMissionValue.builder()
                    .visionTop4(Collections.emptyList())
                    .missionTop4(Collections.emptyList())
                    .valueTop4(Collections.emptyList())
                    .aiVision("").aiMission("").aiValue("")
                    .build();
        }

        return VisionMissionValue.builder()
                .visionTop4(parseFrequencyItems(node, "visionTop4"))
                .missionTop4(parseFrequencyItems(node, "missionTop4"))
                .valueTop4(parseFrequencyItems(node, "valueTop4"))
                .aiVision(node.has("aiVision") ? node.get("aiVision").asText() : "")
                .aiMission(node.has("aiMission") ? node.get("aiMission").asText() : "")
                .aiValue(node.has("aiValue") ? node.get("aiValue").asText() : "")
                .build();
    }

    // ── 공통 유틸 ──

    private String extractJson(String content) {
        int start = content.indexOf("{");
        int end = content.lastIndexOf("}");
        if (start >= 0 && end > start) {
            return content.substring(start, end + 1);
        }
        return content;
    }

    private List<FrequencyItem> parseFrequencyItems(JsonNode root, String fieldName) {
        if (!root.has(fieldName) || !root.get(fieldName).isArray()) return Collections.emptyList();
        return StreamSupport.stream(root.get(fieldName).spliterator(), false)
                .map(item -> FrequencyItem.builder()
                        .content(item.has("content") ? item.get("content").asText() : "")
                        .count(item.has("count") ? item.get("count").asInt() : 0)
                        .build())
                .collect(Collectors.toList());
    }

    private List<String> parseStringList(JsonNode root, String fieldName) {
        if (!root.has(fieldName) || !root.get(fieldName).isArray()) return Collections.emptyList();
        return StreamSupport.stream(root.get(fieldName).spliterator(), false)
                .map(JsonNode::asText)
                .collect(Collectors.toList());
    }

    private List<Integer> parseIntList(JsonNode root, String fieldName, int expectedSize) {
        if (root.has(fieldName) && root.get(fieldName).isArray()) {
            List<Integer> list = StreamSupport.stream(root.get(fieldName).spliterator(), false)
                    .map(JsonNode::asInt)
                    .collect(Collectors.toList());
            if (list.size() == expectedSize) return list;
        }
        return defaultOrder(expectedSize);
    }

    private List<Integer> defaultOrder(int size) {
        return IntStream.range(0, size).boxed().collect(Collectors.toList());
    }

    // ── 통합 분석 결과 DTO ──

    @lombok.Getter
    @lombok.Builder
    public static class FullReportAiResult {
        private final FrequencyAnalysis externalThreats;
        private final FrequencyAnalysis internalLimitations;
        private final VisionMissionValue visionMissionValue;
        private final List<String> goalKeywords;
        private final List<Integer> tacticalOrder;
        private final List<Integer> strategicActivityOrder;
    }
}
