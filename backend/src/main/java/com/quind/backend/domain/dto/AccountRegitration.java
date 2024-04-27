package com.quind.backend.domain.dto;

import java.util.UUID;

import com.quind.backend.domain.model.account.AccountType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRegitration {
  private UUID ownerID;
  private AccountType type;
}
