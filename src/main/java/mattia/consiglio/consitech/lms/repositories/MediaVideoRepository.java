package mattia.consiglio.consitech.lms.repositories;

import mattia.consiglio.consitech.lms.entities.MediaVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MediaVideoRepository extends JpaRepository<MediaVideo, UUID> {
    List<MediaVideo> findByParentId(UUID mediaId);
}
