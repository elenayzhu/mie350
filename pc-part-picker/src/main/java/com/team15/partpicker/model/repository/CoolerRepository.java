package com.team15.partpicker.model.repository;

import com.team15.partpicker.model.entity.Cooler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CoolerRepository extends JpaRepository<Cooler, Long> {

    @Query(value = """
            SELECT *
            FROM coolers
            WHERE (:query IS NULL
                   OR LOWER(model) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(brand) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(COALESCE(socket, '')) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(type) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(COALESCE(color, '')) LIKE LOWER(CONCAT('%', :query, '%')))
              AND (:brand IS NULL OR LOWER(brand) = LOWER(:brand))
              AND (:socket IS NULL OR LOWER(socket) = LOWER(:socket))
              AND (:type IS NULL OR LOWER(type) = LOWER(:type))
              AND (:minMaxTdp IS NULL OR maxTdp >= :minMaxTdp)
              AND (:minPrice IS NULL OR price >= :minPrice)
              AND (:maxPrice IS NULL OR price <= :maxPrice)
            ORDER BY id
            """, nativeQuery = true)
    List<Cooler> search(
            @Param("query") String query,
            @Param("brand") String brand,
            @Param("socket") String socket,
            @Param("type") String type,
            @Param("minMaxTdp") Integer minMaxTdp,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    List<Cooler> findByPriceLessThanEqual(BigDecimal maxPrice);

    List<Cooler> findByBrandIgnoreCaseAndPriceLessThanEqual(String brand, BigDecimal maxPrice);

    List<Cooler> findBySocketIgnoreCase(String socket);

    List<Cooler> findBySocketIgnoreCaseAndPriceLessThanEqual(String socket, BigDecimal maxPrice);

    List<Cooler> findByMaxTdpGreaterThanEqual(Integer minTdp);
}
