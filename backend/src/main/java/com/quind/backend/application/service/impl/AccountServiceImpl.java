package com.quind.backend.application.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.quind.backend.application.service.AccountService;
import com.quind.backend.application.service.ClientService;
import com.quind.backend.domain.dto.AccountRegitration;
import com.quind.backend.domain.model.Client;
import com.quind.backend.domain.model.account.Account;
import com.quind.backend.domain.model.account.AccountSatus;
import com.quind.backend.domain.model.account.AccountType;
import com.quind.backend.infra.config.Properties;
import com.quind.backend.infra.errors.InsufficientBalance;
import com.quind.backend.infra.errors.NotFoundError;
import com.quind.backend.infra.errors.QuindError;
import com.quind.backend.infra.repositories.AccountRepository;

@Service
public class AccountServiceImpl implements AccountService {
  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private ClientService clientRepository;

  @Autowired
  private Properties config;

  @Override
  public Page<Account> getClientAccounts(UUID clientid, Integer page) {
    Pageable pg = PageRequest.of(page, config.getPageSize());
    return this.accountRepository.findByClientId(clientid, pg);
  }

  @Override
  public Account getAccount(Long accountNumber) {
    return accountRepository.findById(accountNumber).orElse(null);
  }

  @Override
  public Account create(AccountRegitration register) throws NotFoundError {
    Client owner = clientRepository.getById(register.getOwnerID());

    if (owner == null) {
      throw new NotFoundError(String.format("Can't find user with id '%s'", register.getOwnerID().toString()));
    }

    Account newAccount = new Account();
    newAccount.setAccountId(0L);
    newAccount.setBalance(BigDecimal.ZERO);
    newAccount.setAccountStatus(AccountSatus.ACTIVE);
    newAccount.setAccountType(register.getType());
    return accountRepository.save(newAccount);
  }

  @Override
  public void diable(Long accountNumber) throws QuindError {
    Account account = accountRepository.findById(accountNumber).orElse(null);

    if (account == null) {
      throw new NotFoundError(String.format("Can't find account '%d'", accountNumber));
    }

    if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
      throw new QuindError("Cand disable account with balance", HttpStatus.BAD_REQUEST);
    }

    account.setAccountStatus(AccountSatus.INACTIVE);
    accountRepository.save(account);
  }

  @Override
  public void enable(Long accountNumber) throws QuindError {
    Account account = accountRepository.findById(accountNumber).orElse(null);

    if (account == null) {
      throw new NotFoundError(String.format("Can't find account '%d'", accountNumber));
    }

    if (account.getAccountStatus() == AccountSatus.CANCELED) {
      throw new QuindError("Can't activate canceled", HttpStatus.BAD_REQUEST);
    }

    account.setAccountStatus(AccountSatus.ACTIVE);
    accountRepository.save(account);
  }

  @Override
  public void cancel(Long accountNumber) throws QuindError {
    Account account = accountRepository.findById(accountNumber).orElse(null);

    if (account == null) {
      throw new NotFoundError(String.format("Can't find account '%d'", accountNumber));
    }

    if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
      throw new QuindError("Cand disable account with balance", HttpStatus.BAD_REQUEST);
    }

    account.setAccountStatus(AccountSatus.CANCELED);
    accountRepository.save(account);
  }

  @Override
  public Account deposit(Long id, BigDecimal amount) throws QuindError {
    Account account = accountRepository.findById(id).orElse(null);

    if (account == null) {
      throw new NotFoundError(String.format("Can't find account '%d'", id));
    }

    if (account.getAccountStatus() != AccountSatus.ACTIVE) {
      throw new NotFoundError(String.format("Can't deposit on $s account", account.getAccountStatus().name()));
    }

    return this.deposit(account, amount);
  }

  @Override
  public Account withdrawal(Long id, BigDecimal amount) throws QuindError {
    Account account = accountRepository.findById(id).orElse(null);

    if (account == null) {
      throw new NotFoundError(String.format("Can't find account '%d'", id));
    }

    if (account.getAccountStatus() != AccountSatus.ACTIVE) {
      throw new NotFoundError(String.format("Can't deposit on $s account", account.getAccountStatus().name()));
    }

    this.verifyWithdrawal(account, amount);
    return this.withdrawal(account, amount);
  }

  @Override
  public Map<String, Account> tranfer(Long origin, Long destination, BigDecimal ammount) throws QuindError {
    Account accountOrigin = accountRepository.findById(origin).orElse(null);
    if (accountOrigin == null) {
      throw new NotFoundError(String.format("Can't find account '%d'", origin));
    }

    Account accountDestination = accountRepository.findById(destination).orElse(null);
    if (accountDestination == null) {
      throw new NotFoundError(String.format("Can't find account '%d'", destination));
    }
    if (accountDestination.getAccountStatus() != AccountSatus.ACTIVE) {
      throw new NotFoundError(String.format("Can't deposit on %s account", accountDestination.getAccountType()));
    }

    verifyWithdrawal(accountOrigin, ammount);

    this.withdrawal(accountOrigin, ammount);
    this.deposit(accountDestination, ammount);
    Map<String, Account> response = new HashMap<>();

    response.put("origin", this.getAccount(origin));
    response.put("destination", this.getAccount(destination));

    return response;
  }

  public Account deposit(Account ac, BigDecimal amount) {
    BigDecimal newBalance = ac.getBalance().add(amount);
    ac.setBalance(newBalance);
    return accountRepository.save(ac);
  }

  public Account withdrawal(Account ac, BigDecimal amount) {
    BigDecimal newBalance = ac.getBalance().subtract(amount);
    ac.setBalance(newBalance);
    return accountRepository.save(ac);
  }

  public void verifyWithdrawal(Account ac, BigDecimal amount) throws QuindError {
    if (ac.getAccountType() == AccountType.CHECKING) {
      return;
    }

    BigDecimal currentBalance = ac.getBalance();
    if (currentBalance.compareTo(amount) < 0) {
      throw new InsufficientBalance(amount, ac.getAccountId(), ac.getBalance());
    }
  }
}
