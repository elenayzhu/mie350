package com.team15.partpicker.model.repository;

import com.team15.partpicker.model.entity.Motherboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface MotherboardRepository extends JpaRepository<Motherboard, Long> {

    @Query(value = """
            SELECT *
            FROM motherboards
            WHERE (:query IS NULL
                   OR LOWER(model) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(brand) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(ddrType) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(socket) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(formFactor) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(COALESCE(color, '')) LIKE LOWER(CONCAT('%', :query, '%')))
              AND (:brand IS NULL OR LOWER(brand) = LOWER(:brand))
              AND (:socket IS NULL OR LOWER(socket) = LOWER(:socket))
              AND (:formFactor IS NULL OR LOWER(formFactor) = LOWER(:formFactor))
              AND (:minPrice IS NULL OR price >= :minPrice)
              AND (:maxPrice IS NULL OR price <= :maxPrice)
            ORDER BY id
            """, nativeQuery = true)
    List<Motherboard> search(
            @Param("query") String query,
            @Param("brand") String brand,
            @Param("socket") String socket,
            @Param("formFactor") String formFactor,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    List<Motherboard> findByPriceLessThanEqual(BigDecimal maxPrice);

    List<Motherboard> findByBrandIgnoreCaseAndPriceLessThanEqual(String brand, BigDecimal maxPrice);

    List<Motherboard> findBySocketIgnoreCaseAndPriceLessThanEqual(String socket, BigDecimal maxPrice);

    List<Motherboard> findBySocketIgnoreCaseAndBrandIgnoreCaseAndPriceLessThanEqual(
            String socket,
            String brand,
            BigDecimal maxPrice
    );
}
