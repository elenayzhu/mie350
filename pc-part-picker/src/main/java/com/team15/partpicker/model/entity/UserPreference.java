package com.team15.partpicker.model.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Entity
@Table(name = "user_preferences")
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String preferredCpuBrand;
    private String preferredGpuBrand;
    private String preferredMotherboardBrand;

    @DecimalMin("0.0")
    private BigDecimal maxBudget;

    public UserPreference() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPreferredCpuBrand() {
        return preferredCpuBrand;
    }

    public void setPreferredCpuBrand(String preferredCpuBrand) {
        this.preferredCpuBrand = preferredCpuBrand;
    }

    public String getPreferredGpuBrand() {
        return preferredGpuBrand;
    }

    public void setPreferredGpuBrand(String preferredGpuBrand) {
        this.preferredGpuBrand = preferredGpuBrand;
    }

    public String getPreferredMotherboardBrand() {
        return preferredMotherboardBrand;
    }

    public void setPreferredMotherboardBrand(String preferredMotherboardBrand) {
        this.preferredMotherboardBrand = preferredMotherboardBrand;
    }

    public BigDecimal getMaxBudget() {
        return maxBudget;
    }

    public void setMaxBudget(BigDecimal maxBudget) {
        this.maxBudget = maxBudget;
    }
}
