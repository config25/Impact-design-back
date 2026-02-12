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
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.repository.FLetterOfIntent2Repository;
import qtedu.Impact_design.domain.repository.FLetterOfIntentRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FundingAppender {

    private final FLetterOfIntentRepository fLetterOfIntentRepository;
    private final FLetterOfIntent2Repository fLetterOfIntent2Repository;
    private final TeamUserRepository teamUserRepository;
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

        FLetterOfIntentModel model = FLetterOfIntentModel.builder()
                .intentIndex(existing.map(FLetterOfIntentModel::getIntentIndex).orElse(null))
                .stdntNo(userId.intValue())
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

        FLetterOfIntent2Model model = FLetterOfIntent2Model.builder()
                .intentIndex(existing.map(FLetterOfIntent2Model::getIntentIndex).orElse(null))
                .stdntNo(userId.intValue())
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
                .build();

        fLetterOfIntent2Repository.save(model);

        return fundingReader.getInvestment("quick", userId, request.getInvestmentTarget());
    }

    private boolean isBuildType(String canvasType) {
        return "build".equalsIgnoreCase(canvasType);
    }
}
