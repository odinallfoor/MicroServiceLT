package com.cgarrido.microservicelt.service;

import com.cgarrido.microservicelt.dto.signup.UserSignupRequest;
import com.cgarrido.microservicelt.dto.signup.UserSignupResponse;
import org.springframework.http.ResponseEntity;

@FunctionalInterface
public interface CreateUserService {

    public ResponseEntity<?> createUser(UserSignupRequest signupRequest) throws Exception;
}
