package com.team15.partpicker.model.repository;

import com.team15.partpicker.model.entity.Ram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface RamRepository extends JpaRepository<Ram, Long> {

    @Query(value = """
            SELECT *
            FROM rams
            WHERE (:query IS NULL
                   OR LOWER(model) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(brand) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(ddrType) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(COALESCE(color, '')) LIKE LOWER(CONCAT('%', :query, '%')))
              AND (:brand IS NULL OR LOWER(brand) = LOWER(:brand))
              AND (:ddrType IS NULL OR LOWER(ddrType) = LOWER(:ddrType))
              AND (:minSpeed IS NULL OR speedRatio >= :minSpeed)
              AND (:minCapacity IS NULL OR capacityGb >= :minCapacity)
              AND (:minPrice IS NULL OR price >= :minPrice)
              AND (:maxPrice IS NULL OR price <= :maxPrice)
            ORDER BY id
            """, nativeQuery = true)
    List<Ram> search(
            @Param("query") String query,
            @Param("brand") String brand,
            @Param("ddrType") String ddrType,
            @Param("minSpeed") Integer minSpeed,
            @Param("minCapacity") Integer minCapacity,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    List<Ram> findByPriceLessThanEqual(BigDecimal maxPrice);

    List<Ram> findByBrandIgnoreCaseAndPriceLessThanEqual(String brand, BigDecimal maxPrice);

    List<Ram> findByDdrTypeIgnoreCase(String ddrType);

    List<Ram> findByDdrTypeIgnoreCaseAndPriceLessThanEqual(String ddrType, BigDecimal maxPrice);
}
