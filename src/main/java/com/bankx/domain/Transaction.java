package com.bankx.domain;

import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Represents a transaction.
 *
 * <p>This class stores the essential data of a transaction.
 * The transaction is identified by its id,
 * which is a UUID. The account ID identifies
 * the account associated with the transaction, the type identifies
 * if the transaction is a credit or a debit
 * (CREDIT or DEBIT), the amount is the amount of the transaction and
 * the timestamp is the time the transaction
 * was performed. The status indicates if the transaction was accepted
 * or not (OK or REJECTED), and the reason
 * indicates the reason for the rejection if applicable.
 *
 * @author Your Name (add your email address here)
 * @version 1.0
 * @since 1.0
 */

@Document("transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
  @Id
    private String id;
  private String accountId;
  private String type; // "CREDIT" or "DEBIT"
  private BigDecimal amount;
  private Instant timestamp;
  private String status; // "OK" or "REJECTED"
  private String reason; // null if OK
}
