package com.quind.backend.application.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import com.quind.backend.domain.dto.ClientBasicData;
import com.quind.backend.domain.model.Client;
import com.quind.backend.infra.errors.QuindError;

import jakarta.validation.ValidationException;

@TestInstance(Lifecycle.PER_CLASS)
@Sql(scripts = { "/dataDrop.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = { "/dataInit.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest
public class ClientServiceTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired
  private ClientService clientService;

  @Test
  void testDeleteClient() {
    UUID id = UUID.fromString("b8e5b32c-3c88-4d73-9473-1e93b3d2f468");
    assertDoesNotThrow(() -> clientService.deleteClient(id));

    int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM client WHERE client_id = ?", Integer.class, id);
    assertEquals(0, count);

    // * Should not delete because have active accounts
    UUID errorId = UUID.fromString("17e62166-fc85-86df-a4d1-bc0e1742c08b");

    assertThrows(QuindError.class,() -> clientService.deleteClient(errorId));
  }

  @Test
  void testGetAll() {
    Page<Client> result = clientService.getAll(0);
    assertTrue(result.getTotalElements() >= 4);
  }

  @Test
  void testGetByEmail() {
    String email = "michael.johnson@example.com";
    Client result = clientService.getByEmail(email);
    assertNotNull(result);
    assertEquals("Michael", result.getFirstName());
    assertEquals("Johnson", result.getLastName());
  }

  @Test
  void testGetById() {
    UUID id = UUID.fromString("1f5c4e23-5bc3-4cd6-a0df-2b765e5d008a");
    Client result = clientService.getById(id);
    assertNotNull(result);
    assertNotNull(result.getCreatedAt());
    assertEquals("Michael", result.getFirstName());
    assertEquals("Johnson", result.getLastName());
  }

  @Test
  void testRegiterClient() throws ParseException {
    var formatter = new SimpleDateFormat("yyyy-mm-dd");
    var date = formatter.parse("1990-01-01");

    ClientBasicData data = new ClientBasicData("Jane", "Doe", date, "jane.doe@example.com");

    Client response1 = clientService.regiterClient(data);

    assertNotNull(response1);
    assertNotNull(response1.getCreatedAt());

    Integer count = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM client WHERE client_id = '" + response1.getClientId().toString() + "'",
        Integer.class);
    assertEquals(1, count);

    jdbcTemplate.execute("DELETE FROM client WHERE client_id = '" + response1.getClientId().toString() + "'");

    var errorDate = new Date();
    ClientBasicData errorClient = new ClientBasicData("Jonh", "Error", errorDate, "john.doe@error.com");

    assertThrows(ValidationException.class, () -> clientService.regiterClient(errorClient));
  }

  @Test
  void testUpdateClient() {
    UUID id = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");
    ClientBasicData newData = new ClientBasicData();
    newData.setLastName("UpdatedLastName");

    Client updated = clientService.updateClient(id, newData);

    assertNotNull(updated);
    assertNotNull(updated.getUpdatedAt());
    assertEquals(updated.getLastName(), newData.getLastName());

    var fromDb = jdbcTemplate.queryForMap("SELECT * FROM client WHERE client_id = ?", id);

    assertEquals(updated.getLastName(), fromDb.get("last_name"));
    assertEquals(updated.getUpdatedAt(), fromDb.get("updated_at"));
  }
}
