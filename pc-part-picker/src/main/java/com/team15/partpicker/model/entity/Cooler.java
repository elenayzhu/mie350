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
@Table(name = "coolers")
public class Cooler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String model;

    @NotBlank
    private String brand;

    @NotBlank
    private String socket; // CPU socket compatibility, e.g., "AM5", "LGA1700"

    @Positive
    @NotNull
    private Integer maxTdp; // Maximum TDP in watts that the cooler can handle

    @NotBlank
    private String type; // e.g., "Air", "AIO 240mm", "AIO 360mm"

    @DecimalMin("0.0")
    @NotNull
    private BigDecimal price;

    public Cooler() {
    }

    public Cooler(String model, String brand, String socket, Integer maxTdp, String type, BigDecimal price) {
        this.model = model;
        this.brand = brand;
        this.socket = socket;
        this.maxTdp = maxTdp;
        this.type = type;
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

    public String getSocket() {
        return socket;
    }

    public void setSocket(String socket) {
        this.socket = socket;
    }

    public Integer getMaxTdp() {
        return maxTdp;
    }

    public void setMaxTdp(Integer maxTdp) {
        this.maxTdp = maxTdp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
