package qtedu.Impact_design.domain.implementation.funding;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.api.dto.response.funding.*;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.team.TbTeamModel;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.repository.FLetterOfIntent2Repository;
import qtedu.Impact_design.domain.repository.FLetterOfIntentRepository;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FundingReader {

    private final FLetterOfIntentRepository fLetterOfIntentRepository;
    private final FLetterOfIntent2Repository fLetterOfIntent2Repository;
    private final TbTeamRepository tbTeamRepository;
    private final TeamUserRepository teamUserRepository;
    private final TeamBusinessNameResolver businessNameResolver;
    private final FundingScoreAggregator scoreAggregator;

    public FundingTeamListResponse getTeamList(String canvasType, Long userId) {
        TeamUserModel teamUser = teamUserRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        Integer myTeamId = teamUser.getTeamId();
        TbTeamModel myTeam = tbTeamRepository.findByTeamId(myTeamId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        List<TbTeamModel> otherTeams = tbTeamRepository.findByCodeExcludingTeam(myTeam.getCode(), myTeamId);

        List<FundingTeamListResponse.TeamItem> teamItems = otherTeams.stream()
                .map(team -> FundingTeamListResponse.TeamItem.from(
                        team.getTeamId(),
                        team.getName(),
                        businessNameResolver.resolve(canvasType, team.getTeamId())))
                .collect(Collectors.toList());

        return FundingTeamListResponse.from(teamItems, myTeamId);
    }

    public FundingInvestmentResponse getInvestment(String canvasType, Long userId, Integer targetTeamId) {
        TbTeamModel team = tbTeamRepository.findByTeamId(targetTeamId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        String businessName = businessNameResolver.resolve(canvasType, targetTeamId);

        if (isBuildType(canvasType)) {
            return fLetterOfIntentRepository.findByUserIdAndTargetTeamId(userId, targetTeamId)
                    .map(model -> FundingInvestmentResponse.from(model, team.getName(), businessName))
                    .orElse(FundingInvestmentResponse.empty(targetTeamId, team.getName(), businessName));
        } else {
            return fLetterOfIntent2Repository.findByUserIdAndTargetTeamId(userId, targetTeamId)
                    .map(model -> FundingInvestmentResponse.from(model, team.getName(), businessName))
                    .orElse(FundingInvestmentResponse.empty(targetTeamId, team.getName(), businessName));
        }
    }

    public FundingPortfolioResponse getPortfolio(String canvasType, Long userId) {
        List<FundingPortfolioResponse.PortfolioItem> items;

        if (isBuildType(canvasType)) {
            items = fLetterOfIntentRepository.findByUserId(userId).stream()
                    .map(inv -> createPortfolioItem(inv.getInvestmentTarget(), inv.getInvestmentPrice()))
                    .filter(item -> item.getInvestmentPrice() > 0)
                    .collect(Collectors.toList());
        } else {
            items = fLetterOfIntent2Repository.findByUserId(userId).stream()
                    .map(inv -> createPortfolioItem(inv.getInvestmentTarget(), inv.getInvestmentPrice()))
                    .filter(item -> item.getInvestmentPrice() > 0)
                    .collect(Collectors.toList());
        }

        return FundingPortfolioResponse.from(items);
    }

    public FundingScoresResponse getScores(String canvasType, Long userId) {
        TeamUserModel teamUser = teamUserRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));
        String myTeamId = String.valueOf(teamUser.getTeamId());

        List<FundingScoresResponse.TeamScoreItem> scoreItems;

        if (isBuildType(canvasType)) {
            scoreItems = fLetterOfIntentRepository.findByUserId(userId).stream()
                    .filter(inv -> !myTeamId.equals(inv.getInvestmentTarget()))
                    .map(scoreAggregator::createScoreItem)
                    .collect(Collectors.toList());
        } else {
            scoreItems = fLetterOfIntent2Repository.findByUserId(userId).stream()
                    .filter(inv -> !myTeamId.equals(inv.getInvestmentTarget()))
                    .map(scoreAggregator::createScoreItem)
                    .collect(Collectors.toList());
        }

        return FundingScoresResponse.from(scoreItems);
    }

    /**
     * 내 팀의 결과 조회 (F3 - BUILD/QUICK 모두)
     */
    public FundingMyResultResponse getMyResult(Long userId) {
        TeamUserModel teamUser = teamUserRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        return getResultByTeamId(teamUser.getTeamId());
    }

    /**
     * 특정 팀의 결과 조회 (F3 - BUILD/QUICK 모두, 강사용)
     */
    public FundingMyResultResponse getResultByTeamId(Integer teamId) {
        return FundingMyResultResponse.from(
                scoreAggregator.aggregateResult(fLetterOfIntentRepository.findByTargetTeamId(teamId)),
                scoreAggregator.aggregateResult2(fLetterOfIntent2Repository.findByTargetTeamId(teamId))
        );
    }

    /**
     * 강사용 - 특정 사용자의 투자 의향서 조회 (canvasType으로 BUILD/QUICK 선택)
     */
    public List<FundingInvestmentResponse> readByUserId(String canvasType, Long userId) {
        if (isBuildType(canvasType)) {
            return fLetterOfIntentRepository.findByUserId(userId).stream()
                    .map(model -> {
                        Integer targetTeamId = Integer.parseInt(model.getInvestmentTarget());
                        String teamName = tbTeamRepository.findByTeamId(targetTeamId)
                                .map(TbTeamModel::getName)
                                .orElse("Unknown");
                        String businessName = businessNameResolver.resolve(canvasType, targetTeamId);
                        return FundingInvestmentResponse.from(model, teamName, businessName);
                    })
                    .collect(Collectors.toList());
        } else {
            return fLetterOfIntent2Repository.findByUserId(userId).stream()
                    .map(model -> {
                        Integer targetTeamId = Integer.parseInt(model.getInvestmentTarget());
                        String teamName = tbTeamRepository.findByTeamId(targetTeamId)
                                .map(TbTeamModel::getName)
                                .orElse("Unknown");
                        String businessName = businessNameResolver.resolve(canvasType, targetTeamId);
                        return FundingInvestmentResponse.from(model, teamName, businessName);
                    })
                    .collect(Collectors.toList());
        }
    }

    private boolean isBuildType(String canvasType) {
        return "build".equalsIgnoreCase(canvasType);
    }

    private FundingPortfolioResponse.PortfolioItem createPortfolioItem(String investmentTarget, String investmentPrice) {
        Integer teamId = Integer.parseInt(investmentTarget);
        String teamName = tbTeamRepository.findByTeamId(teamId)
                .map(TbTeamModel::getName)
                .orElse("Unknown");
        return FundingPortfolioResponse.PortfolioItem.from(teamId, teamName, parsePrice(investmentPrice));
    }

    private Long parsePrice(String price) {
        if (price == null || price.isBlank()) {
            return 0L;
        }
        try {
            return Long.parseLong(price.replaceAll(",", ""));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
