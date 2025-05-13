package com.fatihdurdu.loancase.controller;

import com.fatihdurdu.loancase.model.dto.InstallmentResponse;
import com.fatihdurdu.loancase.service.InstallmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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

}