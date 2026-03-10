package com.team15.partpicker.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.springframework.lang.Nullable;
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
@Table(name = "psus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Psu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String model;

    @NotBlank
    private String brand;

    @Positive
    @NotNull
    private Integer wattage; // e.g., 650, 750, 850

    @Nullable
    private String efficiencyRating; // e.g., 80+ Bronze, 80+ Gold

    @NotBlank
    private String modularType; // Fully Modular, Semi-Modular, Non-Modular

    @Nullable
    private String color;
    
    @DecimalMin("0.0")
    @NotNull
    private BigDecimal price;
}