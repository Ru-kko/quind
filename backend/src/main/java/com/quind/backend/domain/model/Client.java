package com.quind.backend.domain.model;

import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.validation.annotation.Validated;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Validated
@Table(name = "client")
@NoArgsConstructor
@AllArgsConstructor
public class Client {
  @Id
  @GeneratedValue
  @UuidGenerator
  private UUID clientId;

  @Size(min = 2, message = "should containt at least 2 characters")
  @Column(nullable = false)
  private String firstName;
  @Size(min = 2, message = "should containt at least 2 characters")
  @Column(nullable = false)
  private String lastName;

  @Temporal(TemporalType.DATE)
  @Past
  private Date birthDate;
  
  @Column(unique = true, nullable = false)
  @Email
  private String email;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false, insertable = false) 
  private Date createdAt;
  @Temporal(TemporalType.TIMESTAMP)
  @Column(updatable = false, insertable = false)
  private Date updatedAt;
}
