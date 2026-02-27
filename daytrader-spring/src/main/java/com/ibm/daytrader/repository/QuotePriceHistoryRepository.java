package com.ibm.daytrader.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ibm.daytrader.entity.QuotePriceHistory;

public interface QuotePriceHistoryRepository extends JpaRepository<QuotePriceHistory, Long> {

    List<QuotePriceHistory> findBySymbolOrderByRecordedAtAsc(String symbol);

    @Modifying
    @Query("DELETE FROM QuotePriceHistory h WHERE h.recordedAt < :cutoff")
    int deleteOlderThan(@Param("cutoff") LocalDateTime cutoff);
}
