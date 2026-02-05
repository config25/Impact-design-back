package qtedu.Impact_design.storage.jparepository.teach;

import java.time.LocalDateTime;

public interface ClassInfoProjection {
    Integer getGameId();
    String getName();
    Integer getNumTeam();
    Integer getTotalDd();
    Integer getMissionId();
    Integer getSequence();
    String getSubject();
    String getSummary();
    LocalDateTime getStartdate();
    LocalDateTime getEnddate();
    Integer getDdYear();
    Integer getDdTerm();
}
