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
@Table(name = "psus")
public class Psu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String model;

    @NotBlank
    private String brand;

    @Positive
    @NotNull
    private Integer wattage; // e.g., 650, 750, 850

    @NotBlank
    private String efficiencyRating; // e.g., "80+ Bronze", "80+ Gold", "80+ Platinum"

    @NotBlank
    private String modularType; // e.g., "Fully Modular", "Semi-Modular", "Non-Modular"

    @DecimalMin("0.0")
    @NotNull
    private BigDecimal price;

    public Psu() {
    }

    public Psu(String model, String brand, Integer wattage, String efficiencyRating, String modularType, BigDecimal price) {
        this.model = model;
        this.brand = brand;
        this.wattage = wattage;
        this.efficiencyRating = efficiencyRating;
        this.modularType = modularType;
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

    public Integer getWattage() {
        return wattage;
    }

    public void setWattage(Integer wattage) {
        this.wattage = wattage;
    }

    public String getEfficiencyRating() {
        return efficiencyRating;
    }

    public void setEfficiencyRating(String efficiencyRating) {
        this.efficiencyRating = efficiencyRating;
    }

    public String getModularType() {
        return modularType;
    }

    public void setModularType(String modularType) {
        this.modularType = modularType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
