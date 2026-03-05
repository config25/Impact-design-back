package qtedu.Impact_design.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import qtedu.Impact_design.api.dto.request.teach.ClassSaveRequest;
import qtedu.Impact_design.api.dto.request.teach.ClassUpdateRequest;
import qtedu.Impact_design.api.dto.response.teach.*;
import qtedu.Impact_design.domain.implementation.teach.TeachAppender;
import qtedu.Impact_design.domain.implementation.teach.TeachReader;
import qtedu.Impact_design.domain.implementation.teach.TeachUpdater;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class TeachServiceTest {

    @InjectMocks
    private TeachService teachService;

    @Mock
    private TeachReader teachReader;
    @Mock
    private TeachAppender teachAppender;
    @Mock
    private TeachUpdater teachUpdater;

    @Test
    @DisplayName("getTeachIndex - reader에서 수업 인덱스를 조회한다")
    void getTeachIndex() {
        List<ClassInfoResponse> expected = List.of(
                ClassInfoResponse.builder().gameId(1).name("수업1").build()
        );
        given(teachReader.getTeachIndex(1L)).willReturn(expected);

        List<ClassInfoResponse> result = teachService.getTeachIndex(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("수업1");
    }

    @Test
    @DisplayName("getTeachList - reader에서 수업 목록을 조회한다")
    void getTeachList() {
        TeachListResponse expected = TeachListResponse.builder()
                .inProgress(Collections.emptyList()).setting(Collections.emptyList())
                .completed(Collections.emptyList()).etc(Collections.emptyList()).build();
        given(teachReader.getTeachList(1L)).willReturn(expected);

        TeachListResponse result = teachService.getTeachList(1L);

        assertThat(result.getInProgress()).isEmpty();
    }

    @Test
    @DisplayName("getTeachDetail - reader에서 수업 상세를 조회한다")
    void getTeachDetail() {
        TeachDetailResponse expected = TeachDetailResponse.builder()
                .gameId(1).name("수업1").code("ABC").build();
        given(teachReader.getTeachDetail(1)).willReturn(expected);

        TeachDetailResponse result = teachService.getTeachDetail(1);

        assertThat(result.getCode()).isEqualTo("ABC");
    }

    @Test
    @DisplayName("getStudentList - reader에서 학생 목록을 조회한다")
    void getStudentList() {
        StudentListResponse expected = StudentListResponse.builder()
                .teamList(Collections.emptyList()).studentList(Collections.emptyList()).build();
        given(teachReader.getStudentList(1)).willReturn(expected);

        StudentListResponse result = teachService.getStudentList(1);

        assertThat(result.getStudentList()).isEmpty();
    }

    @Test
    @DisplayName("createClass - appender에 수업 생성을 위임한다")
    void createClass() {
        ClassSaveRequest request = new ClassSaveRequest();
        MultipartFile image = mock(MultipartFile.class);
        given(teachAppender.createClass(1L, request, image)).willReturn(10);

        Integer gameId = teachService.createClass(1L, request, image);

        assertThat(gameId).isEqualTo(10);
    }

    @Test
    @DisplayName("updateClass(ClassSaveRequest) - updater에 수업 수정을 위임한다")
    void updateClassWithSaveRequest() {
        ClassSaveRequest request = new ClassSaveRequest();
        MultipartFile image = mock(MultipartFile.class);
        given(teachUpdater.updateClass(1, request, image)).willReturn(1);

        Integer result = teachService.updateClass(1, request, image);

        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("updateClass(ClassUpdateRequest) - updater에 수업 수정을 위임한다")
    void updateClassWithUpdateRequest() {
        ClassUpdateRequest request = new ClassUpdateRequest();
        given(teachUpdater.updateClass(1, request)).willReturn(1);

        Integer result = teachService.updateClass(1, request);

        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("startClass - updater에 수업 시작을 위임한다")
    void startClass() {
        teachService.startClass(1, "2026-04-01");

        then(teachUpdater).should().startClass(1, "2026-04-01");
    }

    @Test
    @DisplayName("endClass - updater에 수업 종료를 위임한다")
    void endClass() {
        teachService.endClass(1);

        then(teachUpdater).should().endClass(1);
    }

    @Test
    @DisplayName("restoreClass - updater에 수업 복원을 위임한다")
    void restoreClass() {
        teachService.restoreClass(1, "2026-05-01");

        then(teachUpdater).should().restoreClass(1, "2026-05-01");
    }

    @Test
    @DisplayName("startNextStage - updater에 다음 단계 시작을 위임한다")
    void startNextStage() {
        teachService.startNextStage(1, "2026-04-15");

        then(teachUpdater).should().startNextStage(1, "2026-04-15");
    }
}
