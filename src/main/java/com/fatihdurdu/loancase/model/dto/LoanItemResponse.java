package com.fatihdurdu.loancase.model.dto;

import com.fatihdurdu.loancase.model.entity.Installment;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class LoanItemResponse {

    private Long id;
    private BigDecimal loanAmount;
    private Integer numberOfInstallment;
    private LocalDate createDate;
    private BigDecimal interestRate;
    private Boolean isPaid;
    private List<InstallmentResponse> installments;
}
