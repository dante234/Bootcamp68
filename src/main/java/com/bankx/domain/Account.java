package com.bankx.domain;

import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Domain class representing an account.
 *
 * @author Your Name (add your email address here)
 * @version 1.0
 * @since 1.0
 */

@Document("accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
  @Id
  private String id;
  private String number;
  private String holderName;
  private String currency; // "PEN" / "USD"
  private BigDecimal balance;
}
