package com.quind.backend.application.service.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.quind.backend.application.service.AccountService;
import com.quind.backend.application.service.TransactionService;
import com.quind.backend.domain.model.account.Account;
import com.quind.backend.domain.model.transaction.Transaction;
import com.quind.backend.domain.model.transaction.TransactionType;
import com.quind.backend.infra.config.Properties;
import com.quind.backend.infra.errors.QuindError;
import com.quind.backend.infra.repositories.TransactionRepository;

@Service
public class TransactionServiceImpl implements TransactionService {
  @Autowired
  private TransactionRepository transactionRepository;
  @Autowired
  private AccountService accountService;
  @Autowired
  private Properties config;

  @Override
  public Transaction deposit(Long target, BigDecimal amount) throws QuindError {
    Account accout = this.accountService.deposit(target, amount);

    Transaction transaction = new Transaction();
    transaction.setAmount(amount);
    transaction.setTarget(accout);
    transaction.setTransactionType(TransactionType.DEPOSIT);
    
    return this.transactionRepository.save(transaction);
  }

  @Override
  public Transaction witdrawal(Long target, BigDecimal amount) throws QuindError {
    Account accout = this.accountService.withdrawal(target, amount);

    Transaction transaction = new Transaction();
    transaction.setAmount(amount);
    transaction.setTarget(accout);
    transaction.setTransactionType(TransactionType.WITHDRAWAL);
    
    return this.transactionRepository.save(transaction);
  }

  @Override
  public Transaction transfer(Long origin, Long destination, BigDecimal amount) throws QuindError {
    var tranferenceAccounts = this.accountService.tranfer(origin, destination, amount);
    Transaction transaction = new Transaction();

    transaction.setAmount(amount);
    transaction.setTransactionType(TransactionType.TRANSFER);
    transaction.setTarget(tranferenceAccounts.get("origin"));
    transaction.setTransferTarget(tranferenceAccounts.get("destination"));

    return this.transactionRepository.save(transaction);
  }

  @Override
  public Transaction getbyid(UUID id) {
    return this.transactionRepository.findById(id).orElse(null);
  }

  @Override
  public Page<Transaction> getAllFromAccount(Long accountId, Integer page) {
    Pageable pg = PageRequest.of(page, config.getPageSize());
    return this.transactionRepository.findByTargetOrTransferTarget(accountId, pg);
  }
}
