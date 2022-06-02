package com.api.beer.dto;

import com.api.beer.entity.Beer;
import com.api.beer.enums.BeerType;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BeerDTO {

    private Long id;

    @NotNull
    @Size(min = 1, max = 200)
    private String name;

    @NotNull
    @Size(min = 1, max = 200)
    private String brand;

    @NotNull
    @Max(500)
    private Integer max;

    @NotNull
    @Max(100)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @NotNull
    private BeerType type;

    public BeerDTO(Beer beer){
        this.id = beer.getId();
        this.name = beer.getName();
        this.brand = beer.getBrand();
        this.max = beer.getMax();
        this.quantity = beer.getQuantity();
        this.type = beer.getType();
    }

}

