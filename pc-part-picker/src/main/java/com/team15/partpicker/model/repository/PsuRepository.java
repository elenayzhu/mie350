package com.team15.partpicker.model.repository;

import com.team15.partpicker.model.entity.Psu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PsuRepository extends JpaRepository<Psu, Long> {

    @Query(value = """
            SELECT *
            FROM psus
            WHERE (:query IS NULL
                   OR LOWER(model) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(brand) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(COALESCE(efficiencyRating, '')) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(modularType) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(COALESCE(color, '')) LIKE LOWER(CONCAT('%', :query, '%')))
              AND (:brand IS NULL OR LOWER(brand) = LOWER(:brand))
              AND (:minWattage IS NULL OR wattage >= :minWattage)
              AND (:efficiencyRating IS NULL OR LOWER(efficiencyRating) = LOWER(:efficiencyRating))
              AND (:modularType IS NULL OR LOWER(modularType) = LOWER(:modularType))
              AND (:minPrice IS NULL OR price >= :minPrice)
              AND (:maxPrice IS NULL OR price <= :maxPrice)
            ORDER BY id
            """, nativeQuery = true)
    List<Psu> search(
            @Param("query") String query,
            @Param("brand") String brand,
            @Param("minWattage") Integer minWattage,
            @Param("efficiencyRating") String efficiencyRating,
            @Param("modularType") String modularType,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    List<Psu> findByPriceLessThanEqual(BigDecimal maxPrice);

    List<Psu> findByBrandIgnoreCaseAndPriceLessThanEqual(String brand, BigDecimal maxPrice);

    List<Psu> findByWattageGreaterThanEqual(Integer minWattage);

    List<Psu> findByWattageGreaterThanEqualAndPriceLessThanEqual(Integer minWattage, BigDecimal maxPrice);
}
