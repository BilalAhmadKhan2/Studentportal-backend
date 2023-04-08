package com.studentportalapi.Studentportalbackend.controller;

import com.studentportalapi.Studentportalbackend.model.Course;
import com.studentportalapi.Studentportalbackend.model.Student;
import com.studentportalapi.Studentportalbackend.model.StudentCourse;
import com.studentportalapi.Studentportalbackend.repository.CourseRepository;
import com.studentportalapi.Studentportalbackend.repository.StudentCourseRepository;
import com.studentportalapi.Studentportalbackend.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/courseselection")
public class SelectCourseController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentCourseRepository studentCourseRepository;

    @PostMapping("/select")
    public ResponseEntity<String> selectCourse(@RequestBody Map<String, Long> requestData) {
        Long studentId = requestData.get("studentId");
        Long courseId = requestData.get("courseId");

        // Check if student exists
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if (studentOptional.isEmpty()) {
            return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
        }

        // Check if course exists
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            return new ResponseEntity<>("Course not found", HttpStatus.NOT_FOUND);
        }

        // Check if student already selected 3 courses
        Long selectedCoursesCount = studentCourseRepository.countByStudentId(studentId);
        if (selectedCoursesCount >= 3) {
            return new ResponseEntity<>("Cannot enroll in more than 3 courses", HttpStatus.BAD_REQUEST);
        }
        // Check if student already selected this course
        Optional<StudentCourse> studentCourseOptional = studentCourseRepository.findByStudentIdAndCourseCourseid(studentId, courseId);
        if (studentCourseOptional.isPresent()) {
            return new ResponseEntity<>("You are already enrolled in the course", HttpStatus.BAD_REQUEST);
        }

        // Save the selected course
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setStudent(studentOptional.get());
        studentCourse.setCourse(courseOptional.get());
        studentCourseRepository.save(studentCourse);

        return new ResponseEntity<>("Course enrollment completed", HttpStatus.OK);
    }
}
