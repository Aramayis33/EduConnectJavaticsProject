package org.example.educonnectjavaproject.model.interfaces;

import org.example.educonnectjavaproject.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {


}
