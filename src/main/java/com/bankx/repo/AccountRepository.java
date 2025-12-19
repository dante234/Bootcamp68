package com.bankx.repo;

import com.bankx.domain.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;


/**
 * Repository interface for {@link Account} entities.
 *
 * <p>This interface extends {@link ReactiveMongoRepository},
 * providing basic CRUD operations.
 *
 * @see ReactiveMongoRepository
 */

public interface AccountRepository extends ReactiveMongoRepository<Account,
        String> {
  Mono<Account> findByNumber(String number);
}
