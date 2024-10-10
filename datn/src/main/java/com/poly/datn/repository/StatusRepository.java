package com.poly.datn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.datn.entities.Status;


public interface StatusRepository extends JpaRepository<Status, Integer>{
    default Optional<Status> findDefaultStatus() {
        return findById(1);
    }
}
