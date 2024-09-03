package com.cgarrido.microservicelt.service.impl;

import com.cgarrido.microservicelt.dto.login.UserLoginRequest;
import com.cgarrido.microservicelt.dto.login.UserLoginResponse;
import com.cgarrido.microservicelt.entities.UserApp;
import com.cgarrido.microservicelt.mappers.UserMapper;
import com.cgarrido.microservicelt.repository.UsersRepository;
import com.cgarrido.microservicelt.security.JwtProvider;
import com.cgarrido.microservicelt.service.LoginUserService;
import com.cgarrido.microservicelt.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginUserServiceImpl implements LoginUserService {

    private JwtProvider jwtProvider;

    private UsersRepository usersRepository;

    private ValidationUtils validationUtils;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public LoginUserServiceImpl(JwtProvider jwtProvider,
                                UsersRepository usersRepository,
                                ValidationUtils validationUtils,
                                PasswordEncoder passwordEncoder){
        this.jwtProvider = jwtProvider;
        this.usersRepository = usersRepository;
        this.validationUtils = validationUtils;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<UserLoginResponse> loginUser(UserLoginRequest loginRequest, String token) throws Exception {

        if(!jwtProvider.validateToken(token)){
            throw new Exception("Token invalido");
        }

        // Validacion de Contraseña y formato de email
        if(!validationUtils.isEmailValid(loginRequest.getEmail())){
            throw new Exception("Formato de email incorrecto");
        }

        if(!validationUtils.isPasswordValid(loginRequest.getPassword())){
            throw new Exception("Formato de contraseña incorrecto");
        }

        // Obtiene el email desde el token y se valida con el ingresado
        String email = jwtProvider.getEmailFromToken(token);

        if(!email.equals(loginRequest.getEmail())){
            throw new Exception("Email incorrecto");
        }

        // Busqueda del usuario en la base de datos
        UserApp userApp = usersRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // Validamos el token ingresado con el registro del ultimo ingreso
        if(!userApp.getToken().equals(token)){
            throw new Exception("Token invalido");
        }

        // Validamos la contraseña
        if(!passwordEncoder.matches(loginRequest.getPassword(), userApp.getPassword())){
            throw new Exception("Contraseña incorrecta");
        }

        // Actualizamos el token y el ultimo ingreso
        userApp.setLastLogin(LocalDateTime.now());
        String newToken = jwtProvider.generateToken(userApp.getEmail());
        userApp.setToken(newToken);
        usersRepository.save(userApp);

        // Convertir entidad a DTO usando el mapper
        UserLoginResponse loginResponse = UserMapper.toUserLoginResponse(userApp);

        return ResponseEntity.ok(loginResponse);
    }
}
