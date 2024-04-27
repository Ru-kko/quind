package com.quind.backend.infra.errors;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsufficientBalance extends QuindError {
  private BigDecimal ammount;
  private Long accountNumber;
  private BigDecimal balance;

  public InsufficientBalance(BigDecimal ammount, Long accountNumber, BigDecimal balance) {
    super("insufficient balance", HttpStatus.BAD_REQUEST);
    this.ammount = ammount;
    this.accountNumber = accountNumber;
    this.balance = balance;
  }
}
