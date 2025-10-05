package com.monthly.ecommercemonolith.repositories;

import com.monthly.ecommercemonolith.entities.AppRole;
import com.monthly.ecommercemonolith.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByRoleName(AppRole roleName);
}
