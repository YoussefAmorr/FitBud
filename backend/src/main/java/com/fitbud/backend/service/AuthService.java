package com.fitbud.backend.service;

import com.fitbud.backend.exception.DuplicateEmailException;
import com.fitbud.backend.exception.InvalidCredentialsException;
import com.fitbud.backend.model.UserAccount;
import com.fitbud.backend.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserAccount register(String email, String password) {

        if (userAccountRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(
                    "An account with this email already exists."
            );
        }

        String passwordHash = passwordEncoder.encode(password);

        UserAccount userAccount = new UserAccount(
                email,
                passwordHash
        );

        return userAccountRepository.save(userAccount);
    }

    public String login(String email, String password) {

        UserAccount userAccount = userAccountRepository
                .findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException(
                        "Invalid email or password."
                ));

        if (!passwordEncoder.matches(
                password,
                userAccount.getPasswordHash())) {

            throw new InvalidCredentialsException(
                    "Invalid email or password."
            );
        }

        return jwtService.generateToken(userAccount.getEmail());
    }
}