package com.example.coursescheduler.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "enrolled_courses",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course_id"})
)
public class EnrolledCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    public EnrolledCourse() {}

    public EnrolledCourse(Long userId, Long courseId) {
        this.userId = userId;
        this.courseId = courseId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}