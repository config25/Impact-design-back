package qtedu.Impact_design.storage.jpaentity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "identity_canvas")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class IdentityCanvas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identity_id")
    private Long identityId;

    @Column(name = "mission", length = 255)
    private String mission;

    @Column(name = "vision", length = 255)
    private String vision;

    @Column(name = "value", length = 255)
    private String value;

    @Column(name = "macro", length = 255)
    private String macro;

    @Column(name = "tech", length = 255)
    private String tech;

    @Column(name = "customer", length = 255)
    private String customer;

    @Column(name = "competitor", length = 255)
    private String competitor;

    @Column(name = "capability", length = 255)
    private String capability;

    @Column(name = "culture", length = 255)
    private String culture;

    @Column(name = "structure", length = 255)
    private String structure;

    @Column(name = "etc", length = 255)
    private String etc;

    @Column(name = "new_mission", length = 255)
    private String newMission;

    @Column(name = "new_vision", length = 255)
    private String newVision;

    @Column(name = "new_value", length = 255)
    private String newValue;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    public void update(String mission, String vision, String value,
                       String macro, String tech, String customer, String competitor,
                       String capability, String culture, String structure, String etc,
                       String newMission, String newVision, String newValue) {
        this.mission = mission;
        this.vision = vision;
        this.value = value;
        this.macro = macro;
        this.tech = tech;
        this.customer = customer;
        this.competitor = competitor;
        this.capability = capability;
        this.culture = culture;
        this.structure = structure;
        this.etc = etc;
        this.newMission = newMission;
        this.newVision = newVision;
        this.newValue = newValue;
    }
}
