package com.ibm.daytrader.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.daytrader.config.TradeProperties;
import com.ibm.daytrader.dto.RunStatsDTO;
import com.ibm.daytrader.entity.AccountDataBean;
import com.ibm.daytrader.util.TradeConfig;

@Service
public class DatabaseInitService {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitService.class);

    private final TradeService tradeService;
    private final TradeProperties tradeProperties;
    private final TradeConfig tradeConfig;

    public DatabaseInitService(TradeService tradeService, TradeProperties tradeProperties,
                               TradeConfig tradeConfig) {
        this.tradeService = tradeService;
        this.tradeProperties = tradeProperties;
        this.tradeConfig = tradeConfig;
    }

    @Transactional
    public String populateDatabase() {
        int maxQuotes = tradeProperties.getMaxQuotes();
        int maxUsers = tradeProperties.getMaxUsers();
        int maxHoldings = tradeProperties.getMaxHoldings();

        StringBuilder sb = new StringBuilder();
        sb.append("Populating database with ").append(maxQuotes).append(" quotes and ")
                .append(maxUsers).append(" users\n");

        // Create quotes
        for (int i = 0; i < maxQuotes; i++) {
            String symbol = "s:" + i;
            String companyName = "S" + i + " Incorporated";
            BigDecimal price = tradeConfig.rndBigDecimal(1000.0f);
            try {
                tradeService.createQuote(symbol, companyName, price);
                if (i % 1000 == 0) {
                    log.info("Created {} quotes", i);
                }
            } catch (Exception e) {
                if (e.getMessage() != null && e.getMessage().contains("duplicate")) {
                    // Quote already exists, skip
                } else {
                    sb.append("Error creating quote ").append(symbol).append(": ").append(e.getMessage()).append("\n");
                }
            }
        }
        sb.append("Created ").append(maxQuotes).append(" quotes\n");

        // Create users and initial holdings
        for (int i = 0; i < maxUsers; i++) {
            String userID = "uid:" + i;
            String fullname = tradeConfig.rndFullName();
            String email = tradeConfig.rndEmail(userID);
            String address = tradeConfig.rndAddress();
            String creditcard = tradeConfig.rndCreditCard();
            BigDecimal openBalance = new BigDecimal("1000000.00");

            try {
                AccountDataBean account = tradeService.register(userID, userID, fullname,
                        address, email, creditcard, openBalance);

                if (account != null) {
                    // Create some initial holdings via buy orders
                    int numHoldings = tradeConfig.rndInt(maxHoldings) + 1;
                    for (int j = 0; j < numHoldings; j++) {
                        String symbol = tradeConfig.rndSymbol();
                        double quantity = tradeConfig.rndQuantity();
                        tradeService.buy(userID, symbol, quantity);
                    }
                }

                if (i % 1000 == 0) {
                    log.info("Created {} users", i);
                }
            } catch (Exception e) {
                sb.append("Error creating user ").append(userID).append(": ").append(e.getMessage()).append("\n");
            }
        }
        sb.append("Created ").append(maxUsers).append(" users with holdings\n");
        sb.append("Database population complete.");

        log.info("Database population complete: {} quotes, {} users", maxQuotes, maxUsers);
        return sb.toString();
    }
}
