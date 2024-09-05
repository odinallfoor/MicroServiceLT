package com.cgarrido.microservicelt.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Phones {

    @Id
    private UUID id = UUID.randomUUID();
    private Long number;
    private Integer cityCode;
    private String countryCode;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private UserApp user;
}
