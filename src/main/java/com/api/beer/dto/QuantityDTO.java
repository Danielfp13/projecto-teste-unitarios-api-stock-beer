package com.api.beer.dto;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuantityDTO {

    @NotNull
    @Max(100)
    private Integer quantity;
}
