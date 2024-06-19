package mattia.consiglio.consitech.lms.repositories;

import mattia.consiglio.consitech.lms.entities.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<Media, UUID> {
//    Media findByCloudinaryPublicId(String publicId);
}
