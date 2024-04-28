package com.quind.backend.infra.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.quind.backend.domain.model.transaction.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
  @Query("SELECT t FROM Transaction t WHERE t.target.accountId = :account OR t.transferTarget.accountId = :account")
  Page<Transaction> findByTargetOrTransferTarget(@Param("account") Long account, Pageable page);
}
