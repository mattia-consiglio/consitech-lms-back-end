package mattia.consiglio.consitech.lms.controllers;

import mattia.consiglio.consitech.lms.entities.Lesson;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.payloads.NewLessonDTO;
import mattia.consiglio.consitech.lms.payloads.UpdateLessonDTO;
import mattia.consiglio.consitech.lms.services.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static mattia.consiglio.consitech.lms.controllers.BaseController.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/lessons")
public class LessonsController {
    @Autowired
    private LessonService lessonService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Lesson createLesson(@Validated @RequestBody NewLessonDTO lesson, BindingResult validation) {
        if (validation.hasErrors()) {
            throw new BadRequestException("Invalid data", validation.getAllErrors());
        }
        return lessonService.createLesson(lesson);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<Lesson> getLessons(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "6") int size,
                                   @RequestParam(defaultValue = "displayOrder") String sort,
                                   @RequestParam(defaultValue = "asc") String direction) {
        return lessonService.getAllLessons(page, size, sort, direction);
    }

    @GetMapping("/course/{courseId}")
    public List<Lesson> getLessonsByCourseId(@PathVariable("courseId") String courseId, @RequestParam(defaultValue = "it") String lang) {
        return lessonService.getLessonsByCourse(courseId, lang);
    }

    @GetMapping("/slug/{slug}")
    public Lesson getLessonBySlug(@PathVariable("slug") String slug, @RequestParam(defaultValue = "it") String lang) {
        return lessonService.getLessonBySlug(slug, lang);
    }

    @GetMapping("/{id}")
    public Lesson getLessonById(@PathVariable("id") String id) {
        return lessonService.getLesson(id);
    }


    @PutMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Lesson updateLesson(@PathVariable("id") String id, @Validated @RequestBody UpdateLessonDTO lesson, BindingResult validation) {
        if (validation.hasErrors()) {
            throw new BadRequestException("Invalid data", validation.getAllErrors());
        }
        return lessonService.updateLesson(id, lesson);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLesson(@PathVariable("id") String id) {
        lessonService.deleteLesson(id);
    }
}
