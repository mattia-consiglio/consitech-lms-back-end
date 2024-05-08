package mattia.consiglio.consitech.lms.controllers;

import mattia.consiglio.consitech.lms.entities.Lesson;
import mattia.consiglio.consitech.lms.payloads.NewLessonDTO;
import mattia.consiglio.consitech.lms.services.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static mattia.consiglio.consitech.lms.controllers.BaseController.BASE_URL;
import static mattia.consiglio.consitech.lms.utils.GeneralChecks.checkUUID;

@RestController
@RequestMapping(BASE_URL + "/lessons")
public class LessonsController {
    @Autowired
    private LessonService lessonService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Lesson createLesson(@RequestBody NewLessonDTO lesson) {
        return lessonService.createLesson(lesson);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (hasAuthority('ADMIN') or hasAuthority('USER')) or !isAuthenticated()")
    public ResponseEntity<?> getLessonById(@PathVariable("id") String id) {
        UUID uuid = checkUUID(id, "id");
        return ResponseEntity.notFound().build();
//        return lessonService.getLesson(uuid);
    }
}
