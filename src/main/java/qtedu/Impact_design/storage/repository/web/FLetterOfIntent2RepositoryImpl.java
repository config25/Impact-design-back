package qtedu.Impact_design.storage.repository.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.FLetterOfIntent2Model;
import qtedu.Impact_design.domain.repository.FLetterOfIntent2Repository;
import qtedu.Impact_design.storage.jpaentity.FLetterOfIntent2;
import qtedu.Impact_design.storage.jparepository.web.FLetterOfIntent2JpaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FLetterOfIntent2RepositoryImpl implements FLetterOfIntent2Repository {

    private final FLetterOfIntent2JpaRepository fLetterOfIntent2JpaRepository;

    @Override
    public List<FLetterOfIntent2Model> findByCanvasIdIn(List<Long> canvasIds) {
        return fLetterOfIntent2JpaRepository.findByCanvasIdIn(canvasIds).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
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
