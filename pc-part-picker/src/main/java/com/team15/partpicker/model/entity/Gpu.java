package com.team15.partpicker.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(name = "gpus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Gpu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String model;

    @NotBlank
    private String brand;

    @NotBlank
    private String manufacturer;

    @Positive
    @Nullable
    private Integer lengthMm;

    @Positive
    @NotNull
    private Integer vramGb;

    @Positive
    @Nullable
    private Integer tdp;

    @Nullable
    private String color;

    @DecimalMin("0.0")
    @NotNull
    private BigDecimal price;
}