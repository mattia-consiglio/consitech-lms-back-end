package mattia.consiglio.consitech.lms.repositories;

import mattia.consiglio.consitech.lms.entities.AbstractContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AbstractContentRepository extends JpaRepository<AbstractContent, UUID> {
}
