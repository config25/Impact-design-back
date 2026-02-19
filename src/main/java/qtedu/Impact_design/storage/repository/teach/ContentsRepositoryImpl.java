package qtedu.Impact_design.storage.repository.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.team.ContentsModel;
import qtedu.Impact_design.domain.repository.teach.ContentsRepository;
import qtedu.Impact_design.storage.jpaentity.teach.Contents;
import qtedu.Impact_design.storage.jparepository.teach.ContentsJpaRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ContentsRepositoryImpl implements ContentsRepository {

    private final ContentsJpaRepository contentsJpaRepository;

    @Override
    public Optional<ContentsModel> findGameLogo(Integer gameId) {
        return contentsJpaRepository.findFirstByStatusTypeAndWorldId(1, gameId)
                .map(this::toModel);
    }

    private ContentsModel toModel(Contents entity) {
        return ContentsModel.builder()
                .contentsId(entity.getContentsId())
                .subject(entity.getSubject())
                .extDir(entity.getExtDir())
                .orgFilename(entity.getOrgFilename())
                .newFilename(entity.getNewFilename())
                .build();
    }
}
