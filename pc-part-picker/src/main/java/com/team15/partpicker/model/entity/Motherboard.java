package com.team15.partpicker.model.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.lang.Nullable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "motherboards")
@Getter
@Setter
@NoArgsConstructor
public class Motherboard extends Part {

    @Nullable
    private String ddrType;

    @Nullable
    private String socket;

    @Nullable
    private String formFactor;

    @Nullable
    private String color;

    @Nullable
    private Integer memorySlots; // 2, 4

    public Motherboard(
            Long id,
            String model,
            String brand,
            String ddrType,
            String socket,
            String formFactor,
            String color,
            Integer memorySlots,
            BigDecimal price
    ) {
        super(id, model, brand, price);
        this.ddrType = ddrType;
        this.socket = socket;
        this.formFactor = formFactor;
        this.color = color;
        this.memorySlots = memorySlots;
    }
}
