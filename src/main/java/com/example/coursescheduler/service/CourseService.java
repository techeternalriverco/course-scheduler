package com.example.coursescheduler.service;

import com.example.coursescheduler.entity.Course;
import com.example.coursescheduler.entity.EnrolledCourse;
import com.example.coursescheduler.repository.CourseRepository;
import com.example.coursescheduler.repository.EnrolledCourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final EnrolledCourseRepository enrolledCourseRepository;

    public CourseService(CourseRepository courseRepository, EnrolledCourseRepository enrolledCourseRepository) {
        this.courseRepository = courseRepository;
        this.enrolledCourseRepository = enrolledCourseRepository;
    }

    // 1. Create a new course
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    // 2. List all available courses
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // 3. Drop a course for a student
    public void dropCourse(Long userId, Long courseId) {
        EnrolledCourse enrolledCourse = enrolledCourseRepository.findByUserIdAndCourseId(userId, courseId);

        if (enrolledCourse == null) {
            throw new IllegalArgumentException(
                    "Enrollment not found for userId: " + userId + " and courseId: " + courseId
            );
        }
        enrolledCourseRepository.delete(enrolledCourse);
    }

    // 4. Book a course for a student with conflict prevention
    public boolean bookCourse(Long userId, Course course) {
        Course existedcourse = courseRepository.findById(course.getId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + course.getId()));

        List<Course> enrolledCourses = enrolledCourseRepository.findAllCoursesByUserId(userId);

        for (Course enrolledCourse : enrolledCourses) {
            if (enrolledCourse.getDayOfWeek() == existedcourse.getDayOfWeek()) {
                if (existedcourse.getStartTime().isBefore(enrolledCourse.getEndTime()) &&
                        existedcourse.getEndTime().isAfter(enrolledCourse.getStartTime())) {
                    return false;
                }
            }
        }

        EnrolledCourse enrolledCourse = new EnrolledCourse(userId, existedcourse.getId());
        enrolledCourseRepository.save(enrolledCourse);

        return true;
    }

    // 5. List all courses for a specific student
    public List<Course> getCoursesByStudent(Long userId) {
        return enrolledCourseRepository.findAllCoursesByUserId(userId);
    }
}
