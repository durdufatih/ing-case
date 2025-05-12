package com.fatihdurdu.loancase.service;

import com.fatihdurdu.loancase.model.dto.CreateLoanRequest;
import com.fatihdurdu.loancase.model.entity.Customer;
import com.fatihdurdu.loancase.model.entity.Installment;
import com.fatihdurdu.loancase.model.entity.Loan;
import com.fatihdurdu.loancase.repository.LoanRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
public class LoanService {
    private static final Logger logger = LoggerFactory.getLogger(LoanService.class);

    private static final List<Integer> VALID_INSTALLMENT_NUMBERS = Arrays.asList(6, 9, 12, 24);
    private static final BigDecimal MIN_INTEREST_RATE = new BigDecimal("0.1");
    private static final BigDecimal MAX_INTEREST_RATE = new BigDecimal("0.5");

    private final LoanRepository loanRepository;
    private final CustomerService customerService;


    public Loan saveLoan(CreateLoanRequest loan) {

        logger.info("Saving loan: {}", loan);
        Customer customer = customerService.getCustomerById(loan.getCustomerId()).orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        logger.info("Customer: {}", customer);
        if (!customerService.hasEnoughLimit(customer.getName(), loan.getLoanAmount())) {
            logger.info("Customer has enough limit.");
            throw new IllegalArgumentException("Customer does not have enough credit limit for this loan");
        }

        if (!VALID_INSTALLMENT_NUMBERS.contains(loan.getNumberOfInstallment())) {
            logger.info("Customer has invalid installment number.");
            throw new IllegalArgumentException("Invalid number of installments. Must be one of: " + VALID_INSTALLMENT_NUMBERS);
        }

        if (loan.getInterestRate().compareTo(MIN_INTEREST_RATE) < 0 || loan.getInterestRate().compareTo(MAX_INTEREST_RATE) > 0) {
            logger.info("Customer has invalid interest rate.");
            throw new IllegalArgumentException("Interest rate must be between " + MIN_INTEREST_RATE + " and " + MAX_INTEREST_RATE);
        }

        logger.info("Starting creating loan value.");
        Loan loanValue= Loan.builder()
                .loanAmount(loan.getLoanAmount())
                .isPaid(false)
                .customer(customer)
                .numberOfInstallment(loan.getNumberOfInstallment())
                .installments(createInstallments(loan.getLoanAmount(), loan.getInterestRate(),loan.getNumberOfInstallment()))
                .createDate(LocalDate.now())
                .build();
        logger.info("Loan value created: {}", loanValue);

        logger.info("Saving loan value has been started.");
        return loanRepository.save(loanValue);

    }

    private List<Installment> createInstallments( BigDecimal amount,BigDecimal interestRate, Integer termInMonths) {
        BigDecimal totalAmount = amount.multiply(BigDecimal.ONE.add(interestRate));
        BigDecimal installmentAmount = totalAmount.divide(BigDecimal.valueOf(termInMonths), RoundingMode.HALF_UP);
        List<Installment> installments = new ArrayList<>();

        for (int i = 1; i <= termInMonths; i++) {
            Installment installment = Installment.builder()
                    .installmentNumber(i)
                    .amount(installmentAmount)
                    .dueDate(LocalDate.now().plusMonths(i).withDayOfMonth(1))
                    .build();
            installments.add(installment);
        }

        return installments;
    }
}
