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
@Table(name = "rams")
@Getter
@Setter
@NoArgsConstructor
public class Ram extends Part {

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
