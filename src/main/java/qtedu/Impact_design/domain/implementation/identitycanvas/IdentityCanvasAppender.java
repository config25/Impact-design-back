package qtedu.Impact_design.domain.implementation.identitycanvas;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.request.identitycanvas.IdentityCanvasRequest;
import qtedu.Impact_design.api.dto.response.identitycanvas.IdentityCanvasResponse;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.domain.model.IdentityCanvasModel;
import qtedu.Impact_design.domain.repository.IdentityCanvasRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class IdentityCanvasAppender {

    private final IdentityCanvasRepository identityCanvasRepository;

    @Transactional
    public IdentityCanvasResponse append(Long userId, IdentityCanvasRequest request) {
        if (identityCanvasRepository.existsSubmittedByUserId(userId)) {
            throw new ConflictException(ErrorCode.ALREADY_SUBMITTED);
        }

        Optional<IdentityCanvasModel> existing = identityCanvasRepository.findByUserId(userId);

        IdentityCanvasModel model = IdentityCanvasModel.builder()
                .identityId(existing.map(IdentityCanvasModel::getIdentityId).orElse(null))
                .userId(userId)
                .mission(request.getMission())
                .vision(request.getVision())
                .value(request.getValue())
                .macro(request.getMacro())
                .tech(request.getTech())
                .customer(request.getCustomer())
                .competitor(request.getCompetitor())
                .capability(request.getCapability())
                .culture(request.getCulture())
                .structure(request.getStructure())
                .etc(request.getEtc())
                .newMission(request.getNewMission())
                .newVision(request.getNewVision())
                .newValue(request.getNewValue())
                .build();

        IdentityCanvasModel saved = identityCanvasRepository.save(model);
        return IdentityCanvasResponse.from(saved);
    }

    @Transactional
    public void submit(Long userId) {
        if (identityCanvasRepository.existsSubmittedByUserId(userId)) {
            throw new ConflictException(ErrorCode.ALREADY_SUBMITTED);
        }
        identityCanvasRepository.submitByUserId(userId);
    }
}
