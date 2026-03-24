package com.team15.partpicker.model.repository;

import com.team15.partpicker.model.entity.Cpu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CpuRepository extends JpaRepository<Cpu, Long> {

    @Query(value = """
            SELECT *
            FROM cpus
            WHERE (:query IS NULL
                   OR LOWER(model) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(brand) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(socket) LIKE LOWER(CONCAT('%', :query, '%')))
              AND (:brand IS NULL OR LOWER(brand) = LOWER(:brand))
              AND (:socket IS NULL OR LOWER(socket) = LOWER(:socket))
              AND (:minCores IS NULL OR cores >= :minCores)
              AND (:minPrice IS NULL OR price >= :minPrice)
              AND (:maxPrice IS NULL OR price <= :maxPrice)
            ORDER BY id
            """, nativeQuery = true)
    List<Cpu> search(
            @Param("query") String query,
            @Param("brand") String brand,
            @Param("socket") String socket,
            @Param("minCores") Integer minCores,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    List<Cpu> findByPriceLessThanEqual(BigDecimal maxPrice);

    List<Cpu> findByBrandIgnoreCaseAndPriceLessThanEqual(String brand, BigDecimal maxPrice);
}
