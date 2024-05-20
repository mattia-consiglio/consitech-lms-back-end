package mattia.consiglio.consitech.lms.services;

import mattia.consiglio.consitech.lms.entities.Course;
import mattia.consiglio.consitech.lms.entities.Lesson;
import mattia.consiglio.consitech.lms.entities.PublishStatus;
import mattia.consiglio.consitech.lms.entities.UserRole;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.exceptions.ResourceNotFoundException;
import mattia.consiglio.consitech.lms.payloads.NewLessonDTO;
import mattia.consiglio.consitech.lms.payloads.NewSeoDTO;
import mattia.consiglio.consitech.lms.payloads.UpdateLessonDTO;
import mattia.consiglio.consitech.lms.repositories.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class LessonService {
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private MediaService mediaService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private LanguageService languageService;
    @Autowired
    private SeoService seoService;

    @Autowired
    private UserService userService;


    public Lesson createLesson(NewLessonDTO lessonDTO) {
        if (lessonRepository.existsBySlugAndMainLanguageId(lessonDTO.slug(), lessonDTO.mainLanguageId())) {
            throw new BadRequestException("Lesson slug already exists");
        }
        NewSeoDTO newSeoDTO = new NewSeoDTO(lessonDTO.title(), lessonDTO.description(), "", lessonDTO.mainLanguageId());
        Lesson lesson = new Lesson();
        if (lessonDTO.thumbnailId() != null) {
            lesson.setThumbnail(mediaService.getMedia(lessonDTO.thumbnailId()));
        }
        lesson.setTitle(lessonDTO.title());
        lesson.setSlug(lessonDTO.slug());
        lesson.setDescription(lessonDTO.description());
        lesson.setMainLanguage(languageService.getLanguage(lessonDTO.mainLanguageId()));
        lesson.setLiveEditor(lessonDTO.liveEditor());
        lesson.setVideoId(lessonDTO.videoId());
        lesson.setContent(lessonDTO.content());
        lesson.setPublishStatus(PublishStatus.DRAFT);
        lesson.setDisplayOrder(lessonRepository.count() + 1);
        lesson.setCourse(courseService.getCourse(lessonDTO.courseId()));
        lesson.setSeo(seoService.createSeo(newSeoDTO));
        return lessonRepository.save(lesson);
    }

    public Lesson getLesson(UUID id) {
        Lesson lesson = lessonRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lesson", id));
        if (!hasAuthority(UserRole.ADMIN.name())) {
            if (lesson.getPublishStatus() != PublishStatus.PUBLIC) {
                throw new ResourceNotFoundException("Lesson", id.toString());
            }
        }
        return lesson;
    }

    public Lesson getLesson(String id) {
        UUID uuid = checkUUID(id, "lesson id");
        return this.getLesson(uuid);
    }

    public Lesson getLessonBySlug(String slug, String lang) {
        Lesson lesson = lessonRepository.findBySlugAndMainLanguageCode(slug, lang).orElseThrow(() -> new ResourceNotFoundException("Lesson", slug));
        if (!hasAuthority(UserRole.ADMIN.name())) {
            if (lesson.getPublishStatus() != PublishStatus.PUBLIC) {
                throw new ResourceNotFoundException("Lesson", "slug", slug);
            }
        }
        return lesson;
    }

    public Lesson updateLesson(String id, UpdateLessonDTO lessonDTO) {
        UUID uuid = checkUUID(id, "lesson id");
        return this.updateLesson(uuid, lessonDTO);
    }

    public Lesson updateLesson(UUID id, UpdateLessonDTO lessonDTO) {
        Lesson lesson = this.getLesson(id);
        if (lessonRepository.existsBySlugAndMainLanguageId(lessonDTO.slug(), lesson.getMainLanguage().getId()) && !lesson.getSlug().equals(lessonDTO.slug())) {
            throw new BadRequestException("Lesson slug already exists");
        }
        if (lessonDTO.publishStatus() != null && lessonDTO.publishStatus().equals(PublishStatus.PUBLIC.name())) {
            List<String> requiredField = new ArrayList<>();
            if (lessonDTO.content() == null) {
                requiredField.add("content");
            }
            if (lessonDTO.thumbnailId() == null) {
                requiredField.add("thumbnail");
            }

        }
        if (lessonDTO.thumbnailId() != null) {
            lesson.setThumbnail(mediaService.getMedia(lessonDTO.thumbnailId()));
        }
        if (lesson.getCreatedAt() == null) {
            lesson.setCreatedAt(LocalDateTime.now());
        }
        lesson.setTitle(lessonDTO.title());
        lesson.setSlug(lessonDTO.slug());
        lesson.setDescription(lessonDTO.description());
        lesson.setLiveEditor(lessonDTO.liveEditor());
        lesson.setVideoId(lessonDTO.videoId());
        lesson.setContent(lessonDTO.content());
        lesson.setPublishStatus(PublishStatus.valueOf(lessonDTO.publishStatus()));
        lesson.setCourse(courseService.getCourse(lessonDTO.courseId()));
        lesson.setCreatedAt(LocalDateTime.now());
        return lessonRepository.save(lesson);
    }

    public List<Lesson> getLessons(String lang) {
        List<PublishStatus> publishStatus = new ArrayList<>(List.of(PublishStatus.PUBLIC));
        if (hasAuthority(UserRole.ADMIN.name())) {
            publishStatus.add(PublishStatus.DRAFT);
        }
        return lessonRepository.findByPublishStatusAndMainLanguageCode(publishStatus, lang);
    }

    public Page<Lesson> getAllLessons(int page, int size, String sort, String direction) {
        Sort.Direction sortDirection = Sort.Direction.ASC; // Default sort direction

        if (direction != null && direction.equalsIgnoreCase("desc")) {
            sortDirection = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        return lessonRepository.findAll(pageable);
    }

    public List<Lesson> getLessonsByCourse(String courseId, String lang) {
        UUID uuid = checkUUID(courseId, "course id");
        return this.getLessonsByCourse(uuid, lang);
    }

    public List<Lesson> getLessonsByCourse(UUID courseId, String lang) {
        Course course = courseService.getCourse(courseId);
        List<PublishStatus> publishStatus = new ArrayList<>(List.of(PublishStatus.PUBLIC));
        if (hasAuthority(UserRole.ADMIN.name())) {
            publishStatus.add(PublishStatus.DRAFT);
        }
        return lessonRepository.findByCourseAndPublishStatusInAndMainLanguageCode(course, publishStatus, lang);
    }

    public List<Lesson> getLessonsByCourseAndStatus(UUID id, PublishStatus status) {
        return lessonRepository.findByCourseIdAndPublishStatus(id, status);
    }


    public List<Lesson> getLessonsByCourseAndStatusAndLanguage(UUID id, PublishStatus status, UUID languageId) {
        return lessonRepository.findByCourseIdAndPublishStatusAndMainLanguageId(id, status, languageId);
    }

    public void deleteLesson(String id) {
        UUID uuid = checkUUID(id, "id");
        this.deleteLesson(uuid);
    }

    public void deleteLesson(UUID id) {
        Lesson lesson = this.getLesson(id);
        lessonRepository.delete(lesson);
    }
}
