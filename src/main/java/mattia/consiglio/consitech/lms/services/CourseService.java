package mattia.consiglio.consitech.lms.services;

import mattia.consiglio.consitech.lms.entities.Course;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.payloads.NewCourseDTO;
import mattia.consiglio.consitech.lms.repositories.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;


    public Course getCourse(UUID id) {
        return courseRepository.findById(id).orElseThrow(() -> new BadRequestException("Course not found"));
    }

    public Course createCourse(NewCourseDTO newCourseDTO) {
        Course course = new Course();
        course.setTitle(newCourseDTO.title());
        course.setDescription(newCourseDTO.description());
        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
}
