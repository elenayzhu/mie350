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
@Table(name = "rams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String model;

    @NotBlank
    private String brand;

    @NotBlank
    private String ddrType; // DDR4, DDR5

    @Positive
    @NotNull
    private Integer speedMhz; // 3200, 3600, 6000

    @Positive
    @NotNull
    private Integer capacityGb; // 8, 16, 32

    @DecimalMin("0.0")
    @NotNull
    private BigDecimal price;
}