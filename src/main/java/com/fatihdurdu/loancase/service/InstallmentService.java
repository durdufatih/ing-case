package com.fatihdurdu.loancase.service;

import com.fatihdurdu.loancase.model.dto.InstallmentResponse;
import com.fatihdurdu.loancase.model.entity.Installment;
import com.fatihdurdu.loancase.model.entity.Loan;
import com.fatihdurdu.loancase.repository.InstallmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class InstallmentService {

    private final InstallmentRepository installmentRepository;
    private final LoanService loanService;

    public List<InstallmentResponse> getInstallmentsByLoanId(Long loanId) {
       Loan loan = loanService.findById(loanId);
       List<Installment> installmentList=installmentRepository.findAll();
       if(Objects.isNull(loan)){
           throw new IllegalArgumentException("loan not found");
       }
       return installmentRepository.findByLoanId(loanId).stream().map(this::convertToInstallmentResponse).toList();
    }

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
