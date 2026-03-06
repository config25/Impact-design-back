package qtedu.Impact_design.domain.implementation.teach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.model.team.TbTeamModel;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.model.user.UserinfoModel;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.teach.GameTeamRepository;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TeachTeamAppenderTest {

    @InjectMocks
    private TeachTeamAppender teachTeamAppender;

    @Mock private TbGameRepository tbGameRepository;
    @Mock private TbTeamRepository tbTeamRepository;
    @Mock private GameTeamRepository gameTeamRepository;
    @Mock private UserinfoRepository userinfoRepository;
    @Mock private TeamUserRepository teamUserRepository;
    @Mock private PasswordEncoder passwordEncoder;

    private TbGameModel sampleGame() {
        return TbGameModel.builder().gameId(1).code("ABC123").build();
    }

    @Nested
    @DisplayName("addTeam")
    class AddTeam {

        @Test
        @DisplayName("팀을 정상 추가한다")
        void addsTeamSuccessfully() {
            given(tbGameRepository.findById(1)).willReturn(Optional.of(sampleGame()));
            given(gameTeamRepository.countByGameId(1)).willReturn(3);
            given(tbTeamRepository.findMaxSequenceByGameId(1)).willReturn(3);
            given(tbTeamRepository.save(any())).willReturn(
                    TbTeamModel.builder().teamId(10).name("팀4").sequence(4).build());

            Integer teamId = teachTeamAppender.addTeam(1);

            assertThat(teamId).isEqualTo(10);
            then(gameTeamRepository).should().save(1, 10);
            then(tbGameRepository).should().incrementNumTeam(1);
        }

        @Test
        @DisplayName("게임이 없으면 NotFoundException")
        void gameNotFound() {
            given(tbGameRepository.findById(99)).willReturn(Optional.empty());

            assertThatThrownBy(() -> teachTeamAppender.addTeam(99))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        @DisplayName("팀 수가 최대(6)를 초과하면 ConflictException")
        void maxTeamsExceeded() {
            given(tbGameRepository.findById(1)).willReturn(Optional.of(sampleGame()));
            given(gameTeamRepository.countByGameId(1)).willReturn(7);

            assertThatThrownBy(() -> teachTeamAppender.addTeam(1))
                    .isInstanceOf(ConflictException.class);
        }

        @Test
        @DisplayName("생성된 팀 이름은 '팀{nextSequence}'이다")
        void teamNameFormat() {
            given(tbGameRepository.findById(1)).willReturn(Optional.of(sampleGame()));
            given(gameTeamRepository.countByGameId(1)).willReturn(2);
            given(tbTeamRepository.findMaxSequenceByGameId(1)).willReturn(2);

            ArgumentCaptor<TbTeamModel> captor = ArgumentCaptor.forClass(TbTeamModel.class);
            given(tbTeamRepository.save(captor.capture())).willReturn(
                    TbTeamModel.builder().teamId(5).build());

            teachTeamAppender.addTeam(1);

            assertThat(captor.getValue().getName()).isEqualTo("팀3");
            assertThat(captor.getValue().getSequence()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("addEvaluationTeam")
    class AddEvaluationTeam {

        @Test
        @DisplayName("평가팀을 정상 추가하며 teamCategory=1이다")
        void addsEvaluationTeam() {
            given(tbGameRepository.findById(1)).willReturn(Optional.of(sampleGame()));
            given(gameTeamRepository.countByGameId(1)).willReturn(2);
            given(tbTeamRepository.findMaxSequenceByGameId(1)).willReturn(2);

            ArgumentCaptor<TbTeamModel> captor = ArgumentCaptor.forClass(TbTeamModel.class);
            given(tbTeamRepository.save(captor.capture())).willReturn(
                    TbTeamModel.builder().teamId(11).build());

            Integer teamId = teachTeamAppender.addEvaluationTeam(1);

            assertThat(teamId).isEqualTo(11);
            assertThat(captor.getValue().getName()).isEqualTo("평가팀");
            assertThat(captor.getValue().getTeamCategory()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("addTeamMember")
    class AddTeamMember {

        @Test
        @DisplayName("팀원을 추가하고 loginId를 반환한다")
        void addsMemberSuccessfully() {
            given(tbTeamRepository.findByTeamId(10))
                    .willReturn(Optional.of(TbTeamModel.builder().teamId(10).sequence(1).code("ABC").build()));
            given(tbGameRepository.findById(1)).willReturn(Optional.of(sampleGame()));
            given(teamUserRepository.countByTeamId(10)).willReturn(3);
            given(userinfoRepository.findLastLoginIdByPattern("a1%")).willReturn(Optional.of("a12"));
            given(userinfoRepository.existsByLoginId("a13")).willReturn(false);
            given(passwordEncoder.encode("a13")).willReturn("encoded");
            given(userinfoRepository.save(any())).willReturn(
                    UserinfoModel.builder().userId(100L).loginId("a13").build());
            given(teamUserRepository.findByTeamId(10)).willReturn(List.of(
                    TeamUserModel.builder().userId(100L).build()));
            given(userinfoRepository.findByUserId(100L)).willReturn(
                    Optional.of(UserinfoModel.builder().userId(100L).writer("0").build()));

            String loginId = teachTeamAppender.addTeamMember(10, 1);

            assertThat(loginId).isEqualTo("a13");
            then(tbTeamRepository).should().incrementNumUser(10);
            then(userinfoRepository).should().setWriter(100L); // writer 없으므로 자동 지정
        }

        @Test
        @DisplayName("팀원 수가 최대(10)를 초과하면 ConflictException")
        void maxMembersExceeded() {
            given(tbTeamRepository.findByTeamId(10))
                    .willReturn(Optional.of(TbTeamModel.builder().teamId(10).sequence(1).build()));
            given(tbGameRepository.findById(1)).willReturn(Optional.of(sampleGame()));
            given(teamUserRepository.countByTeamId(10)).willReturn(11);

            assertThatThrownBy(() -> teachTeamAppender.addTeamMember(10, 1))
                    .isInstanceOf(ConflictException.class);
        }

        @Test
        @DisplayName("기존 loginId가 없으면 번호 1부터 시작한다")
        void firstMemberGetsNumber1() {
            given(tbTeamRepository.findByTeamId(10))
                    .willReturn(Optional.of(TbTeamModel.builder().teamId(10).sequence(2).code("ABC").build()));
            given(tbGameRepository.findById(1)).willReturn(Optional.of(sampleGame()));
            given(teamUserRepository.countByTeamId(10)).willReturn(0);
            given(userinfoRepository.findLastLoginIdByPattern("b1%")).willReturn(Optional.empty());
            given(userinfoRepository.existsByLoginId("b11")).willReturn(false);
            given(passwordEncoder.encode("b11")).willReturn("encoded");
            given(userinfoRepository.save(any())).willReturn(
                    UserinfoModel.builder().userId(200L).loginId("b11").build());
            given(teamUserRepository.findByTeamId(10)).willReturn(List.of(
                    TeamUserModel.builder().userId(200L).build()));
            given(userinfoRepository.findByUserId(200L)).willReturn(
                    Optional.of(UserinfoModel.builder().userId(200L).writer("0").build()));

            String loginId = teachTeamAppender.addTeamMember(10, 1);

            assertThat(loginId).isEqualTo("b11");
        }

        @Test
        @DisplayName("loginId 중복 시 다음 번호를 사용한다")
        void handlesDuplicateLoginId() {
            given(tbTeamRepository.findByTeamId(10))
                    .willReturn(Optional.of(TbTeamModel.builder().teamId(10).sequence(1).code("ABC").build()));
            given(tbGameRepository.findById(1)).willReturn(Optional.of(sampleGame()));
            given(teamUserRepository.countByTeamId(10)).willReturn(2);
            given(userinfoRepository.findLastLoginIdByPattern("a1%")).willReturn(Optional.of("a13"));
            given(userinfoRepository.existsByLoginId("a14")).willReturn(true);  // 중복
            given(userinfoRepository.existsByLoginId("a15")).willReturn(false); // 사용 가능
            given(passwordEncoder.encode("a15")).willReturn("encoded");
            given(userinfoRepository.save(any())).willReturn(
                    UserinfoModel.builder().userId(300L).loginId("a15").build());
            given(teamUserRepository.findByTeamId(10)).willReturn(List.of(
                    TeamUserModel.builder().userId(300L).build()));
            given(userinfoRepository.findByUserId(300L)).willReturn(
                    Optional.of(UserinfoModel.builder().userId(300L).writer("0").build()));

            String loginId = teachTeamAppender.addTeamMember(10, 1);

            assertThat(loginId).isEqualTo("a15");
        }

        @Test
        @DisplayName("이미 writer가 있으면 setWriter를 호출하지 않는다")
        void doesNotSetWriterWhenAlreadyExists() {
            given(tbTeamRepository.findByTeamId(10))
                    .willReturn(Optional.of(TbTeamModel.builder().teamId(10).sequence(1).code("ABC").build()));
            given(tbGameRepository.findById(1)).willReturn(Optional.of(sampleGame()));
            given(teamUserRepository.countByTeamId(10)).willReturn(1);
            given(userinfoRepository.findLastLoginIdByPattern("a1%")).willReturn(Optional.empty());
            given(userinfoRepository.existsByLoginId("a11")).willReturn(false);
            given(passwordEncoder.encode("a11")).willReturn("encoded");
            given(userinfoRepository.save(any())).willReturn(
                    UserinfoModel.builder().userId(400L).loginId("a11").build());
            // 기존 멤버 중 writer=1인 사용자가 이미 있음
            given(teamUserRepository.findByTeamId(10)).willReturn(List.of(
                    TeamUserModel.builder().userId(50L).build(),
                    TeamUserModel.builder().userId(400L).build()));
            given(userinfoRepository.findByUserId(50L)).willReturn(
                    Optional.of(UserinfoModel.builder().userId(50L).writer("1").build()));

            teachTeamAppender.addTeamMember(10, 1);

            then(userinfoRepository).should(org.mockito.Mockito.never()).setWriter(anyLong());
        }
    }
}
