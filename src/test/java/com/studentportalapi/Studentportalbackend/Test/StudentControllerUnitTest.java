package com.studentportalapi.Studentportalbackend.Test;


import com.studentportalapi.Studentportalbackend.controller.StudentController;
import com.studentportalapi.Studentportalbackend.model.Student;
import com.studentportalapi.Studentportalbackend.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentControllerUnitTest {

    @InjectMocks
    private StudentController studentController;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RestTemplate restTemplate;

    private Student student;

    @BeforeEach
    public void setup() {
        student = new Student();
        student.setName("John Doe");
        student.setEmail("john.doe@example.com");
        student.setPassword("password123");
        student.setExternalId("c1234567");
        student.setGraduationStatus(Student.GraduationStatus.NOT_GRADUATED);
    }

    @Test
    public void testRegisterStudent() {
        when(studentRepository.existsByEmail(anyString())).thenReturn(false);
        when(studentRepository.existsByExternalId(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        ResponseEntity<String> response = studentController.registerStudent(student);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Account created successfully", response.getBody());
        verify(studentRepository, times(1)).save(any(Student.class));
        verify(restTemplate, times(1)).postForObject(anyString(), anyMap(), eq(String.class));
    }
    @Test
    public void testLoginStudent_success() {
        when(studentRepository.findByEmail(anyString())).thenReturn(student);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = studentController.loginStudent(student);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(student.getExternalId(), response.getBody().get("studentId"));
        verify(studentRepository, times(1)).findByEmail(anyString());
    }

    @Test
    public void testLoginStudent_invalidEmailOrPassword() {
        ResponseEntity<Map<String, Object>> response = studentController.loginStudent(student);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid email or password", response.getBody().get("message"));
        verify(studentRepository, times(1)).findByEmail(anyString());
    }



    @Test
    public void testUpdateStudentName_success() {
        when(studentRepository.findByExternalId(anyString())).thenReturn(student);

        Student updatedStudent = new Student();
        updatedStudent.setName("Jane Doe");
        ResponseEntity<String> response = studentController.updateStudentName(student.getExternalId(), updatedStudent);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Name updated successfully", response.getBody());
        verify(studentRepository, times(1)).findByExternalId(anyString());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    public void testUpdateStudentName_notFound() {
        when(studentRepository.findByExternalId(anyString())).thenReturn(null);

        Student updatedStudent = new Student();
        updatedStudent.setName("Jane Doe");
        ResponseEntity<String> response = studentController.updateStudentName(student.getExternalId(), updatedStudent);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Student not found", response.getBody());
        verify(studentRepository, times(1)).findByExternalId(anyString());
    }

    @Test
    public void testUpdateStudentEmail_success() {
        when(studentRepository.findByExternalId(anyString())).thenReturn(student);
        when(studentRepository.existsByEmail(anyString())).thenReturn(false);

        Student updatedStudent = new Student();
        updatedStudent.setEmail("jane.doe@example.com");
        ResponseEntity<String> response = studentController.updateStudentEmail(student.getExternalId(), updatedStudent);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Email updated successfully", response.getBody());
        verify(studentRepository, times(1)).findByExternalId(anyString());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    public void testUpdateStudentEmail_invalidEmail() {
        Student updatedStudent = new Student();
        updatedStudent.setEmail("invalid_email");

        ResponseEntity<String> response = studentController.updateStudentEmail(student.getExternalId(), updatedStudent);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Student not found", response.getBody());
    }

    @Test
    public void testUpdateStudentPassword_success() {
        when(studentRepository.findByExternalId(anyString())).thenReturn(student);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_new_password");

        ResponseEntity<String> response = studentController.updateStudentPassword(student.getExternalId(), "new_password");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password updated successfully", response.getBody());
        verify(studentRepository, times(1)).findByExternalId(anyString());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    public void testUpdateStudentPassword_notFound() {
        when(studentRepository.findByExternalId(anyString())).thenReturn(null);

        ResponseEntity<String> response = studentController.updateStudentPassword(student.getExternalId(), "new_password");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Student not found", response.getBody());
        verify(studentRepository, times(1)).findByExternalId(anyString());
    }

    @Test
    public void testGetStudentDetails_success() {
        when(studentRepository.findByExternalId(anyString())).thenReturn(student);

        ResponseEntity<Map<String, Object>> response = studentController.getStudentDetails(student.getExternalId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(student.getName(), response.getBody().get("name"));
        assertEquals(student.getEmail(), response.getBody().get("email"));
        assertEquals(student.getExternalId(), response.getBody().get("externalId"));
        verify(studentRepository, times(1)).findByExternalId(anyString());
    }

    @Test
    public void testRegisterStudent_invalidEmail() {
        Student invalidStudent = new Student();
        invalidStudent.setName("John Doe");
        invalidStudent.setEmail("john.doe@invalid_email");
        invalidStudent.setPassword("password123");

        ResponseEntity<String> response = studentController.registerStudent(invalidStudent);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid email format", response.getBody());
    }

    @Test
    public void testRegisterStudent_emailAlreadyRegistered() {
        when(studentRepository.existsByEmail(anyString())).thenReturn(true);

        ResponseEntity<String> response = studentController.registerStudent(student);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Email already registered", response.getBody());
    }


}
