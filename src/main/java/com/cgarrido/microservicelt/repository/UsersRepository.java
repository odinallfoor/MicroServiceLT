package com.cgarrido.microservicelt.repository;

import com.cgarrido.microservicelt.entities.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<UserApp, UUID> {

    boolean existsByEmail(String email);

    Optional<UserApp> findByEmail(String email);
}
