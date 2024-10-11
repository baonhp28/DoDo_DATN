package com.poly.datn.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.poly.datn.entities.Address;
@Repository
public interface AddressRepository extends JpaRepository<Address,Integer> {
  List<Address> findByUserId(Integer userId);
}
