package mattia.consiglio.consitech.lms.controllers;

import mattia.consiglio.consitech.lms.entities.Lesson;
import mattia.consiglio.consitech.lms.services.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static mattia.consiglio.consitech.lms.controllers.BaseController.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/public/lessons")
public class PublicLessonController {
    @Autowired
    private LessonService lessonService;

    @GetMapping("/course/{courseId}")
    public List<Lesson> getLessons(@PathVariable("courseId") String courseId, @RequestParam(defaultValue = "it") String lang) {
        return lessonService.getLessonsByCourse(courseId, lang);
    }


    @GetMapping("/{id}")
    public Lesson getLessonById(@PathVariable("id") String id) {
        return lessonService.getLesson(id);
    }


    @GetMapping("/slug/{slug}")
    public Lesson getLessonBySlug(@PathVariable("slug") String slug, @RequestParam(defaultValue = "it") String lang) {
        return lessonService.getLessonBySlug(slug, lang);
    }

}
