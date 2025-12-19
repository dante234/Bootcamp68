package com.bankx;

import com.bankx.legacy.DataSeeder;
import com.bankx.legacy.RiskRuleRepository;
import com.bankx.repo.AccountRepository;
import com.bankx.repo.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest
class BankxApplicationTests {

	@MockBean
	private WebClient riskWebClient;

	@MockBean
	private AccountRepository accountRepository;

	@MockBean
	private TransactionRepository transactionRepository;

	@MockBean
	private RiskRuleRepository riskRuleRepository;

	@MockBean
	private DataSeeder dataSeeder;

	@Test
	void contextLoads() {
	}

}
