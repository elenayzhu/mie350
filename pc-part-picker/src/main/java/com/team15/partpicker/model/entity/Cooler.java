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
@Table(name = "coolers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cooler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String model;

    @NotBlank
    private String brand;

    @Nullable
    private String socket; // e.g. AM5, LGA1700

    @Positive
    @Nullable
    private Integer maxTdp; // cooler TDP capacity (watts)

    @NotBlank
    private String type; // Air, AIO 240mm, AIO 360mm

    @Nullable
    private String color;

    @DecimalMin("0.0")
    @NotNull
    private BigDecimal price;
}