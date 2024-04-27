package com.quind.backend.infra.errors;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotFoundError extends QuindError {
  public NotFoundError(String message) {
    super(message, HttpStatus.NOT_FOUND);
  }
}
