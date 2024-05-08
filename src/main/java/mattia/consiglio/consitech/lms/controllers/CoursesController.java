package mattia.consiglio.consitech.lms.controllers;

import mattia.consiglio.consitech.lms.entities.Course;
import mattia.consiglio.consitech.lms.entities.PublishStatus;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.payloads.NewCourseDTO;
import mattia.consiglio.consitech.lms.payloads.UpdateCourseDTO;
import mattia.consiglio.consitech.lms.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static mattia.consiglio.consitech.lms.controllers.BaseController.BASE_URL;
import static mattia.consiglio.consitech.lms.utils.GeneralChecks.checkUUID;
import static mattia.consiglio.consitech.lms.utils.SecurityUtils.hasAuthority;

@RestController
@RequestMapping(BASE_URL + "/courses")
public class CoursesController {
    @Autowired
    private CourseService courseService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Course createCourse(@Validated @RequestBody NewCourseDTO course, BindingResult validation) {
        if (validation.hasErrors()) {
            throw new BadRequestException("Invalid data", validation.getAllErrors());
        }
        return courseService.createCourse(course);
    }

    @GetMapping
    public ResponseEntity<?> getCourses(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "6") int size,
                                        @RequestParam(defaultValue = "displayOrder") String sort,
                                        @RequestParam(defaultValue = "it") String lang) {
        if (hasAuthority("ADMIN")) {
            return ResponseEntity.ok(courseService.getAllCourses(page, size, sort, lang, List.of(PublishStatus.values())));
        }
        return ResponseEntity.ok(courseService.getAllCourses(page, size, sort, lang, List.of(PublishStatus.PUBLIC)));
    }

    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable("id") String id) {
        UUID uuid = checkUUID(id, "id");
        return courseService.getCourse(uuid);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Course updateCourse(@PathVariable("id") String id, @Validated @RequestBody UpdateCourseDTO course, BindingResult validation) {
        UUID uuid = checkUUID(id, "id");
        if (validation.hasErrors()) {
            throw new BadRequestException("Invalid data", validation.getAllErrors());
        }
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