package com.team15.partpicker.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import org.springframework.lang.Nullable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(name = "psus")
@Getter
@Setter
@NoArgsConstructor
public class Psu extends Part {

    @Positive
    @NotNull
    private Integer wattage; // e.g., 650, 750, 850

    @Nullable
    private String efficiencyRating; // e.g., 80+ Bronze, 80+ Gold

    @NotBlank
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
