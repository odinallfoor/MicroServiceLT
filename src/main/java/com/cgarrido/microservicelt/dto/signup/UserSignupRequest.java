package com.cgarrido.microservicelt.dto.signup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupRequest {


    private String name;
    private String email;
    private String password;
    private List<PhoneRequest> phones;
}
