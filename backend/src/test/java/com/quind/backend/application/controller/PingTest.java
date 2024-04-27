package com.quind.backend.application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;


@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PingTest {
  @LocalServerPort
  private Integer port;
  private String HOST;

  @Autowired
  TestRestTemplate restTemplate;

  @BeforeAll
  void initialize() {
    this.HOST = "http://localhost:" + this.port;
  }

  @Test
  void testHealthCheck() {
    var res = restTemplate.getForEntity(this.HOST + "/ping", String.class);
    var data = res.getBody();

    assertEquals(res.getStatusCode(), HttpStatus.OK);
    assertNotNull(data);
    assertEquals("Pong", data);
  }
}
