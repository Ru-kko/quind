package com.quind.backend.infra.errors;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * It is intended to be used for handling common errors within the Quind
 * application.
 */
@Getter
public class QuindError extends Exception {
  private HttpStatus status;

  public QuindError(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }
}
