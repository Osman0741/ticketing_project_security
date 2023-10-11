package com.cydeo.repository;

import com.cydeo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    //give me the role based on the description
    Role findByDescription(String description);
}
