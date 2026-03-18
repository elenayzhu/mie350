package com.team15.partpicker.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String model;

    @NotBlank
    private String brand;

    @DecimalMin("0.0")
    @NotNull
    private BigDecimal price;

    protected Part(Long id, String model, String brand, BigDecimal price) {
        this.id = id;
        this.model = model;
        this.brand = brand;
        this.price = price;
    }
}
