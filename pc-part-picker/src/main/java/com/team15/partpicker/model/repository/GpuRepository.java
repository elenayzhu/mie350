package com.team15.partpicker.model.repository;

import com.team15.partpicker.model.entity.Gpu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface GpuRepository extends JpaRepository<Gpu, Long> {

    @Query(value = """
            SELECT *
            FROM gpus
            WHERE (:query IS NULL
                   OR LOWER(model) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(brand) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(manufacturer) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(COALESCE(color, '')) LIKE LOWER(CONCAT('%', :query, '%')))
              AND (:brand IS NULL OR LOWER(brand) = LOWER(:brand))
              AND (:minVramGb IS NULL OR vramGb >= :minVramGb)
              AND (:minPrice IS NULL OR price >= :minPrice)
              AND (:maxPrice IS NULL OR price <= :maxPrice)
            ORDER BY id
            """, nativeQuery = true)
    List<Gpu> search(
            @Param("query") String query,
            @Param("brand") String brand,
            @Param("minVramGb") Integer minVramGb,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    List<Gpu> findByPriceLessThanEqual(BigDecimal maxPrice);

    List<Gpu> findByBrandIgnoreCaseAndPriceLessThanEqual(String brand, BigDecimal maxPrice);
}
