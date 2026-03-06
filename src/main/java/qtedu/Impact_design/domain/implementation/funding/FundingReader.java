package qtedu.Impact_design.domain.implementation.funding;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.api.dto.response.funding.*;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.team.TbTeamModel;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.repository.FLetterOfIntentRepository;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FundingReader {

    private final FLetterOfIntentRepository fLetterOfIntentRepository;
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

        List<Integer> otherTeamIds = otherTeams.stream()
                .map(TbTeamModel::getTeamId)
                .collect(Collectors.toList());
        Map<Integer, String> businessNameMap = businessNameResolver.resolveBulk(canvasType, otherTeamIds);

        List<FundingTeamListResponse.TeamItem> teamItems = otherTeams.stream()
                .map(team -> FundingTeamListResponse.TeamItem.from(
                        team.getTeamId(),
                        team.getName(),
                        businessNameMap.getOrDefault(team.getTeamId(), "")))
                .collect(Collectors.toList());

        return FundingTeamListResponse.from(teamItems, myTeamId);
    }

    public FundingInvestmentResponse getInvestment(String canvasType, Long userId, Integer targetTeamId) {
        TbTeamModel team = tbTeamRepository.findByTeamId(targetTeamId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        String businessName = businessNameResolver.resolve(canvasType, targetTeamId);
        CanvasType type = toCanvasType(canvasType);

        return fLetterOfIntentRepository.findByUserIdAndTargetTeamId(userId, targetTeamId, type)
                .map(model -> FundingInvestmentResponse.from(model, team.getName(), businessName))
                .orElse(FundingInvestmentResponse.empty(targetTeamId, team.getName(), businessName));
    }

    public FundingPortfolioResponse getPortfolio(String canvasType, Long userId) {
        CanvasType type = toCanvasType(canvasType);
        var investments = fLetterOfIntentRepository.findByUserId(userId, type);
        Map<Integer, String> teamNameMap = preloadTeamNames(
                investments.stream().map(i -> Integer.parseInt(i.getInvestmentTarget())).collect(Collectors.toList()));
        List<FundingPortfolioResponse.PortfolioItem> items = investments.stream()
                .map(inv -> createPortfolioItem(inv.getInvestmentTarget(), inv.getInvestmentPrice(), teamNameMap))
                .filter(item -> item.getInvestmentPrice() > 0)
                .collect(Collectors.toList());

        return FundingPortfolioResponse.from(items);
    }

    public FundingScoresResponse getScores(String canvasType, Long userId) {
        TeamUserModel teamUser = teamUserRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));
        String myTeamId = String.valueOf(teamUser.getTeamId());

        CanvasType type = toCanvasType(canvasType);
        var investments = fLetterOfIntentRepository.findByUserId(userId, type).stream()
                .filter(inv -> !myTeamId.equals(inv.getInvestmentTarget()))
                .collect(Collectors.toList());
        Map<Integer, String> teamNameMap = preloadTeamNames(
                investments.stream().map(i -> Integer.parseInt(i.getInvestmentTarget())).collect(Collectors.toList()));
        List<FundingScoresResponse.TeamScoreItem> scoreItems = investments.stream()
                .map(inv -> scoreAggregator.createScoreItem(inv, teamNameMap))
                .collect(Collectors.toList());

        return FundingScoresResponse.from(scoreItems);
    }

    public FundingMyResultResponse getMyResult(Long userId) {
        TeamUserModel teamUser = teamUserRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        return getResultByTeamId(teamUser.getTeamId());
    }

    public FundingMyResultResponse getResultByTeamId(Integer teamId) {
        return FundingMyResultResponse.from(
                scoreAggregator.aggregateResult(fLetterOfIntentRepository.findByTargetTeamId(teamId, CanvasType.BUILD)),
                scoreAggregator.aggregateResult(fLetterOfIntentRepository.findByTargetTeamId(teamId, CanvasType.QUICK))
        );
    }

    public List<FundingInvestmentResponse> readByUserId(String canvasType, Long userId) {
        CanvasType type = toCanvasType(canvasType);
        var investments = fLetterOfIntentRepository.findByUserId(userId, type);
        List<Integer> targetTeamIds = investments.stream()
                .map(m -> Integer.parseInt(m.getInvestmentTarget()))
                .collect(Collectors.toList());
        Map<Integer, String> teamNameMap = preloadTeamNames(targetTeamIds);
        Map<Integer, String> businessNameMap = businessNameResolver.resolveBulk(canvasType, targetTeamIds);
        return investments.stream()
                .map(model -> {
                    Integer targetTeamId = Integer.parseInt(model.getInvestmentTarget());
                    return FundingInvestmentResponse.from(model,
                            teamNameMap.getOrDefault(targetTeamId, "Unknown"),
                            businessNameMap.getOrDefault(targetTeamId, ""));
                })
                .collect(Collectors.toList());
    }

    private CanvasType toCanvasType(String canvasType) {
        return "build".equalsIgnoreCase(canvasType) ? CanvasType.BUILD : CanvasType.QUICK;
    }

    private Map<Integer, String> preloadTeamNames(List<Integer> teamIds) {
        if (teamIds.isEmpty()) {
            return Map.of();
        }
        return tbTeamRepository.findByTeamIdIn(teamIds).stream()
                .collect(Collectors.toMap(TbTeamModel::getTeamId, TbTeamModel::getName, (a, b) -> a));
    }

    private FundingPortfolioResponse.PortfolioItem createPortfolioItem(
            String investmentTarget, String investmentPrice, Map<Integer, String> teamNameMap) {
        Integer teamId = Integer.parseInt(investmentTarget);
        String teamName = teamNameMap.getOrDefault(teamId, "Unknown");
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
