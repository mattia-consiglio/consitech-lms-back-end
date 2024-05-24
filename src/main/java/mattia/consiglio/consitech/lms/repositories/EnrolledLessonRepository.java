package mattia.consiglio.consitech.lms.repositories;

import mattia.consiglio.consitech.lms.entities.EnrolledLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrolledLessonRepository extends JpaRepository<EnrolledLesson, UUID> {
    Optional<EnrolledLesson> findByLessonIdAndUserId(UUID lessonId, UUID userId);
}
