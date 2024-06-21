package mattia.consiglio.consitech.lms.repositories;

import mattia.consiglio.consitech.lms.entities.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<Media, UUID> {
    List<Media> findByHash(String hash);

    List<Media> findByHashOrderByFilenameDesc(String hash);

    List<Media> findByHashAndParentIdNotNull(String hash);

    Optional<Media> findByFilename(String filename);

    @Query(value = "SELECT m FROM Media m WHERE m.parentId = :hash ORDER BY m.filename DESC LIMIT 1")
    Optional<Media> findLastReference(String hash);
}
