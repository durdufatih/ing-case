package com.fatihdurdu.loancase.repository;

import com.fatihdurdu.loancase.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    // Find customer by name
    Optional<Customer> findByName(String name);
}