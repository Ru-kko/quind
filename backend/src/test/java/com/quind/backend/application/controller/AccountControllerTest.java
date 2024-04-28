package com.quind.backend.application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import com.quind.backend.domain.dto.AccountRegitration;
import com.quind.backend.domain.dto.PageResponse;
import com.quind.backend.domain.model.account.Account;
import com.quind.backend.domain.model.account.AccountSatus;
import com.quind.backend.domain.model.account.AccountType;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = { "/dataDrop.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = { "/dataInit.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
public class AccountControllerTest {
  @LocalServerPort
  private Integer port;
  private String HOST;

  @Autowired
  private TestRestTemplate restTemplate;
  @Autowired
  private JdbcTemplate jdbcTemplate;

  @BeforeAll
  void initialize() {
    this.HOST = "http://localhost:" + this.port + "/account";
  }

  @Test
  void testCancelAccount() {
    Long valid = 5300032153L,
        inValid = 5300454524L;

    var response = restTemplate.postForEntity(HOST + "/" + inValid + "/cancel", Void.class, null);
    assertNotEquals(HttpStatus.OK, response.getStatusCode());

    response = restTemplate.postForEntity(HOST + "/" + valid + "/cancel", Void.class, null);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    AccountSatus status = jdbcTemplate.queryForObject("SELECT account_status FROM account WHERE account_id = ?",
        AccountSatus.class, valid);
    assertEquals(AccountSatus.CANCELED, status);
  }

  @Test
  void testCreate() {
    UUID owner = UUID.fromString("17e62166-fc85-86df-a4d1-bc0e1742c08b");
    AccountRegitration register = new AccountRegitration(owner, AccountType.CHECKING);

    var response = restTemplate.postForEntity(HOST, register, Account.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    assertEquals(response.getBody().getAccountType(), register.getType());
    assertEquals(response.getBody().getAccountStatus(), AccountSatus.ACTIVE);
    assertEquals(owner, response.getBody().getOwner().getClientId());
  }

  @Test
  void testDisableAccount() {
    Long valid = 5300032153L,
        inValid = 5300054001L;

    var response = restTemplate.postForEntity(HOST + "/" + inValid + "/disable", Void.class, null);
    assertNotEquals(HttpStatus.OK, response.getStatusCode());

    response = restTemplate.postForEntity(HOST + "/" + valid + "/disable", Void.class, null);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    AccountSatus status = jdbcTemplate.queryForObject("SELECT account_status FROM account WHERE account_id = ?",
        AccountSatus.class, valid);
    assertEquals(AccountSatus.INACTIVE, status);
  }

  @Test
  void testEnableAccount() {
    Long valid = 3300000003L,
        inValid = 5300054553L;

    var response = restTemplate.postForEntity(HOST + "/" + inValid + "/enable", Void.class, null);
    assertNotEquals(HttpStatus.OK, response.getStatusCode());

    response = restTemplate.postForEntity(HOST + "/" + valid + "/enable", Void.class, null);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    AccountSatus status = jdbcTemplate.queryForObject("SELECT account_status FROM account WHERE account_id = ?",
        AccountSatus.class, valid);
    assertEquals(AccountSatus.ACTIVE, status);
  }

  @Test
  void testGetAccount() {
    Long id = 3300150001L;

    Account account = restTemplate.getForEntity(HOST + "/" + id, Account.class).getBody();
    BigDecimal balance = jdbcTemplate.queryForObject("SELECT balance FROM account WHERE account_id = ?",
        BigDecimal.class, id);

    assertTrue(account.getBalance().equals(balance));
  }

  @Test
  void testGetClientAccounts() {
    UUID clientId = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");
    var response = restTemplate.getForEntity(HOST + "/client/" + clientId.toString(), PageResponse.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    Integer fromDb = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM account WHERE client_id = ?", Integer.class, clientId);
    assertEquals(response.getBody().getTotalItems().intValue(), fromDb);
  }
}
