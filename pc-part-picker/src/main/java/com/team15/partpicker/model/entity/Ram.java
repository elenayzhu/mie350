package com.team15.partpicker.model.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(name = "rams")
public class Ram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String model;

    @NotBlank
    private String brand;

    @NotBlank
    private String ddrType; // e.g., "DDR4", "DDR5"

    @Positive
    @NotNull
    private Integer speedMhz; // e.g., 3200, 3600, 6000

    @Positive
    @NotNull
    private Integer capacityGb; // e.g., 8, 16, 32

    @DecimalMin("0.0")
    @NotNull
    private BigDecimal price;

    public Ram() {
    }

    public Ram(String model, String brand, String ddrType, Integer speedMhz, Integer capacityGb, BigDecimal price) {
        this.model = model;
        this.brand = brand;
        this.ddrType = ddrType;
        this.speedMhz = speedMhz;
        this.capacityGb = capacityGb;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDdrType() {
        return ddrType;
    }

    public void setDdrType(String ddrType) {
        this.ddrType = ddrType;
    }

    public Integer getSpeedMhz() {
        return speedMhz;
    }

    public void setSpeedMhz(Integer speedMhz) {
        this.speedMhz = speedMhz;
    }

    public Integer getCapacityGb() {
        return capacityGb;
    }

    public void setCapacityGb(Integer capacityGb) {
        this.capacityGb = capacityGb;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
