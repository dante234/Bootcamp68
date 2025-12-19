package com.bankx.repo;

import com.bankx.domain.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;


/**
 * Repository interface for {@link Transaction} entities.
 *
 * <p>This interface extends {@link ReactiveMongoRepository},
 * providing basic CRUD operations.
 *
 * @see ReactiveMongoRepository
 */

public interface TransactionRepository extends
        ReactiveMongoRepository<Transaction, String> {
  Flux<Transaction> findByAccountIdOrderByTimestampDesc(String accountId);
}
