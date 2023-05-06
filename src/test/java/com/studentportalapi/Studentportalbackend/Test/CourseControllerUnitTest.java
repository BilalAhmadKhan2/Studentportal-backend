package com.studentportalapi.Studentportalbackend.Test;

import com.studentportalapi.Studentportalbackend.controller.CourseController;
import com.studentportalapi.Studentportalbackend.model.Course;
import com.studentportalapi.Studentportalbackend.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CourseController.class)
public class CourseControllerUnitTest {

    @Autowired
    private CourseController courseController;

    @MockBean
    private CourseRepository courseRepository;
    @TestConfiguration
    static class TestConfig {

        @Bean
        public RestTemplateBuilder restTemplateBuilder() {
            return new RestTemplateBuilder();
        }
    }
    @Test
    public void getCourseById_existingCourse_returnsCourse() {
        // Arrange
        Long courseId = 1L;
        Course course = new Course();
        course.setCourseid(courseId);
        course.setName("Test Course");
        course.setDescription("Test Course Description");
        course.setFee(new BigDecimal("100.00"));

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Act
        ResponseEntity<Course> response = courseController.getCourseById(courseId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(course, response.getBody());
    }
    @Test
    public void getCourseById_nonExistingCourse_returnsNotFound() {
        // Arrange
        Long courseId = 1L;

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Course> response = courseController.getCourseById(courseId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    @Test
    public void getAllCourses_returnsAllCourses() {
        // Arrange
        Course course1 = new Course();
        Course course2 = new Course();
        List<Course> courses = Arrays.asList(course1, course2);

        when(courseRepository.findAll()).thenReturn(courses);

        // Act
        List<Course> response = courseController.getAllCourses();

        // Assert
        assertEquals(courses, response);
    }
    @Test
    public void createCourse_successfullyCreatesNewCourse() {
        // Arrange
        Course course = new Course();
        course.setName("Test Course");
        course.setDescription("Test Course Description");
        course.setFee(new BigDecimal("100.00"));

        when(courseRepository.save(any(Course.class))).thenReturn(course);

        // Act
        ResponseEntity<Course> response = courseController.createCourse(course);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(course, response.getBody());
    }
    @Test
    public void updateCourse_existingCourse_updatesCourse() {
        // Arrange
        Long courseId = 1L;
        Course course = new Course();
        course.setCourseid(courseId);
        course.setName("Test Course");
        course.setDescription("Test Course Description");
        course.setFee(new BigDecimal("100.00"));

        Course updatedCourse = new Course();
        updatedCourse.setCourseid(courseId);
        updatedCourse.setName("Updated Test Course");
        updatedCourse.setDescription("Updated Test Course Description");
        updatedCourse.setFee(new BigDecimal("150.00"));

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(updatedCourse);

        // Act
        ResponseEntity<Course> response = courseController.updateCourse(courseId, updatedCourse);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedCourse, response.getBody());
    }
    @Test
    public void deleteCourse_existingCourse_deletesCourse() {
        // Arrange
        Long courseId = 1L;
        Course course = new Course();
        course.setCourseid(courseId);
        course.setName("Test Course");
        course.setDescription("Test Course Description");
        course.setFee(new BigDecimal("100.00"));

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Act
        ResponseEntity<HttpStatus> response = courseController.deleteCourse(courseId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(courseRepository, times(1)).deleteById(courseId);
    }

}
