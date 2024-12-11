package org.example.educonnectjavaproject.model.interfaces;

import org.example.educonnectjavaproject.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

    Teacher findTeacherByUsername(String username);
}
