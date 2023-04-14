package com.studentportalapi.Studentportalbackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "SelectedCourses")
public class StudentCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "external_id", referencedColumnName = "external_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "courseid")
    private Course course;

    public StudentCourse(Student student, Course course) {

        this.student = student;
        this.course = course;
    }

    public StudentCourse() {

    }



    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
// constructors, getters, and setters
}
