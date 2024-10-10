package com.poly.datn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.datn.entities.Address;

public interface AddressRepository extends JpaRepository<Address,Integer> {

}
