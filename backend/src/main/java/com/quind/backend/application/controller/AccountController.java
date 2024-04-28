package com.quind.backend.application.controller;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.quind.backend.application.service.AccountService;
import com.quind.backend.domain.dto.AccountRegitration;
import com.quind.backend.domain.dto.PageResponse;
import com.quind.backend.domain.model.account.Account;
import com.quind.backend.infra.errors.QuindError;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/account")
public class AccountController {
  private static final Logger LOGGER = Logger.getLogger("com.quind.backend.application.controller");
  @Autowired
  private AccountService accountService;

  @GetMapping("/client/{id}")
  public ResponseEntity<PageResponse<Account>> getClientAccounts(@PathVariable("id") UUID id,
      @RequestParam(name = "page", defaultValue = "1") Integer pagenum) {
    return ResponseEntity.ok(new PageResponse<>(accountService.getClientAccounts(id, pagenum - 1)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Account> getAccount(@PathVariable("id") Long id) {
    return ResponseEntity.ok(accountService.getAccount(id));
  }

  @PostMapping
  public ResponseEntity<Account> create(@RequestBody AccountRegitration data) {
    try {
      Account res = accountService.create(data);
      return ResponseEntity.ok(res);
    } catch (QuindError e) {
      throw new ResponseStatusException(e.getStatus(), e.getMessage(), e);
    } catch (DataIntegrityViolationException e) {
      LOGGER.log(Level.WARNING, e.getCause().getMessage(), e);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/{id}/disable")
  public void disableAccount(@PathVariable("id") Long id) {
    try {
      accountService.diable(id);
    } catch (QuindError e) {
      throw new ResponseStatusException(e.getStatus(), e.getMessage(), e);
    }
  }

  @PostMapping("/{id}/enable")
  public void enableAccount(@PathVariable("id") Long id) {
    try {
      accountService.enable(id);
    } catch (QuindError e) {
      throw new ResponseStatusException(e.getStatus(), e.getMessage(), e);
    }
  }

  @PostMapping("/{id}/cancel")
  public void cancelAccount(@PathVariable("id") Long id) {
    try {
      accountService.cancel(id);
    } catch (QuindError e) {
      throw new ResponseStatusException(e.getStatus(), e.getMessage(), e);
    }
  }
}
