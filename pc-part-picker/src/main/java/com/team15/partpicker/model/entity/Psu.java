package com.team15.partpicker.model.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Positive;

import org.springframework.lang.Nullable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "psus")
@Getter
@Setter
@NoArgsConstructor
public class Psu extends Part {

    @Positive
    @Nullable
    private Integer wattage; // e.g., 650, 750, 850

    @Nullable
    private String efficiencyRating; // e.g., 80+ Bronze, 80+ Gold

    @Nullable
    private String modularType; // Fully Modular, Semi-Modular, Non-Modular

    @Nullable
    private String color;

    public Psu(
            Long id,
            String model,
            String brand,
            Integer wattage,
            String efficiencyRating,
            String modularType,
            String color,
            BigDecimal price
    ) {
        super(id, model, brand, price);
        this.wattage = wattage;
        this.efficiencyRating = efficiencyRating;
        this.modularType = modularType;
        this.color = color;
    }
}
