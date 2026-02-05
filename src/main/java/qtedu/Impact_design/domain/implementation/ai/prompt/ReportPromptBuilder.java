package qtedu.Impact_design.domain.implementation.ai.prompt;

import java.util.List;

public class ReportPromptBuilder {

    private ReportPromptBuilder() {}

    public static String frequency(List<String> data, String category) {
        return String.format(
                "다음은 교육생들이 작성한 %s 목록입니다:\n%s\n\n" +
                "1. 의미적으로 유사한 문장끼리 그룹핑하여 빈도가 높은 순으로 상위 4개 대표 문장을 선정해주세요. (각 문장 30자 이내)\n" +
                "2. 상위 4개 문장에서 핵심 키워드 4개를 추출해주세요. (각 키워드 10자 이내)\n" +
                "3. 전체 내용을 3개의 핵심 문장으로 요약해주세요. (각 문장 30자 이내)\n\n" +
                "JSON 형식: {\"top4\":[{\"content\":\"문장\",\"count\":빈도수}], \"keywords\":[\"키워드1\",\"키워드2\",\"키워드3\",\"키워드4\"], \"summary\":[\"요약1\",\"요약2\",\"요약3\"]}",
                category, String.join("\n- ", data));
    }

    public static String visionMissionValue(List<String> visions, List<String> missions, List<String> values) {
        return String.format(
                "다음은 교육생들이 작성한 비전/미션/가치 목록입니다:\n" +
                "비전: %s\n미션: %s\n가치: %s\n\n" +
                "1. 각각 유사한 항목끼리 그룹핑하여 빈도순 상위 4개를 선정해주세요. (각 50자 이내)\n" +
                "2. 전체를 종합하여 통합 비전, 통합 미션, 통합 가치를 각각 1개씩 작성해주세요. (각 50자 이내)\n\n" +
                "JSON 형식: {\"visionTop4\":[{\"content\":\"비전\",\"count\":빈도수}], " +
                "\"missionTop4\":[{\"content\":\"미션\",\"count\":빈도수}], " +
                "\"valueTop4\":[{\"content\":\"가치\",\"count\":빈도수}], " +
                "\"aiVision\":\"통합 비전\", \"aiMission\":\"통합 미션\", \"aiValue\":\"통합 가치\"}",
                String.join(", ", visions), String.join(", ", missions), String.join(", ", values));
    }

    public static String goals(List<String> goalData) {
        return String.format(
                "다음은 교육생들이 작성한 전략 목표 목록입니다:\n%s\n\n" +
                "1. 의미적으로 유사한 문장끼리 그룹핑하여 빈도가 높은 순으로 정렬해주세요. (각 문장 50자 이내)\n" +
                "2. 전체에서 주요 키워드 4개를 추출해주세요. (각 키워드 10자 이내)\n\n" +
                "JSON 형식: {\"goals\":[{\"content\":\"목표\",\"count\":빈도수}], \"keywords\":[\"키워드1\",\"키워드2\",\"키워드3\",\"키워드4\"]}",
                String.join("\n- ", goalData));
    }

    public static String simpleFrequency(List<String> data, String category) {
        return String.format(
                "다음은 교육생들이 작성한 %s 목록입니다:\n%s\n\n" +
                "의미적으로 유사한 항목끼리 그룹핑하여 빈도가 높은 순으로 정렬해주세요. (각 항목 50자 이내)\n\n" +
                "JSON 형식: {\"items\":[{\"content\":\"항목\",\"count\":빈도수}]}",
                category, String.join("\n- ", data));
    }
}
