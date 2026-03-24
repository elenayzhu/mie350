package com.team15.partpicker.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class PartUpdateRequest {

    @NotBlank
    private String model;

    @NotBlank
    private String brand;

    @DecimalMin("0.0")
    @NotNull
    private BigDecimal price;
}
