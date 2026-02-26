package com.ibm.daytrader.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ibm.daytrader.config.TradeProperties;
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

    // No @Transactional here — each service call runs in its own transaction.
    // This prevents a single failure from rolling back the entire populate,
    // and avoids an unbounded Hibernate persistence context.
    public String populateDatabase() {
        int maxQuotes = tradeProperties.getMaxQuotes();
        int maxUsers = tradeProperties.getMaxUsers();
        int maxHoldings = tradeProperties.getMaxHoldings();

        StringBuilder sb = new StringBuilder();
        sb.append("Populating database with ").append(maxQuotes).append(" quotes and ")
                .append(maxUsers).append(" users\n");

        int quotesCreated = 0;
        int quoteErrors = 0;

        // Create quotes
        for (int i = 0; i < maxQuotes; i++) {
            String symbol = "s:" + i;
            String companyName = "S" + i + " Incorporated";
            BigDecimal price = tradeConfig.rndBigDecimal(1000.0f);
            try {
                tradeService.createQuote(symbol, companyName, price);
                quotesCreated++;
            } catch (Exception e) {
                quoteErrors++;
                if (e.getMessage() == null || !e.getMessage().contains("duplicate")) {
                    log.warn("Error creating quote {}: {}", symbol, e.getMessage());
                }
            }
            if (i % 1000 == 0 && i > 0) {
                log.info("Quote progress: {}/{}", i, maxQuotes);
            }
        }
        sb.append("Created ").append(quotesCreated).append(" quotes");
        if (quoteErrors > 0) {
            sb.append(" (").append(quoteErrors).append(" skipped/errors)");
        }
        sb.append("\n");
        log.info("Quotes done: {} created, {} errors", quotesCreated, quoteErrors);

        int usersCreated = 0;
        int userErrors = 0;

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
                    usersCreated++;
                    // Create some initial holdings via buy orders
                    int numHoldings = tradeConfig.rndInt(maxHoldings) + 1;
                    for (int j = 0; j < numHoldings; j++) {
                        try {
                            String symbol = tradeConfig.rndSymbol();
                            double quantity = tradeConfig.rndQuantity();
                            tradeService.buy(userID, symbol, quantity);
                        } catch (Exception e) {
                            log.warn("Error creating holding for {}: {}", userID, e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                userErrors++;
                log.warn("Error creating user {}: {}", userID, e.getMessage());
            }
            if (i % 1000 == 0 && i > 0) {
                log.info("User progress: {}/{}", i, maxUsers);
            }
        }
        sb.append("Created ").append(usersCreated).append(" users with holdings");
        if (userErrors > 0) {
            sb.append(" (").append(userErrors).append(" errors)");
        }
        sb.append("\nDatabase population complete.");

        log.info("Population complete: {} quotes, {} users, {} user errors",
                quotesCreated, usersCreated, userErrors);
        return sb.toString();
    }
}
