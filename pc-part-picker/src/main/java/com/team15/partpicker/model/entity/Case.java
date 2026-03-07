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
@Table(name = "cases")
public class Case {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String model;

    @NotBlank
    private String brand;

    @NotBlank
    private String formFactor; // e.g., "ATX", "Micro-ATX", "Mini-ITX"

    @Positive
    @NotNull
    private Integer maxGpuLengthMm; // Maximum GPU length supported in mm

    @DecimalMin("0.0")
    @NotNull
    private BigDecimal price;

    public Case() {
    }

    public Case(String model, String brand, String formFactor, Integer maxGpuLengthMm, BigDecimal price) {
        this.model = model;
        this.brand = brand;
        this.formFactor = formFactor;
        this.maxGpuLengthMm = maxGpuLengthMm;
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

    public String getFormFactor() {
        return formFactor;
    }

    public void setFormFactor(String formFactor) {
        this.formFactor = formFactor;
    }

    public Integer getMaxGpuLengthMm() {
        return maxGpuLengthMm;
    }

    public void setMaxGpuLengthMm(Integer maxGpuLengthMm) {
        this.maxGpuLengthMm = maxGpuLengthMm;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
