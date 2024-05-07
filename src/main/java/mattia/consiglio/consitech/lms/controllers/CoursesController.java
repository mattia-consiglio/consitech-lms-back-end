package mattia.consiglio.consitech.lms.controllers;

import mattia.consiglio.consitech.lms.entities.Course;
import mattia.consiglio.consitech.lms.payloads.NewCourseDTO;
import mattia.consiglio.consitech.lms.payloads.UpdateCourseDTO;
import mattia.consiglio.consitech.lms.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static mattia.consiglio.consitech.lms.controllers.BaseController.BASE_URL;
import static mattia.consiglio.consitech.lms.utities.GeneralChecks.checkUUID;

@RestController
@RequestMapping(BASE_URL + "/courses")
public class CoursesController {
    @Autowired
    private CourseService courseService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Course createCourse(NewCourseDTO course) {
        return courseService.createCourse(course);
    }

    @GetMapping
    public Page<Course> getCourses(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6") int size, @RequestParam(defaultValue = "createdAt") String sort) {
        return courseService.getAllCourses(page, size, sort);
    }

    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable("id") String id) {
        UUID uuid = checkUUID(id, "id");
        return courseService.getCourse(uuid);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Course updateCourse(@PathVariable("id") String id, @RequestBody UpdateCourseDTO course) {
        UUID uuid = checkUUID(id, "id");
        return courseService.updateCourse(uuid, course);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourse(@PathVariable("id") String id) {
        UUID uuid = checkUUID(id, "id");
        courseService.deleteCourse(uuid);
    }
}
