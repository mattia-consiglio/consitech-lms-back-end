package mattia.consiglio.consitech.lms.repositories;

import mattia.consiglio.consitech.lms.entities.VideoResolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoResolutionsRepository extends JpaRepository<VideoResolution, String> {
}
