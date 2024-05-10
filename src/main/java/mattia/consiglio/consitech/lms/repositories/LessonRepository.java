package mattia.consiglio.consitech.lms.repositories;

import mattia.consiglio.consitech.lms.entities.Course;
import mattia.consiglio.consitech.lms.entities.Lesson;
import mattia.consiglio.consitech.lms.entities.PublishStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID> {

    List<Lesson> findByCourseAndPublishStatusInAndMainLanguageCode(Course course, List<PublishStatus> status, String languageCode);

    @Query(value = "SELECT l FROM Lesson l WHERE l.course.id = :courseId AND l.publishStatus = :status")
    List<Lesson> findByCourseIdAndPublishStatus(UUID courseId, PublishStatus status);

    @Query(value = "SELECT l FROM Lesson l WHERE l.course.id = :courseId AND l.publishStatus = :status AND l.mainLanguage.id = :mainLanguageId")
    List<Lesson> findByCourseIdAndPublishStatusAndMainLanguageId(UUID courseId, PublishStatus status, UUID mainLanguageId);

    Optional<Lesson> findBySlug(String slug);

    @Query(value = "SELECT l FROM Lesson l WHERE l.publishStatus IN :status AND l.mainLanguage.code = :mainLanguageCode")
    List<Lesson> findByPublishStatusAndMainLanguageCode(List<PublishStatus> status, String mainLanguageCode);

    Optional<Lesson> findBySlugAndMainLanguageCode(String slug, String mainLanguageCode);

    boolean existsBySlugAndMainLanguageId(String slug, UUID mainLanguageId);
}
