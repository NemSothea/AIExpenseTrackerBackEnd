package com.aiexpense.TrackerBackEnd.controller;

import java.util.ArrayList;
import java.util.List;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.aiexpense.TrackerBackEnd.model.Student;


@RestController
public class StudentController {

    private List<Student> students = new ArrayList<>(List.of(
        new Student(1, "Sothea", 60),
        new Student(2, "Pisey", 90)
    ));

    @GetMapping("/students")
    public List<Student> getStudents() {
        return students;
    }

    @PostMapping("/students")
    public Student addStudent(@RequestBody Student student) {
        students.add(student);
        return student;
    }

}
