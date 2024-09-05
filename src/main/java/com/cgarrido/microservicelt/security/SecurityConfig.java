package com.cgarrido.microservicelt.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .authorizeRequests()
                // Permitir acceso a Swagger
                .antMatchers("/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/webjars/**").permitAll()
                // Permitir acceso a consola de base de datos H2
                .antMatchers("/h2-console/**").permitAll()
                // Permitir acceso a endpoints de creacion y login de usuario
                .antMatchers("/api/v1/user/sign-up", "/api/v1/user/login").permitAll()
                .anyRequest().authenticated();

        // Desactivar las restricciones de frame para H2
        httpSecurity.headers().frameOptions().disable();

        return httpSecurity.build();
    }
}
