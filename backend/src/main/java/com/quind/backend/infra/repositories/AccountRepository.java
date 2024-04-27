package com.quind.backend.infra.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.quind.backend.domain.model.account.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
  @Query("SELECT a FROM Account a WHERE a.owner.clientId = :clientId")
  Page<Account> findByClientId(@Param("clientId") UUID clientId, Pageable pageable);
}
