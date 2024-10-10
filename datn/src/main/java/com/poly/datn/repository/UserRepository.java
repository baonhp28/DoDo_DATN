package com.poly.datn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.poly.datn.entities.Users;

public interface UserRepository extends JpaRepository<Users, Integer> {
    Users findByFullName(String fullName);

    Optional<Users> findByEmail(String email);

    Users findByUsernameAndPassword(String username, String password);

    @Query("SELECT COUNT(c) FROM Users c")
    long countTotalCustomers();

    Optional<Users> findByUsername(String username);
}
