package mattia.consiglio.consitech.lms.repositories;

import mattia.consiglio.consitech.lms.entities.MediaVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MediaVideoRepository extends JpaRepository<MediaVideo, UUID> {

}
