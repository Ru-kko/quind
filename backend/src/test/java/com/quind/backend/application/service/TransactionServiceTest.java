package com.quind.backend.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import com.quind.backend.domain.model.transaction.Transaction;
import com.quind.backend.infra.errors.InsufficientBalance;
import com.quind.backend.infra.errors.QuindError;

@TestInstance(Lifecycle.PER_CLASS)
@Sql(scripts = { "/dataDrop.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = { "/dataInit.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest
public class TransactionServiceTest {
  @Autowired
  private TransactionService transactionService;
  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  void testDeposit() throws QuindError {
    Long account = 3300150001L;
    BigDecimal baseBalance = new BigDecimal("1000.00");

    Transaction deposited = transactionService.deposit(account, new BigDecimal("1000"));

    assertNotNull(deposited);
    assertEquals(account, deposited.getTarget().getAccountId());
    assertEquals(baseBalance.add(new BigDecimal("1000")), deposited.getTarget().getBalance());

    Long errorAccount = 5300054553L;
    assertThrows(QuindError.class, () -> transactionService.deposit(errorAccount, baseBalance));
  }

  @Test
  void testGetAllFromAccount() {
    Long account = 3300150001L;
    Page<Transaction> transactions = transactionService.getAllFromAccount(account, 1);

    assertNotNull(transactions);
    assertTrue(transactions.getTotalElements() >= 1);
  }

  @Test
  void testGetbyid() {
    UUID id = UUID.fromString("72c3c5d5-23dc-4d2c-8711-cbfcf72f4550");
    Transaction transaction = transactionService.getbyid(id);

    assertNotNull(transaction);
    assertTrue(new BigDecimal("1000.00").equals(transaction.getAmount()));
  }

  @Test
  void testTransfer() throws QuindError {
    Long origin = 5300000003L, destination = 5300032153L;
    BigDecimal overedAmmount = new BigDecimal("110000"),
        normalAmmount = new BigDecimal("1000.00"),
        base_ammount = new BigDecimal("10000.00");

    // ! more than current balance
    assertThrows(QuindError.class, () -> transactionService.transfer(origin, destination, overedAmmount));

    Transaction result = transactionService.transfer(origin, destination, normalAmmount);
    assertEquals(base_ammount.subtract(normalAmmount), result.getTarget().getBalance());
    assertEquals(new BigDecimal("0").add(normalAmmount), result.getTransferTarget().getBalance());

    // ? On db
    BigDecimal originAm = jdbcTemplate.queryForObject(
        "SELECT balance FROM account WHERE account_id = ?",
        BigDecimal.class, origin);
    BigDecimal destinationAm = jdbcTemplate.queryForObject(
        "SELECT balance FROM account WHERE account_id = ?",
        BigDecimal.class, destination);

    
    assertEquals(base_ammount.subtract(normalAmmount), originAm);
    assertEquals(new BigDecimal("0").add(normalAmmount), destinationAm);
  }

  @Test
  void testWitdrawal() throws QuindError {
    Long account = 3301003002L;
    BigDecimal baseBalance = new BigDecimal("2500.00");
    BigDecimal result = baseBalance.subtract(new BigDecimal("1000.00"));

    Transaction withdraweld = transactionService.witdrawal(account, new BigDecimal("1000"));

    assertNotNull(withdraweld);
    assertEquals(account, withdraweld.getTarget().getAccountId());
    assertEquals(result, withdraweld.getTarget().getBalance());

    // * check in db
    BigDecimal ammoutResult = jdbcTemplate.queryForObject("SELECT balance FROM account WHERE account_id = ?",
        BigDecimal.class, withdraweld.getTarget().getAccountId());
    assertEquals(ammoutResult, result);

    Long errorAccount = 5300054553L;
    assertThrows(QuindError.class,
        () -> transactionService.witdrawal(errorAccount, baseBalance)); // ! cant deposit on not actived accounts

    Long notBalance = 5300032153L;
    assertThrows(InsufficientBalance.class,
        () -> transactionService.witdrawal(notBalance, baseBalance));

  }
}
