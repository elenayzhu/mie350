package com.team15.partpicker.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import org.springframework.lang.Nullable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(name = "cases")
@Getter
@Setter
@NoArgsConstructor
public class Case extends Part {

    @Nullable
    private String formFactor; // e.g., ATX, Micro-ATX, Mini-ITX

    @Positive
    @Nullable
    private Integer maxGpuLengthMm; // Maximum GPU length supported in mm

    @NotNull
    private String type;

    @Nullable
    private String color;

    public Case(
            Long id,
            String model,
            String brand,
            String formFactor,
            Integer maxGpuLengthMm,
            String type,
            String color,
            BigDecimal price
    ) {
        super(id, model, brand, price);
        this.formFactor = formFactor;
        this.maxGpuLengthMm = maxGpuLengthMm;
        this.type = type;
        this.color = color;
    }
}
