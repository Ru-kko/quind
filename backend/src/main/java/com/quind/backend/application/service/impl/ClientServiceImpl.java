package com.quind.backend.application.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.quind.backend.application.service.ClientService;
import com.quind.backend.domain.dto.ClientBasicData;
import com.quind.backend.domain.model.Client;
import com.quind.backend.infra.config.Properties;
import com.quind.backend.infra.repositories.ClientRepository;

import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Email;

@Service
class ClientServiceImpl implements ClientService {
  @Autowired
  private ClientRepository clientRepository;
  @Autowired
  private Properties config;

  @Override
  public Page<Client> getAll(Integer page) {
    Pageable pg = PageRequest.of(page, config.getPageSize());
    return clientRepository.findAll(pg);
  }

  @Override
  public Client getById(UUID id) {
    Optional<Client> res = clientRepository.findById(id);
    if (res.isPresent())
      return res.get();
    return null;
  }

  @Override
  public Client getByEmail(@Email String email) {
    Optional<Client> res = clientRepository.findByEmail(email);
    if (res.isPresent())
      return res.get();
    return null;
  }

  @Override
  public Client regiterClient(ClientBasicData data) throws ValidationException {
    this.validateClient(data);

    Client toStore = new Client();

    toStore.setBirthDate(data.getBirthDate());
    toStore.setEmail(data.getEmail());
    toStore.setFirstName(data.getFirstName());
    toStore.setLastName(data.getLastName());

    Client stored = clientRepository.save(toStore);

    return this.getById(stored.getClientId());
  }

  @Override
  public Client updateClient(UUID id, ClientBasicData data) throws ValidationException {
    Client prev = this.getById(id);

    if (prev == null) {
      return null;
    }

    if (data.getLastName() != null) {
      prev.setLastName(data.getLastName());
    }
    
    if (data.getFirstName() != null) {
      prev.setFirstName(data.getFirstName());
    }

    if (data.getEmail() != null) {
      prev.setEmail(data.getEmail());
    }

    if (data.getBirthDate() != null) {
      prev.setBirthDate(data.getBirthDate());
    }

    data.setBirthDate(prev.getBirthDate());
    data.setFirstName(prev.getFirstName());
    data.setLastName(prev.getLastName());
    data.setEmail(prev.getEmail());
    this.validateClient(data);

    clientRepository.save(prev);
    return this.getById(id);
  }

  @Override
  public void deleteClient(UUID id) {
    clientRepository.deleteById(id);
  }

  /**
   * Validate attributes that wasn't validated with spring started validator
   * 
   * @param data
   * @throws ValidationException
   */
  private void validateClient(ClientBasicData data) throws ValidationException {
    Date today = new Date();

    Calendar born = Calendar.getInstance();
    born.setTime(data.getBirthDate());
    Calendar actual = Calendar.getInstance();
    actual.setTime(today);

    Integer age = actual.get(Calendar.YEAR) - born.get(Calendar.YEAR);

    if (actual.get(Calendar.DAY_OF_YEAR) < born.get(Calendar.DAY_OF_YEAR)) {
      age--;
    }

    if (age < 18) {
      throw new ValidationException("Client must be over 18");
    }
  }
}
