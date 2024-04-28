package com.quind.backend.application.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.quind.backend.domain.model.transaction.Transaction;
import com.quind.backend.infra.errors.QuindError;

public interface TransactionService {
  public Transaction getbyid(UUID id);
  public Page<Transaction> getAllFromAccount(Long accountId, Integer page);
  public Transaction deposit(Long target, BigDecimal amount) throws QuindError;
  public Transaction witdrawal(Long target, BigDecimal amount) throws QuindError;
  public Transaction transfer(Long origin, Long destination, BigDecimal amount) throws QuindError;
}
