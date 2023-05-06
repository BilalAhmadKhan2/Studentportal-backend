package com.studentportalapi.Studentportalbackend.Test;


import com.studentportalapi.Studentportalbackend.model.Course;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CourseControllerFunctionalTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getAllCourses() {
        ResponseEntity<Course[]> response = restTemplate.getForEntity("/courseAPI/getallcoursesdata", Course[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void getCourseById() {
        Long courseId = 1L;
        ResponseEntity<Course> response = restTemplate.getForEntity("/courseAPI/getcoursedata/" + courseId, Course.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCourseid()).isEqualTo(courseId);
    }
    @Test
    public void createCourse() {
        Course newCourse = new Course();
        newCourse.setName("New Course");
        newCourse.setFee(BigDecimal.valueOf(500));
        newCourse.setDescription("New Course Description");

        ResponseEntity<Course> response = restTemplate.postForEntity("/courseAPI/addnewcourse", newCourse, Course.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(newCourse.getName());
        assertThat(response.getBody().getFee()).isEqualTo(newCourse.getFee());
        assertThat(response.getBody().getDescription()).isEqualTo(newCourse.getDescription());
    }

    @Test
    public void updateCourse() {
        Long courseId = 1L;
        Course updatedCourse = new Course();
        updatedCourse.setName("Updated Course");
        updatedCourse.setFee(BigDecimal.valueOf(600));
        updatedCourse.setDescription("Updated Course Description");

        restTemplate.put("/courseAPI/editcoursedata/" + courseId, updatedCourse, Course.class);

        ResponseEntity<Course> response = restTemplate.getForEntity("/courseAPI/getcoursedata/" + courseId, Course.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(updatedCourse.getName());
        assertThat(response.getBody().getFee().compareTo(updatedCourse.getFee())).isEqualTo(0);
        assertThat(response.getBody().getDescription()).isEqualTo(updatedCourse.getDescription());
    }
    @Test
    public void getCourseByIdNotFound() {
        Long courseId = 999L;

        ResponseEntity<Course> response = restTemplate.getForEntity("/courseAPI/getcoursedata/" + courseId, Course.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }



}
