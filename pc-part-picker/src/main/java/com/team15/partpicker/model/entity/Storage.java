package com.team15.partpicker.model.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import org.springframework.lang.Nullable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "storages")
@Getter
@Setter
@NoArgsConstructor
public class Storage extends Part {

    @NotBlank
    private String type; // NVMe SSD, SATA SSD, HDD

    @Positive
    @Nullable
    private Integer capacityGb; // 500, 1000, 2000

    public Storage(Long id, String model, String brand, String type, Integer capacityGb, BigDecimal price) {
        super(id, model, brand, price);
        this.type = type;
        this.capacityGb = capacityGb;
    }
}
