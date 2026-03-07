package com.team15.partpicker.controller;

import com.team15.partpicker.model.entity.Cpu;
import com.team15.partpicker.model.entity.Gpu;
import com.team15.partpicker.model.entity.Motherboard;

import java.math.BigDecimal;

public class RecommendationResponse {

    private Cpu cpu;
    private Gpu gpu;
    private Motherboard motherboard;
    private BigDecimal totalPrice;

    public RecommendationResponse(Cpu cpu, Gpu gpu, Motherboard motherboard, BigDecimal totalPrice) {
        this.cpu = cpu;
        this.gpu = gpu;
        this.motherboard = motherboard;
        this.totalPrice = totalPrice;
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

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
