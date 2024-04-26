package com.quind.backend.domain.model.transaction;

import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.quind.backend.domain.model.account.Account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "transaction")
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
   @Id
   @GeneratedValue
   @UuidGenerator
   private UUID id;

   @ManyToOne
   @JoinColumn(name = "id")
   @Column(name = "targetAccount", nullable = false)
   private Account target;

   @ManyToOne
   @JoinColumn(name = "id")
   @Column(name = "targetAccount", nullable = true)
   private Account transferTarget;

   private BigDecimal amount;
}