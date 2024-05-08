package mattia.consiglio.consitech.lms.repositories;

import mattia.consiglio.consitech.lms.entities.Lesson;
import mattia.consiglio.consitech.lms.entities.PublishStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID> {

    public List<Lesson> findByCourseId(UUID courseId);

    @Query(value = "SELECT l FROM Lesson l WHERE l.course.id = :courseId AND l.publishStatus = :status")
    public List<Lesson> findByCourseIdAndPublishStatus(UUID courseId, PublishStatus status);

    @Query(value = "SELECT l FROM Lesson l WHERE l.course.id = :courseId AND l.publishStatus = :status AND l.mainLanguage.id = :mainLanguageId")
    public List<Lesson> findByCourseIdAndPublishStatusAndMainLanguageId(UUID courseId, PublishStatus status, UUID mainLanguageId);
}
