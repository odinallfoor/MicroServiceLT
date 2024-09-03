package com.cgarrido.microservicelt.mappers;

import com.cgarrido.microservicelt.dto.login.PhonesResponse;
import com.cgarrido.microservicelt.dto.login.UserLoginResponse;
import com.cgarrido.microservicelt.entities.Phones;
import com.cgarrido.microservicelt.entities.UserApp;

import java.util.Optional;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserLoginResponse toUserLoginResponse(UserApp userApp) {
        return UserLoginResponse.builder()
                .id(userApp.getId())
                .created(userApp.getCreated())
                .lastLogin(userApp.getLastLogin())
                .token(userApp.getToken())
                .isActive(userApp.isActive())
                .name(userApp.getName())
                .email(userApp.getEmail())
                .password(userApp.getPassword())
                .phones(Optional.ofNullable(userApp.getPhones())
                        .map(phones -> phones.stream()
                                .map(UserMapper::toPhonesResponse)
                                .collect(Collectors.toList()))
                        .orElse(null))
                .build();
    }

    private static PhonesResponse toPhonesResponse(Phones phone) {
        return PhonesResponse.builder()
                .number(phone.getNumber())
                .cityCode(phone.getCityCode())
                .countryCode(phone.getCountryCode())
                .build();
    }
}
