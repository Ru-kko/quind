package com.quind.backend.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
@ConfigurationProperties(prefix = "app")
public class Properties {
  @Value("${app.page-size}")
  private Integer pageSize;
}
