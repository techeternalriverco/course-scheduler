package com.example.coursescheduler.controller;

import com.example.coursescheduler.entity.Course;
import com.example.coursescheduler.entity.User;
import com.example.coursescheduler.enums.DayOfWeek;
import com.example.coursescheduler.repository.CourseRepository;
import com.example.coursescheduler.repository.EnrolledCourseRepository;
import com.example.coursescheduler.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrolledCourseRepository enrolledCourseRepository;

    private User testUser;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        // Clear all repositories before each test
        enrolledCourseRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setName("John Doe");
        testUser = userRepository.save(testUser);

        // Create test course
        testCourse = new Course("Math 101", DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        testCourse = courseRepository.save(testCourse);
    }

    @Test
    void registerUser_shouldReturnCreatedUser() throws Exception {
        String userJson = """
                {
                    "name": "Alice Johnson"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Alice Johnson"));
    }

    @Test
    void bookCourse_shouldReturnSuccess() throws Exception {
        String courseJson = String.format("""
                {
                    "id": %d,
                    "name": "Math 101",
                    "dayOfWeek": "MONDAY",
                    "startTime": "10:00",
                    "endTime": "11:00"
                }
                """, testCourse.getId());

        mockMvc.perform(post("/users/" + testUser.getId() + "/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(courseJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Course booked successfully!"));
    }

    @Test
    void bookCourse_shouldReturnNotFoundIfCourseDoesNotExist() throws Exception {
        String invalidCourseJson = """
                {
                    "id": 999,
                    "name": "Nonexistent Course",
                    "dayOfWeek": "WEDNESDAY",
                    "startTime": "12:00",
                    "endTime": "13:00"
                }
                """;

        mockMvc.perform(post("/users/" + testUser.getId() + "/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidCourseJson))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Course not found with id: 999"));
    }

    @Test
    void dropCourse_shouldReturnSuccess() throws Exception {
        // First, enroll the user in the course
        String courseJson = String.format("""
                {
                    "id": %d,
                    "name": "Math 101",
                    "dayOfWeek": "MONDAY",
                    "startTime": "10:00",
                    "endTime": "11:00"
                }
                """, testCourse.getId());

        mockMvc.perform(post("/users/" + testUser.getId() + "/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(courseJson))
                .andExpect(status().isOk());

        // Now, drop the course
        mockMvc.perform(delete("/users/" + testUser.getId() + "/courses/" + testCourse.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Course dropped successfully."));
    }

    @Test
    void dropCourse_shouldReturnNotFoundIfEnrollmentDoesNotExist() throws Exception {
        mockMvc.perform(delete("/users/" + testUser.getId() + "/courses/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Enrollment not found for userId: " + testUser.getId() + " and courseId: 999"));
    }

    @Test
    void listStudentCourses_shouldReturnListOfCourses() throws Exception {
        // First, enroll the user in the course
        String courseJson = String.format("""
                {
                    "id": %d,
                    "name": "Math 101",
                    "dayOfWeek": "MONDAY",
                    "startTime": "10:00",
                    "endTime": "11:00"
                }
                """, testCourse.getId());

        mockMvc.perform(post("/users/" + testUser.getId() + "/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(courseJson))
                .andExpect(status().isOk());

        // Get the list of enrolled courses
        mockMvc.perform(get("/users/" + testUser.getId() + "/courses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Math 101"));
    }
}
