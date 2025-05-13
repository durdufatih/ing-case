package com.fatihdurdu.loancase.service;

import com.fatihdurdu.loancase.model.dto.CreateLoanRequest;
import com.fatihdurdu.loancase.model.dto.InstallmentResponse;
import com.fatihdurdu.loancase.model.dto.LoanItemResponse;
import com.fatihdurdu.loancase.model.dto.LoanResponse;
import com.fatihdurdu.loancase.model.entity.Customer;
import com.fatihdurdu.loancase.model.entity.Installment;
import com.fatihdurdu.loancase.model.entity.Loan;
import com.fatihdurdu.loancase.repository.LoanRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Service class for managing loan-related operations such as creating, retrieving, and filtering loans.
 */
@Service
@AllArgsConstructor
public class LoanService {
    private static final Logger logger = LoggerFactory.getLogger(LoanService.class);

    private static final List<Integer> VALID_INSTALLMENT_NUMBERS = Arrays.asList(6, 9, 12, 24);
    private static final BigDecimal MIN_INTEREST_RATE = new BigDecimal("0.1");
    private static final BigDecimal MAX_INTEREST_RATE = new BigDecimal("0.5");

    private final LoanRepository loanRepository;
    private final CustomerService customerService;

    /**
     * Finds a loan by its unique identifier.
     *
     * @param id the unique identifier of the loan
     * @return the Loan entity if found, otherwise null
     */
    public Loan findById(Long id) {
        return loanRepository.findById(id).orElse(null);
    }

    /**
     * Saves a loan entity to the database.
     *
     * @param loan the loan entity to be saved
     * @return the saved Loan entity
     */
    public Loan save(Loan loan) {
        if(Objects.isNull(loan)) throw new IllegalArgumentException("Loan is null");
        return loanRepository.save(loan);
    }

    /**
     * Creates and saves a new loan for a customer, including validation of input data,
     * updating the customer's credit limit, and generating loan installments.
     *
     * @param loan the request object containing loan details
     * @return a response object with the saved loan information
     */
    @Transactional
    public LoanResponse saveLoan(CreateLoanRequest loan) {

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
        Loan loanValue = Loan.builder()
                .loanAmount(loan.getLoanAmount())
                .isPaid(false)
                .customer(customer)
                .interestRate(loan.getInterestRate())
                .numberOfInstallment(loan.getNumberOfInstallment())
                .createDate(LocalDate.now())
                .build();
        logger.info("Loan value created: {}", loanValue);
        loanValue = loanRepository.save(loanValue);
        logger.info("Saving loan value has been started.");
        loanValue.setInstallments(createInstallments(loanValue, loan.getInterestRate(), loan.getNumberOfInstallment()));
        loanValue = loanRepository.save(loanValue);
        logger.info("Saving loan value has been finished.");

        customer.setUsedCreditLimit(loan.getLoanAmount());
        customer.setCreditLimit(customer.getCreditLimit().subtract(loan.getLoanAmount()));
        customerService.saveCustomer(customer);
        logger.info("Saving customer limit value has been finished.");
        return LoanResponse.builder()
                .customerName(customer.getName())
                .customerSurname(customer.getSurname())
                .customerLimit(customer.getCreditLimit())
                .loanItems(List.of(convertToLoanResponse(loanValue)))
                .build();

    }

    /**
     * Creates a list of installments for a given loan, interest rate, and term.
     *
     * @param loan         the loan entity for which installments are created
     * @param interestRate the interest rate applied to the loan
     * @param termInMonths the number of months (installments)
     * @return a list of generated Installment entities
     */
    private List<Installment> createInstallments(Loan loan, BigDecimal interestRate, Integer termInMonths) {
        BigDecimal totalAmount = loan.getLoanAmount().multiply(BigDecimal.ONE.add(interestRate));
        BigDecimal installmentAmount = totalAmount.divide(BigDecimal.valueOf(termInMonths), RoundingMode.HALF_UP);
        List<Installment> installments = new ArrayList<>();

        for (int i = 1; i <= termInMonths; i++) {
            Installment installment = Installment.builder()
                    .installmentNumber(i)
                    .amount(installmentAmount)
                    .loan(loan)
                    .dueDate(LocalDate.now().plusMonths(i).withDayOfMonth(1))
                    .build();
            installments.add(installment);
        }

        return installments;
    }

    /**
     * Retrieves loans for a customer filtered by number of installments and payment status.
     *
     * @param customerId          the ID of the customer
     * @param numberOfInstallment the number of installments to filter by (nullable)
     * @param isPaid              the payment status to filter by (nullable)
     * @return a LoanResponse containing the filtered loans for the customer
     */
    public LoanResponse getLoansByFilters(Long customerId, Integer numberOfInstallment, Boolean isPaid) {
        Customer customer = customerService.getCustomerById(customerId).orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        List<Loan> loans;
        if (Objects.nonNull(numberOfInstallment) && Objects.nonNull(isPaid)) {
            loans = loanRepository.findByCustomerAndNumberOfInstallmentAndIsPaid(customer, numberOfInstallment, isPaid);
        } else if (Objects.nonNull(numberOfInstallment)) {
            loans = loanRepository.findByCustomerAndNumberOfInstallment(customer, numberOfInstallment);
        } else if (Objects.nonNull(isPaid)) {
            loans = loanRepository.findByCustomerAndIsPaid(customer, isPaid);
        } else {
            loans = loanRepository.findByCustomer(customer);
        }

        LoanResponse loanResponse = LoanResponse.builder()
                .customerName(customer.getName())
                .customerSurname(customer.getSurname())
                .customerLimit(customer.getCreditLimit()).build();

        loanResponse.setLoanItems(loans.stream()
                .map(this::convertToLoanResponse)
                .toList());
        return loanResponse;
    }

    /**
     * Converts a `Loan` entity to a `LoanItemResponse` DTO.
     *
     * @param loan the `Loan` entity to convert
     * @return the corresponding `LoanItemResponse` DTO
     */
    private LoanItemResponse convertToLoanResponse(Loan loan) {
        return LoanItemResponse.builder()
                .id(loan.getId())
                .loanAmount(loan.getLoanAmount())
                .numberOfInstallment(loan.getNumberOfInstallment())
                .interestRate(loan.getInterestRate())
                .createDate(loan.getCreateDate())
                .isPaid(loan.getIsPaid())
                .installments(loan.getInstallments() != null 
                    ? loan.getInstallments().stream()
                        .map(this::convertToInstallmentResponse)
                        .collect(Collectors.toList())
                    : new ArrayList<>())
                .build();
    }

    /**
     * Converts an `Installment` entity to an `InstallmentResponse` DTO.
     *
     * @param installment the `Installment` entity to convert
     * @return the corresponding `InstallmentResponse` DTO
     */
    private InstallmentResponse convertToInstallmentResponse(Installment installment) {
        return InstallmentResponse.builder()
                .id(installment.getId())
                .amount(installment.getAmount())
                .dueDate(installment.getDueDate())
                .isPaid(installment.getIsPaid())
                .installmentNumber(installment.getInstallmentNumber())
                .paidAmount(installment.getPaidAmount())
                .paymentDate(installment.getPaymentDate())
                .build();
    }

}