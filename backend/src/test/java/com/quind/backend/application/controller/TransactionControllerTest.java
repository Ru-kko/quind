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

import com.quind.backend.domain.dto.PageResponse;
import com.quind.backend.domain.model.transaction.Transaction;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = { "/dataDrop.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = { "/dataInit.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
public class TransactionControllerTest {
  @LocalServerPort
  private Integer port;
  private String HOST;

  @Autowired
  private TestRestTemplate restTemplate;
  @Autowired
  private JdbcTemplate jdbcTemplate;

  @BeforeAll
  void initialize() {
    this.HOST = "http://localhost:" + this.port + "/transaction";
  }

  @Test
  void testDeposit() {
    Long cantDeposit = 5300054553L, depositable = 3300150001L;
    BigDecimal toAdd = new BigDecimal("455"), inital = new BigDecimal("1000.00");

    var response = restTemplate.postForEntity(HOST + "/deposit/" + cantDeposit + "?amount=" + toAdd.toString(), null,
        Transaction.class);

    assertNotEquals(response.getStatusCode(), HttpStatus.OK);

    response = restTemplate.postForEntity(HOST + "/deposit/" + depositable + "?amount=" + toAdd.toString(), null,
        Transaction.class);

    assertEquals(response.getStatusCode(), HttpStatus.OK);
    assertEquals(response.getBody().getAmount(), toAdd);

    BigDecimal newValue = jdbcTemplate.queryForObject("SELECT balance FROM account WHERE account_id = ?",
        BigDecimal.class, depositable);

    assertTrue(inital.add(toAdd).equals(newValue));
  }

  @Test
  void testGetAllFromAccount() {
    Long account = 3300150001L;

    var response = restTemplate.getForEntity(HOST + "/account/" + account, PageResponse.class);
    Integer countOndb = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM transaction WHERE target_account_id = ?",
        Integer.class, account);

    assertEquals(countOndb, response.getBody().getTotalItems().intValue());
  }

  @Test
  void testGetByIDEntity() {
    UUID id = UUID.fromString("72c3c5d5-23dc-4d2c-8711-cbfcf72f4550");
    var response = restTemplate.getForEntity(HOST + "/" + id.toString(), Transaction.class);

    BigDecimal realAmount = jdbcTemplate.queryForObject("SELECT amount FROM transaction WHERE id = ?", BigDecimal.class,
        id);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(realAmount.equals(response.getBody().getAmount()));
  }

  @Test
  void testTransfer() {
    Long origin = 3301003002L, destination = 5300032153L;
    BigDecimal amount = new BigDecimal("500"),
        originInitial = new BigDecimal("2500.00"),
        destinationInitial = new BigDecimal("0.00");

    var response = restTemplate.postForEntity(
        HOST + "/transfer/" + origin + "/" + destination + "?amount=" + amount.toString(), null, Transaction.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    BigDecimal originResult = jdbcTemplate.queryForObject("SELECT balance FROM account WHERE account_id = ?",
        BigDecimal.class, origin);
    BigDecimal destinationResult = jdbcTemplate.queryForObject("SELECT balance FROM account WHERE account_id = ?",
        BigDecimal.class, destination);

    assertTrue(originInitial.subtract(amount).equals(originResult));
    assertTrue(destinationInitial.add(amount).equals(destinationResult));
  }

  @Test
  void testWithdrawal() {
    Long insuficent = 5300054553L, chechking = 3300150001L, normal = 5300454524L;
    BigDecimal toAdd = new BigDecimal("3000.00"),
        initalNormal = new BigDecimal("7500.00"),
        initialChecking = new BigDecimal("1000.00");

    // * Not enought founds
    var noEnought = restTemplate.postForEntity(HOST + "/withdrawal/" + insuficent + "?amount=" + toAdd.toString(), null,
        Transaction.class);
    assertNotEquals(noEnought.getStatusCode(), HttpStatus.OK);

    // * wilthdrawal on saving
    var savingWithdrawal = restTemplate.postForEntity(HOST + "/withdrawal/" + normal + "?amount=" + toAdd.toString(), null,
        Transaction.class);
    assertEquals(savingWithdrawal.getStatusCode(), HttpStatus.OK);
    assertEquals(savingWithdrawal.getBody().getAmount(), toAdd);

    BigDecimal newValue = jdbcTemplate.queryForObject("SELECT balance FROM account WHERE account_id = ?",
        BigDecimal.class, normal);
    assertTrue(initalNormal.subtract(toAdd).equals(newValue));

    // * wilthdrawal on checking
    var checkingWithdrawal = restTemplate.postForEntity(HOST + "/withdrawal/" + chechking + "?amount=" + toAdd.toString(), null,
        Transaction.class);

    assertEquals(checkingWithdrawal.getStatusCode(), HttpStatus.OK);
    assertEquals(checkingWithdrawal.getBody().getAmount(), toAdd);

    newValue = jdbcTemplate.queryForObject("SELECT balance FROM account WHERE account_id = ?",
        BigDecimal.class, chechking);

    assertTrue(initialChecking.subtract(toAdd).equals(newValue));
  }
}
