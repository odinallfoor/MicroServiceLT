package com.cgarrido.microservicelt.service;


import com.cgarrido.microservicelt.dto.login.UserLoginRequest;
import com.cgarrido.microservicelt.dto.login.UserLoginResponse;
import org.springframework.http.ResponseEntity;

@FunctionalInterface
public interface LoginUserService {

    public ResponseEntity<UserLoginResponse> loginUser(UserLoginRequest loginRequest, String token) throws Exception;
}
