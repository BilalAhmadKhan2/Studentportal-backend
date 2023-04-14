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
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
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
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/select")
    public ResponseEntity<String> selectCourse(@RequestBody Map<String, Object> requestData, @RequestParam String externalId) {
        Student student = studentRepository.findByExternalId(externalId);
        Long courseId = ((Number) requestData.get("courseId")).longValue();

        // Check if student exists
        if (student == null) {
            return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
        }

        // Check if course exists
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            return new ResponseEntity<>("Course not found", HttpStatus.NOT_FOUND);
        }

        // Check if student already selected 3 courses
        Long selectedCoursesCount = studentCourseRepository.countByStudentExternalId(externalId);
        if (selectedCoursesCount >= 3) {
            return new ResponseEntity<>("Cannot enroll in more than 3 courses", HttpStatus.BAD_REQUEST);
        }
        // Check if student already selected this course
        Optional<StudentCourse> studentCourseOptional = studentCourseRepository.findByStudentExternalIdAndCourseCourseid(externalId, courseId);
        if (studentCourseOptional.isPresent()) {
            return new ResponseEntity<>("You are already enrolled in the course", HttpStatus.BAD_REQUEST);
        }

        // Save the selected course
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setStudent(student);
        studentCourse.setCourse(courseOptional.get());
        studentCourseRepository.save(studentCourse);

        return new ResponseEntity<>("Course enrollment completed", HttpStatus.OK);
    }




    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/selectedcourses/{externalId}")
    public ResponseEntity<List<Course>> getSelectedCourses(@PathVariable String externalId) {
        Student student = studentRepository.findByExternalId(externalId);
        // Check if student exists
        if (student == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }

        // Get the selected courses for the student
        List<StudentCourse> studentCourses = studentCourseRepository.findByStudentExternalId(externalId);
        List<Course> courses = new ArrayList<>();
        for (StudentCourse studentCourse : studentCourses) {
            Course course = studentCourse.getCourse();
            courses.add(course);
        }

        return new ResponseEntity<>(courses, HttpStatus.OK);
    }



}
