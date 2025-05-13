package com.fatihdurdu.loancase.config;

import com.fatihdurdu.loancase.model.entity.Customer;
import com.fatihdurdu.loancase.model.entity.Loan;
import com.fatihdurdu.loancase.model.entity.Role;
import com.fatihdurdu.loancase.model.entity.User;
import com.fatihdurdu.loancase.repository.CustomerRepository;
import com.fatihdurdu.loancase.repository.LoanRepository;
import com.fatihdurdu.loancase.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            Customer customer = Customer.builder().name("John").surname("Doe")
                    .creditLimit(new BigDecimal("100000.00")).usedCreditLimit(new BigDecimal("00.00")).build();

            Customer customer1 = Customer.builder().name("Michael").surname("Johnson")
                    .creditLimit(new BigDecimal("500000.00")).usedCreditLimit(new BigDecimal("00.00")).build();

            customer = customerRepository.save(customer);
            customer1 = customerRepository.save(customer1);

            User customerUser = User.builder()
                    .username("customer1")
                    .password(passwordEncoder.encode("password123"))
                    .role(Role.CUSTOMER)
                    .customer(customer1)
                    .build();

            User adminUser = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("password123"))
                    .role(Role.ADMIN)
                    .build();

            userRepository.save(customerUser);
            userRepository.save(adminUser);

            System.out.println("Sample data initialized!");
        };
    }
}
