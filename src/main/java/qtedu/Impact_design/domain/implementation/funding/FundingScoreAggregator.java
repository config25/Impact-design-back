package qtedu.Impact_design.domain.implementation.funding;

import org.springframework.stereotype.Component;
import qtedu.Impact_design.api.dto.response.funding.FundingMyResultResponse;
import qtedu.Impact_design.api.dto.response.funding.FundingScoresResponse;
import qtedu.Impact_design.domain.model.FLetterOfIntent2Model;
import qtedu.Impact_design.domain.model.FLetterOfIntentModel;
import qtedu.Impact_design.domain.model.team.TbTeamModel;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 투자 점수 집계 로직
 */
@Component
public class FundingScoreAggregator {

    private final TbTeamRepository tbTeamRepository;

    public FundingScoreAggregator(TbTeamRepository tbTeamRepository) {
        this.tbTeamRepository = tbTeamRepository;
    }

    // ========== ResultSection 생성 (F3용) ==========

    public FundingMyResultResponse.ResultSection aggregateResult(List<FLetterOfIntentModel> investments) {
        if (investments.isEmpty()) {
            return emptyResultSection();
        }

        int problemSum = 0, solutionSum = 0, scaleUpSum = 0, effectSum = 0;
        for (FLetterOfIntentModel inv : investments) {
            problemSum += safe(inv.getScore1()) + safe(inv.getScore2());
            solutionSum += safe(inv.getScore3()) + safe(inv.getScore4()) + safe(inv.getScore5());
            scaleUpSum += safe(inv.getScore6()) + safe(inv.getScore7());
            effectSum += safe(inv.getScore8()) + safe(inv.getScore9());
        }

        List<String> opinions = investments.stream()
                .map(FLetterOfIntentModel::getOpinion)
                .filter(o -> o != null && !o.isBlank())
                .collect(Collectors.toList());

        return buildResultSection(problemSum, solutionSum, scaleUpSum, effectSum, investments.size(), opinions);
    }

    public FundingMyResultResponse.ResultSection aggregateResult2(List<FLetterOfIntent2Model> investments) {
        if (investments.isEmpty()) {
            return emptyResultSection();
        }

        int problemSum = 0, solutionSum = 0, scaleUpSum = 0, effectSum = 0;
        for (FLetterOfIntent2Model inv : investments) {
            problemSum += safe(inv.getScore1()) + safe(inv.getScore2());
            solutionSum += safe(inv.getScore3()) + safe(inv.getScore4()) + safe(inv.getScore5());
            scaleUpSum += safe(inv.getScore6()) + safe(inv.getScore7());
            effectSum += safe(inv.getScore8()) + safe(inv.getScore9());
        }

        List<String> opinions = investments.stream()
                .map(FLetterOfIntent2Model::getOpinion)
                .filter(o -> o != null && !o.isBlank())
                .collect(Collectors.toList());

        return buildResultSection(problemSum, solutionSum, scaleUpSum, effectSum, investments.size(), opinions);
    }

    // ========== TeamScoreItem 생성 ==========

    public FundingScoresResponse.TeamScoreItem createScoreItem(FLetterOfIntentModel inv) {
        return buildScoreItem(
                inv.getInvestmentTarget(),
                inv.getScore1(), inv.getScore2(), inv.getScore3(), inv.getScore4(), inv.getScore5(),
                inv.getScore6(), inv.getScore7(), inv.getScore8(), inv.getScore9()
        );
    }

    public FundingScoresResponse.TeamScoreItem createScoreItem(FLetterOfIntent2Model inv) {
        return buildScoreItem(
                inv.getInvestmentTarget(),
                inv.getScore1(), inv.getScore2(), inv.getScore3(), inv.getScore4(), inv.getScore5(),
                inv.getScore6(), inv.getScore7(), inv.getScore8(), inv.getScore9()
        );
    }

    // ========== Private Helpers ==========

    private FundingMyResultResponse.ResultSection emptyResultSection() {
        return FundingMyResultResponse.ResultSection.from(
                FundingMyResultResponse.ScoreResult.empty(),
                Collections.emptyList()
        );
    }

    private FundingMyResultResponse.ResultSection buildResultSection(
            int problemSum, int solutionSum, int scaleUpSum, int effectSum,
            int count, List<String> opinions) {
        FundingMyResultResponse.ScoreResult scores = FundingMyResultResponse.ScoreResult.from(
                problemSum / count,
                solutionSum / count,
                scaleUpSum / count,
                effectSum / count,
                count
        );
        return FundingMyResultResponse.ResultSection.from(scores, opinions);
    }

    private FundingScoresResponse.TeamScoreItem buildScoreItem(
            String investmentTarget,
            Integer s1, Integer s2, Integer s3, Integer s4, Integer s5,
            Integer s6, Integer s7, Integer s8, Integer s9) {
        Integer teamId = Integer.parseInt(investmentTarget);
        String teamName = tbTeamRepository.findByTeamId(teamId)
                .map(TbTeamModel::getName)
                .orElse("Unknown");

        return FundingScoresResponse.TeamScoreItem.from(
                teamId, teamName,
                safe(s1), safe(s2), safe(s3), safe(s4), safe(s5),
                safe(s6), safe(s7), safe(s8), safe(s9)
        );
    }

    private int safe(Integer value) {
        return value != null ? value : 0;
    }
}
