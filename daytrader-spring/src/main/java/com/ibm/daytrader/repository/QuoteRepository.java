package com.ibm.daytrader.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ibm.daytrader.entity.QuoteDataBean;

import jakarta.persistence.LockModeType;

public interface QuoteRepository extends JpaRepository<QuoteDataBean, String> {

    @Query("SELECT q FROM QuoteDataBean q ORDER BY q.change1 DESC")
    List<QuoteDataBean> findAllOrderByChangeDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT q FROM QuoteDataBean q WHERE q.symbol = :symbol")
    Optional<QuoteDataBean> findBySymbolForUpdate(@Param("symbol") String symbol);
}
