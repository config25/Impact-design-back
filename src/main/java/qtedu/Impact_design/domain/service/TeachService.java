package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import qtedu.Impact_design.api.dto.request.teach.ClassSaveRequest;
import qtedu.Impact_design.api.dto.response.teach.ClassInfoResponse;
import qtedu.Impact_design.api.dto.response.teach.StudentListResponse;
import qtedu.Impact_design.api.dto.response.teach.TeachDetail2Response;
import qtedu.Impact_design.api.dto.response.teach.TeachDetailResponse;
import qtedu.Impact_design.api.dto.response.teach.TeachListResponse;
import qtedu.Impact_design.domain.implementation.teach.TeachAppender;
import qtedu.Impact_design.domain.implementation.teach.TeachReader;
import qtedu.Impact_design.domain.implementation.teach.TeachUpdater;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeachService {

    private final TeachReader teachReader;
    private final TeachAppender teachAppender;
    private final TeachUpdater teachUpdater;

    public List<ClassInfoResponse> getTeachIndex(Long userId) {
        return teachReader.getTeachIndex(userId);
    }

    public TeachListResponse getTeachList(Long userId) {
        return teachReader.getTeachList(userId);
    }

    public TeachDetailResponse getTeachDetail(Integer gameId) {
        return teachReader.getTeachDetail(gameId);
    }

    public TeachDetail2Response getTeachDetail2(Long userId, Integer gameId) {
        return teachReader.getTeachDetail2(userId, gameId);
    }

    public StudentListResponse getStudentList(Integer gameId) {
        return teachReader.getStudentList(gameId);
    }

    public Integer createClass(Long userId, ClassSaveRequest request, MultipartFile image) {
        return teachAppender.createClass(userId, request, image);
    }

    public Integer updateClass(Integer gameId, ClassSaveRequest request, MultipartFile image) {
        return teachUpdater.updateClass(gameId, request, image);
    }

    public void startClass(Integer gameId) {
        teachUpdater.startClass(gameId);
    }

    public void endClass(Integer gameId) {
        teachUpdater.endClass(gameId);
    }

    public void restoreClass(Integer gameId) {
        teachUpdater.restoreClass(gameId);
    }

    public void startNextStage(Integer gameId, String enddate) {
        teachUpdater.startNextStage(gameId, enddate);
    }
}
