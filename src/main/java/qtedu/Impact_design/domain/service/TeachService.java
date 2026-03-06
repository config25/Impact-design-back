package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import qtedu.Impact_design.api.dto.request.teach.ClassSaveRequest;
import qtedu.Impact_design.api.dto.request.teach.ClassUpdateRequest;
import qtedu.Impact_design.api.dto.response.teach.ClassInfoResponse;
import qtedu.Impact_design.api.dto.response.teach.StudentListResponse;
import qtedu.Impact_design.api.dto.response.teach.TeachDetail2Response;
import qtedu.Impact_design.api.dto.response.teach.TeachDetailResponse;
import qtedu.Impact_design.api.dto.response.teach.TeachListResponse;
import qtedu.Impact_design.domain.implementation.teach.TeachAppender;
import qtedu.Impact_design.domain.implementation.teach.TeachReader;
import qtedu.Impact_design.domain.implementation.teach.TeachUpdater;
import qtedu.Impact_design.domain.implementation.teach.TeachValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeachService {

    private final TeachReader teachReader;
    private final TeachAppender teachAppender;
    private final TeachUpdater teachUpdater;
    private final TeachValidator teachValidator;

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
        teachValidator.validateClassSaveInput(request.getName(), request.getNumTeam(), request.getNumMember());
        return teachAppender.createClass(userId, request, image);
    }

    public Integer updateClass(Integer gameId, ClassSaveRequest request, MultipartFile image) {
        teachValidator.validateClassSaveInput(request.getName(), request.getNumTeam(), request.getNumMember());
        return teachUpdater.updateClass(gameId, request, image);
    }

    public Integer updateClass(Integer gameId, ClassUpdateRequest request) {
        teachValidator.validateClassUpdateInput(request.getName());
        return teachUpdater.updateClass(gameId, request);
    }

    public void startClass(Integer gameId, String enddate) {
        teachValidator.validateEnddate(enddate);
        teachUpdater.startClass(gameId, enddate);
    }

    public void endClass(Integer gameId) {
        teachUpdater.endClass(gameId);
    }

    public void restoreClass(Integer gameId, String enddate) {
        teachValidator.validateEnddate(enddate);
        teachUpdater.restoreClass(gameId, enddate);
    }

    public void startNextStage(Integer gameId, String enddate) {
        teachValidator.validateEnddate(enddate);
        teachUpdater.startNextStage(gameId, enddate);
    }
}
