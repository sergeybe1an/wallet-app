package com.sbelan.walletapp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sbelan.walletapp.model.api.Balance;
import com.sbelan.walletapp.model.api.CreditRequest;
import com.sbelan.walletapp.model.api.CreditResponse;
import com.sbelan.walletapp.model.api.DebitRequest;
import com.sbelan.walletapp.model.api.DebitResponse;
import com.sbelan.walletapp.model.api.Transactions;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(locations = {"classpath:application-test.yml"})
class WalletAppApplicationTests {

	private static final ObjectMapper mapper =
		(new ObjectMapper()).registerModule(new JavaTimeModule());

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void get_balance() {
		ResponseEntity<Balance> balance = this.restTemplate
			.getForEntity("/wallet/api/v1/balance/3333", Balance.class);

		assertEquals(BigDecimal.valueOf(100),
			Objects.requireNonNull(balance.getBody()).getBalance());
	}

	@Test
	public void debit_some_money() {
		var debitRequest = read(new ClassPathResource("json/debit_request.json"), DebitRequest.class);
		ResponseEntity<DebitResponse> debitResponse = this.restTemplate
			.postForEntity("/wallet/api/v1/debit", debitRequest, DebitResponse.class);

		assertEquals(BigDecimal.valueOf(96),
			Objects.requireNonNull(debitResponse.getBody()).getBalance());
	}

	@Test
	public void credit_some_money() {
		var creditRequest = read(new ClassPathResource("json/credit_request.json"), CreditRequest.class);
		ResponseEntity<CreditResponse> creditResponse = this.restTemplate
			.postForEntity("/wallet/api/v1/credit", creditRequest, CreditResponse.class);

		assertEquals(BigDecimal.valueOf(105),
			Objects.requireNonNull(creditResponse.getBody()).getBalance());
	}

	@Test
	public void debit_too_much_money() {
		var debitRequest = read(new ClassPathResource("json/debit_request_error.json"), DebitRequest.class);
		ResponseEntity<DebitResponse> debitResponse = this.restTemplate
			.postForEntity("/wallet/api/v1/debit", debitRequest, DebitResponse.class);

		assertEquals(HttpStatus.BAD_REQUEST, debitResponse.getStatusCode());
	}

	@Test
	public void get_transcactions_history() {
		var debitRequest1 = read(new ClassPathResource("json/debit_request.json"), DebitRequest.class);
		this.restTemplate.postForEntity("/wallet/api/v1/debit", debitRequest1, DebitResponse.class);

		var debitRequest2 = read(new ClassPathResource("json/debit_request.json"), DebitRequest.class);
		this.restTemplate.postForEntity("/wallet/api/v1/debit", debitRequest2, DebitResponse.class);

		var creditRequest1 = read(new ClassPathResource("json/credit_request.json"), CreditRequest.class);
		this.restTemplate.postForEntity("/wallet/api/v1/credit", creditRequest1, CreditResponse.class);

		ResponseEntity<Transactions> transactionsHistory = this.restTemplate
			.getForEntity("/wallet/api/v1/history/3333", Transactions.class);

		assertEquals(3, Objects.requireNonNull(transactionsHistory.getBody()).getTransactions().size());
	}

	@Test
	public void get_empty_transactions_history() {
		ResponseEntity<Transactions> transactionsHistory = this.restTemplate
			.getForEntity("/wallet/api/v1/history/3333", Transactions.class);

		assertEquals(0, Objects.requireNonNull(transactionsHistory.getBody()).getTransactions().size());
	}

	private static <T> T read(Resource resource, Class<T> tClass) {
		try {
			return mapper.readValue(resource.getInputStream(), tClass);
		} catch (IOException var3) {
			throw new TestResourceException(var3);
		}
	}

	private static class TestResourceException extends RuntimeException {
		public TestResourceException(Throwable cause) {
			super(cause);
		}
	}
}
