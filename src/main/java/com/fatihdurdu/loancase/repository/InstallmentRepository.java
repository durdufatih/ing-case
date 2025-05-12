package com.fatihdurdu.loancase.repository;

import com.fatihdurdu.loancase.model.entity.Installment;
import com.fatihdurdu.loancase.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstallmentRepository extends JpaRepository<Installment, Long> {
    
    // Find installments by loan
    List<Installment> findByLoan(Loan loan);
    
    // Find installments by loan ordered by installment number
    List<Installment> findByLoanOrderByInstallmentNumber(Loan loan);
}