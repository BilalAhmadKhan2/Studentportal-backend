package com.studentportalapi.Studentportalbackend.controller;

import com.studentportalapi.Studentportalbackend.model.Course;
import com.studentportalapi.Studentportalbackend.model.Student;
import com.studentportalapi.Studentportalbackend.model.StudentCourse;
import com.studentportalapi.Studentportalbackend.repository.CourseRepository;
import com.studentportalapi.Studentportalbackend.repository.StudentCourseRepository;
import com.studentportalapi.Studentportalbackend.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;
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
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/createinvoice/{externalId}")
    public ResponseEntity<String> createInvoice(@PathVariable("externalId") String externalId) {
        List<StudentCourse> selectedCourses = studentCourseRepository.findByStudentExternalId(externalId);
        BigDecimal totalFees = BigDecimal.ZERO;

        for (StudentCourse studentCourse : selectedCourses) {
            totalFees = totalFees.add(studentCourse.getCourse().getFee());
        }

        RestTemplate restTemplate = new RestTemplate();
        String financeApiUrl = "http://localhost:8081/api/invoice/create";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("studentId", externalId);
        requestData.put("totalAmount", totalFees);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestData, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(financeApiUrl, request, String.class);

        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/checkGraduationStatus/{studentId}")
    public ResponseEntity<String> checkGraduationStatus(@PathVariable("studentId") String studentId) {
        RestTemplate restTemplate = new RestTemplate();
        String financeApiUrl = "http://localhost:8081/api/invoice/check-clearance"; // Replace with the actual Finance API URL

        ResponseEntity<String> financeApiResponse = restTemplate.getForEntity(financeApiUrl + "/" + studentId, String.class);

        if (financeApiResponse.getBody().equals("clear")) {
            Student student = studentRepository.findByExternalId(studentId);
            if (student != null) {
                student.setGraduationStatus(Student.GraduationStatus.GRADUATED);
                studentRepository.save(student);
                return new ResponseEntity<>("GRADUATED", HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>("NOT_GRADUATED", HttpStatus.OK);
        }
    }




}
