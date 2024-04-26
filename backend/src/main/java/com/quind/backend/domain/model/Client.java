package com.quind.backend.domain.model;

import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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
  private String fistName;
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
  @CreatedDate
  private Date createdAt;
  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  private Date updatedAt;
}
