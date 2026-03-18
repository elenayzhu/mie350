package com.team15.partpicker.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(name = "cpus")
@Getter
@Setter
@NoArgsConstructor
public class Cpu extends Part {

    @NotBlank
    private String socket;

    @Positive
    @NotNull
    private Integer cores;

    @Positive
    @NotNull
    private Integer tdp;

    public Cpu(Long id, String model, String brand, String socket, Integer cores, Integer tdp, BigDecimal price) {
        super(id, model, brand, price);
        this.socket = socket;
        this.cores = cores;
        this.tdp = tdp;
    }
}
