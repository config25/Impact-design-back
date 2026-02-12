package qtedu.Impact_design.api.dto.response.identitycanvas;

import lombok.Builder;
import lombok.Getter;
import qtedu.Impact_design.domain.model.IdentityCanvasModel;

@Getter
@Builder
public class IdentityCanvasResponse {
    private Long identityId;
    private String mission;
    private String vision;
    private String value;
    private String macro;
    private String tech;
    private String customer;
    private String competitor;
    private String capability;
    private String culture;
    private String structure;
    private String etc;
    private String newMission;
    private String newVision;
    private String newValue;
    private Long userId;
    private Boolean submitted;
    private String imageUrl;

    public static IdentityCanvasResponse from(IdentityCanvasModel model, String imageUrl) {
        return IdentityCanvasResponse.builder()
                .identityId(model.getIdentityId())
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
                .userId(model.getUserId())
                .submitted(model.getSubmitted())
                .imageUrl(imageUrl)
                .build();
    }
}
