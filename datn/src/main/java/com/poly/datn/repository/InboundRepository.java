package com.poly.datn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.datn.entities.Inbound;
import com.poly.datn.entities.Skus;

public interface InboundRepository extends JpaRepository<Inbound,Integer>{
List<Inbound> findBySku(Skus sku);
}
