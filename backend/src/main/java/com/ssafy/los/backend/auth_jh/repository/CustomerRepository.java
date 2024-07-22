package com.ssafy.los.backend.auth_jh.repository;

import com.ssafy.los.backend.auth_jh.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
