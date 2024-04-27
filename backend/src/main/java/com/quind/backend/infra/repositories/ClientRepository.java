package com.quind.backend.infra.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.quind.backend.domain.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
  Optional<Client> findByEmail(String email);
  @Query(value = "SELECT COUNT(a) FROM Account a WHERE a.owner.clientId = :clientId AND a.accountStatus != 'CANCELED'")
  int countNonCanceledAccountsByClientId(@Param("clientId") UUID clientId);
}
