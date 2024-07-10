package mattia.consiglio.consitech.lms.services;

import lombok.RequiredArgsConstructor;
import mattia.consiglio.consitech.lms.entities.*;
import mattia.consiglio.consitech.lms.entities.enums.PublishStatus;
import mattia.consiglio.consitech.lms.entities.enums.UserRole;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.exceptions.ResourceNotFoundException;
import mattia.consiglio.consitech.lms.payloads.NewCourseDTO;
import mattia.consiglio.consitech.lms.payloads.NewSeoDTO;
import mattia.consiglio.consitech.lms.payloads.UpdateCourseDTO;
import mattia.consiglio.consitech.lms.repositories.CourseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static mattia.consiglio.consitech.lms.utils.GeneralChecks.checkUUID;
import static mattia.consiglio.consitech.lms.utils.SecurityUtils.hasAuthority;

@RequiredArgsConstructor
@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final SeoService seoService;
    private final LanguageService languageService;
    private final MediaService mediaService;

    public Course getCourse(UUID uuid) {
        Course course = courseRepository.findById(uuid).orElseThrow(() -> new ResourceNotFoundException("Course", uuid.toString()));
        if (!hasAuthority(UserRole.ADMIN.name())) {
            if (course.getPublishStatus() != PublishStatus.PUBLIC) {
                throw new ResourceNotFoundException("Course", uuid);
            }
        }
        return course;
    }

    public Course getCourse(String id) {
        UUID uuid = checkUUID(id, "course id");
        return this.getCourse(uuid);
    }


    public Course getCourseBySlug(String slug) {
        Course course = courseRepository.findBySlug(slug).orElseThrow(() -> new ResourceNotFoundException("Course", slug));
        if (!hasAuthority(UserRole.ADMIN.name())) {
            if (course.getPublishStatus() != PublishStatus.PUBLIC) {
                throw new ResourceNotFoundException("Course", "slug", slug);
            }
        }
        return course;
    }

    public Course createCourse(NewCourseDTO newCourseDTO) {
        if (courseRepository.existsBySlug(newCourseDTO.slug())) {
            throw new BadRequestException("Course slug already exists");
        }
        Language language = languageService.getLanguage(newCourseDTO.mainLanguageId());
        NewSeoDTO newSeoDTO = new NewSeoDTO(newCourseDTO.title(), newCourseDTO.description(), "", newCourseDTO.mainLanguageId());
        MediaImage thumbnail = null;
        if (newCourseDTO.thumbnailId() != null) {
            thumbnail = (MediaImage) mediaService.getMedia(newCourseDTO.thumbnailId());
        }
        Course course = new Course();
        course.setTitle(newCourseDTO.title());
        course.setDescription(newCourseDTO.description());
        course.setSlug(newCourseDTO.slug());
        course.setMainLanguage(language);
        course.setSeo(seoService.createSeo(newSeoDTO));
        course.setEnrolledStudents(0);
        course.setThumbnailImage(thumbnail);
        course.setDisplayOrder(courseRepository.count() + 1);
        course.setPublishStatus(PublishStatus.DRAFT);
        course.setCreatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }

    public Page<Course> getAllCourses(int page, int size, String sort, String lang) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        List<PublishStatus> publishStatus = getPublishStatusList();
        return courseRepository.findByLanguageAndPublishStatus(pageable, lang, publishStatus);
    }

    public List<Course> getAllCourses(String lang) {
        List<PublishStatus> publishStatus = getPublishStatusList();
        return courseRepository.findByLanguageAndPublishStatus(lang, publishStatus);
    }

    private List<PublishStatus> getPublishStatusList() {
        List<PublishStatus> publishStatus = new ArrayList<>(List.of(PublishStatus.PUBLIC));
        if (hasAuthority(UserRole.ADMIN.name())) {
            publishStatus.add(PublishStatus.DRAFT);
        }
        return publishStatus;
    }


    public List<Lesson> getLessonsByCourseId(String courseId) {
        Course course = this.getCourse(courseId);
        return filterLessons(course);
    }

    public List<Lesson> getLessonsByCourseSlug(String slug) {
        Course course = this.getCourseBySlug(slug);
        return filterLessons(course);
    }

    private List<Lesson> filterLessons(Course course) {
        if (hasAuthority(UserRole.ADMIN.name())) {
            return course.getLessons();
        } else {
            return course.getLessons().stream()
                    .filter(lesson -> lesson.getPublishStatus() == PublishStatus.PUBLIC)
                    .toList();
        }
    }

    public Course updateCourse(UUID id, UpdateCourseDTO courseDTO) {

        MediaImage thumbnail = null;
        if (courseDTO.thumbnailId() != null) {
            thumbnail = (MediaImage) mediaService.getMedia(courseDTO.thumbnailId());
        }
        Course course = this.getCourse(id);
        if (courseRepository.existsBySlug(courseDTO.slug()) && !course.getSlug().equals(courseDTO.slug())) {
            throw new BadRequestException("Course slug already exists");
        }
        if (courseDTO.publishStatus() != null && courseDTO.publishStatus().equals(PublishStatus.PUBLIC.name()) && courseDTO.thumbnailId() == null) {
            throw new BadRequestException("Thumbnail is required for public courses");
        }

        course.setTitle(courseDTO.title());
        course.setDescription(courseDTO.description());
        course.setSlug(courseDTO.slug());
        course.setThumbnailImage(thumbnail);
        course.setPublishStatus(PublishStatus.valueOf(courseDTO.publishStatus()));
        return courseRepository.save(course);
    }

    public Course trashCourse(String id) {
        UUID uuid = checkUUID(id, "course id");
        return this.trashCourse(uuid);
    }

    public Course trashCourse(UUID id) {
        Course course = this.getCourse(id);
        course.setPublishStatus(PublishStatus.TRASHED);
        return courseRepository.save(course);
    }

    public void deleteCourse(String id) {
        UUID uuid = checkUUID(id, "course id");
        this.deleteCourse(uuid);
    }

    public void deleteCourse(UUID id) {
        Course course = this.getCourse(id);
        courseRepository.delete(course);
        seoService.deleteSeo(course.getSeo().getId());
    }
}
