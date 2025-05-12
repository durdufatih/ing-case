package com.fatihdurdu.loancase.service;

import com.fatihdurdu.loancase.model.entity.Customer;
import com.fatihdurdu.loancase.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public Optional<Customer> getCustomerByName(String name) {
        return customerRepository.findByName(name);
    }

    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
    
    // Check if customer has enough limit for a new loan
    public boolean hasEnoughLimit(String customerName, BigDecimal loanAmount) {
        Optional<Customer> customerOpt = customerRepository.findByName(customerName);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            return customer.getCreditLimit().compareTo(loanAmount) >= 0;
        }
        return false;
    }

}