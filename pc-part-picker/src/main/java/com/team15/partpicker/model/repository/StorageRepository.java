package com.team15.partpicker.model.repository;

import com.team15.partpicker.model.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface StorageRepository extends JpaRepository<Storage, Long> {

    @Query(value = """
            SELECT *
            FROM storages
            WHERE (:query IS NULL
                   OR LOWER(model) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(brand) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(type) LIKE LOWER(CONCAT('%', :query, '%')))
              AND (:brand IS NULL OR LOWER(brand) = LOWER(:brand))
              AND (:type IS NULL OR LOWER(type) = LOWER(:type))
              AND (:minCapacity IS NULL OR capacityGb >= :minCapacity)
              AND (:minPrice IS NULL OR price >= :minPrice)
              AND (:maxPrice IS NULL OR price <= :maxPrice)
            ORDER BY id
            """, nativeQuery = true)
    List<Storage> search(
            @Param("query") String query,
            @Param("brand") String brand,
            @Param("type") String type,
            @Param("minCapacity") Integer minCapacity,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    List<Storage> findByPriceLessThanEqual(BigDecimal maxPrice);

    List<Storage> findByBrandIgnoreCaseAndPriceLessThanEqual(String brand, BigDecimal maxPrice);

    List<Storage> findByTypeIgnoreCase(String type);

    List<Storage> findByTypeIgnoreCaseAndPriceLessThanEqual(String type, BigDecimal maxPrice);
}
