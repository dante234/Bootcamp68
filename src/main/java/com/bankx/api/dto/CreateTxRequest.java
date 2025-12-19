package com.bankx.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

/**
 * Represents a transaction request for creating a new transaction.
 *
 * <p>This DTO class contains the necessary data to create a transaction. It is used in the
 * {@link com.bankx.api.TransactionController#create(CreateTxRequest)} endpoint.
 *
 * @author Your Name (add your email address here)
 * @version 1.0
 * @since 1.0
 */

@Data
public class CreateTxRequest {
  @NotBlank
    private String accountNumber;
  @NotBlank private String type; // CREDIT/DEBIT
  @NotNull
  @DecimalMin("0.01") private BigDecimal amount;
}