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
@Table(name = "rams")
@Getter
@Setter
@NoArgsConstructor
public class Ram extends Part {

    @Nullable
    private String ddrType; // DDR4, DDR5

    @Positive
    @Nullable
    private Integer speedRatio; // changed from speedMhz to speedRatio (speedMhz / CAS Latency)

    @Positive
    @Nullable
    private Integer capacityGb; // 8, 16, 32

    @Nullable
    private String color;

    public Ram(
            Long id,
            String model,
            String brand,
            String ddrType,
            Integer speedRatio,
            Integer capacityGb,
            String color,
            BigDecimal price
    ) {
        super(id, model, brand, price);
        this.ddrType = ddrType;
        this.speedRatio = speedRatio;
        this.capacityGb = capacityGb;
        this.color = color;
    }
}
