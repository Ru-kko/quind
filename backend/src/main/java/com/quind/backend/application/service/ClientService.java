package com.quind.backend.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;

import com.quind.backend.domain.dto.ClientBasicData;
import com.quind.backend.domain.model.Client;
import com.quind.backend.infra.errors.QuindError;

import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Email;

public interface ClientService {
  public Page<Client> getAll(Integer page);
  public Client getById(UUID id);
  public Client getByEmail(@Email String email);
  public Client regiterClient(ClientBasicData data) throws ValidationException;
  public Client updateClient(UUID id, ClientBasicData data) throws ValidationException;
  public void deleteClient(UUID id) throws QuindError;
}
