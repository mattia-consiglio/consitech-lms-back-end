package mattia.consiglio.consitech.lms.repositories;

import mattia.consiglio.consitech.lms.entities.Course;
import mattia.consiglio.consitech.lms.entities.PublishStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    //    @Query("SELECT c FROM Course c LEFT JOIN c.translations t WHERE (LOWER(c.mainLanguage.code) = LOWER(:languageCode) AND c.publishStatus IN :publishStatus) " +
//            "OR (LOWER(t.translationLanguage.code) = LOWER(:languageCode) AND t.publishStatus IN :publishStatus)")
//    Page<Course> findByLanguageAndPublishStatus(Pageable pageable, String languageCode, List<PublishStatus> publishStatus);
    @Query("SELECT c FROM Course c  WHERE LOWER(c.mainLanguage.code) = LOWER(:languageCode) AND c.publishStatus IN :publishStatus")
    Page<Course> findByLanguageAndPublishStatus(Pageable pageable, String languageCode, List<PublishStatus> publishStatus);
}
