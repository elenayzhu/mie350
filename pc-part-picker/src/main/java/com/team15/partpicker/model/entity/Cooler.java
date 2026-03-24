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
@Table(name = "coolers")
@Getter
@Setter
@NoArgsConstructor
public class Cooler extends Part {

    @Nullable
    private String socket; // e.g. AM5, LGA1700

    @Positive
    @Nullable
    private Integer maxTdp; // cooler TDP capacity (watts)

    @Nullable
    private String type; // Air, AIO 240mm, AIO 360mm

    @Nullable
    private String color;

    public Cooler(
            Long id,
            String model,
            String brand,
            String socket,
            Integer maxTdp,
            String type,
            String color,
            BigDecimal price
    ) {
        super(id, model, brand, price);
        this.socket = socket;
        this.maxTdp = maxTdp;
        this.type = type;
        this.color = color;
    }
}
