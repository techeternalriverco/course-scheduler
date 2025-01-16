package com.example.coursescheduler.service;

import com.example.coursescheduler.entity.Course;
import com.example.coursescheduler.entity.EnrolledCourse;
import com.example.coursescheduler.enums.DayOfWeek;
import com.example.coursescheduler.repository.CourseRepository;
import com.example.coursescheduler.repository.EnrolledCourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrolledCourseRepository enrolledCourseRepository;

    @InjectMocks
    private CourseService courseService;

    private Course testCourse;
    private EnrolledCourse testEnrollment;

    @BeforeEach
    void setUp() {
        testCourse = new Course("Math 101", DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        testCourse.setId(1L);

        testEnrollment = new EnrolledCourse(1L, testCourse.getId());
        testEnrollment.setId(1L);
    }

    @Test
    void createCourse_shouldSaveCourse() {
        when(courseRepository.save(testCourse)).thenReturn(testCourse);

        Course savedCourse = courseService.createCourse(testCourse);

        assertEquals(testCourse, savedCourse);
        verify(courseRepository, times(1)).save(testCourse);
    }

    @Test
    void getAllCourses_shouldReturnListOfCourses() {
        List<Course> courses = Arrays.asList(testCourse);
        when(courseRepository.findAll()).thenReturn(courses);

        List<Course> result = courseService.getAllCourses();

        assertEquals(courses, result);
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void dropCourse_shouldDeleteEnrollment() {
        when(enrolledCourseRepository.findByUserIdAndCourseId(1L, testCourse.getId())).thenReturn(testEnrollment);

        courseService.dropCourse(1L, testCourse.getId());

        verify(enrolledCourseRepository, times(1)).findByUserIdAndCourseId(1L, testCourse.getId());
        verify(enrolledCourseRepository, times(1)).delete(testEnrollment);
    }

    @Test
    void dropCourse_shouldThrowExceptionIfEnrollmentNotFound() {
        when(enrolledCourseRepository.findByUserIdAndCourseId(1L, testCourse.getId())).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                courseService.dropCourse(1L, testCourse.getId()));

        assertEquals("Enrollment not found for userId: 1 and courseId: 1", exception.getMessage());
        verify(enrolledCourseRepository, times(1)).findByUserIdAndCourseId(1L, testCourse.getId());
    }

    @Test
    void bookCourse_shouldThrowExceptionIfCourseNotFound() {
        Long nonExistentCourseId = 999L;
        Course invalidCourse = new Course();
        invalidCourse.setId(nonExistentCourseId);

        when(courseRepository.findById(nonExistentCourseId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                courseService.bookCourse(1L, invalidCourse));

        assertEquals("Course not found with id: " + nonExistentCourseId, exception.getMessage());
        verify(courseRepository, times(1)).findById(nonExistentCourseId);
        verify(enrolledCourseRepository, never()).save(any(EnrolledCourse.class));
    }

    @Test
    void bookCourse_shouldSaveEnrollmentIfNoConflict() {
        when(courseRepository.findById(testCourse.getId())).thenReturn(Optional.of(testCourse));
        when(enrolledCourseRepository.findAllCoursesByUserId(1L)).thenReturn(List.of());

        boolean isBooked = courseService.bookCourse(1L, testCourse);

        assertTrue(isBooked);
        verify(courseRepository, times(1)).findById(testCourse.getId());
        verify(enrolledCourseRepository, times(1)).findAllCoursesByUserId(1L);
        verify(enrolledCourseRepository, times(1)).save(any(EnrolledCourse.class));
    }

    @Test
    void bookCourse_shouldReturnFalseIfConflictExists() {
        Course conflictingCourse = new Course("Physics 101", DayOfWeek.MONDAY, LocalTime.of(10, 30), LocalTime.of(11, 30));
        conflictingCourse.setId(2L);

        when(courseRepository.findById(conflictingCourse.getId())).thenReturn(Optional.of(conflictingCourse));
        when(enrolledCourseRepository.findAllCoursesByUserId(1L)).thenReturn(List.of(testCourse));

        boolean isBooked = courseService.bookCourse(1L, conflictingCourse);

        assertFalse(isBooked);
        verify(courseRepository, times(1)).findById(conflictingCourse.getId());
        verify(enrolledCourseRepository, times(1)).findAllCoursesByUserId(1L);
        verify(enrolledCourseRepository, never()).save(any(EnrolledCourse.class));
    }

    @Test
    void getCoursesByStudent_shouldReturnListOfCourses() {
        List<Course> courses = Arrays.asList(testCourse);
        when(enrolledCourseRepository.findAllCoursesByUserId(1L)).thenReturn(courses);

        List<Course> result = courseService.getCoursesByStudent(1L);

        assertEquals(courses, result);
        verify(enrolledCourseRepository, times(1)).findAllCoursesByUserId(1L);
    }
}
