package com.team15.partpicker.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.Column;
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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    @Column(name = "build_category")
    private BuildCategory buildCategory;

    @DecimalMin("0.0")
    @Column(name = "max_budget")
    private BigDecimal maxBudget;
}