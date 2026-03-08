package com.team15.partpicker.model.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
    private String preferredRamBrand;
    private String preferredPsuBrand;
    private String preferredCaseBrand;
    private String preferredStorageBrand;
    private String preferredCoolerBrand;

    @Enumerated(EnumType.STRING)
    private BuildCategory buildCategory;

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

    public String getPreferredRamBrand() {
        return preferredRamBrand;
    }

    public void setPreferredRamBrand(String preferredRamBrand) {
        this.preferredRamBrand = preferredRamBrand;
    }

    public String getPreferredPsuBrand() {
        return preferredPsuBrand;
    }

    public void setPreferredPsuBrand(String preferredPsuBrand) {
        this.preferredPsuBrand = preferredPsuBrand;
    }

    public String getPreferredCaseBrand() {
        return preferredCaseBrand;
    }

    public void setPreferredCaseBrand(String preferredCaseBrand) {
        this.preferredCaseBrand = preferredCaseBrand;
    }

    public String getPreferredStorageBrand() {
        return preferredStorageBrand;
    }

    public void setPreferredStorageBrand(String preferredStorageBrand) {
        this.preferredStorageBrand = preferredStorageBrand;
    }

    public String getPreferredCoolerBrand() {
        return preferredCoolerBrand;
    }

    public void setPreferredCoolerBrand(String preferredCoolerBrand) {
        this.preferredCoolerBrand = preferredCoolerBrand;
    }

    public BuildCategory getBuildCategory() {
        return buildCategory;
    }

    public void setBuildCategory(BuildCategory buildCategory) {
        this.buildCategory = buildCategory;
    }
}
