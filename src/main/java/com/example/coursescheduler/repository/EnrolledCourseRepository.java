package com.example.coursescheduler.repository;

import com.example.coursescheduler.entity.Course;
import com.example.coursescheduler.entity.EnrolledCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrolledCourseRepository extends JpaRepository<EnrolledCourse, Long> {

    @Query("SELECT ec.course FROM EnrolledCourse ec WHERE ec.user.id = :userId")
    List<Course> findAllCoursesByUserId(@Param("userId") Long userId);
}
