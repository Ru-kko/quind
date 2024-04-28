package com.quind.backend.application.controller;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.quind.backend.application.service.ClientService;
import com.quind.backend.domain.dto.ClientBasicData;
import com.quind.backend.domain.dto.PageResponse;
import com.quind.backend.domain.model.Client;
import com.quind.backend.infra.errors.NotFoundError;
import com.quind.backend.infra.errors.QuindError;

import jakarta.validation.ValidationException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/client")
public class ClientController {
  private static final Logger LOGGER = Logger.getLogger("com.quind.backend.application.controller");
  @Autowired
  ClientService clientService;

  @GetMapping
  public ResponseEntity<PageResponse<Client>> getAll(@RequestParam(name = "page", defaultValue = "1") Integer pageNum) {
    return ResponseEntity.ok(new PageResponse<>(clientService.getAll(pageNum - 1)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Client> getClient(@PathVariable("id") UUID id) {
    var res = clientService.getById(id);

    if (res == null) {
      NotFoundError notFound = new NotFoundError(String.format("Couldn't find a client with id = %s", id.toString()));
      throw new ResponseStatusException(notFound.getStatus(), notFound.getMessage(), notFound);
    }

    return ResponseEntity.ok(res);
  }

  @PostMapping
  public ResponseEntity<Client> registerClient(@RequestBody ClientBasicData data) {
    try {
      Client res = clientService.regiterClient(data);
      return ResponseEntity.ok(res);
    } catch (ValidationException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    } catch (DataIntegrityViolationException e) {
      LOGGER.log(Level.WARNING, e.getCause().getMessage(), e);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<Client> updateClient(@PathVariable("id") UUID id, @RequestBody ClientBasicData data) {
    try {
      Client res = clientService.updateClient(id, data);
      return ResponseEntity.ok(res);
    } catch (ValidationException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    } catch (DataIntegrityViolationException e) {
      LOGGER.log(Level.WARNING, e.getCause().getMessage(), e);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping("/{id}")
  public void deleteClient(@PathVariable("id") UUID id) {
    try {
      clientService.deleteClient(id);
    } catch (QuindError e) {
      throw new ResponseStatusException(e.getStatus(), e.getMessage(), e);
    }
  }
}
