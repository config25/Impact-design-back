package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qtedu.Impact_design.api.dto.request.identitycanvas.IdentityCanvasRequest;
import qtedu.Impact_design.api.dto.response.identitycanvas.IdentityCanvasResponse;
import qtedu.Impact_design.domain.implementation.identitycanvas.IdentityCanvasAppender;
import qtedu.Impact_design.domain.implementation.identitycanvas.IdentityCanvasReader;
import qtedu.Impact_design.domain.model.IdentityCanvasModel;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IdentityCanvasService {

    private final IdentityCanvasReader identityCanvasReader;
    private final IdentityCanvasAppender identityCanvasAppender;

    public IdentityCanvasResponse getIdentityCanvas(Long userId) {
        return identityCanvasReader.read(userId);
    }

    public IdentityCanvasResponse saveIdentityCanvas(Long userId, IdentityCanvasRequest request) {
        return identityCanvasAppender.append(userId, request);
    }

    public List<IdentityCanvasModel> getIdentityCanvasesByUserIds(List<Long> userIds) {
        return identityCanvasReader.readByUserIds(userIds);
    }

    public IdentityCanvasResponse submitIdentityCanvas(Long userId) {
        identityCanvasAppender.submit(userId);
        return identityCanvasReader.read(userId);
    }
}
