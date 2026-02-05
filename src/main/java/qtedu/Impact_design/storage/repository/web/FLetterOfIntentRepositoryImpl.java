package qtedu.Impact_design.storage.repository.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.FLetterOfIntentModel;
import qtedu.Impact_design.domain.repository.FLetterOfIntentRepository;
import qtedu.Impact_design.storage.jpaentity.FLetterOfIntent;
import qtedu.Impact_design.storage.jparepository.web.FLetterOfIntentJpaRepository;

import java.util.List;
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
                .mdfcnId(entity.getMdfcnId())
                .mdfcnDt(entity.getMdfcnDt())
                .commitYn(entity.getCommitYn())
                .teamId(entity.getTeamId())
                .gameId(entity.getGameId())
                .categoryCd(entity.getCategoryCd())
                .canvasId(entity.getCanvasId())
                .build();
    }
}
