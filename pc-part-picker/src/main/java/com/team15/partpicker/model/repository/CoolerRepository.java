package com.team15.partpicker.model.repository;

import com.team15.partpicker.model.entity.Cooler;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface CoolerRepository extends JpaRepository<Cooler, Long> {

    List<Cooler> findByPriceLessThanEqual(BigDecimal maxPrice);

    List<Cooler> findByBrandIgnoreCaseAndPriceLessThanEqual(String brand, BigDecimal maxPrice);

    List<Cooler> findBySocketIgnoreCase(String socket);

    List<Cooler> findBySocketIgnoreCaseAndPriceLessThanEqual(String socket, BigDecimal maxPrice);

    List<Cooler> findByMaxTdpGreaterThanEqual(Integer minTdp);
}
