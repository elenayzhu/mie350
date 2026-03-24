package com.team15.partpicker.model.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import org.springframework.lang.Nullable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cpus")
@Getter
@Setter
@NoArgsConstructor
public class Cpu extends Part {

    @NotBlank
    private String socket;

    @Positive
    @Nullable
    private Integer cores;

    @Positive
    @Nullable
    private Integer tdp;

    public Cpu(Long id, String model, String brand, String socket, Integer cores, Integer tdp, BigDecimal price) {
        super(id, model, brand, price);
        this.socket = socket;
        this.cores = cores;
        this.tdp = tdp;
    }
}
