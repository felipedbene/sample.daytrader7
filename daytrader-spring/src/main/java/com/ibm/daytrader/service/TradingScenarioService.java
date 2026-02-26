package com.ibm.daytrader.service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ibm.daytrader.entity.HoldingDataBean;
import com.ibm.daytrader.util.TradeConfig;

@Service
public class TradingScenarioService {

    private static final Logger log = LoggerFactory.getLogger(TradingScenarioService.class);

    private final TradeService tradeService;
    private final TradeConfig tradeConfig;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicLong totalOps = new AtomicLong(0);
    private final AtomicLong errors = new AtomicLong(0);
    private volatile long startTime;
    private volatile int threadCount;
    private ExecutorService executor;

    public TradingScenarioService(TradeService tradeService, TradeConfig tradeConfig) {
        this.tradeService = tradeService;
        this.tradeConfig = tradeConfig;
    }

    public synchronized void start(int threads) {
        if (running.get()) {
            log.warn("Scenario already running");
            return;
        }

        threadCount = threads;
        totalOps.set(0);
        errors.set(0);
        startTime = System.currentTimeMillis();
        running.set(true);

        executor = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            executor.submit(this::tradeLoop);
        }

        log.info("Trading scenario started with {} threads", threads);
    }

    public synchronized void stop() {
        if (!running.get()) {
            return;
        }
        running.set(false);
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
        log.info("Trading scenario stopped. Total ops: {}, errors: {}", totalOps.get(), errors.get());
    }

    public boolean isRunning() {
        return running.get();
    }

    public String getStats() {
        long ops = totalOps.get();
        long errs = errors.get();
        long elapsed = System.currentTimeMillis() - startTime;
        double opsPerSec = elapsed > 0 ? (ops * 1000.0) / elapsed : 0;

        return String.format("Threads: %d | Ops: %d | Errors: %d | %.1f ops/sec | Elapsed: %ds",
                threadCount, ops, errs, opsPerSec, elapsed / 1000);
    }

    private void tradeLoop() {
        while (running.get() && !Thread.currentThread().isInterrupted()) {
            try {
                String userID = tradeConfig.rndUserID();

                // 50/50 buy vs sell
                if (tradeConfig.random() < 0.5) {
                    // Buy a random stock
                    String symbol = tradeConfig.rndSymbol();
                    double quantity = tradeConfig.rndQuantity();
                    tradeService.buy(userID, symbol, quantity);
                } else {
                    // Sell a random holding
                    List<HoldingDataBean> holdings = tradeService.getHoldings(userID);
                    if (holdings != null && !holdings.isEmpty()) {
                        int idx = tradeConfig.rndInt(holdings.size());
                        tradeService.sell(userID, holdings.get(idx).getHoldingID());
                    } else {
                        // No holdings — buy instead
                        String symbol = tradeConfig.rndSymbol();
                        double quantity = tradeConfig.rndQuantity();
                        tradeService.buy(userID, symbol, quantity);
                    }
                }

                totalOps.incrementAndGet();
            } catch (Exception e) {
                errors.incrementAndGet();
                if (log.isDebugEnabled()) {
                    log.debug("Scenario error: {}", e.getMessage());
                }
            }
        }
    }
}
