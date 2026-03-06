package qtedu.Impact_design.domain.implementation.teach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.model.user.UserinfoModel;
import qtedu.Impact_design.domain.repository.FLetterOfIntent2Repository;
import qtedu.Impact_design.domain.repository.FLetterOfIntentRepository;
import qtedu.Impact_design.domain.repository.ImpactCheckRepository;
import qtedu.Impact_design.domain.repository.IdentityCanvasRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.flow_canvas.FlowCanvasRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;
import qtedu.Impact_design.domain.repository.win_canvas.WinCanvasRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TeamSubmitStatusCheckerTest {

    @InjectMocks
    private TeamSubmitStatusChecker checker;

    @Mock private TeamUserRepository teamUserRepository;
    @Mock private ImpactCheckRepository impactCheckRepository;
    @Mock private IdentityCanvasRepository identityCanvasRepository;
    @Mock private FlowCanvasRepository flowCanvasRepository;
    @Mock private WinCanvasRepository winCanvasRepository;
    @Mock private FLetterOfIntentRepository fLetterOfIntentRepository;
    @Mock private FLetterOfIntent2Repository fLetterOfIntent2Repository;
    @Mock private UserinfoRepository userinfoRepository;

    @Nested
    @DisplayName("findWriterUserId")
    class FindWriterUserId {

        @Test
        @DisplayName("writer=1인 사용자의 userId를 반환한다")
        void returnsWriterUserId() {
            given(teamUserRepository.findByTeamId(1)).willReturn(List.of(
                    TeamUserModel.builder().userId(10L).build(),
                    TeamUserModel.builder().userId(20L).build()));
            given(userinfoRepository.findWriterByUserIds(List.of(10L, 20L)))
                    .willReturn(Optional.of(UserinfoModel.builder().userId(20L).writer("1").build()));

            Long writerId = checker.findWriterUserId(1);

            assertThat(writerId).isEqualTo(20L);
        }

        @Test
        @DisplayName("writer가 없으면 첫 번째 사용자를 반환한다")
        void fallsBackToFirstUser() {
            given(teamUserRepository.findByTeamId(1)).willReturn(List.of(
                    TeamUserModel.builder().userId(10L).build(),
                    TeamUserModel.builder().userId(20L).build()));
            given(userinfoRepository.findWriterByUserIds(List.of(10L, 20L)))
                    .willReturn(Optional.empty());

            Long writerId = checker.findWriterUserId(1);

            assertThat(writerId).isEqualTo(10L);
        }

        @Test
        @DisplayName("팀에 멤버가 없으면 NotFoundException")
        void throwsWhenNoMembers() {
            given(teamUserRepository.findByTeamId(1)).willReturn(Collections.emptyList());

            assertThatThrownBy(() -> checker.findWriterUserId(1))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("checkSubmit A~F")
    class CheckSubmitIndividual {

        @Test
        @DisplayName("submitA - 제출됨이면 '제출'")
        void submitA_submitted() {
            given(impactCheckRepository.existsSubmittedByUserId(1L)).willReturn(true);
            assertThat(checker.checkSubmitA(1L)).isEqualTo("제출");
        }

        @Test
        @DisplayName("submitA - 미제출이면 '미제출'")
        void submitA_notSubmitted() {
            given(impactCheckRepository.existsSubmittedByUserId(1L)).willReturn(false);
            assertThat(checker.checkSubmitA(1L)).isEqualTo("미제출");
        }

        @Test
        @DisplayName("submitF - intent1만 제출해도 '제출'")
        void submitF_onlyIntent1() {
            given(fLetterOfIntentRepository.existsSubmittedByUserId(1L)).willReturn(true);
            given(fLetterOfIntent2Repository.existsSubmittedByUserId(1L)).willReturn(false);
            assertThat(checker.checkSubmitF(1L)).isEqualTo("제출");
        }

        @Test
        @DisplayName("submitF - intent2만 제출해도 '제출'")
        void submitF_onlyIntent2() {
            given(fLetterOfIntentRepository.existsSubmittedByUserId(1L)).willReturn(false);
            given(fLetterOfIntent2Repository.existsSubmittedByUserId(1L)).willReturn(true);
            assertThat(checker.checkSubmitF(1L)).isEqualTo("제출");
        }

        @Test
        @DisplayName("submitF - 둘 다 미제출이면 '미제출'")
        void submitF_neitherSubmitted() {
            given(fLetterOfIntentRepository.existsSubmittedByUserId(1L)).willReturn(false);
            given(fLetterOfIntent2Repository.existsSubmittedByUserId(1L)).willReturn(false);
            assertThat(checker.checkSubmitF(1L)).isEqualTo("미제출");
        }
    }

    @Nested
    @DisplayName("checkAllSubmitStatuses (배치)")
    class CheckAllSubmitStatuses {

        @Test
        @DisplayName("빈 맵이면 빈 결과를 반환한다")
        void emptyInput() {
            Map<Integer, TeamSubmitStatusChecker.TeamSubmitResult> result =
                    checker.checkAllSubmitStatuses(Collections.emptyMap());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("writerUserIds가 모두 null이면 전부 미제출로 반환한다")
        void allEmptyTeams() {
            Map<Integer, List<Long>> teamUserIdsMap = Map.of(
                    1, Collections.emptyList(),
                    2, Collections.emptyList());

            given(userinfoRepository.findAllByUserIds(Collections.emptyList()))
                    .willReturn(Collections.emptyList());

            Map<Integer, TeamSubmitStatusChecker.TeamSubmitResult> result =
                    checker.checkAllSubmitStatuses(teamUserIdsMap);

            assertThat(result).hasSize(2);
            assertThat(result.get(1).getSubmitA()).isEqualTo("미제출");
            assertThat(result.get(2).getSubmitF()).isEqualTo("미제출");
        }
    }
}
