package com.studentportalapi.Studentportalbackend.repository;

import com.studentportalapi.Studentportalbackend.model.Course;
import com.studentportalapi.Studentportalbackend.model.StudentCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;



@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {

    Long countByStudentExternalId(String externalId);

    Optional<StudentCourse> findByStudentExternalIdAndCourseCourseid(String externalId, Long courseId);

    List<StudentCourse> findByStudentExternalId(String externalId);
}
