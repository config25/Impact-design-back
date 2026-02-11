package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qtedu.Impact_design.api.dto.response.teach.ClassInfoResponse;
import qtedu.Impact_design.api.dto.response.teach.TeachListResponse;
import qtedu.Impact_design.domain.implementation.admin.AdminTeachReader;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminTeachReader adminTeachReader;

    public List<ClassInfoResponse> getAllClassList(Long userId) {
        return adminTeachReader.getAllClassList(userId);
    }

    public TeachListResponse getAllTeachList(Long userId) {
        return adminTeachReader.getAllTeachList(userId);
    }
}
