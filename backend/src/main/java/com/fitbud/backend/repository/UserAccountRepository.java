package com.fitbud.backend.repository;

import com.fitbud.backend.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fitbud.backend.model.UserAccount;
import com.fitbud.backend.repository.UserAccountRepository;

import java.util.Optional;


public interface UserAccountRepository
        extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByEmail(String email);

    boolean existsByEmail(String email);
}