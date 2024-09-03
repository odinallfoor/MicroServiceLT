package com.cgarrido.microservicelt.service.impl;

import com.cgarrido.microservicelt.dto.signup.PhoneRequest;
import com.cgarrido.microservicelt.dto.signup.UserSignupRequest;
import com.cgarrido.microservicelt.dto.signup.UserSignupResponse;
import com.cgarrido.microservicelt.entities.UserApp;
import com.cgarrido.microservicelt.repository.UsersRepository;
import com.cgarrido.microservicelt.security.JwtProvider;
import com.cgarrido.microservicelt.utils.ValidationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class CreateUserServiceImplTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private ValidationUtils validationUtils;

    @InjectMocks
    private CreateUserServiceImpl createUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deberiaCrearUsuarioCorrectamente() throws Exception {
        // Datos de prueba
        UserSignupRequest request = UserSignupRequest.builder()
                .name("Juan")
                .email("juan@example.com")
                .password("Password123")
                .phones(Arrays.asList(
                        PhoneRequest.builder().number(1234567890L).citycode(1).contrycode("US").build(),
                        PhoneRequest.builder().number(9876543210L).citycode(1).contrycode("US").build()
                ))
                .build();

        UserApp expectedUser = UserApp.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .email(request.getEmail())
                .password("encryptedPassword")
                .created(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .token("token")
                .isActive(true)
                .build();

        // Configuración de mocks
        when(validationUtils.isEmailValid(request.getEmail())).thenReturn(true);
        when(validationUtils.isPasswordValid(request.getPassword())).thenReturn(true);
        when(usersRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encryptedPassword");
        when(jwtProvider.generateToken(request.getEmail())).thenReturn("token");
        when(usersRepository.save(any(UserApp.class))).thenReturn(expectedUser);

        // Ejecución del método a probar
        ResponseEntity<?> response = createUserService.createUser(request);

        // Verificación de resultados
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        UserSignupResponse signupResponse = (UserSignupResponse) response.getBody();
        assertEquals(true, signupResponse.isActive());

        // Verificar que el usuario ha sido guardado
        verify(usersRepository, times(1)).save(any(UserApp.class));
    }

    @Test
    void deberiaLanzarExcepcionSiEmailEsInvalido() {
        // Datos de prueba
        UserSignupRequest request = UserSignupRequest.builder()
                .email("invalid-email")
                .password("Password123")
                .build();

        // Configuración de mocks
        when(validationUtils.isEmailValid(request.getEmail())).thenReturn(false);

        // Ejecución y verificación de la excepción
        Exception exception = assertThrows(Exception.class, () -> {
            createUserService.createUser(request);
        });
        assertEquals("Formato de email incorrecto", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiPasswordEsInvalida() {
        // Datos de prueba
        UserSignupRequest request = UserSignupRequest.builder()
                .email("juan@example.com")
                .password("short")
                .build();

        // Configuración de mocks
        when(validationUtils.isEmailValid(request.getEmail())).thenReturn(true);
        when(validationUtils.isPasswordValid(request.getPassword())).thenReturn(false);

        // Ejecución y verificación de la excepción
        Exception exception = assertThrows(Exception.class, () -> {
            createUserService.createUser(request);
        });
        assertEquals("Formato de contraseña incorrecto", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiUsuarioYaExiste() {
        // Datos de prueba
        UserSignupRequest request = UserSignupRequest.builder()
                .email("juan@example.com")
                .password("Password123")
                .build();

        // Configuración de mocks
        when(validationUtils.isEmailValid(request.getEmail())).thenReturn(true);
        when(validationUtils.isPasswordValid(request.getPassword())).thenReturn(true);
        when(usersRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Ejecución y verificación de la excepción
        Exception exception = assertThrows(Exception.class, () -> {
            createUserService.createUser(request);
        });
        assertEquals("Usuario ya existe", exception.getMessage());
    }

    @Test
    void deberiaAgregarTelefonosCorrectamente() throws Exception {
        // Datos de prueba
        UserSignupRequest request = UserSignupRequest.builder()
                .name("Ana")
                .email("ana@example.com")
                .password("Password123")
                .phones(Arrays.asList(
                        PhoneRequest.builder().number(1234567890L).citycode(1).contrycode("US").build(),
                        PhoneRequest.builder().number(9876543210L).citycode(1).contrycode("US").build()
                ))
                .build();

        UserApp expectedUser = UserApp.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .email(request.getEmail())
                .password("encryptedPassword")
                .created(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .token("token")
                .isActive(true)
                .build();

        // Configuración de mocks
        when(validationUtils.isEmailValid(request.getEmail())).thenReturn(true);
        when(validationUtils.isPasswordValid(request.getPassword())).thenReturn(true);
        when(usersRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encryptedPassword");
        when(jwtProvider.generateToken(request.getEmail())).thenReturn("token");
        when(usersRepository.save(any(UserApp.class))).thenReturn(expectedUser);

        // Ejecución del método a probar
        ResponseEntity<?> response = createUserService.createUser(request);

        // Verificación de resultados
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        UserSignupResponse signupResponse = (UserSignupResponse) response.getBody();
        assertEquals(true, signupResponse.isActive());

        // Verificar que el usuario ha sido guardado con los teléfonos
        verify(usersRepository, times(1)).save(argThat(user ->
                user.getPhones() != null && !user.getPhones().isEmpty()
        ));
    }
}