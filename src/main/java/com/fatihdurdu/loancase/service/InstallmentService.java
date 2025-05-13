package com.fatihdurdu.loancase.service;

import com.fatihdurdu.loancase.model.dto.InstallmentResponse;
import com.fatihdurdu.loancase.model.dto.PayResponse;
import com.fatihdurdu.loancase.model.entity.Customer;
import com.fatihdurdu.loancase.model.entity.Installment;
import com.fatihdurdu.loancase.model.entity.Loan;
import com.fatihdurdu.loancase.repository.InstallmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service class for handling installment-related operations such as retrieving and paying installments.
 */
@Service
@AllArgsConstructor
public class InstallmentService {

    private final InstallmentRepository installmentRepository;
    private final LoanService loanService;
    private final CustomerService customerService;

   /**
      * Retrieves a list of installment responses for a given loan ID.
      *
      * @param loanId the ID of the loan whose installments are to be retrieved
      * @return a list of InstallmentResponse objects associated with the specified loan
      * @throws IllegalArgumentException if the loan is not found
      */
    public List<InstallmentResponse> getInstallmentsByLoanId(Long loanId) {
       Loan loan = loanService.findById(loanId);
       List<Installment> installmentList=installmentRepository.findAll();
       if(Objects.isNull(loan)){
           throw new IllegalArgumentException("loan not found");
       }
       return installmentRepository.findByLoanId(loanId).stream().map(this::convertToInstallmentResponse).toList();
    }


    /**
     * Pays up to three unpaid installments for the specified loan using the provided payment amount.
     *
     * @param loanId the ID of the loan whose installments are to be paid
     * @param paymentAmount the total amount to be used for paying installments
     * @return a PayResponse containing payment details such as total amount, paid amount, unpaid amount, pay count, and whether all installments are paid
     * @throws IllegalArgumentException if the loan is not found
     */
    public PayResponse payInstallmentsByLoan(Long loanId, BigDecimal paymentAmount) {
        Loan loan = loanService.findById(loanId);
        if(Objects.isNull(loan)){throw new IllegalArgumentException("loan not found");}
        Customer customer = loan.getCustomer();

        List<Installment> unpaidInstallments = loan.getInstallments().stream()
                .filter(i -> (Boolean.FALSE.equals(i.getIsPaid())||Objects.isNull(i.getIsPaid())))
                .sorted(Comparator.comparing(Installment::getDueDate))
                .collect(Collectors.toList());


        int paidCount = 0;
        BigDecimal totalAmount = paymentAmount;
        BigDecimal totalPaid = BigDecimal.ZERO;

        for (Installment inst : unpaidInstallments) {
            if(paidCount==3) break;
            if (paymentAmount.compareTo(inst.getAmount()) >= 0) {
                inst.setIsPaid(true);
                inst.setPaidAmount(inst.getAmount());
                inst.setPaymentDate(LocalDate.now());
                installmentRepository.save(inst);
                paymentAmount = paymentAmount.subtract(inst.getAmount());
                totalPaid = totalPaid.add(inst.getAmount());
                paidCount++;
            } else {
                break;
            }
        }



       boolean loanPaidAll = installmentRepository.findByLoanId(loanId)
            .stream()
            .allMatch(installment -> Boolean.TRUE.equals(installment.getIsPaid()));
        loan.setIsPaid(loanPaidAll);
        loan=loanService.save(loan);


        if (loanPaidAll) {
            customer.setUsedCreditLimit(customer.getUsedCreditLimit().subtract(loan.getLoanAmount()));
            customer.setCreditLimit(customer.getCreditLimit().add(loan.getLoanAmount()));
            customerService.saveCustomer(customer);
        }

        return PayResponse.builder().totalAmount(totalAmount).paidAmount(totalPaid).payAll(loanPaidAll).unpaidAmount(totalAmount.subtract(totalPaid)).payCount(paidCount).build();
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
