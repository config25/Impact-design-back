package qtedu.Impact_design.storage.repository.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.FLetterOfIntentModel;
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
    public List<FLetterOfIntentModel> findByUserId(Long userId) {
        return fLetterOfIntentJpaRepository.findByStdntNoAndDelYn(userId, "N").stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<FLetterOfIntentModel> findByUserIdAndTargetTeamId(Long userId, Integer targetTeamId) {
        return fLetterOfIntentJpaRepository
                .findByStdntNoAndInvestmentTargetAndDelYn(userId, String.valueOf(targetTeamId), "N")
                .map(this::toModel);
    }

    @Override
    public FLetterOfIntentModel save(FLetterOfIntentModel model) {
        FLetterOfIntent entity;

        if (model.getIntentIndex() != null) {
            entity = fLetterOfIntentJpaRepository.findById(model.getIntentIndex())
                    .orElseThrow(() -> new IllegalArgumentException("FLetterOfIntent not found"));
            entity.update(
                    model.getInvestmentPrice(),
                    model.getScore1(), model.getScore2(), model.getScore3(),
                    model.getScore4(), model.getScore5(), model.getScore6(),
                    model.getScore7(), model.getScore8(), model.getScore9(),
                    model.getOpinion(), model.getSubmitted()
            );
        } else {
            entity = FLetterOfIntent.builder()
                    .stdntNo(model.getStdntNo())
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
                    .regId(String.valueOf(model.getStdntNo()))
                    .regDt(LocalDateTime.now())
                    .submitted(Boolean.TRUE.equals(model.getSubmitted()))
                    .teamId(model.getTeamId())
                    .gameId(model.getGameId())
                    .canvasId(model.getCanvasId())
                    .build();
        }

        return toModel(fLetterOfIntentJpaRepository.save(entity));
    }

    @Override
    public boolean existsSubmittedByUserId(Long userId) {
        return fLetterOfIntentJpaRepository.existsByStdntNoAndSubmittedAndDelYn(userId, true, "N");
    }

    @Override
    public void submitAllByUserId(Long userId) {
        List<FLetterOfIntent> entities = fLetterOfIntentJpaRepository.findByStdntNoAndDelYn(userId, "N");
        entities.forEach(FLetterOfIntent::submit);
        fLetterOfIntentJpaRepository.saveAll(entities);
    }

    @Override
    public List<FLetterOfIntentModel> findByTargetTeamId(Integer teamId) {
        return fLetterOfIntentJpaRepository
                .findByInvestmentTargetAndDelYn(String.valueOf(teamId), "N")
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<FLetterOfIntentModel> findByTargetTeamIds(List<Integer> teamIds) {
        List<String> targets = teamIds.stream().map(String::valueOf).collect(Collectors.toList());
        return fLetterOfIntentJpaRepository
                .findByInvestmentTargetInAndDelYn(targets, "N")
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Set<Long> findSubmittedUserIds(List<Long> userIds) {
        return fLetterOfIntentJpaRepository
                .findByStdntNoInAndSubmittedAndDelYn(userIds, true, "N")
                .stream()
                .map(e -> e.getStdntNo())
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public void updateInvestmentTargetByCanvasOwner(Long userId, String teamId) {
        fLetterOfIntentJpaRepository.updateInvestmentTargetByCanvasOwner(userId, teamId);
    }

    private FLetterOfIntentModel toModel(FLetterOfIntent entity) {
        return FLetterOfIntentModel.builder()
                .intentIndex(entity.getIntentIndex())
                .stdntNo(entity.getStdntNo())
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
                .build();
    }
}
