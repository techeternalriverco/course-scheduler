package com.example.coursescheduler.controller;

import com.example.coursescheduler.entity.Course;
import com.example.coursescheduler.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final CourseService courseService;

    public UserController(UserService userService, CourseService courseService) {
        this.userService = userService;
        this.courseService = courseService;
    }

    // POST /users - Register a new user
    @PostMapping
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }

    // POST /users/{userId}/courses - Book a course for a student
    @PostMapping("/{userId}/courses")
    public ResponseEntity<String> bookCourse(@PathVariable Long userId, @RequestBody Course course) {
        boolean isBooked = courseService.bookCourse(userId, course);
        if (isBooked) {
            return ResponseEntity.ok("Course booked successfully!");
        } else {
            return ResponseEntity.badRequest().body("Course booking failed due to conflicts.");
        }
    }

    // DELETE /users/{userId}/courses/{courseId} - Drop a course for a student
    @DeleteMapping("/{userId}/courses/{courseId}")
    public ResponseEntity<String> dropCourse(@PathVariable Long userId, @PathVariable Long courseId) {
        courseService.dropCourse(userId, courseId);
        return ResponseEntity.ok("Course dropped successfully.");
    }

    // GET /users/{userId}/courses - List all courses for a student
    @GetMapping("/{userId}/courses")
    public ResponseEntity<List<Course>> listStudentCourses(@PathVariable Long userId) {
        List<Course> courses = courseService.getCoursesByStudent(userId);
        return ResponseEntity.ok(courses);
    }
}