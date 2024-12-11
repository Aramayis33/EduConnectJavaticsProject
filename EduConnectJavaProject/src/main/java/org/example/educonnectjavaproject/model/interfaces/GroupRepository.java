package org.example.educonnectjavaproject.model.interfaces;

import org.apache.catalina.Group;
import org.example.educonnectjavaproject.model.GroupInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<GroupInfo, Integer> {

    GroupInfo findFirstByOrderByGroupNumber();
}
