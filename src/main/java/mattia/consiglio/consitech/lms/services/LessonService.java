package mattia.consiglio.consitech.lms.services;

import mattia.consiglio.consitech.lms.entities.Lesson;
import mattia.consiglio.consitech.lms.entities.PublishStatus;
import mattia.consiglio.consitech.lms.exceptions.ResourceNotFoundException;
import mattia.consiglio.consitech.lms.payloads.NewLessonDTO;
import mattia.consiglio.consitech.lms.payloads.SeoDTO;
import mattia.consiglio.consitech.lms.payloads.UpdateLessonDTO;
import mattia.consiglio.consitech.lms.repositories.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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
        SeoDTO seoDTO = new SeoDTO(lessonDTO.title(), lessonDTO.description(), "", lessonDTO.mainLanguageId());
        Lesson lesson = new Lesson();
        lesson.setTitle(lessonDTO.title());
        lesson.setSlug(lessonDTO.slug());
        lesson.setDescription(lessonDTO.description());
        lesson.setThumbnail(mediaService.getMedia(lessonDTO.thumbnailId()));
        lesson.setMainLanguage(languageService.getLanguage(lessonDTO.mainLanguageId()));
        lesson.setLiveEditor(lessonDTO.liveEditor());
        lesson.setVideoUrl(lessonDTO.videoUrl());
        lesson.setContent(lessonDTO.content());
        lesson.setPublishStatus(PublishStatus.DRAFT);
        lesson.setDisplayOrder(lessonRepository.count() + 1);
        lesson.setCourse(courseService.getCourse(lessonDTO.courseId()));
        lesson.setSeo(seoService.createSeo(seoDTO));
        return lessonRepository.save(lesson);
    }

    public Lesson getLesson(UUID id) {
        return lessonRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lesson", id));
    }


    public Lesson updateLesson(UUID id, UpdateLessonDTO lessonDTO) {
        Lesson lesson = this.getLesson(id);
        lesson.setTitle(lessonDTO.title());
        lesson.setSlug(lessonDTO.slug());
        lesson.setDescription(lessonDTO.description());
        lesson.setThumbnail(mediaService.getMedia(lessonDTO.thumbnailId()));
        lesson.setLiveEditor(lessonDTO.liveEditor());
        lesson.setVideoUrl(lessonDTO.videoUrl());
        lesson.setContent(lessonDTO.content());
        lesson.setPublishStatus(PublishStatus.valueOf(lessonDTO.publishStatus()));
        lesson.setCourse(courseService.getCourse(lessonDTO.courseId()));
        return lessonRepository.save(lesson);
    }

    public List<Lesson> getLessons() {
        return lessonRepository.findAll();
    }

    public List<Lesson> getLessonsByCourse(UUID id) {
        return lessonRepository.findByCourseId(id);
    }


    public List<Lesson> getLessonsByCourseAndStatus(UUID id, PublishStatus status) {
        return lessonRepository.findByCourseIdAndPublishStatus(id, status);
    }


    public List<Lesson> getLessonsByCourseAndStatusAndLanguage(UUID id, PublishStatus status, UUID languageId) {
        return lessonRepository.findByCourseIdAndPublishStatusAndMainLanguageId(id, status, languageId);
    }

    public void deleteLesson(UUID id) {
        Lesson lesson = this.getLesson(id);
        lessonRepository.delete(lesson);
    }
}
