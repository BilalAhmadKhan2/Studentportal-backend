package com.studentportalapi.Studentportalbackend.controller;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.studentportalapi.Studentportalbackend.model.Student;
import com.studentportalapi.Studentportalbackend.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;



@RestController

@RequestMapping("/studentAPI")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/register")
    public ResponseEntity<String> registerStudent(@RequestBody Student student) {

        String name = student.getName();
        String email = student.getEmail();
        String password = student.getPassword();

        if (name == null || name.trim().isEmpty()) {
            return new ResponseEntity<>("Name cannot be empty", HttpStatus.BAD_REQUEST);
        }
        if (email == null || email.trim().isEmpty() || !isValidEmail(email)) {
            return new ResponseEntity<>("Invalid email format", HttpStatus.BAD_REQUEST);
        }
        if (password == null || password.trim().isEmpty()) {
            return new ResponseEntity<>("Password cannot be empty", HttpStatus.BAD_REQUEST);
        }
        if(studentRepository.existsByEmail(email)) {
            return new ResponseEntity<>("Email already registered", HttpStatus.CONFLICT);
        }

        // Generate a unique external ID for the new student with only numbers after "c"
        String externalId = "c";
        Random rand = new Random();
        for (int i = 1; i < 8; i++) {
            externalId += rand.nextInt(10);
        }
        while (studentRepository.existsByExternalId(externalId)) {
            externalId = "c";
            for (int i = 1; i < 8; i++) {
                externalId += rand.nextInt(10);
            }
        }
        student.setExternalId(externalId);

        String encodedPassword = passwordEncoder.encode(password);
        student.setPassword(encodedPassword);

        studentRepository.save(student);
        return new ResponseEntity<>("Account created successfully", HttpStatus.CREATED);
    }




    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/Login")
    public ResponseEntity<Map<String, Object>> loginStudent(@RequestBody Student student) {
        if(student.getEmail() == null || student.getEmail().isEmpty() || student.getPassword() == null || student.getPassword().isEmpty()) {
            return new ResponseEntity<>(Collections.singletonMap("message", "Email or password is empty"), HttpStatus.BAD_REQUEST);
        }
        Student savedStudent = studentRepository.findByEmail(student.getEmail());
        if (savedStudent == null || !passwordEncoder.matches(student.getPassword(), savedStudent.getPassword())) {
            return new ResponseEntity<>(Collections.singletonMap("message", "Invalid email or password"), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("studentId", savedStudent.getExternalId()); // Send the externalId field as the studentId
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PutMapping("/editname/{externalId}")
    public ResponseEntity<String> updateStudentName(@PathVariable String externalId, @RequestBody Student updatedStudent) {
        Student student = studentRepository.findByExternalId(externalId);
        if (student != null) {
            student.setName(updatedStudent.getName());
            studentRepository.save(student);
            return new ResponseEntity<>("Name updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
        }
    }


    @CrossOrigin(origins = "http://localhost:3000")
    @PutMapping("/editemail/{externalId}")
    public ResponseEntity<String> updateStudentEmail(@PathVariable String externalId, @RequestBody Student student) {
        Student existingStudent = studentRepository.findByExternalId(externalId);
        if (existingStudent == null) {
            return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
        }

        String email = student.getEmail();
        if (!isValidEmail(email)) {
            return new ResponseEntity<>("Invalid email format", HttpStatus.BAD_REQUEST);
        }

        if (studentRepository.existsByEmail(email)) {
            if (!existingStudent.getEmail().equals(email)) {
                return new ResponseEntity<>("Email already registered!", HttpStatus.CONFLICT);
            }
        }

        existingStudent.setEmail(email);
        studentRepository.save(existingStudent);

        return new ResponseEntity<>("Email updated successfully", HttpStatus.OK);
    }


    @PutMapping("/editpassword/{externalId}")
    public ResponseEntity<String> updateStudentPassword(@PathVariable String externalId, @RequestBody String password) {
        Student student = studentRepository.findByExternalId(externalId);
        if (student != null) {
            String encodedPassword = passwordEncoder.encode(password);
            student.setPassword(encodedPassword);
            studentRepository.save(student);
            return new ResponseEntity<>("Password updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/getstudentdata/{externalId}")
    public ResponseEntity<Map<String, Object>> getStudentDetails(@PathVariable String externalId) {
        Student student = studentRepository.findByExternalId(externalId);
        if (student != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("name", student.getName());
            response.put("email", student.getEmail());
            response.put("externalId", student.getExternalId()); // Add externalId to the response
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }




}
