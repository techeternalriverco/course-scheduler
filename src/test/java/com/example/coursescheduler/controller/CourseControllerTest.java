package com.example.coursescheduler.controller;

import com.example.coursescheduler.entity.Course;
import com.example.coursescheduler.enums.DayOfWeek;
import com.example.coursescheduler.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        // Clear all existing courses before each test
        courseRepository.deleteAll();

        // Insert test courses
        Course course1 = new Course("Math 101", DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        Course course2 = new Course("Physics 101", DayOfWeek.TUESDAY, LocalTime.of(14, 0), LocalTime.of(15, 30));

        courseRepository.saveAll(List.of(course1, course2));
    }

    @Test
    void createCourse_shouldReturnCreatedCourse() throws Exception {
        String courseJson = """
                {
                    "name": "Chemistry 101",
                    "dayOfWeek": "WEDNESDAY",
                    "startTime": "09:00",
                    "endTime": "10:30"
                }
                """;

        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(courseJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Chemistry 101"))
                .andExpect(jsonPath("$.dayOfWeek").value("WEDNESDAY"))
                .andExpect(jsonPath("$.startTime").value("09:00:00"))
                .andExpect(jsonPath("$.endTime").value("10:30:00"));
    }

    @Test
    void listAllCourses_shouldReturnListOfCourses() throws Exception {
        mockMvc.perform(get("/courses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Math 101"))
                .andExpect(jsonPath("$[1].name").value("Physics 101"));
    }
}