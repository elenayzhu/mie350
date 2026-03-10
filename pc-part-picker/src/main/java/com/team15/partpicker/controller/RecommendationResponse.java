package com.team15.partpicker.controller;

import com.team15.partpicker.model.entity.Case;
import com.team15.partpicker.model.entity.Cooler;
import com.team15.partpicker.model.entity.Cpu;
import com.team15.partpicker.model.entity.Gpu;
import com.team15.partpicker.model.entity.Motherboard;
import com.team15.partpicker.model.entity.Psu;
import com.team15.partpicker.model.entity.Ram;
import com.team15.partpicker.model.entity.Storage;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RecommendationResponse {

    private Long buildId;
    private Long preferenceId;
    private Cpu cpu;
    private Gpu gpu;
    private Motherboard motherboard;
    private Ram ram;
    private Storage storage;
    private Psu psu;
    private Cooler cooler;
    private Case computerCase;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;

    public RecommendationResponse(
            Long buildId,
            Long preferenceId,
            Cpu cpu,
            Gpu gpu,
            Motherboard motherboard,
            Ram ram,
            Storage storage,
            Psu psu,
            Cooler cooler,
            Case computerCase,
            BigDecimal totalPrice,
            LocalDateTime createdAt
    ) {
        this.buildId = buildId;
        this.preferenceId = preferenceId;
        this.cpu = cpu;
        this.gpu = gpu;
        this.motherboard = motherboard;
        this.ram = ram;
        this.storage = storage;
        this.psu = psu;
        this.cooler = cooler;
        this.computerCase = computerCase;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
    }

    public Long getBuildId() {
        return buildId;
    }

    public void setBuildId(Long buildId) {
        this.buildId = buildId;
    }

    public Long getPreferenceId() {
        return preferenceId;
    }

    public void setPreferenceId(Long preferenceId) {
        this.preferenceId = preferenceId;
    }

    public Cpu getCpu() {
        return cpu;
    }

    public void setCpu(Cpu cpu) {
        this.cpu = cpu;
    }

    public Gpu getGpu() {
        return gpu;
    }

    public void setGpu(Gpu gpu) {
        this.gpu = gpu;
    }

    public Motherboard getMotherboard() {
        return motherboard;
    }

    public void setMotherboard(Motherboard motherboard) {
        this.motherboard = motherboard;
    }

    public Ram getRam() {
        return ram;
    }

    public void setRam(Ram ram) {
        this.ram = ram;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public Psu getPsu() {
        return psu;
    }

    public void setPsu(Psu psu) {
        this.psu = psu;
    }

    public Cooler getCooler() {
        return cooler;
    }

    public void setCooler(Cooler cooler) {
        this.cooler = cooler;
    }

    public Case getComputerCase() {
        return computerCase;
    }

    public void setComputerCase(Case computerCase) {
        this.computerCase = computerCase;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
