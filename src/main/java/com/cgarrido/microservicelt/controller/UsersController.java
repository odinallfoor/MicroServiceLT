package com.cgarrido.microservicelt.controller;

import com.cgarrido.microservicelt.dto.login.UserLoginRequest;
import com.cgarrido.microservicelt.dto.login.UserLoginResponse;
import com.cgarrido.microservicelt.dto.signup.UserSignupRequest;
import com.cgarrido.microservicelt.dto.signup.UserSignupResponse;


import com.cgarrido.microservicelt.exceptions.ApiError;
import com.cgarrido.microservicelt.service.CreateUserService;
import com.cgarrido.microservicelt.service.LoginUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1/user")
public class UsersController {

    private CreateUserService createUserService;

    private LoginUserService loginUserService;

    @Autowired
    public UsersController(CreateUserService createUserService,
                           LoginUserService loginUserService){
        this.createUserService = createUserService;
        this.loginUserService = loginUserService;
    }

    @Operation(summary = "Solicita la creación de un nuevo usuario en la base de datos", responses = {

            @ApiResponse(responseCode = "500", description = "Error de sistema", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserSignupResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserSignupResponse.class))}),
    })
    @PostMapping(value = "/sign-up", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> UserSignUp(@RequestBody UserSignupRequest signupRequest) throws Exception {

        return createUserService.createUser(signupRequest);
    }

    @Operation(summary = "Solicita el acceso del usuario con sus credenciales", responses = {
            @ApiResponse(responseCode = "500", description = "Error de sistema", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "401", description = "Token invalido", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "201", description = "Usuario ingresado exitosamente", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserLoginResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Usuario ingresado exitosamente", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserLoginResponse.class))}),
    })
    @GetMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserLoginResponse> UserLogin(@RequestBody UserLoginRequest loginRequest,
                                                       @RequestHeader("Authorization") String token) throws Exception{

        return loginUserService.loginUser(loginRequest, token);
    }
}
