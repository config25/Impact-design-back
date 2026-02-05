package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.FrequencyAnalysis;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.FrequencyItem;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.GoalAnalysis;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.VisionMissionValue;
import qtedu.Impact_design.domain.implementation.ai.AiImplementation;
import qtedu.Impact_design.domain.model.ai.AiModel;
import qtedu.Impact_design.domain.model.ai.AiResponse;

import java.util.List;

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

    public FrequencyAnalysis analyzeFrequency(List<String> data, String category) {
        return aiImplementation.analyzeFrequency(data, category);
    }

    public VisionMissionValue analyzeVisionMissionValue(List<String> visions, List<String> missions, List<String> values) {
        return aiImplementation.analyzeVisionMissionValue(visions, missions, values);
    }

    public GoalAnalysis analyzeGoals(List<String> goalData) {
        return aiImplementation.analyzeGoals(goalData);
    }

    public List<FrequencyItem> analyzeSimpleFrequency(List<String> data, String category) {
        return aiImplementation.analyzeSimpleFrequency(data, category);
    }
}
