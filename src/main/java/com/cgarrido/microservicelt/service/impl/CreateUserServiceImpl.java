package com.cgarrido.microservicelt.service.impl;

import com.cgarrido.microservicelt.dto.signup.UserSignupRequest;
import com.cgarrido.microservicelt.dto.signup.UserSignupResponse;
import com.cgarrido.microservicelt.entities.Phones;
import com.cgarrido.microservicelt.entities.UserApp;
import com.cgarrido.microservicelt.repository.UsersRepository;
import com.cgarrido.microservicelt.security.JwtProvider;
import com.cgarrido.microservicelt.service.CreateUserService;
import com.cgarrido.microservicelt.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CreateUserServiceImpl implements CreateUserService {

    private UsersRepository usersRepository;

    private PasswordEncoder passwordEncoder;

    private JwtProvider jwtProvider;

    private ValidationUtils validationUtils;

    @Autowired
    public CreateUserServiceImpl(UsersRepository usersRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtProvider jwtProvider,
                                 ValidationUtils validationUtils){
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.validationUtils = validationUtils;
    }

    public ResponseEntity<?> createUser(UserSignupRequest signupRequest) throws Exception {

        // Validacion de formato de email
        if(!validationUtils.isEmailValid(signupRequest.getEmail())){
            throw new Exception("Formato de email incorrecto");
        }

        // Validacion de formato de contraseña
        if(!validationUtils.isPasswordValid(signupRequest.getPassword())){
            throw new Exception("Formato de contraseña incorrecto");
        }

        // Validacion de Usuario ya existente
        if (usersRepository.existsByEmail(signupRequest.getEmail())){
            throw new Exception("Usuario ya existe");
        }

        // Encriptacion de contraseña
        String encryptPass = passwordEncoder.encode(signupRequest.getPassword());

        // Generar token de autenticacion
        String token = jwtProvider.generateToken(signupRequest.getEmail());

        UserApp newUser = UserApp.builder()
                .id(UUID.randomUUID())
                .name(signupRequest.getName())
                .email(signupRequest.getEmail())
                .password(encryptPass)
                .created(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .token(token)
                .isActive(true)
                .build();

        // Agregamos telefonos si existen
        if(!signupRequest.getPhones().isEmpty() && signupRequest.getPhones() != null){
            List<Phones> phones = signupRequest.getPhones()
                    .stream()
                    .map(phoneRequest -> Phones.builder()
                            .id(UUID.randomUUID())
                            .number(phoneRequest.getNumber())
                            .cityCode(phoneRequest.getCitycode())
                            .countryCode(phoneRequest.getContrycode())
                            .user(newUser)
                            .build())
                    .collect(Collectors.toList());

            newUser.setPhones(phones);
        }

        // Guardar usuario en la base de datos
        UserApp savedUser = usersRepository.save(newUser);

        // Respuesta de usuario y token
        UserSignupResponse signupResponse = UserSignupResponse.builder()
                .id(savedUser.getId())
                .created(savedUser.getCreated())
                .lastLogin(savedUser.getLastLogin())
                .token(savedUser.getToken())
                .isActive(savedUser.isActive())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(signupResponse);
    }
}
