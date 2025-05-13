package com.fatihdurdu.loancase.controller;

import com.fatihdurdu.loancase.model.dto.CreateLoanRequest;
import com.fatihdurdu.loancase.model.dto.LoanResponse;
import com.fatihdurdu.loancase.security.UserDetailsImpl;
import com.fatihdurdu.loancase.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(@RequestBody CreateLoanRequest request) {
        try {
            validateUserAccess(request.getCustomerId());
            LoanResponse loanResponse = loanService.saveLoan(request);
            return new ResponseEntity<>(loanResponse, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input: " + e.getMessage());
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found: " + e.getMessage());

        }
    }

    @GetMapping
    public ResponseEntity<LoanResponse> listLoans(
            @RequestParam Long customerId,
            @RequestParam(required = false) Integer numberOfInstallment,
            @RequestParam(required = false) Boolean isPaid) {

        validateUserAccess(customerId);
        // Delegate to service to handle filtering logic
        return ResponseEntity.ok(
            loanService.getLoansByFilters(customerId, numberOfInstallment, isPaid)
        );
    }

    private void validateUserAccess(Long requestedCustomerId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        // Admin can access any customer data
        if (isAdmin) {
            return;
        }

        // For regular customers, verify they're accessing their own data
        if (auth.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            Long authenticatedCustomerId = userDetails.getCustomerId();

            if (authenticatedCustomerId == null || !authenticatedCustomerId.equals(requestedCustomerId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own data");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
    }



}
