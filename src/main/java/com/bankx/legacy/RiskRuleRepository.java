package com.bankx.legacy;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for managing {@link RiskRule} entities.
 */
public interface RiskRuleRepository extends JpaRepository<RiskRule, Long> {

  Optional<RiskRule> findFirstByCurrency(String currency);
}
