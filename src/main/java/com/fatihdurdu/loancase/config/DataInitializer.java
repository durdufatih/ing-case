package com.fatihdurdu.loancase.config;

import com.fatihdurdu.loancase.model.entity.Customer;
import com.fatihdurdu.loancase.model.entity.Loan;
import com.fatihdurdu.loancase.repository.CustomerRepository;
import com.fatihdurdu.loancase.repository.LoanRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(LoanRepository loanRepository, CustomerRepository customerRepository) {
        return args -> {

            Customer customer= Customer.builder().name("John").surname("Doe")
                    .creditLimit(new BigDecimal("100000.00")).usedCreditLimit(new BigDecimal("00.00")).build();

            Customer customer1= Customer.builder().name("Michael").surname("Johnson")
                    .creditLimit(new BigDecimal("500000.00")).usedCreditLimit(new BigDecimal("00.00")).build();

            customerRepository.save(customer);
            customerRepository.save(customer1);

            System.out.println("Sample data initialized!");
        };
    }
}
