package org.example.educonnectjavaproject.model.interfaces;

import org.example.educonnectjavaproject.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {

    Subject findByStudentId(int studentId);

}
