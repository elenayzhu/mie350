package com.team15.partpicker.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "builds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Build {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "preferenceId", nullable = false)
    @JsonIgnore
    private UserPreference userPreference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userProfileId")
    @JsonIgnore
    private UserProfile userProfile;

    @NotNull
    @Column(nullable = false)
    private String buildTitle;

    @ManyToOne
    @JoinColumn(name = "cpuId")
    private Cpu cpu;

    @ManyToOne
    @JoinColumn(name = "gpuId")
    private Gpu gpu;

    @ManyToOne
    @JoinColumn(name = "motherboardId")
    private Motherboard motherboard;

    @ManyToOne
    @JoinColumn(name = "ramId")
    private Ram ram;

    @ManyToOne
    @JoinColumn(name = "storageId")
    private Storage storage;

    @ManyToOne
    @JoinColumn(name = "psuId")
    private Psu psu;

    @ManyToOne
    @JoinColumn(name = "coolerId")
    private Cooler cooler;

    @ManyToOne
    @JoinColumn(name = "caseId")
    private Case computerCase;

    @DecimalMin("0.0")
    @NotNull
    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getPreferenceId() {
        return userPreference == null ? null : userPreference.getId();
    }

    public Long getUserProfileId() {
        return userProfile == null ? null : userProfile.getId();
    }
}
