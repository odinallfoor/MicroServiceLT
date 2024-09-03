package com.cgarrido.microservicelt.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ValidationUtils {

    @Value("${email.regex}")
    private String emailPattern;

    @Value("${pass.regex}")
    private String passPattern;

    public boolean isEmailValid(String email){
        return email.matches(emailPattern.strip());
    }

    public boolean isPasswordValid(String password){
        return password.matches(passPattern.strip());
    }
}
