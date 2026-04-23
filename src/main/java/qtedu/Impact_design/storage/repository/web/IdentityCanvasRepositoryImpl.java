package qtedu.Impact_design.storage.repository.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.IdentityCanvasModel;
import qtedu.Impact_design.domain.repository.IdentityCanvasRepository;
import qtedu.Impact_design.storage.jpaentity.IdentityCanvas;
import qtedu.Impact_design.storage.jparepository.web.IdentityCanvasJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class IdentityCanvasRepositoryImpl implements IdentityCanvasRepository {

    private final IdentityCanvasJpaRepository identityCanvasJpaRepository;

    @Override
    public Optional<IdentityCanvasModel> findByUserId(Long userId) {
        return identityCanvasJpaRepository.findByUserId(userId)
                .map(this::toModel);
    }

    @Override
    public IdentityCanvasModel save(IdentityCanvasModel model) {
        IdentityCanvas entity;
        if (model.getIdentityId() != null) {
            entity = identityCanvasJpaRepository.findById(model.getIdentityId())
                    .orElseThrow(() -> new IllegalArgumentException("IdentityCanvas not found with id: " + model.getIdentityId()));
            entity.update(
                    model.getMission(), model.getVision(), model.getValue(),
                    model.getMacro(), model.getTech(), model.getCustomer(),
                    model.getCompetitor(), model.getCapability(), model.getCulture(),
                    model.getStructure(), model.getEtc(),
                    model.getNewMission(), model.getNewVision(), model.getNewValue()
            );
            entity = identityCanvasJpaRepository.save(entity);
        } else {
            entity = IdentityCanvas.builder()
                    .userId(model.getUserId())
                    .mission(model.getMission())
                    .vision(model.getVision())
                    .value(model.getValue())
                    .macro(model.getMacro())
                    .tech(model.getTech())
                    .customer(model.getCustomer())
                    .competitor(model.getCompetitor())
                    .capability(model.getCapability())
                    .culture(model.getCulture())
                    .structure(model.getStructure())
                    .etc(model.getEtc())
                    .newMission(model.getNewMission())
                    .newVision(model.getNewVision())
                    .newValue(model.getNewValue())
                    .build();
            entity = identityCanvasJpaRepository.save(entity);
        }
        return toModel(entity);
    }

    @Override
    public List<IdentityCanvasModel> findByUserIdIn(List<Long> userIds) {
        return identityCanvasJpaRepository.findByUserIdIn(userIds)
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsSubmittedByUserId(Long userId) {
        return identityCanvasJpaRepository.existsByUserIdAndSubmittedTrue(userId);
    }

    @Override
    public void submitByUserId(Long userId) {
        identityCanvasJpaRepository.findByUserId(userId)
                .ifPresent(entity -> {
                    entity.submit();
                    identityCanvasJpaRepository.save(entity);
                });
    }

    @Override
    public void rollbackByUserId(Long userId) {
        identityCanvasJpaRepository.findByUserId(userId)
                .ifPresent(entity -> {
                    entity.rollback();
                    identityCanvasJpaRepository.save(entity);
                });
    }

    private IdentityCanvasModel toModel(IdentityCanvas entity) {
        return IdentityCanvasModel.builder()
                .identityId(entity.getIdentityId())
                .mission(entity.getMission())
                .vision(entity.getVision())
                .value(entity.getValue())
                .macro(entity.getMacro())
                .tech(entity.getTech())
                .customer(entity.getCustomer())
                .competitor(entity.getCompetitor())
                .capability(entity.getCapability())
                .culture(entity.getCulture())
                .structure(entity.getStructure())
                .etc(entity.getEtc())
                .newMission(entity.getNewMission())
                .newVision(entity.getNewVision())
                .newValue(entity.getNewValue())
                .userId(entity.getUserId())
                .submitted(entity.getSubmitted())
                .build();
    }
}
