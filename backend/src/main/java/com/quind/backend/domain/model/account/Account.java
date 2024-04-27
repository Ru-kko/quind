package com.quind.backend.domain.model.account;

import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.quind.backend.domain.model.Client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
  @Id
  private Long accountId;
  
  @ManyToOne
  @JoinColumn(name = "clientId", nullable = false)
  @OnDelete(action = OnDeleteAction.NO_ACTION)
  private Client owner;
  private BigDecimal balance;

  @Enumerated(EnumType.STRING)
  private AccountType accountType; // * Can be normalized in another table, also status field

  @Enumerated(EnumType.STRING)
  private AccountSatus accountStatus; 

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false, insertable = false) 
  private Date createdAt;
  @Temporal(TemporalType.TIMESTAMP)
  @Column(updatable = false, insertable = false)
  private Date updatedAt;
}
