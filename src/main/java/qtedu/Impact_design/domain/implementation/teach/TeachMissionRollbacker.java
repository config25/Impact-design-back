package qtedu.Impact_design.domain.implementation.teach;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.common.error.BadRequestException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.en.RollbackStage;
import qtedu.Impact_design.domain.repository.FLetterOfIntentRepository;
import qtedu.Impact_design.domain.repository.ImpactCheckRepository;
import qtedu.Impact_design.domain.repository.IdentityCanvasRepository;
import qtedu.Impact_design.domain.repository.flow_canvas.FlowCanvasRepository;
import qtedu.Impact_design.domain.repository.win_canvas.WinCanvasRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeachMissionRollbacker {

    private final TeamSubmitStatusChecker submitStatusChecker;
    private final ImpactCheckRepository impactCheckRepository;
    private final IdentityCanvasRepository identityCanvasRepository;
    private final FlowCanvasRepository flowCanvasRepository;
    private final WinCanvasRepository winCanvasRepository;
    private final FLetterOfIntentRepository fLetterOfIntentRepository;

    @Transactional
    public void rollback(Integer teamId, RollbackStage stage) {
        if (stage == null) {
            throw new BadRequestException(ErrorCode.VARIABLE_WRONG);
        }

        Long writerUserId = submitStatusChecker.findWriterUserId(teamId);

        switch (stage) {
            case A -> impactCheckRepository.rollbackByUserId(writerUserId);
            case B -> identityCanvasRepository.rollbackByUserId(writerUserId);
            case C -> flowCanvasRepository.rollbackAllByUserId(writerUserId);
            case D -> winCanvasRepository.rollbackByUserIdAndCanvasType(writerUserId, CanvasType.QUICK);
            case E -> winCanvasRepository.rollbackByUserIdAndCanvasType(writerUserId, CanvasType.BUILD);
            case F_BUILD -> fLetterOfIntentRepository.rollbackAllByUserId(writerUserId, CanvasType.BUILD);
            case F_QUICK -> fLetterOfIntentRepository.rollbackAllByUserId(writerUserId, CanvasType.QUICK);
        }

        log.info("미션 롤백 - teamId: {}, stage: {}, writerUserId: {}", teamId, stage, writerUserId);
    }
}
