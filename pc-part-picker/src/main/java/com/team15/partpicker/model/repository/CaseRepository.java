package com.team15.partpicker.model.repository;

import com.team15.partpicker.model.entity.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CaseRepository extends JpaRepository<Case, Long> {

    @Query(value = """
            SELECT *
            FROM cases
            WHERE (:query IS NULL
                   OR LOWER(model) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(brand) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(COALESCE(formFactor, '')) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(type) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(COALESCE(color, '')) LIKE LOWER(CONCAT('%', :query, '%')))
              AND (:brand IS NULL OR LOWER(brand) = LOWER(:brand))
              AND (:formFactor IS NULL OR LOWER(formFactor) = LOWER(:formFactor))
              AND (:minMaxGpuLengthMm IS NULL OR maxGpuLengthMm >= :minMaxGpuLengthMm)
              AND (:minPrice IS NULL OR price >= :minPrice)
              AND (:maxPrice IS NULL OR price <= :maxPrice)
            ORDER BY id
            """, nativeQuery = true)
    List<Case> search(
            @Param("query") String query,
            @Param("brand") String brand,
            @Param("formFactor") String formFactor,
            @Param("minMaxGpuLengthMm") Integer minMaxGpuLengthMm,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    List<Case> findByPriceLessThanEqual(BigDecimal maxPrice);

    List<Case> findByBrandIgnoreCaseAndPriceLessThanEqual(String brand, BigDecimal maxPrice);

    List<Case> findByFormFactorIgnoreCase(String formFactor);

    List<Case> findByFormFactorIgnoreCaseAndPriceLessThanEqual(String formFactor, BigDecimal maxPrice);

    List<Case> findByMaxGpuLengthMmGreaterThanEqual(Integer minGpuLength);
}
