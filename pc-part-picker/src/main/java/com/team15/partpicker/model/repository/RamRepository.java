package com.team15.partpicker.model.repository;

import com.team15.partpicker.model.entity.Ram;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface RamRepository extends JpaRepository<Ram, Long> {

    List<Ram> findByPriceLessThanEqual(BigDecimal maxPrice);

    List<Ram> findByBrandIgnoreCaseAndPriceLessThanEqual(String brand, BigDecimal maxPrice);

    List<Ram> findByDdrTypeIgnoreCase(String ddrType);

    List<Ram> findByDdrTypeIgnoreCaseAndPriceLessThanEqual(String ddrType, BigDecimal maxPrice);
}
