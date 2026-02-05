package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qtedu.Impact_design.api.dto.response.teach.ClassInfoResponse;
import qtedu.Impact_design.api.dto.response.teach.TeachListResponse;
import qtedu.Impact_design.domain.implementation.teach.TeachFacade;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeachService {

    private final TeachFacade teachFacade;

    public List<ClassInfoResponse> getTeachIndex(Long userId) {
        return teachFacade.getTeachIndex(userId);
    }

    public TeachListResponse getTeachList(Long userId) {
        return teachFacade.getTeachList(userId);
    }
}
