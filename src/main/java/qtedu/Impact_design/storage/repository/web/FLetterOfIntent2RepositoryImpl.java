package qtedu.Impact_design.storage.repository.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.FLetterOfIntent2Model;
import qtedu.Impact_design.domain.repository.FLetterOfIntent2Repository;
import qtedu.Impact_design.storage.jpaentity.FLetterOfIntent2;
import qtedu.Impact_design.storage.jparepository.web.FLetterOfIntent2JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FLetterOfIntent2RepositoryImpl implements FLetterOfIntent2Repository {

    private final FLetterOfIntent2JpaRepository jpaRepository;

    @Override
    public List<FLetterOfIntent2Model> findByCanvasIdIn(List<Long> canvasIds) {
        return jpaRepository.findByCanvasIdIn(canvasIds).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<FLetterOfIntent2Model> findByUserId(Long userId) {
        return jpaRepository.findByStdntNoAndDelYn(userId, "N").stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<FLetterOfIntent2Model> findByUserIdAndTargetTeamId(Long userId, Integer targetTeamId) {
        return jpaRepository
                .findByStdntNoAndInvestmentTargetAndDelYn(userId, String.valueOf(targetTeamId), "N")
                .map(this::toModel);
    }

    @Override
    public FLetterOfIntent2Model save(FLetterOfIntent2Model model) {
        FLetterOfIntent2 entity;

        if (model.getIntentIndex() != null) {
            entity = jpaRepository.findById(model.getIntentIndex())
                    .orElseThrow(() -> new IllegalArgumentException("FLetterOfIntent2 not found"));
            entity.update(
                    model.getInvestmentPrice(),
                    model.getScore1(), model.getScore2(), model.getScore3(),
                    model.getScore4(), model.getScore5(), model.getScore6(),
                    model.getScore7(), model.getScore8(), model.getScore9(),
                    model.getOpinion(), model.getSubmitted()
            );
        } else {
            entity = FLetterOfIntent2.builder()
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
                    .build();
        }

        return toModel(jpaRepository.save(entity));
    }

    @Override
    public boolean existsSubmittedByUserId(Long userId) {
        return jpaRepository.existsByStdntNoAndSubmittedAndDelYn(userId, true, "N");
    }

    @Override
    public void submitAllByUserId(Long userId) {
        List<FLetterOfIntent2> entities = jpaRepository.findByStdntNoAndDelYn(userId, "N");
        entities.forEach(FLetterOfIntent2::submit);
        jpaRepository.saveAll(entities);
    }

    @Override
    public List<FLetterOfIntent2Model> findByTargetTeamId(Integer teamId) {
        return jpaRepository
                .findByInvestmentTargetAndDelYn(String.valueOf(teamId), "N")
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void updateInvestmentTargetByCanvasOwner(Long userId, String teamId) {
        jpaRepository.updateInvestmentTargetByCanvasOwner(userId, teamId);
    }

    private FLetterOfIntent2Model toModel(FLetterOfIntent2 entity) {
        return FLetterOfIntent2Model.builder()
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
