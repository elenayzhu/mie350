package com.team15.partpicker.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userProfileId")
    @JsonIgnore
    private UserProfile userProfile;

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

    @JsonProperty("userProfileId")
    public Long getUserProfileId() {
        return userProfile == null ? null : userProfile.getId();
    }

    @JsonProperty("userProfileId")
    public void setUserProfileId(Long userProfileId) {
        if (userProfileId == null) {
            this.userProfile = null;
            return;
        }
        if (this.userProfile == null) {
            this.userProfile = new UserProfile();
        }
        this.userProfile.setId(userProfileId);
    }
}
