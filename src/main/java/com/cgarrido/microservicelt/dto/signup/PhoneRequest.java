package com.cgarrido.microservicelt.dto.signup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhoneRequest {

    private long number;
    private Integer citycode;
    private String contrycode;
}
