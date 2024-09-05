package com.cgarrido.microservicelt.dto.login;

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
public class PhonesResponse {

    private Long number;
    private Integer cityCode;
    private String countryCode;
}
