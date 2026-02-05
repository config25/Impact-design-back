package qtedu.Impact_design.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IdentityCanvasModel {
    private final Long identityId;
    private final String mission;
    private final String vision;
    private final String value;
    private final String macro;
    private final String tech;
    private final String customer;
    private final String competitor;
    private final String capability;
    private final String culture;
    private final String structure;
    private final String etc;
    private final String newMission;
    private final String newVision;
    private final String newValue;
    private final Long userId;
}
