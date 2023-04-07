package com.studentportalapi.Studentportalbackend.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
    @PostMapping("/register")
    public ResponseEntity<String> registerStudent(@RequestBody Student student) {

        String email = student.getEmail();
        if (!isValidEmail(email)) {
            return new ResponseEntity<>("Invalid email format", HttpStatus.BAD_REQUEST);
        }
        if(studentRepository.existsByEmail(student.getEmail())) {
            return new ResponseEntity<>("Email already registered", HttpStatus.CONFLICT);
        }

        String encodedPassword = passwordEncoder.encode(student.getPassword());
        student.setPassword(encodedPassword);

        studentRepository.save(student);
        return new ResponseEntity<>("Account created successfully", HttpStatus.CREATED);
    }
    @PostMapping("/Login")
    public ResponseEntity<String> loginStudent(@RequestBody Student student) {
        Student savedStudent = studentRepository.findByEmail(student.getEmail());
        if (savedStudent == null || !passwordEncoder.matches(student.getPassword(), savedStudent.getPassword())) {
            return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>("Login successful", HttpStatus.OK);
    }


    @PutMapping("/editname/{id}")
    public ResponseEntity<String> updateStudentName(@PathVariable Long id, @RequestBody Student updatedStudent) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            student.setName(updatedStudent.getName());
            studentRepository.save(student);
            return new ResponseEntity<>("Name updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
        }
    }


    @PutMapping("/editemail/{id}")
    public ResponseEntity<String> updateStudentEmail(@PathVariable Long id, @RequestBody Student student) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (!optionalStudent.isPresent()) {
            return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
        }

        String email = student.getEmail();
        if (!isValidEmail(email)) {
            return new ResponseEntity<>("Invalid email format", HttpStatus.BAD_REQUEST);
        }

        Student existingStudent = optionalStudent.get();
        if (studentRepository.existsByEmail(email)) {
            if (!existingStudent.getEmail().equals(email)) {
                return new ResponseEntity<>("Email already registered!", HttpStatus.CONFLICT);
            }
        }

        existingStudent.setEmail(email);
        studentRepository.save(existingStudent);

        return new ResponseEntity<>("Email updated successfully", HttpStatus.OK);
    }

    @PutMapping("/editpassword/{id}")
    public ResponseEntity<String> updateStudentPassword(@PathVariable Long id, @RequestBody String password) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            String encodedPassword = passwordEncoder.encode(password);
            student.setPassword(encodedPassword);
            studentRepository.save(student);
            return new ResponseEntity<>("Password updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getstudentdata/{id}")
    public ResponseEntity<Map<String, Object>> getStudentDetails(@PathVariable Long id) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            Map<String, Object> response = new HashMap<>();
            response.put("id", student.getId());
            response.put("name", student.getName());
            response.put("email", student.getEmail());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
