package com.quind.backend.application.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

import com.quind.backend.domain.dto.AccountRegitration;
import com.quind.backend.domain.model.account.Account;
import com.quind.backend.domain.model.account.AccountSatus;
import com.quind.backend.domain.model.account.AccountType;
import com.quind.backend.infra.errors.NotFoundError;
import com.quind.backend.infra.errors.QuindError;

@TestInstance(Lifecycle.PER_CLASS)
@Sql(scripts = { "/dataDrop.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = { "/dataInit.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest
public class AccountServiceTest {
  @Autowired
  private AccountService accountService;
  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  void testCreate() throws NotFoundError {
    UUID ownerId = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");
    AccountRegitration registration = new AccountRegitration(ownerId, AccountType.SAVING);

    Account resultAccount = accountService.create(registration);

    assertEquals(BigDecimal.ZERO, resultAccount.getBalance());
    assertEquals(AccountSatus.ACTIVE, resultAccount.getAccountStatus());
  }

  @Test
  void testDeposit() throws QuindError {
    Long numberAccount = 3301003002L;
    Account original = accountService.getAccount(numberAccount);

    Account withdrweld = accountService.deposit(numberAccount, new BigDecimal("500"));
    assertEquals(original.getBalance().add(new BigDecimal("500")), withdrweld.getBalance());
  }

  @Test
  void testDiable() throws QuindError {
    Long cantDisable = 5300000003L;

    assertThrows(QuindError.class, () -> accountService.diable(cantDisable));

    Long toDisable = 5300032153L;
    assertDoesNotThrow(() -> accountService.diable(toDisable));

    AccountSatus satus = jdbcTemplate.queryForObject("SELECT account_status FROM account WHERE account_id = ?",
        AccountSatus.class, toDisable);

    assertEquals(AccountSatus.INACTIVE, satus);
  }

  @Test
  void testGetAccount() {
    Long accountNumber = 3300150001L;
    Account account = accountService.getAccount(accountNumber);

    assertNotNull(account);
    assertEquals(account.getOwner().getFirstName(), "John");
  }

  @Test
  void testGetClientAccounts() {
    Page<Account> result = accountService.getClientAccounts(UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"), 1);
    assertTrue(result.getTotalElements() >= 2);
  }

  @Test
  void testWithdrawal() throws QuindError {
    Long numberAccount = 3300150001L;
    Account original = accountService.getAccount(numberAccount);

    assertThrows(QuindError.class, () -> accountService.withdrawal(numberAccount, new BigDecimal("10000.10")));

    Account withdrweld = accountService.withdrawal(numberAccount, new BigDecimal("500"));
    assertEquals(original.getBalance().subtract(new BigDecimal("500")), withdrweld.getBalance());
  }
}
