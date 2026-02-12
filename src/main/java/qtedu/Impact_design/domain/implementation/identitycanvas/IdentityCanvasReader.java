package qtedu.Impact_design.domain.implementation.identitycanvas;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.api.dto.response.identitycanvas.IdentityCanvasResponse;
import qtedu.Impact_design.domain.model.IdentityCanvasModel;
import qtedu.Impact_design.domain.repository.IdentityCanvasRepository;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class IdentityCanvasReader {

    private final IdentityCanvasRepository identityCanvasRepository;
    private final TbGameRepository tbGameRepository;

    public IdentityCanvasResponse read(Long userId) {
        String imageUrl = tbGameRepository.findGameImageUrlByUserId(userId);
        return identityCanvasRepository.findByUserId(userId)
                .map(model -> IdentityCanvasResponse.from(model, imageUrl))
                .orElse(null);
    }

    public List<IdentityCanvasModel> readByUserIds(List<Long> userIds) {
        return identityCanvasRepository.findByUserIdIn(userIds);
    }
}
