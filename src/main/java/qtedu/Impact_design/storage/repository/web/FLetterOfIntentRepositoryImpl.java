package qtedu.Impact_design.storage.repository.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.FLetterOfIntentModel;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.repository.FLetterOfIntentRepository;
import qtedu.Impact_design.storage.jpaentity.FLetterOfIntent;
import qtedu.Impact_design.storage.jparepository.web.FLetterOfIntentJpaRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FLetterOfIntentRepositoryImpl implements FLetterOfIntentRepository {

    private final FLetterOfIntentJpaRepository fLetterOfIntentJpaRepository;

    @Override
    public List<FLetterOfIntentModel> findByCanvasIdIn(List<Long> canvasIds) {
        return fLetterOfIntentJpaRepository.findByCanvasIdIn(canvasIds).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<FLetterOfIntentModel> findByUserId(Long userId, CanvasType canvasType) {
        return fLetterOfIntentJpaRepository.findByUserIdAndDelYnAndCanvasType(userId, "N", canvasType).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<FLetterOfIntentModel> findByUserIdAndTargetTeamId(Long userId, Integer targetTeamId, CanvasType canvasType) {
        return fLetterOfIntentJpaRepository
                .findByUserIdAndInvestmentTargetAndDelYnAndCanvasType(userId, String.valueOf(targetTeamId), "N", canvasType)
                .map(this::toModel);
    }

    @Override
    public FLetterOfIntentModel save(FLetterOfIntentModel model) {
        FLetterOfIntent entity;

        if (model.getIntentIndex() != null) {
            entity = fLetterOfIntentJpaRepository.findById(model.getIntentIndex())
                    .orElseThrow(() -> new NotFoundException(ErrorCode.WRONG_ACCESS));
            entity.update(
                    model.getInvestmentPrice(),
                    model.getScore1(), model.getScore2(), model.getScore3(),
                    model.getScore4(), model.getScore5(), model.getScore6(),
                    model.getScore7(), model.getScore8(), model.getScore9(),
                    model.getOpinion(), model.getSubmitted()
            );
        } else {
            entity = FLetterOfIntent.builder()
                    .userId(model.getUserId())
                    .courseCd(model.getCourseCd() != null ? model.getCourseCd() : "C0001")
                    .categoryCd(model.getCategoryCd() != null ? model.getCategoryCd() : "CT001")
                    .investmentTarget(model.getInvestmentTarget())
                    .investmentPrice(model.getInvestmentPrice())
                    .score1(model.getScore1())
                    .score2(model.getScore2())
                    .score3(model.getScore3())
                    .score4(model.getScore4())
                    .score5(model.getScore5())
                    .score6(model.getScore6())
                    .score7(model.getScore7())
                    .score8(model.getScore8())
                    .score9(model.getScore9())
                    .opinion(model.getOpinion())
                    .delYn("N")
                    .regId(String.valueOf(model.getUserId()))
                    .regDt(LocalDateTime.now())
                    .submitted(Boolean.TRUE.equals(model.getSubmitted()))
                    .teamId(model.getTeamId())
                    .gameId(model.getGameId())
                    .canvasId(model.getCanvasId())
                    .canvasType(model.getCanvasType())
                    .build();
        }

        return toModel(fLetterOfIntentJpaRepository.save(entity));
    }

    @Override
    public boolean existsSubmittedByUserId(Long userId, CanvasType canvasType) {
        return fLetterOfIntentJpaRepository.existsByUserIdAndSubmittedAndDelYnAndCanvasType(userId, true, "N", canvasType);
    }

    @Override
    public void submitAllByUserId(Long userId, CanvasType canvasType) {
        List<FLetterOfIntent> entities = fLetterOfIntentJpaRepository.findByUserIdAndDelYnAndCanvasType(userId, "N", canvasType);
        entities.forEach(FLetterOfIntent::submit);
        fLetterOfIntentJpaRepository.saveAll(entities);
    }

    @Override
    public List<FLetterOfIntentModel> findByTargetTeamId(Integer teamId, CanvasType canvasType) {
        return fLetterOfIntentJpaRepository
                .findByInvestmentTargetAndDelYnAndCanvasType(String.valueOf(teamId), "N", canvasType)
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<FLetterOfIntentModel> findByTargetTeamIds(List<Integer> teamIds, CanvasType canvasType) {
        List<String> targets = teamIds.stream().map(String::valueOf).collect(Collectors.toList());
        return fLetterOfIntentJpaRepository
                .findByInvestmentTargetInAndDelYnAndCanvasType(targets, "N", canvasType)
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Set<Long> findSubmittedUserIds(List<Long> userIds, CanvasType canvasType) {
        return fLetterOfIntentJpaRepository
                .findByUserIdInAndSubmittedAndDelYnAndCanvasType(userIds, true, "N", canvasType)
                .stream()
                .map(FLetterOfIntent::getUserId)
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public void updateInvestmentTargetByCanvasOwner(Long userId, String teamId) {
        fLetterOfIntentJpaRepository.updateInvestmentTargetByCanvasOwner(userId, teamId);
    }

    private FLetterOfIntentModel toModel(FLetterOfIntent entity) {
        return FLetterOfIntentModel.builder()
                .intentIndex(entity.getIntentIndex())
                .userId(entity.getUserId())
                .courseCd(entity.getCourseCd())
                .investmentTarget(entity.getInvestmentTarget())
                .investmentPrice(entity.getInvestmentPrice())
                .score1(entity.getScore1())
                .score2(entity.getScore2())
                .score3(entity.getScore3())
                .score4(entity.getScore4())
                .score5(entity.getScore5())
                .score6(entity.getScore6())
                .score7(entity.getScore7())
                .score8(entity.getScore8())
                .score9(entity.getScore9())
                .score10(entity.getScore10())
                .opinion(entity.getOpinion())
                .delYn(entity.getDelYn())
                .regId(entity.getRegId())
                .regDt(entity.getRegDt())
                .modifiedBy(entity.getModifiedBy())
                .modifiedAt(entity.getModifiedAt())
                .submitted(entity.getSubmitted())
                .teamId(entity.getTeamId())
                .gameId(entity.getGameId())
                .categoryCd(entity.getCategoryCd())
                .canvasId(entity.getCanvasId())
                .canvasType(entity.getCanvasType())
                .build();
    }
}
