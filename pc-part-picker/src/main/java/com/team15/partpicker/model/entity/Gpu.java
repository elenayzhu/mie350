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
@Table(name = "gpus")
@Getter
@Setter
@NoArgsConstructor
public class Gpu extends Part {

    @NotBlank
    private String manufacturer;

    @Positive
    @Nullable
    private Integer lengthMm;

    @Positive
    @NotNull
    private Integer vramGb;

    @Positive
    @Nullable
    private Integer tdp;

    @Nullable
    private String color;

    public Gpu(
            Long id,
            String model,
            String brand,
            String manufacturer,
            Integer lengthMm,
            Integer vramGb,
            Integer tdp,
            String color,
            BigDecimal price
    ) {
        super(id, model, brand, price);
        this.manufacturer = manufacturer;
        this.lengthMm = lengthMm;
        this.vramGb = vramGb;
        this.tdp = tdp;
        this.color = color;
    }
}
