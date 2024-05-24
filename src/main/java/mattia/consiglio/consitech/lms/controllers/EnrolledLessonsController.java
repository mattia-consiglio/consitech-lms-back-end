package mattia.consiglio.consitech.lms.controllers;

import mattia.consiglio.consitech.lms.entities.EnrolledLesson;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.payloads.UpdateEnrolledLessonsDTO;
import mattia.consiglio.consitech.lms.services.EnrolledLessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static mattia.consiglio.consitech.lms.controllers.BaseController.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/enrolled-lessons")
public class EnrolledLessonsController {
    @Autowired
    EnrolledLessonService enrolledLessonService;


    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public EnrolledLesson getEnrolledLesson(@PathVariable("id") String id) {
        return enrolledLessonService.getEnrolledLesson(id);
    }


    @PutMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public EnrolledLesson updateEnrolledLesson(@PathVariable("id") String id, @RequestBody @Validated UpdateEnrolledLessonsDTO updateEnrolledLessonsDTO, BindingResult validation) {
        if (validation.hasErrors()) {
            throw new BadRequestException("Invalid data", validation.getAllErrors());
        }
        return enrolledLessonService.updateEnrolledLesson(id, updateEnrolledLessonsDTO);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEnrolledLesson(@PathVariable("id") String id) {
        enrolledLessonService.deleteEnrolledLesson(id);
    }
}
