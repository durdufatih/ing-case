package com.fatihdurdu.loancase.security;


import com.fatihdurdu.loancase.model.entity.Customer;
import com.fatihdurdu.loancase.model.entity.Loan;
import com.fatihdurdu.loancase.model.entity.Role;
import com.fatihdurdu.loancase.model.entity.User;
import com.fatihdurdu.loancase.repository.LoanRepository;
import com.fatihdurdu.loancase.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerAccessEvaluator {

    private final UserRepository userRepository;
    private final LoanRepository loanRepository;

    public boolean hasAccessToCustomer(Long customerId) {
        if (customerId == null) {
            return false;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));

        // Admins can access any customer
        if (user.getRole() == Role.ADMIN) {
            return true;
        }

        // Customers can only access their own data
        if (user.getRole() == Role.CUSTOMER) {
            Customer userCustomer = user.getCustomer();
            return userCustomer != null && userCustomer.getId().equals(customerId);
        }

        return false;
    }

    public boolean hasAccessToLoan(Long loanId) {
        if (loanId == null) {
            return false;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));

        // Admins can access any loan
        if (user.getRole() == Role.ADMIN) {
            return true;
        }

        // Customers can only access their own loans
        if (user.getRole() == Role.CUSTOMER) {
            Loan loan = loanRepository.findById(loanId)
                    .orElseThrow(() -> new IllegalArgumentException("Loan not found: " + loanId));

            Customer userCustomer = user.getCustomer();
            return userCustomer != null && loan.getCustomer() != null &&
                    userCustomer.getId().equals(loan.getCustomer().getId());
        }

        return false;
    }

    public Long getCurrentUserCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));

        return user.getCustomer() != null ? user.getCustomer().getId() : null;
    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));

        return user.getRole() == Role.ADMIN;
    }
}


