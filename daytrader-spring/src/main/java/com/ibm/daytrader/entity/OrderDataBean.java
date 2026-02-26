package com.ibm.daytrader.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "orderejb")
public class OrderDataBean implements Serializable {

    private static final long serialVersionUID = 120650490200739057L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDERID", nullable = false)
    private Integer orderID;

    @Column(name = "ORDERTYPE")
    private String orderType;

    @Column(name = "ORDERSTATUS")
    private String orderStatus;

    @Column(name = "OPENDATE")
    private LocalDateTime openDate;

    @Column(name = "COMPLETIONDATE")
    private LocalDateTime completionDate;

    @NotNull
    @Column(name = "QUANTITY", nullable = false)
    private double quantity;

    @Column(name = "PRICE")
    private BigDecimal price;

    @Column(name = "ORDERFEE")
    private BigDecimal orderFee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ACCOUNTID")
    private AccountDataBean account;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "QUOTE_SYMBOL")
    private QuoteDataBean quote;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HOLDING_HOLDINGID")
    private HoldingDataBean holding;

    @Transient
    private String symbol;

    public OrderDataBean() {
    }

    public OrderDataBean(String orderType, String orderStatus, LocalDateTime openDate,
                         LocalDateTime completionDate, double quantity, BigDecimal price,
                         BigDecimal orderFee, AccountDataBean account, QuoteDataBean quote,
                         HoldingDataBean holding) {
        this.orderType = orderType;
        this.orderStatus = orderStatus;
        this.openDate = openDate;
        this.completionDate = completionDate;
        this.quantity = quantity;
        this.price = price;
        this.orderFee = orderFee;
        this.account = account;
        this.quote = quote;
        this.holding = holding;
    }

    @Override
    public String toString() {
        return "Order " + orderID
                + " orderType: " + orderType
                + " orderStatus: " + orderStatus
                + " openDate: " + openDate
                + " completionDate: " + completionDate
                + " quantity: " + quantity
                + " price: " + price
                + " orderFee: " + orderFee
                + " symbol: " + getSymbol();
    }

    public boolean isBuy() {
        return "buy".equalsIgnoreCase(orderType);
    }

    public boolean isSell() {
        return "sell".equalsIgnoreCase(orderType);
    }

    public boolean isOpen() {
        return "open".equalsIgnoreCase(orderStatus) || "processing".equalsIgnoreCase(orderStatus);
    }

    public boolean isCompleted() {
        return "completed".equalsIgnoreCase(orderStatus)
                || "alertcompleted".equalsIgnoreCase(orderStatus)
                || "cancelled".equalsIgnoreCase(orderStatus);
    }

    public boolean isCancelled() {
        return "cancelled".equalsIgnoreCase(orderStatus);
    }

    public void cancel() {
        this.orderStatus = "cancelled";
    }

    // Getters and Setters

    public Integer getOrderID() {
        return orderID;
    }

    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getOpenDate() {
        return openDate;
    }

    public void setOpenDate(LocalDateTime openDate) {
        this.openDate = openDate;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOrderFee() {
        return orderFee;
    }

    public void setOrderFee(BigDecimal orderFee) {
        this.orderFee = orderFee;
    }

    public String getSymbol() {
        if (quote != null) {
            return quote.getSymbol();
        }
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public AccountDataBean getAccount() {
        return account;
    }

    public void setAccount(AccountDataBean account) {
        this.account = account;
    }

    public QuoteDataBean getQuote() {
        return quote;
    }

    public void setQuote(QuoteDataBean quote) {
        this.quote = quote;
    }

    public HoldingDataBean getHolding() {
        return holding;
    }

    public void setHolding(HoldingDataBean holding) {
        this.holding = holding;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(orderID);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof OrderDataBean other)) return false;
        return Objects.equals(orderID, other.orderID);
    }
}
