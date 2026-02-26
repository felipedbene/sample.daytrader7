package com.ibm.daytrader.config;

import java.math.BigDecimal;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "trade")
public class TradeProperties {

    private int maxUsers = 15000;
    private int maxQuotes = 10000;
    private int maxHoldings = 10;
    private BigDecimal orderFee = new BigDecimal("24.95");
    private long marketSummaryIntervalMs = 20000;
    private boolean updateQuotePrices = true;
    private boolean publishQuotePriceChange = true;
    private int percentSentToWebsocket = 5;
    private boolean displayOrderAlerts = true;
    private boolean longRun = true;

    public int getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }

    public int getMaxQuotes() {
        return maxQuotes;
    }

    public void setMaxQuotes(int maxQuotes) {
        this.maxQuotes = maxQuotes;
    }

    public int getMaxHoldings() {
        return maxHoldings;
    }

    public void setMaxHoldings(int maxHoldings) {
        this.maxHoldings = maxHoldings;
    }

    public BigDecimal getOrderFee() {
        return orderFee;
    }

    public void setOrderFee(BigDecimal orderFee) {
        this.orderFee = orderFee;
    }

    public long getMarketSummaryIntervalMs() {
        return marketSummaryIntervalMs;
    }

    public void setMarketSummaryIntervalMs(long marketSummaryIntervalMs) {
        this.marketSummaryIntervalMs = marketSummaryIntervalMs;
    }

    public boolean isUpdateQuotePrices() {
        return updateQuotePrices;
    }

    public void setUpdateQuotePrices(boolean updateQuotePrices) {
        this.updateQuotePrices = updateQuotePrices;
    }

    public boolean isPublishQuotePriceChange() {
        return publishQuotePriceChange;
    }

    public void setPublishQuotePriceChange(boolean publishQuotePriceChange) {
        this.publishQuotePriceChange = publishQuotePriceChange;
    }

    public int getPercentSentToWebsocket() {
        return percentSentToWebsocket;
    }

    public void setPercentSentToWebsocket(int percentSentToWebsocket) {
        this.percentSentToWebsocket = percentSentToWebsocket;
    }

    public boolean isDisplayOrderAlerts() {
        return displayOrderAlerts;
    }

    public void setDisplayOrderAlerts(boolean displayOrderAlerts) {
        this.displayOrderAlerts = displayOrderAlerts;
    }

    public boolean isLongRun() {
        return longRun;
    }

    public void setLongRun(boolean longRun) {
        this.longRun = longRun;
    }
}
