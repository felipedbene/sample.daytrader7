package com.ibm.daytrader.event;

import java.math.BigDecimal;

import org.springframework.context.ApplicationEvent;

public class QuotePriceChangeEvent extends ApplicationEvent {

    private final String symbol;
    private final BigDecimal oldPrice;
    private final BigDecimal newPrice;

    public QuotePriceChangeEvent(Object source, String symbol, BigDecimal oldPrice, BigDecimal newPrice) {
        super(source);
        this.symbol = symbol;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }
}
