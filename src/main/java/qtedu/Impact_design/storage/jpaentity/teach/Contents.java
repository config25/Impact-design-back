package qtedu.Impact_design.storage.jpaentity.teach;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "contents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Contents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contents_id")
    private Integer contentsId;

    @Column(name = "team_id")
    private Integer teamId;

    @Column(name = "game_id")
    private Integer gameId;

    @Column(name = "type")
    private Integer type;

    @Column(name = "subject", length = 255)
    private String subject;

    @Column(name = "detail", columnDefinition = "TEXT")
    private String detail;

    @Column(name = "reg_date")
    private LocalDate regDate;

    @Column(name = "writer", length = 100)
    private String writer;

    @Column(name = "ext_dir", length = 255)
    private String extDir;

    @Column(name = "orgfilenm", length = 255)
    private String orgFilename;

    @Column(name = "newfilenm", length = 255)
    private String newFilename;

    @Column(name = "st_tp")
    private Integer statusType;

    @Column(name = "world_id")
    private Integer worldId;
}
