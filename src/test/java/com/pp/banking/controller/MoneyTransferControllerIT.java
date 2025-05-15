package com.pp.banking.controller;

import com.pp.banking.security.JwtTokenProvider;
import com.pp.banking.service.AccountService;
import com.pp.banking.service.MoneyTransferService;
import com.pp.banking.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class MoneyTransferControllerIT {

	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.5")
		.withDatabaseName("banking")
		.withUsername("test")
		.withPassword("test");
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private MoneyTransferService moneyTransferService;

	@DynamicPropertySource
	static void postgresProperties(DynamicPropertyRegistry registry) {
		postgres.start();
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@BeforeEach
	void setUp() {
		when(jwtTokenProvider.getId("mocked-jwt")).thenReturn(1L);
	}

	@Test
	@WithMockUser
	void shouldTransferMoneySuccessfully() throws Exception {
		mockMvc.perform(post("/api/v1/transactions/2")
				.header("Authorization", "Bearer mocked-jwt")
				.param("amount", "100.00")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		verify(moneyTransferService).transferMoney(1L, 2L, new BigDecimal("100.00"));
	}

	@Test
	void shouldReturnUnauthorizedWhenMissingToken() throws Exception {
		mockMvc.perform(post("/api/v1/transactions/2")
				.param("amount", "100.00")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().is4xxClientError());
	}

	@TestConfiguration
	static class MockBeansConfig {

		@Bean
		@Primary
		public JwtTokenProvider jwtTokenProvider() {
			return mock(JwtTokenProvider.class);
		}

		@Bean
		@Primary
		public MoneyTransferService moneyTransferService() {
			return mock(MoneyTransferService.class);
		}

		@Bean
		@Primary
		public UserService userService() {
			return mock(UserService.class);
		}

		@Bean
		@Primary
		public AccountService accountService() {
			return mock(AccountService.class);
		}

	}

}
