package qtedu.Impact_design.domain.implementation.funding;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.request.funding.FundingInvestmentRequest;
import qtedu.Impact_design.api.dto.response.funding.FundingInvestmentResponse;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.FLetterOfIntent2Model;
import qtedu.Impact_design.domain.model.FLetterOfIntentModel;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.model.win_canvas.WinCanvasModel;
import qtedu.Impact_design.domain.repository.FLetterOfIntent2Repository;
import qtedu.Impact_design.domain.repository.FLetterOfIntentRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.win_canvas.WinCanvasRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FundingAppender {

    private final FLetterOfIntentRepository fLetterOfIntentRepository;
    private final FLetterOfIntent2Repository fLetterOfIntent2Repository;
    private final TeamUserRepository teamUserRepository;
    private final WinCanvasRepository winCanvasRepository;
    private final FundingReader fundingReader;
    private final FundingValidator fundingValidator;

    @Transactional
    public FundingInvestmentResponse saveInvestment(String canvasType, Long userId, FundingInvestmentRequest request) {
        if (isBuildType(canvasType)) {
            return saveInvestmentBuild(userId, request);
        } else {
            return saveInvestmentQuick(userId, request);
        }
    }

    private FundingInvestmentResponse saveInvestmentBuild(Long userId, FundingInvestmentRequest request) {
        if (fLetterOfIntentRepository.existsSubmittedByUserId(userId)) {
            throw new ConflictException(ErrorCode.ALREADY_SUBMITTED);
        }

        TeamUserModel teamUser = teamUserRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        Integer myTeamId = teamUser.getTeamId();

        Optional<FLetterOfIntentModel> existing = fLetterOfIntentRepository
                .findByUserIdAndTargetTeamId(userId, request.getInvestmentTarget());

        long currentTotal = fLetterOfIntentRepository.findByUserId(userId).stream()
                .mapToLong(inv -> fundingValidator.parsePrice(inv.getInvestmentPrice()))
                .sum();
        long existingPrice = existing.map(inv -> fundingValidator.parsePrice(inv.getInvestmentPrice())).orElse(0L);
        fundingValidator.validateInvestmentLimit(currentTotal - existingPrice, request.getInvestmentPrice());

        Long canvasId = existing.map(FLetterOfIntentModel::getCanvasId).orElse(null);
        if (canvasId == null) {
            canvasId = resolveCanvasId(request.getInvestmentTarget(), CanvasType.BUILD);
        }

        FLetterOfIntentModel model = FLetterOfIntentModel.builder()
                .intentIndex(existing.map(FLetterOfIntentModel::getIntentIndex).orElse(null))
                .stdntNo(userId)
                .investmentTarget(String.valueOf(request.getInvestmentTarget()))
                .investmentPrice(request.getInvestmentPrice())
                .score1(request.getScore1())
                .score2(request.getScore2())
                .score3(request.getScore3())
                .score4(request.getScore4())
                .score5(request.getScore5())
                .score6(request.getScore6())
                .score7(request.getScore7())
                .score8(request.getScore8())
                .score9(request.getScore9())
                .opinion(request.getOpinion())
                .submitted(Boolean.TRUE.equals(request.getSubmit()))
                .teamId(myTeamId)
                .courseCd(existing.map(FLetterOfIntentModel::getCourseCd).orElse("C0001"))
                .categoryCd(existing.map(FLetterOfIntentModel::getCategoryCd).orElse("CT001"))
                .gameId(existing.map(FLetterOfIntentModel::getGameId).orElse(null))
                .canvasId(canvasId)
                .build();

        fLetterOfIntentRepository.save(model);

        return fundingReader.getInvestment("build", userId, request.getInvestmentTarget());
    }

    private FundingInvestmentResponse saveInvestmentQuick(Long userId, FundingInvestmentRequest request) {
        if (fLetterOfIntent2Repository.existsSubmittedByUserId(userId)) {
            throw new ConflictException(ErrorCode.ALREADY_SUBMITTED);
        }

        TeamUserModel teamUser = teamUserRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        Integer myTeamId = teamUser.getTeamId();

        Optional<FLetterOfIntent2Model> existing = fLetterOfIntent2Repository
                .findByUserIdAndTargetTeamId(userId, request.getInvestmentTarget());

        long currentTotal = fLetterOfIntent2Repository.findByUserId(userId).stream()
                .mapToLong(inv -> fundingValidator.parsePrice(inv.getInvestmentPrice()))
                .sum();
        long existingPrice = existing.map(inv -> fundingValidator.parsePrice(inv.getInvestmentPrice())).orElse(0L);
        fundingValidator.validateInvestmentLimit(currentTotal - existingPrice, request.getInvestmentPrice());

        Long canvasId = existing.map(FLetterOfIntent2Model::getCanvasId).orElse(null);
        if (canvasId == null) {
            canvasId = resolveCanvasId(request.getInvestmentTarget(), CanvasType.QUICK);
        }

        FLetterOfIntent2Model model = FLetterOfIntent2Model.builder()
                .intentIndex(existing.map(FLetterOfIntent2Model::getIntentIndex).orElse(null))
                .stdntNo(userId)
                .investmentTarget(String.valueOf(request.getInvestmentTarget()))
                .investmentPrice(request.getInvestmentPrice())
                .score1(request.getScore1())
                .score2(request.getScore2())
                .score3(request.getScore3())
                .score4(request.getScore4())
                .score5(request.getScore5())
                .score6(request.getScore6())
                .score7(request.getScore7())
                .score8(request.getScore8())
                .score9(request.getScore9())
                .opinion(request.getOpinion())
                .submitted(Boolean.TRUE.equals(request.getSubmit()))
                .teamId(myTeamId)
                .courseCd(existing.map(FLetterOfIntent2Model::getCourseCd).orElse("C0001"))
                .categoryCd(existing.map(FLetterOfIntent2Model::getCategoryCd).orElse("CT001"))
                .gameId(existing.map(FLetterOfIntent2Model::getGameId).orElse(null))
                .canvasId(canvasId)
                .build();

        fLetterOfIntent2Repository.save(model);

        return fundingReader.getInvestment("quick", userId, request.getInvestmentTarget());
    }

    /**
     * 대상 팀의 캔버스 중 평가자에게 할당할 canvasId를 찾는다.
     * 팀원들의 캔버스를 조회하여 평가가 가장 적은 캔버스를 선택 (균등 분배).
     * 캔버스가 없으면 null 반환.
     */
    private Long resolveCanvasId(Integer targetTeamId, CanvasType canvasType) {
        List<Long> targetUserIds = teamUserRepository.findByTeamId(targetTeamId).stream()
                .map(TeamUserModel::getUserId)
                .collect(Collectors.toList());

        if (targetUserIds.isEmpty()) return null;

        List<WinCanvasModel> canvases = winCanvasRepository.findByUserIdInAndCanvasType(targetUserIds, canvasType);
        if (canvases.isEmpty()) return null;

        // 캔버스가 1개면 바로 반환
        if (canvases.size() == 1) {
            return canvases.get(0).getCanvasId();
        }

        // 여러 개면 평가가 가장 적은 캔버스에 할당 (균등 분배)
        List<Long> canvasIds = canvases.stream().map(WinCanvasModel::getCanvasId).collect(Collectors.toList());

        List<?> existingIntents;
        if (canvasType == CanvasType.QUICK) {
            existingIntents = fLetterOfIntentRepository.findByCanvasIdIn(canvasIds);
        } else {
            existingIntents = fLetterOfIntent2Repository.findByCanvasIdIn(canvasIds);
        }

        // 각 캔버스별 평가 수 카운트
        java.util.Map<Long, Long> countByCanvas = new java.util.HashMap<>();
        for (Long cid : canvasIds) {
            countByCanvas.put(cid, 0L);
        }
        for (Object intent : existingIntents) {
            Long cid = (intent instanceof FLetterOfIntentModel)
                    ? ((FLetterOfIntentModel) intent).getCanvasId()
                    : ((FLetterOfIntent2Model) intent).getCanvasId();
            if (cid != null) {
                countByCanvas.merge(cid, 1L, Long::sum);
            }
        }

        // 평가가 가장 적은 캔버스 선택
        return countByCanvas.entrySet().stream()
                .min(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse(canvases.get(0).getCanvasId());
    }

    private boolean isBuildType(String canvasType) {
        return "build".equalsIgnoreCase(canvasType);
    }
}
