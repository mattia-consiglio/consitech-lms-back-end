package mattia.consiglio.consitech.lms.services;

import lombok.RequiredArgsConstructor;
import mattia.consiglio.consitech.lms.entities.EnrolledLesson;
import mattia.consiglio.consitech.lms.entities.User;
import mattia.consiglio.consitech.lms.exceptions.ResourceNotFoundException;
import mattia.consiglio.consitech.lms.payloads.NewEnrolledLessonDTO;
import mattia.consiglio.consitech.lms.payloads.UpdateEnrolledLessonsDTO;
import mattia.consiglio.consitech.lms.repositories.EnrolledLessonRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

import static mattia.consiglio.consitech.lms.utils.GeneralChecks.checkUUID;

@RequiredArgsConstructor
@Service
public class EnrolledLessonService {
    private final EnrolledLessonRepository enrolledLessonRepository;
    private final LessonService lessonService;
    private final UserService userService;

    public EnrolledLesson addEnrolledLesson(NewEnrolledLessonDTO newEnrolledLessonDTO, User user) {
        EnrolledLesson enrolledLesson = new EnrolledLesson();
        enrolledLesson.setLesson(lessonService.getLesson(newEnrolledLessonDTO.lessonId()));
        enrolledLesson.setUser(user);
        enrolledLesson.setStartDate(LocalDate.now());
        enrolledLesson.setVideoWatchTimePercentage(0);
        enrolledLesson.setQuizDone(false);
        return enrolledLessonRepository.save(enrolledLesson);
    }

    public EnrolledLesson getEnrolledLesson(String id) {
        UUID uuid = checkUUID(id, "enrolled lesson id");
        return getEnrolledLesson(uuid);
    }

    public EnrolledLesson getEnrolledLesson(UUID id) {
        EnrolledLesson enrolledLesson = enrolledLessonRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("EnrolledLesson", id));
        return enrolledLesson;
    }

    public EnrolledLesson getEnrolledLesson(UUID lessonId, User user) {
        return enrolledLessonRepository.findByLessonIdAndUserId(lessonId, user.getId()).orElseThrow(() -> new ResourceNotFoundException("EnrolledLesson", lessonId));
    }

    public EnrolledLesson getEnrolledLesson(String lessonId, User user) {
        UUID uuid = checkUUID(lessonId, "lesson id");
        return getEnrolledLesson(uuid, user);
    }

    public EnrolledLesson updateEnrolledLesson(String id, UpdateEnrolledLessonsDTO enrolledLessonDTO) {
        EnrolledLesson enrolledLesson = getEnrolledLesson(id);
        enrolledLesson.setEndDate(enrolledLessonDTO.endDate());
        enrolledLesson.setVideoWatchTimePercentage(enrolledLessonDTO.videoWatchTimePercentage());
        enrolledLesson.setQuizDone(enrolledLessonDTO.quizDone());
        return enrolledLessonRepository.save(enrolledLesson);
    }

    public void deleteEnrolledLesson(String id) {
        UUID uuid = checkUUID(id, "enrolled lesson id");
        EnrolledLesson enrolledLesson = getEnrolledLesson(uuid);
        enrolledLessonRepository.delete(enrolledLesson);
    }
}
