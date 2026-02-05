package qtedu.Impact_design.domain.implementation.identitycanvas;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.api.dto.response.identitycanvas.IdentityCanvasResponse;
import qtedu.Impact_design.domain.model.IdentityCanvasModel;
import qtedu.Impact_design.domain.repository.IdentityCanvasRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class IdentityCanvasReader {

    private final IdentityCanvasRepository identityCanvasRepository;

    public IdentityCanvasResponse read(Long userId) {
        return identityCanvasRepository.findByUserId(userId)
                .map(IdentityCanvasResponse::from)
                .orElse(null);
    }

    public List<IdentityCanvasModel> readByUserIds(List<Long> userIds) {
        return identityCanvasRepository.findByUserIdIn(userIds);
    }
}
