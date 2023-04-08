package com.studentportalapi.Studentportalbackend.repository;

import com.studentportalapi.Studentportalbackend.model.Course;
import com.studentportalapi.Studentportalbackend.model.StudentCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;



@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {
    Long countByStudentId(Long studentId);
    Optional<StudentCourse> findByStudentIdAndCourseCourseid(Long studentId, Long courseId);
}
