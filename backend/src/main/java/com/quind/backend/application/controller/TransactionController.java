package com.quind.backend.application.controller;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.quind.backend.application.service.TransactionService;
import com.quind.backend.domain.dto.PageResponse;
import com.quind.backend.domain.model.transaction.Transaction;
import com.quind.backend.infra.errors.QuindError;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
  @Autowired
  private TransactionService transactionService;

  @GetMapping("{id}")
  public ResponseEntity<Transaction> getById(@PathVariable("id") UUID id) {
    return ResponseEntity.ok(transactionService.getbyid(id));
  }

  @GetMapping("/account/{id}")
  public ResponseEntity<PageResponse<Transaction>> getAllFromAccount(@PathVariable("id") Long id,
      @RequestParam(name = "page", defaultValue = "1") Integer page) {
    return ResponseEntity.ok(new PageResponse<>(transactionService.getAllFromAccount(id, page - 1)));
  }

  @PostMapping("/deposit/{id}")
  public ResponseEntity<Transaction> deposit(@PathVariable("id") Long id,
      @RequestParam(name = "amount", required = true) BigDecimal amount) {
    try {
      return ResponseEntity.ok(transactionService.deposit(id, amount));
    } catch (QuindError e) {
      throw new ResponseStatusException(e.getStatus(), e.getMessage(), e);
    }
  }

  @PostMapping("/withdrawal/{id}")
  public ResponseEntity<Transaction> withdrawal(@PathVariable("id") Long id,
      @RequestParam(name = "amount", required = true) BigDecimal amount) {
    try {
      return ResponseEntity.ok(transactionService.witdrawal(id, amount));
    } catch (QuindError e) {
      throw new ResponseStatusException(e.getStatus(), e.getMessage(), e);
    }
  }

  @PostMapping("/transfer/{id}/{destination}")
  public ResponseEntity<Transaction> transfer(@PathVariable("id") Long id,
      @PathVariable("destination") Long dest,
      @RequestParam(name = "amount", required = true) BigDecimal amount) {
    try {
      return ResponseEntity.ok(transactionService.transfer(id, dest, amount));
    } catch (QuindError e) {
      throw new ResponseStatusException(e.getStatus(), e.getMessage(), e);
    }
  }
}
