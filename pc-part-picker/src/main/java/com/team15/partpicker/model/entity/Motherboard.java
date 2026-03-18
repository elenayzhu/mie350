package com.team15.partpicker.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import org.springframework.lang.Nullable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Entity
@Table(name = "motherboards")
@Getter
@Setter
@NoArgsConstructor
public class Motherboard extends Part {

    @NotBlank
    private String ddrType;

    @NotBlank
    private String socket;

    @NotBlank
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
