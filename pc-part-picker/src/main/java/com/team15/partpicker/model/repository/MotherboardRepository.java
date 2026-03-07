package com.team15.partpicker.model.repository;

import com.team15.partpicker.model.entity.Motherboard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface MotherboardRepository extends JpaRepository<Motherboard, Long> {

    List<Motherboard> findByPriceLessThanEqual(BigDecimal maxPrice);

    List<Motherboard> findByBrandIgnoreCaseAndPriceLessThanEqual(String brand, BigDecimal maxPrice);

    List<Motherboard> findBySocketIgnoreCaseAndPriceLessThanEqual(String socket, BigDecimal maxPrice);

    List<Motherboard> findBySocketIgnoreCaseAndBrandIgnoreCaseAndPriceLessThanEqual(
            String socket,
            String brand,
            BigDecimal maxPrice
    );
}
