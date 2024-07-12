package mattia.consiglio.consitech.lms.repositories;

import mattia.consiglio.consitech.lms.entities.Media;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<Media, UUID> {
    List<Media> findByHashOrderByFilenameDesc(String hash);

    List<Media> findByParentId(UUID parentId);

    Optional<Media> findByFilename(String filename);

    Page<Media> findAll(Specification<Media> spec, Pageable pageable);
}
