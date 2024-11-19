package com.devloop.stock.repository;

import com.devloop.stock.entity.Stock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByProductId(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Stock s JOIN FETCH s.product p WHERE p.id IN :productIds")
    List<Stock> findByProductIdsWithLock(@Param("productIds") List<Long> productIds);
}
