package com.quind.backend.infra.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PropertiesTest {
  @Autowired
  private Properties properties;

  @Test
  void testGetPageSize() {
    assertEquals(10, properties.getPageSize());
  }
}
