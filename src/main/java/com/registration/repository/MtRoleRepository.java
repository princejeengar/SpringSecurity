package com.registration.repository;

import com.registration.entity.MtRole;
import com.registration.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MtRoleRepository extends JpaRepository<MtRole, Long> {
    Optional<MtRole> findByName(Role name);
}