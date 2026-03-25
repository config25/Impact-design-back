package qtedu.Impact_design.domain.implementation.funding;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.model.user.UserinfoModel;
import qtedu.Impact_design.domain.model.win_canvas.WinCanvasModel;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;
import qtedu.Impact_design.domain.repository.win_canvas.WinCanvasRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TeamBusinessNameResolverTest {

    @InjectMocks
    private TeamBusinessNameResolver
            resolver;

    @Mock private TeamUserRepository teamUserRepository;
    @Mock private UserinfoRepository userinfoRepository;
    @Mock private WinCanvasRepository winCanvasRepository;

    @Nested
    @DisplayName("resolve")
    class Resolve {

        @Test
        @DisplayName("팀원이 없으면 빈 문자열을 반환한다")
        void emptyTeam() {
            given(teamUserRepository.findByTeamId(1)).willReturn(Collections.emptyList());

            String result = resolver.resolve("build", 1);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("writer가 있으면 해당 사용자의 taskName을 반환한다")
        void writerExists() {
            TeamUserModel tu1 = TeamUserModel.builder().userId(10L).teamId(1).build();
            TeamUserModel tu2 = TeamUserModel.builder().userId(20L).teamId(1).build();
            given(teamUserRepository.findByTeamId(1)).willReturn(List.of(tu1, tu2));

            UserinfoModel writer = UserinfoModel.builder().userId(20L).writer("1").build();
            given(userinfoRepository.findWriterByUserIds(List.of(10L, 20L)))
                    .willReturn(Optional.of(writer));

            WinCanvasModel canvas = WinCanvasModel.builder()
                    .userId(20L).canvasType(CanvasType.BUILD).taskName("AI 플랫폼").build();
            given(winCanvasRepository.findByUserIdAndCanvasType(20L, CanvasType.BUILD))
                    .willReturn(Optional.of(canvas));

            String result = resolver.resolve("build", 1);

            assertThat(result).isEqualTo("AI 플랫폼");
        }

        @Test
        @DisplayName("writer가 없으면 첫 번째 사용자의 taskName을 반환한다")
        void noWriterFallsBackToFirst() {
            TeamUserModel tu = TeamUserModel.builder().userId(10L).teamId(1).build();
            given(teamUserRepository.findByTeamId(1)).willReturn(List.of(tu));
            given(userinfoRepository.findWriterByUserIds(List.of(10L)))
                    .willReturn(Optional.empty());

            WinCanvasModel canvas = WinCanvasModel.builder()
                    .userId(10L).canvasType(CanvasType.QUICK).taskName("퀵윈 과제").build();
            given(winCanvasRepository.findByUserIdAndCanvasType(10L, CanvasType.QUICK))
                    .willReturn(Optional.of(canvas));

            String result = resolver.resolve("quick", 1);

            assertThat(result).isEqualTo("퀵윈 과제");
        }

        @Test
        @DisplayName("canvas가 없으면 빈 문자열을 반환한다")
        void noCanvas() {
            TeamUserModel tu = TeamUserModel.builder().userId(10L).teamId(1).build();
            given(teamUserRepository.findByTeamId(1)).willReturn(List.of(tu));
            given(userinfoRepository.findWriterByUserIds(List.of(10L)))
                    .willReturn(Optional.empty());
            given(winCanvasRepository.findByUserIdAndCanvasType(10L, CanvasType.BUILD))
                    .willReturn(Optional.empty());

            String result = resolver.resolve("BUILD", 1);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("canvasType 대소문자 무시 - 'Build'도 BUILD 타입으로 처리한다")
        void canvasTypeCaseInsensitive() {
            TeamUserModel tu = TeamUserModel.builder().userId(10L).teamId(1).build();
            given(teamUserRepository.findByTeamId(1)).willReturn(List.of(tu));
            given(userinfoRepository.findWriterByUserIds(List.of(10L)))
                    .willReturn(Optional.empty());
            given(winCanvasRepository.findByUserIdAndCanvasType(10L, CanvasType.BUILD))
                    .willReturn(Optional.of(WinCanvasModel.builder().taskName("테스트").build()));

            String result = resolver.resolve("Build", 1);

            assertThat(result).isEqualTo("테스트");
        }
    }

    @Nested
    @DisplayName("resolveBulk")
    class ResolveBulk {

        @Test
        @DisplayName("빈 teamIds이면 빈 맵을 반환한다")
        void emptyTeamIds() {
            Map<Integer, String> result = resolver.resolveBulk("build", Collections.emptyList());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("여러 팀의 taskName을 일괄 조회한다")
        void multipleTeams() {
            TeamUserModel tu1 = TeamUserModel.builder().userId(10L).teamId(1).build();
            TeamUserModel tu2 = TeamUserModel.builder().userId(20L).teamId(2).build();
            given(teamUserRepository.findByTeamIdIn(List.of(1, 2)))
                    .willReturn(List.of(tu1, tu2));

            UserinfoModel user1 = UserinfoModel.builder().userId(10L).writer("1").build();
            UserinfoModel user2 = UserinfoModel.builder().userId(20L).writer("1").build();
            given(userinfoRepository.findAllByUserIds(anyList()))
                    .willReturn(List.of(user1, user2));

            WinCanvasModel canvas1 = WinCanvasModel.builder()
                    .userId(10L).taskName("프로젝트A").build();
            WinCanvasModel canvas2 = WinCanvasModel.builder()
                    .userId(20L).taskName("프로젝트B").build();
            given(winCanvasRepository.findByUserIdInAndCanvasType(anyList(), any()))
                    .willReturn(List.of(canvas1, canvas2));

            Map<Integer, String> result = resolver.resolveBulk("build", List.of(1, 2));

            assertThat(result).containsEntry(1, "프로젝트A");
            assertThat(result).containsEntry(2, "프로젝트B");
        }

        @Test
        @DisplayName("팀원이 없는 팀은 빈 문자열을 반환한다")
        void teamWithNoMembers() {
            given(teamUserRepository.findByTeamIdIn(List.of(1)))
                    .willReturn(Collections.emptyList());
            given(userinfoRepository.findAllByUserIds(anyList()))
                    .willReturn(Collections.emptyList());
            given(winCanvasRepository.findByUserIdInAndCanvasType(anyList(), any()))
                    .willReturn(Collections.emptyList());

            Map<Integer, String> result = resolver.resolveBulk("build", List.of(1));

            assertThat(result).containsEntry(1, "");
        }

        @Test
        @DisplayName("writer가 없으면 첫 번째 사용자의 canvas를 사용한다")
        void fallbackToFirstUser() {
            TeamUserModel tu = TeamUserModel.builder().userId(10L).teamId(1).build();
            given(teamUserRepository.findByTeamIdIn(List.of(1))).willReturn(List.of(tu));

            UserinfoModel user = UserinfoModel.builder().userId(10L).writer("0").build();
            given(userinfoRepository.findAllByUserIds(List.of(10L))).willReturn(List.of(user));

            WinCanvasModel canvas = WinCanvasModel.builder()
                    .userId(10L).taskName("폴백 과제").build();
            given(winCanvasRepository.findByUserIdInAndCanvasType(List.of(10L), CanvasType.QUICK))
                    .willReturn(List.of(canvas));

            Map<Integer, String> result = resolver.resolveBulk("quick", List.of(1));

            assertThat(result).containsEntry(1, "폴백 과제");
        }

        @Test
        @DisplayName("canvas의 taskName이 null이면 빈 문자열을 반환한다")
        void nullTaskName() {
            TeamUserModel tu = TeamUserModel.builder().userId(10L).teamId(1).build();
            given(teamUserRepository.findByTeamIdIn(List.of(1))).willReturn(List.of(tu));

            UserinfoModel user = UserinfoModel.builder().userId(10L).writer("1").build();
            given(userinfoRepository.findAllByUserIds(List.of(10L))).willReturn(List.of(user));

            WinCanvasModel canvas = WinCanvasModel.builder()
                    .userId(10L).taskName(null).build();
            given(winCanvasRepository.findByUserIdInAndCanvasType(List.of(10L), CanvasType.BUILD))
                    .willReturn(List.of(canvas));

            Map<Integer, String> result = resolver.resolveBulk("build", List.of(1));

            assertThat(result).containsEntry(1, "");
        }
    }
}
