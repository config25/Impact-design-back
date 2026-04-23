package qtedu.Impact_design.domain.implementation.teach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.common.error.BadRequestException;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.en.RollbackStage;
import qtedu.Impact_design.domain.repository.FLetterOfIntentRepository;
import qtedu.Impact_design.domain.repository.ImpactCheckRepository;
import qtedu.Impact_design.domain.repository.IdentityCanvasRepository;
import qtedu.Impact_design.domain.repository.flow_canvas.FlowCanvasRepository;
import qtedu.Impact_design.domain.repository.win_canvas.WinCanvasRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class TeachMissionRollbackerTest {

    @InjectMocks
    private TeachMissionRollbacker rollbacker;

    @Mock private TeamSubmitStatusChecker submitStatusChecker;
    @Mock private ImpactCheckRepository impactCheckRepository;
    @Mock private IdentityCanvasRepository identityCanvasRepository;
    @Mock private FlowCanvasRepository flowCanvasRepository;
    @Mock private WinCanvasRepository winCanvasRepository;
    @Mock private FLetterOfIntentRepository fLetterOfIntentRepository;

    private static final Integer TEAM_ID = 1;
    private static final Long WRITER_USER_ID = 10L;

    @Test
    @DisplayName("A 스테이지 - ImpactCheck writer row submitted=false")
    void rollback_A() {
        given(submitStatusChecker.findWriterUserId(TEAM_ID)).willReturn(WRITER_USER_ID);

        rollbacker.rollback(TEAM_ID, RollbackStage.A);

        verify(impactCheckRepository).rollbackByUserId(WRITER_USER_ID);
        verifyNoInteractions(identityCanvasRepository, flowCanvasRepository, winCanvasRepository, fLetterOfIntentRepository);
    }

    @Test
    @DisplayName("B 스테이지 - IdentityCanvas writer row submitted=false")
    void rollback_B() {
        given(submitStatusChecker.findWriterUserId(TEAM_ID)).willReturn(WRITER_USER_ID);

        rollbacker.rollback(TEAM_ID, RollbackStage.B);

        verify(identityCanvasRepository).rollbackByUserId(WRITER_USER_ID);
        verifyNoInteractions(impactCheckRepository, flowCanvasRepository, winCanvasRepository, fLetterOfIntentRepository);
    }

    @Test
    @DisplayName("C 스테이지 - FlowCanvas writer 전 row submitted=false")
    void rollback_C() {
        given(submitStatusChecker.findWriterUserId(TEAM_ID)).willReturn(WRITER_USER_ID);

        rollbacker.rollback(TEAM_ID, RollbackStage.C);

        verify(flowCanvasRepository).rollbackAllByUserId(WRITER_USER_ID);
        verifyNoInteractions(impactCheckRepository, identityCanvasRepository, winCanvasRepository, fLetterOfIntentRepository);
    }

    @Test
    @DisplayName("D 스테이지 - WinCanvas QUICK submitted=false")
    void rollback_D() {
        given(submitStatusChecker.findWriterUserId(TEAM_ID)).willReturn(WRITER_USER_ID);

        rollbacker.rollback(TEAM_ID, RollbackStage.D);

        verify(winCanvasRepository).rollbackByUserIdAndCanvasType(WRITER_USER_ID, CanvasType.QUICK);
        verifyNoInteractions(impactCheckRepository, identityCanvasRepository, flowCanvasRepository, fLetterOfIntentRepository);
    }

    @Test
    @DisplayName("E 스테이지 - WinCanvas BUILD submitted=false")
    void rollback_E() {
        given(submitStatusChecker.findWriterUserId(TEAM_ID)).willReturn(WRITER_USER_ID);

        rollbacker.rollback(TEAM_ID, RollbackStage.E);

        verify(winCanvasRepository).rollbackByUserIdAndCanvasType(WRITER_USER_ID, CanvasType.BUILD);
        verifyNoInteractions(impactCheckRepository, identityCanvasRepository, flowCanvasRepository, fLetterOfIntentRepository);
    }

    @Test
    @DisplayName("F_BUILD 스테이지 - FLetterOfIntent BUILD 전 row submitted=false")
    void rollback_F_BUILD() {
        given(submitStatusChecker.findWriterUserId(TEAM_ID)).willReturn(WRITER_USER_ID);

        rollbacker.rollback(TEAM_ID, RollbackStage.F_BUILD);

        verify(fLetterOfIntentRepository).rollbackAllByUserId(WRITER_USER_ID, CanvasType.BUILD);
        verifyNoInteractions(impactCheckRepository, identityCanvasRepository, flowCanvasRepository, winCanvasRepository);
    }

    @Test
    @DisplayName("F_QUICK 스테이지 - FLetterOfIntent QUICK 전 row submitted=false")
    void rollback_F_QUICK() {
        given(submitStatusChecker.findWriterUserId(TEAM_ID)).willReturn(WRITER_USER_ID);

        rollbacker.rollback(TEAM_ID, RollbackStage.F_QUICK);

        verify(fLetterOfIntentRepository).rollbackAllByUserId(WRITER_USER_ID, CanvasType.QUICK);
        verifyNoInteractions(impactCheckRepository, identityCanvasRepository, flowCanvasRepository, winCanvasRepository);
    }

    @Test
    @DisplayName("stage가 null이면 BadRequestException")
    void rollback_nullStage() {
        assertThatThrownBy(() -> rollbacker.rollback(TEAM_ID, null))
                .isInstanceOf(BadRequestException.class);

        verifyNoInteractions(submitStatusChecker, impactCheckRepository, identityCanvasRepository,
                flowCanvasRepository, winCanvasRepository, fLetterOfIntentRepository);
    }
}
