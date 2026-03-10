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
    private Integer speedRatio; // changed from speedMhz to speedRatio (speedMhz / CAS Latency)

    @Positive
    @NotNull
    private Integer capacityGb; // 8, 16, 32

    @Nullable
    private String color;

    @DecimalMin("0.0")
    @NotNull
    private BigDecimal price;
}