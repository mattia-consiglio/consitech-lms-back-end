package mattia.consiglio.consitech.lms.services;

import mattia.consiglio.consitech.lms.entities.Course;
import mattia.consiglio.consitech.lms.entities.Language;
import mattia.consiglio.consitech.lms.entities.Media;
import mattia.consiglio.consitech.lms.entities.Seo;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.payloads.NewCourseDTO;
import mattia.consiglio.consitech.lms.payloads.UpdateCourseDTO;
import mattia.consiglio.consitech.lms.repositories.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
        Language language = languageService.getLanguage(newCourseDTO.languageId());
        Seo seo = seoService.getSeo(newCourseDTO.seoId());
        Media thumbnail = null;
        if (newCourseDTO.thumbnailId() != null) {
            thumbnail = mediaService.getMedia(newCourseDTO.thumbnailId());
        }
        Course course = new Course();
        course.setTitle(newCourseDTO.title());
        course.setDescription(newCourseDTO.description());
        course.setMainlanguage(language);
        course.setSeo(seo);
        course.setEnrolledStudents(0);
        course.setThumbnail(thumbnail);
        return courseRepository.save(course);
    }

    public Page<Course> getAllCourses(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return courseRepository.findAll(pageable);
    }

    public Course updateCourse(UUID id, UpdateCourseDTO courseDTO) {
        Media thumbnail = null;
        if (courseDTO.thumbnailId() != null) {
            thumbnail = mediaService.getMedia(courseDTO.thumbnailId());
        }
        Course course = this.getCourse(id);
        course.setTitle(courseDTO.title());
        course.setDescription(courseDTO.description());
        course.setThumbnail(thumbnail);
        return courseRepository.save(course);
    }

    public void deleteCourse(UUID id) {
        Course course = this.getCourse(id);
        courseRepository.delete(course);
    }
}
