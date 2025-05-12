package com.fatihdurdu.loancase.controller;

import com.fatihdurdu.loancase.model.entity.Installment;
import com.fatihdurdu.loancase.repository.InstallmentRepository;
import com.fatihdurdu.loancase.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/installments")
public class InstallmentController {

    private final InstallmentRepository installmentRepository;
    private final LoanService loanService;

    @Autowired
    public InstallmentController(InstallmentRepository installmentRepository, LoanService loanService) {
        this.installmentRepository = installmentRepository;
        this.loanService = loanService;
    }

    @GetMapping
    public ResponseEntity<List<Installment>> getAllInstallments() {
        return ResponseEntity.ok(installmentRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Installment> getInstallmentById(@PathVariable Long id) {
        return installmentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<Installment>> getInstallmentsByLoanId(@PathVariable Long loanId) {
        return loanService.getLoanById(loanId)
                .map(loan -> ResponseEntity.ok(installmentRepository.findByLoanOrderByInstallmentNumber(loan)))
                .orElse(ResponseEntity.notFound().build());
    }
}