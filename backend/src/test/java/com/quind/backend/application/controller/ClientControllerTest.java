package com.quind.backend.application.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import com.quind.backend.domain.dto.ClientBasicData;
import com.quind.backend.domain.dto.PageResponse;
import com.quind.backend.domain.model.Client;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = { "/dataDrop.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = { "/dataInit.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
public class ClientControllerTest {
  @LocalServerPort
  private Integer port;
  private String HOST;

  @Autowired
  private TestRestTemplate restTemplate;
  @Autowired
  private JdbcTemplate jdbcTemplate;

  @BeforeAll
  void initialize() {
    this.HOST = "http://localhost:" + this.port + "/client";
  }

  @Test
  void testDeleteClient() {
    String validId = "b8e5b32c-3c88-4d73-9473-1e93b3d2f468";
    assertDoesNotThrow(() -> restTemplate.delete(HOST + "/" + validId));

    // ? from db
    int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM client WHERE client_id = ?", Integer.class,
        UUID.fromString(validId));
    assertEquals(0, count);

    String invaildId = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
    var badResponse = restTemplate.exchange(
        HOST + "/" + invaildId,
        HttpMethod.DELETE,
        HttpEntity.EMPTY,
        Void.class);
    assertEquals(badResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
  }

  @Test
  void testGetAll() {
    var response = restTemplate.exchange(HOST, HttpMethod.GET, null, PageResponse.class);

    Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM client", Long.class);
    assertEquals(count, response.getBody().getTotalItems());
  }

  @Test
  void testGetClient() {
    UUID id = UUID.fromString("1f5c4e23-5bc3-4cd6-a0df-2b765e5d008a");
    Client client = restTemplate.getForEntity(HOST + "/" + id.toString(), Client.class).getBody();

    String name = jdbcTemplate.queryForObject("SELECT first_name FROM client WHERE client_id = ?", String.class, id);

    assertEquals(client.getFirstName(), name);
  }

  @Test
  void testRegisterClient() {
    Date today = new Date();
    Calendar date = Calendar.getInstance();
    date.set(2000, 2, 15);
    // !  invalid
    ClientBasicData client = new ClientBasicData();
    client.setFirstName("Jonh"); 
    client.setLastName("Doe 2");
    client.setBirthDate(today); // * validate age
    client.setEmail("hello@foo.com");

    ResponseEntity<Client> response = restTemplate.postForEntity(HOST, client, Client.class);
    assertNotEquals(HttpStatus.OK, response.getStatusCode());

    client.setFirstName("h");
    client.setBirthDate(date.getTime()); // * validate name

    response = restTemplate.postForEntity(HOST, client, Client.class);
    assertNotEquals(HttpStatus.OK, response.getStatusCode());

    client.setFirstName("Jonh");
    client.setEmail("a"); // * validate email

    response = restTemplate.postForEntity(HOST, client, Client.class);
    assertNotEquals(HttpStatus.OK, response.getStatusCode());

    client.setEmail("hello@foo.com");


    response = restTemplate.postForEntity(HOST, client, Client.class);
    assertNotNull(response.getBody().getClientId());
  }

  @Test
  void testUpdateClient() {
    UUID id = UUID.fromString("b8e5b32c-3c88-4d73-9473-1e93b3d2f468");
    
    ClientBasicData changes = new ClientBasicData();
    changes.setFirstName("update");

    HttpEntity<ClientBasicData> data = new HttpEntity<>(changes);
    ResponseEntity<Client> response = restTemplate.exchange(HOST + "/" + id.toString(), HttpMethod.PUT, data, Client.class);

    String name = jdbcTemplate.queryForObject("SELECT first_name FROM client WHERE client_id = ?", String.class, id);

    assertEquals(changes.getFirstName(), name);
    assertEquals(response.getBody().getFirstName(), name);
  }
}
