package org.example.educonnectjavaproject.model.interfaces;

import org.example.educonnectjavaproject.model.HomeWork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HomeWorkRepository extends JpaRepository<HomeWork, Integer> {

    List<HomeWork> findHomeWorkByGroupNumber(int groupNumber);

}
