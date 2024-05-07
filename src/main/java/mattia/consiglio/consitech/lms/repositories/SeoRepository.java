package mattia.consiglio.consitech.lms.repositories;

import mattia.consiglio.consitech.lms.entities.Seo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SeoRepository extends JpaRepository<Seo, UUID> {
}
