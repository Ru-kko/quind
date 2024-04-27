package com.quind.backend.application.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.quind.backend.domain.dto.AccountRegitration;
import com.quind.backend.domain.model.account.Account;
import com.quind.backend.infra.errors.NotFoundError;
import com.quind.backend.infra.errors.QuindError;

public interface AccountService {
  public Page<Account> getClientAccounts(UUID clientid, Integer page);
  public Account getAccount(Long accountNumber);
  public Account create(AccountRegitration register) throws NotFoundError;
  public void diable(Long accountNumber) throws QuindError;
  public void enable(Long accountNumber) throws QuindError;
  public void cancel(Long accountNumber) throws QuindError;
  public Map<String, Account> tranfer(Long origin, Long destination, BigDecimal ammount) throws QuindError;
  public Account deposit(Long id, BigDecimal amount) throws QuindError;
  public Account withdrawal(Long id, BigDecimal amount) throws QuindError;
}
