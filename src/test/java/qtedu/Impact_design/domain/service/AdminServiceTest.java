package qtedu.Impact_design.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.api.dto.response.teach.ClassInfoResponse;
import qtedu.Impact_design.api.dto.response.teach.TeachListResponse;
import qtedu.Impact_design.domain.implementation.admin.AdminTeachReader;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private AdminTeachReader adminTeachReader;

    @Test
    @DisplayName("getAllClassList - reader에서 전체 수업 목록을 조회한다")
    void getAllClassList() {
        List<ClassInfoResponse> expected = List.of(
                ClassInfoResponse.builder().gameId(1).name("수업1").build(),
                ClassInfoResponse.builder().gameId(2).name("수업2").build()
        );
        given(adminTeachReader.getAllClassList(1L)).willReturn(expected);

        List<ClassInfoResponse> result = adminService.getAllClassList(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("수업1");
    }

    @Test
    @DisplayName("getAllTeachList - reader에서 전체 교수 목록을 조회한다")
    void getAllTeachList() {
        TeachListResponse expected = TeachListResponse.builder()
                .inProgress(Collections.emptyList()).setting(Collections.emptyList())
                .completed(Collections.emptyList()).etc(Collections.emptyList()).build();
        given(adminTeachReader.getAllTeachList(1L)).willReturn(expected);

        TeachListResponse result = adminService.getAllTeachList(1L);

        assertThat(result.getInProgress()).isEmpty();
    }
}
