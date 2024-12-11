package org.example.educonnectjavaproject.model.interfaces;

import org.example.educonnectjavaproject.model.Student;
import org.example.educonnectjavaproject.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student,Integer> {

    List<Student> findStudentByGroupNumber(int groupNumber);
    Student findStudentByEmail(String username);

    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.subjects")
    List<Student> findAllStudentsWithSubjects();

    Student findStudentById(int id);

    List<Student> findAllByGroupNumber(int i);

    void deleteByEmail(String email);
}
