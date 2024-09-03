package com.cgarrido.microservicelt.service.impl;

import com.cgarrido.microservicelt.dto.login.PhonesResponse;
import com.cgarrido.microservicelt.dto.login.UserLoginRequest;
import com.cgarrido.microservicelt.dto.login.UserLoginResponse;
import com.cgarrido.microservicelt.entities.UserApp;
import com.cgarrido.microservicelt.mappers.UserMapper;
import com.cgarrido.microservicelt.repository.UsersRepository;
import com.cgarrido.microservicelt.security.JwtProvider;
import com.cgarrido.microservicelt.utils.ValidationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LoginUserServiceImplTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private ValidationUtils validationUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginUserServiceImpl loginUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deberiaIniciarSesionCorrectamente() throws Exception {
        // Datos de prueba
        String token = "validToken";
        UserLoginRequest loginRequest = UserLoginRequest.builder()
                .email("juan@example.com")
                .password("Password123")
                .build();

        UserApp userApp = UserApp.builder()
                .id(UUID.randomUUID())
                .email("juan@example.com")
                .password("encryptedPassword")
                .token("validToken")
                .lastLogin(LocalDateTime.now().minusDays(1))
                .created(LocalDateTime.now().minusDays(10))
                .isActive(true)
                .build();

        UserLoginResponse expectedResponse = UserLoginResponse.builder()
                .id(userApp.getId())
                .created(userApp.getCreated())
                .lastLogin(userApp.getLastLogin())
                .token("newToken")
                .isActive(userApp.isActive())
                .name(userApp.getName())
                .email(userApp.getEmail())
                .password(userApp.getPassword())
                .phones(userApp.getPhones() == null ? null : userApp.getPhones().stream()
                    .map(phone -> PhonesResponse.builder()
                        .number(phone.getNumber())
                        .cityCode(phone.getCityCode())
                        .countryCode(phone.getCountryCode())
                        .build())
                    .collect(Collectors.toList()))
                .build();

        // Configuración de mocks
        try (MockedStatic<UserMapper> mockedUserMapper = mockStatic(UserMapper.class)) {
            mockedUserMapper.when(() -> UserMapper.toUserLoginResponse(userApp)).thenReturn(expectedResponse);

            when(jwtProvider.validateToken(eq(token))).thenReturn(true);
            when(jwtProvider.getEmailFromToken(eq(token))).thenReturn("juan@example.com");
            when(validationUtils.isEmailValid(eq(loginRequest.getEmail()))).thenReturn(true);
            when(validationUtils.isPasswordValid(eq(loginRequest.getPassword()))).thenReturn(true);
            when(usersRepository.findByEmail(eq(loginRequest.getEmail()))).thenReturn(Optional.of(userApp));
            when(passwordEncoder.matches(eq(loginRequest.getPassword()), eq(userApp.getPassword()))).thenReturn(true);
            when(jwtProvider.generateToken(eq(userApp.getEmail()))).thenReturn("newToken");
            when(usersRepository.save(any(UserApp.class))).thenReturn(userApp);
//        when(UserMapper.toUserLoginResponse(userApp)).thenReturn(expectedResponse);

            // Ejecución del método a probar
            ResponseEntity<UserLoginResponse> response = loginUserService.loginUser(loginRequest, token);

            // Verificación de resultados
            assertEquals(HttpStatus.OK, response.getStatusCode());
            UserLoginResponse loginResponse = response.getBody();
            assertNotNull(loginResponse);
            assertEquals("newToken", loginResponse.getToken());
            verify(usersRepository, times(1)).save(userApp);
        }
    }

    @Test
    void deberiaLanzarExcepcionSiTokenEsInvalido() {
        // Datos de prueba
        String token = "invalidToken";
        UserLoginRequest loginRequest = UserLoginRequest.builder()
                .email("juan@example.com")
                .password("Password123")
                .build();

        // Configuración de mocks
        when(jwtProvider.validateToken(token)).thenReturn(false);

        // Ejecución y verificación de la excepción
        Exception exception = assertThrows(Exception.class, () -> {
            loginUserService.loginUser(loginRequest, token);
        });
        assertEquals("Token invalido", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiEmailEsInvalido() {
        // Datos de prueba
        String token = "validToken";
        UserLoginRequest loginRequest = UserLoginRequest.builder()
                .email("invalid-email")
                .password("Password123")
                .build();

        // Configuración de mocks
        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(validationUtils.isEmailValid(loginRequest.getEmail())).thenReturn(false);

        // Ejecución y verificación de la excepción
        Exception exception = assertThrows(Exception.class, () -> {
            loginUserService.loginUser(loginRequest, token);
        });
        assertEquals("Formato de email incorrecto", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiPasswordEsInvalida() {
        // Datos de prueba
        String token = "validToken";
        UserLoginRequest loginRequest = UserLoginRequest.builder()
                .email("juan@example.com")
                .password("short")
                .build();

        // Configuración de mocks
        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(validationUtils.isEmailValid(loginRequest.getEmail())).thenReturn(true);
        when(validationUtils.isPasswordValid(loginRequest.getPassword())).thenReturn(false);

        // Ejecución y verificación de la excepción
        Exception exception = assertThrows(Exception.class, () -> {
            loginUserService.loginUser(loginRequest, token);
        });
        assertEquals("Formato de contraseña incorrecto", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiEmailNoCoincideConToken() {
        // Datos de prueba
        String token = "validToken";
        UserLoginRequest loginRequest = UserLoginRequest.builder()
                .email("juan2@example.com")
                .password("Password123")
                .build();

        // Configuración de mocks
        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(jwtProvider.getEmailFromToken(token)).thenReturn("juan@example.com");
        when(validationUtils.isEmailValid(loginRequest.getEmail())).thenReturn(true);
        when(validationUtils.isPasswordValid(loginRequest.getPassword())).thenReturn(true);

        // Ejecución y verificación de la excepción
        Exception exception = assertThrows(Exception.class, () -> {
            loginUserService.loginUser(loginRequest, token);
        });
        assertEquals("Email incorrecto", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiUsuarioNoEncontrado() {
        // Datos de prueba
        String token = "validToken";
        UserLoginRequest loginRequest = UserLoginRequest.builder()
                .email("juan@example.com")
                .password("Password123")
                .build();

        // Configuración de mocks
        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(validationUtils.isEmailValid(loginRequest.getEmail())).thenReturn(true);
        when(validationUtils.isPasswordValid(loginRequest.getPassword())).thenReturn(true);
        when(jwtProvider.getEmailFromToken(token)).thenReturn("juan@example.com");
        when(usersRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        // Ejecución y verificación de la excepción
        Exception exception = assertThrows(Exception.class, () -> {
            loginUserService.loginUser(loginRequest, token);
        });
        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiTokenNoCoincideConUsuario() {
        // Datos de prueba
        String token = "validToken";
        UserLoginRequest loginRequest = UserLoginRequest.builder()
                .email("juan@example.com")
                .password("Password123")
                .build();

        UserApp userApp = UserApp.builder()
                .id(UUID.randomUUID())
                .email("juan@example.com")
                .password("encryptedPassword")
                .token("differentToken")
                .lastLogin(LocalDateTime.now().minusDays(1))
                .created(LocalDateTime.now().minusDays(10))
                .isActive(true)
                .build();

        // Configuración de mocks
        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(jwtProvider.getEmailFromToken(token)).thenReturn("juan@example.com");
        when(validationUtils.isEmailValid(loginRequest.getEmail())).thenReturn(true);
        when(validationUtils.isPasswordValid(loginRequest.getPassword())).thenReturn(true);
        when(usersRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(userApp));

        // Ejecución y verificación de la excepción
        Exception exception = assertThrows(Exception.class, () -> {
            loginUserService.loginUser(loginRequest, token);
        });
        assertEquals("Token invalido", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiContraseñaEsIncorrecta() {
        // Datos de prueba
        String token = "validToken";
        UserLoginRequest loginRequest = UserLoginRequest.builder()
                .email("juan@example.com")
                .password("Password123")
                .build();

        UserApp userApp = UserApp.builder()
                .id(UUID.randomUUID())
                .email("juan@example.com")
                .password("encryptedPassword") // La contraseña en la base de datos
                .token("validToken")
                .lastLogin(LocalDateTime.now().minusDays(1))
                .created(LocalDateTime.now().minusDays(10))
                .isActive(true)
                .build();

        // Configuración de mocks
        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(jwtProvider.getEmailFromToken(token)).thenReturn("juan@example.com");
        when(validationUtils.isEmailValid(loginRequest.getEmail())).thenReturn(true);
        when(validationUtils.isPasswordValid(loginRequest.getPassword())).thenReturn(true);
        when(usersRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(userApp));
        when(passwordEncoder.matches(loginRequest.getPassword(), userApp.getPassword())).thenReturn(false);

        // Ejecución y verificación de la excepción
        Exception exception = assertThrows(Exception.class, () -> {
            loginUserService.loginUser(loginRequest, token);
        });
        assertEquals("Contraseña incorrecta", exception.getMessage());
    }
}
