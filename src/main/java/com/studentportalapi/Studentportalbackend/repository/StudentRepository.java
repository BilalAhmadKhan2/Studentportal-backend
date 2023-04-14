package com.studentportalapi.Studentportalbackend.repository;

import com.studentportalapi.Studentportalbackend.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Student findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Student> findById(Long studentId);
    boolean existsByExternalId(String externalId);

    Student findByExternalId(String externalId);
}