package mattia.consiglio.consitech.lms.services;

import mattia.consiglio.consitech.lms.entities.Course;
import mattia.consiglio.consitech.lms.entities.Language;
import mattia.consiglio.consitech.lms.entities.Media;
import mattia.consiglio.consitech.lms.entities.PublishStatus;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.payloads.NewCourseDTO;
import mattia.consiglio.consitech.lms.payloads.SeoDTO;
import mattia.consiglio.consitech.lms.payloads.UpdateCourseDTO;
import mattia.consiglio.consitech.lms.repositories.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SeoService seoService;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private MediaService mediaService;


    public Course getCourse(UUID id) {
        return courseRepository.findById(id).orElseThrow(() -> new BadRequestException("Course not found"));
    }

    public Course createCourse(NewCourseDTO newCourseDTO) {
        Language language = languageService.getLanguage(newCourseDTO.mainLanguageId());
        SeoDTO seoDTO = new SeoDTO(newCourseDTO.title(), newCourseDTO.description(), "", newCourseDTO.mainLanguageId());
        Media thumbnail = null;
        if (newCourseDTO.thumbnailId() != null) {
            thumbnail = mediaService.getMedia(newCourseDTO.thumbnailId());
        }
        Course course = new Course();
        course.setTitle(newCourseDTO.title());
        course.setDescription(newCourseDTO.description());
        course.setSlug(newCourseDTO.slug());
        course.setMainLanguage(language);
        course.setSeo(seoService.createSeo(seoDTO));
        course.setEnrolledStudents(0);
        course.setThumbnail(thumbnail);
        course.setDisplayOrder(courseRepository.count() + 1);
        course.setPublishStatus(PublishStatus.DRAFT);
        course.setCreatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }

    public Page<Course> getAllCourses(int page, int size, String sort, String lang, List<PublishStatus> publishStatus) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return courseRepository.findByLanguageAndPublishStatus(pageable, lang, publishStatus);
    }

    public Course updateCourse(UUID id, UpdateCourseDTO courseDTO) {
        Media thumbnail = null;
        if (courseDTO.thumbnailId() != null) {
            thumbnail = mediaService.getMedia(courseDTO.thumbnailId());
        }
        Course course = this.getCourse(id);
        course.setTitle(courseDTO.title());
        course.setDescription(courseDTO.description());
        course.setSlug(courseDTO.slug());
        course.setThumbnail(thumbnail);
        course.setPublishStatus(PublishStatus.valueOf(courseDTO.publishStatus()));
        return courseRepository.save(course);
    }

    public void deleteCourse(UUID id) {
        Course course = this.getCourse(id);
        seoService.deleteSeo(course.getSeo().getId());
        courseRepository.delete(course);
    }
}
