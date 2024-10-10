package com.poly.datn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.datn.entities.Role;
import com.poly.datn.entities.Users;



public interface EmployeeRepository extends JpaRepository<Users, Integer> {

    Optional<Role> findByUsername(String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}
