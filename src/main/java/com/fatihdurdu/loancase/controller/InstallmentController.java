package com.fatihdurdu.loancase.controller;

import com.fatihdurdu.loancase.model.dto.InstallmentResponse;
import com.fatihdurdu.loancase.model.dto.PayResponse;
import com.fatihdurdu.loancase.security.UserDetailsImpl;
import com.fatihdurdu.loancase.service.InstallmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/installments")
@AllArgsConstructor
public class InstallmentController {

    private final InstallmentService installmentService;


    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<InstallmentResponse>> getInstallmentsByLoanId(@PathVariable Long loanId) {
        try {
            return ResponseEntity.ok(installmentService.getInstallmentsByLoanId(loanId));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input: " + e.getMessage());
        }
    }

    @PostMapping("/loan/{loanId}")
    public ResponseEntity<PayResponse> payInstallments(
            @PathVariable Long loanId,
            @RequestParam BigDecimal amount) {
        try {
            PayResponse response = installmentService.payInstallmentsByLoan(loanId, amount);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input: " + e.getMessage());
        }
    }

}