package qtedu.Impact_design.domain.implementation.ai.prompt;

import java.util.List;

public class ReportPromptGenerator {

    private ReportPromptGenerator() {}

    /**
     * 리포트 전체 분석을 위한 통합 프롬프트 (AI 1회 호출)
     */
    public static String fullReport(
            List<String> externalThreats,
            List<String> internalLimitations,
            List<String> visions,
            List<String> missions,
            List<String> values,
            List<String> goalTitles,
            List<String> tacticalPairs,
            List<String> strategicActivityPairs
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("교육생들이 작성한 전략 기획 데이터를 분석해주세요.\n\n");

        sb.append("=== 1. 외부 위협 신호 ===\n");
        sb.append(externalThreats.isEmpty() ? "(데이터 없음)" : String.join("\n- ", externalThreats));
        sb.append("\n\n");

        sb.append("=== 2. 내부 한계점 ===\n");
        sb.append(internalLimitations.isEmpty() ? "(데이터 없음)" : String.join("\n- ", internalLimitations));
        sb.append("\n\n");

        sb.append("=== 3. 비전/미션/가치 ===\n");
        sb.append("비전: ").append(visions.isEmpty() ? "(없음)" : String.join(", ", visions)).append("\n");
        sb.append("미션: ").append(missions.isEmpty() ? "(없음)" : String.join(", ", missions)).append("\n");
        sb.append("가치: ").append(values.isEmpty() ? "(없음)" : String.join(", ", values)).append("\n\n");

        sb.append("=== 4. 전략 목표 ===\n");
        sb.append(goalTitles.isEmpty() ? "(데이터 없음)" : String.join("\n- ", goalTitles));
        sb.append("\n\n");

        sb.append("=== 5. 전술 지표 (인덱스 0부터 시작) ===\n");
        for (int i = 0; i < tacticalPairs.size(); i++) {
            sb.append(i).append(": ").append(tacticalPairs.get(i)).append("\n");
        }
        sb.append("\n");

        sb.append("=== 6. 전략적 행동 지표 (인덱스 0부터 시작) ===\n");
        for (int i = 0; i < strategicActivityPairs.size(); i++) {
            sb.append(i).append(": ").append(strategicActivityPairs.get(i)).append("\n");
        }
        sb.append("\n");

        sb.append("=== 분석 요청 ===\n");
        sb.append("각 섹션에 대해 다음을 수행해주세요:\n");
        sb.append("- 외부위협/내부한계: 유사 항목 그룹핑 → 빈도순 상위12개(top12), 키워드4개(keywords), 요약3문장(aiSummary)\n");
        sb.append("- 비전/미션/가치: 각각 빈도순 상위4개 + 통합 비전/미션/가치 1개씩 생성\n");
        sb.append("- 전략목표: 키워드 4개 추출(goalKeywords)\n");
        sb.append("- 전술지표/전략적행동지표: 유사한 항목끼리 인접하도록 정렬한 인덱스 배열 반환 (원본 데이터 유지, 순서만 변경)\n\n");

        sb.append("JSON 형식으로 응답 (데이터 없는 섹션은 빈 배열):\n");
        sb.append("{\n");
        sb.append("  \"externalThreats\": { \"top12\": [{\"content\":\"문장\",\"count\":숫자}], \"keywords\": [\"키워드\"], \"aiSummary\": [\"요약\"] },\n");
        sb.append("  \"internalLimitations\": { \"top12\": [{\"content\":\"문장\",\"count\":숫자}], \"keywords\": [\"키워드\"], \"aiSummary\": [\"요약\"] },\n");
        sb.append("  \"visionMissionValue\": {\n");
        sb.append("    \"visionTop4\": [{\"content\":\"비전\",\"count\":숫자}],\n");
        sb.append("    \"missionTop4\": [{\"content\":\"미션\",\"count\":숫자}],\n");
        sb.append("    \"valueTop4\": [{\"content\":\"가치\",\"count\":숫자}],\n");
        sb.append("    \"aiVision\": \"통합비전\", \"aiMission\": \"통합미션\", \"aiValue\": \"통합가치\"\n");
        sb.append("  },\n");
        sb.append("  \"goalKeywords\": [\"키워드1\",\"키워드2\",\"키워드3\",\"키워드4\"],\n");
        sb.append("  \"tacticalOrder\": [0,2,1,3,...],\n");
        sb.append("  \"strategicActivityOrder\": [0,2,1,3,...]\n");
        sb.append("}");

        return sb.toString();
    }
}
