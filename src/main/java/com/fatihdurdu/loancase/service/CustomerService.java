package com.fatihdurdu.loancase.service;

import com.fatihdurdu.loancase.model.entity.Customer;
import com.fatihdurdu.loancase.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing customer-related operations such as retrieval, creation,
 * update, deletion, and credit limit checks.
 */
@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Retrieves a customer by their unique ID.
     * @param id the unique identifier of the customer
     * @return an Optional containing the Customer if found, or empty if not found
     */
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    /**
         * Saves a customer entity to the repository.
         * @param customer the customer entity to be saved
         * @return the saved customer entity
         */
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }


    /**
     * Checks if the customer with the given name has enough credit limit for the specified loan amount.
     * @param customerName the name of the customer
     * @param loanAmount the amount of the loan to check against the credit limit
     * @return true if the customer has enough credit limit, false otherwise
     */
    public boolean hasEnoughLimit(String customerName, BigDecimal loanAmount) {
        Optional<Customer> customerOpt = customerRepository.findByName(customerName);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            return customer.getCreditLimit().compareTo(loanAmount) >= 0;
        }
        return false;
    }

}