package com.ssafy.los.backend.auth_jh.repository;

import com.ssafy.los.backend.auth_jh.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByUsername(String username);
}
