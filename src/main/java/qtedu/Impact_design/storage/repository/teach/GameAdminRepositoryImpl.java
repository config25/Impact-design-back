package qtedu.Impact_design.storage.repository.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.repository.teach.GameAdminRepository;
import qtedu.Impact_design.storage.jpaentity.teach.GameAdmin;
import qtedu.Impact_design.storage.jparepository.teach.GameAdminJpaRepository;

@Repository
@RequiredArgsConstructor
public class GameAdminRepositoryImpl implements GameAdminRepository {

    private final GameAdminJpaRepository gameAdminJpaRepository;

    @Override
    public void save(Integer gameId, Long userId) {
        GameAdmin gameAdmin = GameAdmin.builder()
                .gameId(gameId)
                .userId(userId)
                .build();
        gameAdminJpaRepository.save(gameAdmin);
    }
}
