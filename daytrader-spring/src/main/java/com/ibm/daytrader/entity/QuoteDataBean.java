package com.ibm.daytrader.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "quoteejb")
public class QuoteDataBean implements Serializable {

    private static final long serialVersionUID = 1847932261895838791L;

    @Id
    @NotNull
    @Column(name = "SYMBOL", nullable = false)
    private String symbol;

    @Column(name = "COMPANYNAME")
    private String companyName;

    @NotNull
    @Column(name = "VOLUME", nullable = false)
    private double volume;

    @Column(name = "PRICE")
    private BigDecimal price;

    @Column(name = "OPEN1")
    private BigDecimal open1;

    @Column(name = "LOW")
    private BigDecimal low;

    @Column(name = "HIGH")
    private BigDecimal high;

    @NotNull
    @Column(name = "CHANGE1", nullable = false)
    private double change1;

    public QuoteDataBean() {
    }

    public QuoteDataBean(String symbol, String companyName, double volume,
                         BigDecimal price, BigDecimal open, BigDecimal low,
                         BigDecimal high, double change) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.volume = volume;
        this.price = price;
        this.open1 = open;
        this.low = low;
        this.high = high;
        this.change1 = change;
    }

    @Override
    public String toString() {
        return "Quote Data for: " + symbol
                + " companyName: " + companyName
                + " volume: " + volume
                + " price: " + price
                + " open1: " + open1
                + " low: " + low
                + " high: " + high
                + " change1: " + change1;
    }

    // Getters and Setters

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOpen() {
        return open1;
    }

    public void setOpen(BigDecimal open) {
        this.open1 = open;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public double getChange() {
        return change1;
    }

    public void setChange(double change) {
        this.change1 = change;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(symbol);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof QuoteDataBean other)) return false;
        return Objects.equals(symbol, other.symbol);
    }
}
