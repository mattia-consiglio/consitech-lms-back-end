package mattia.consiglio.consitech.lms.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import mattia.consiglio.consitech.lms.entities.Course;
import mattia.consiglio.consitech.lms.entities.Lesson;
import mattia.consiglio.consitech.lms.services.CourseService;
import mattia.consiglio.consitech.lms.utils.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static mattia.consiglio.consitech.lms.controllers.BaseController.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/public/courses")
public class PublicCoursesController {
    @Autowired
    private CourseService courseService;

    @GetMapping
    public Page<Course> getCourses(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "6") int size,
                                   @RequestParam(defaultValue = "displayOrder") String sort,
                                   @RequestParam(defaultValue = "it") String lang) {
        return courseService.getAllCourses(page, size, sort, lang);
    }

    @GetMapping("slug/{slug}")
    public Course getCourseBySlug(@PathVariable("slug") String slug) {
        return courseService.getCourseBySlug(slug);
    }

    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable("id") String id) {
        return courseService.getCourse(id);
    }

    @GetMapping("/slug/{slug}/lessons")
    @JsonView(View.Public.class)
    public List<Lesson> getLessonsByCourseSlug(@PathVariable("slug") String slug) {
        return courseService.getLessonsByCourseSlug(slug);
    }

    @GetMapping("/{id}/lessons")
    public List<Lesson> getLessonsByCourseId(@PathVariable("id") String id) {
        return courseService.getLessonsByCourseId(id);
    }
}
