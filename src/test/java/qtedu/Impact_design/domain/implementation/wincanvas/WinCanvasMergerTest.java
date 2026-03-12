package qtedu.Impact_design.domain.implementation.wincanvas;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.api.dto.request.wincanvas.WinCanvasSaveRequest;
import qtedu.Impact_design.domain.model.win_canvas.TaskActivityModel;
import qtedu.Impact_design.domain.model.win_canvas.TaskInputModel;
import qtedu.Impact_design.domain.model.win_canvas.TaskOutcomeModel;
import qtedu.Impact_design.domain.model.win_canvas.TeamworkModel;
import qtedu.Impact_design.domain.repository.win_canvas.TaskActivityRepository;
import qtedu.Impact_design.domain.repository.win_canvas.TaskInputRepository;
import qtedu.Impact_design.domain.repository.win_canvas.TaskOutcomeRepository;
import qtedu.Impact_design.domain.repository.win_canvas.TeamworkRepository;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WinCanvasMergerTest {

    @Mock
    private TaskInputRepository taskInputRepository;
    @Mock
    private TaskActivityRepository taskActivityRepository;
    @Mock
    private TeamworkRepository teamworkRepository;
    @Mock
    private TaskOutcomeRepository taskOutcomeRepository;

    @InjectMocks
    private WinCanvasMerger merger;

    // WinCanvasSaveRequest는 @NoArgsConstructor + @Getter라 리플렉션으로 세팅
    private <T> void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private WinCanvasSaveRequest.TaskInputItem createTaskInputItem(String resourceName, Integer quantity, Integer orderNo) throws Exception {
        WinCanvasSaveRequest.TaskInputItem item = createInstance(WinCanvasSaveRequest.TaskInputItem.class);
        setField(item, "resourceName", resourceName);
        setField(item, "quantity", quantity);
        setField(item, "orderNo", orderNo);
        return item;
    }

    private WinCanvasSaveRequest.TeamworkItem createTeamworkItem(String activityTeamwork, String workType) throws Exception {
        WinCanvasSaveRequest.TeamworkItem item = createInstance(WinCanvasSaveRequest.TeamworkItem.class);
        setField(item, "activityTeamwork", activityTeamwork);
        setField(item, "workType", workType);
        return item;
    }

    @SuppressWarnings("unchecked")
    private <T> T createInstance(Class<T> clazz) throws Exception {
        return clazz.getDeclaredConstructor().newInstance();
    }

    @Nested
    @DisplayName("mergeTaskInputs")
    class MergeTaskInputs {

        @Test
        @DisplayName("기존 항목이 있으면 기존 ID를 유지하며 업데이트한다")
        void updatesExistingItemWithExistingId() throws Exception {
            Long canvasId = 1L;
            TaskInputModel existing = TaskInputModel.builder()
                    .inputId(100L).canvasId(canvasId).resourceName("기존자원").quantity(5).orderNo(1).build();
            when(taskInputRepository.findByCanvasId(canvasId)).thenReturn(List.of(existing));

            WinCanvasSaveRequest request = createInstance(WinCanvasSaveRequest.class);
            setField(request, "taskInputs", List.of(createTaskInputItem("새자원", 10, 1)));

            merger.mergeTaskInputs(canvasId, request);

            ArgumentCaptor<TaskInputModel> captor = ArgumentCaptor.forClass(TaskInputModel.class);
            verify(taskInputRepository).save(captor.capture());

            TaskInputModel saved = captor.getValue();
            assertThat(saved.getInputId()).isEqualTo(100L); // 기존 ID 유지
            assertThat(saved.getResourceName()).isEqualTo("새자원");
            assertThat(saved.getQuantity()).isEqualTo(10);
        }

        @Test
        @DisplayName("새 항목이면 ID가 null로 저장된다")
        void createsNewItemWithNullId() throws Exception {
            Long canvasId = 1L;
            when(taskInputRepository.findByCanvasId(canvasId)).thenReturn(Collections.emptyList());

            WinCanvasSaveRequest request = createInstance(WinCanvasSaveRequest.class);
            setField(request, "taskInputs", List.of(createTaskInputItem("새자원", 3, 1)));

            merger.mergeTaskInputs(canvasId, request);

            ArgumentCaptor<TaskInputModel> captor = ArgumentCaptor.forClass(TaskInputModel.class);
            verify(taskInputRepository).save(captor.capture());

            assertThat(captor.getValue().getInputId()).isNull();
            assertThat(captor.getValue().getCanvasId()).isEqualTo(canvasId);
        }

        @Test
        @DisplayName("taskInputs가 null이면 아무 작업도 하지 않는다")
        void doesNothingWhenTaskInputsIsNull() {
            WinCanvasSaveRequest request = new WinCanvasSaveRequest();

            merger.mergeTaskInputs(1L, request);

            verifyNoInteractions(taskInputRepository);
        }
    }

    @Nested
    @DisplayName("mergeTeamwork")
    class MergeTeamwork {

        @Test
        @DisplayName("기존 teamwork가 있으면 기존 ID를 유지하며 업데이트한다")
        void updatesExistingTeamwork() throws Exception {
            Long canvasId = 1L;
            TeamworkModel existing = TeamworkModel.builder().teamworkId(50L).canvasId(canvasId).build();
            when(teamworkRepository.findByCanvasId(canvasId)).thenReturn(Optional.of(existing));

            WinCanvasSaveRequest request = createInstance(WinCanvasSaveRequest.class);
            setField(request, "teamwork", createTeamworkItem("협업내용", "온라인"));

            merger.mergeTeamwork(canvasId, request);

            ArgumentCaptor<TeamworkModel> captor = ArgumentCaptor.forClass(TeamworkModel.class);
            verify(teamworkRepository).save(captor.capture());

            assertThat(captor.getValue().getTeamworkId()).isEqualTo(50L);
            assertThat(captor.getValue().getActivityTeamwork()).isEqualTo("협업내용");
        }

        @Test
        @DisplayName("기존 teamwork가 없으면 새로 생성한다")
        void createsNewTeamwork() throws Exception {
            Long canvasId = 1L;
            when(teamworkRepository.findByCanvasId(canvasId)).thenReturn(Optional.empty());

            WinCanvasSaveRequest request = createInstance(WinCanvasSaveRequest.class);
            setField(request, "teamwork", createTeamworkItem("협업내용", "오프라인"));

            merger.mergeTeamwork(canvasId, request);

            ArgumentCaptor<TeamworkModel> captor = ArgumentCaptor.forClass(TeamworkModel.class);
            verify(teamworkRepository).save(captor.capture());

            assertThat(captor.getValue().getTeamworkId()).isNull();
        }

        @Test
        @DisplayName("teamwork가 null이면 아무 작업도 하지 않는다")
        void doesNothingWhenTeamworkIsNull() {
            WinCanvasSaveRequest request = new WinCanvasSaveRequest();

            merger.mergeTeamwork(1L, request);

            verifyNoInteractions(teamworkRepository);
        }
    }
}
