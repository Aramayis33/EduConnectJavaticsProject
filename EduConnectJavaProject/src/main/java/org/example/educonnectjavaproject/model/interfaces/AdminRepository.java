package org.example.educonnectjavaproject.model.interfaces;

import org.example.educonnectjavaproject.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Admin findByUsername(String username);
}
