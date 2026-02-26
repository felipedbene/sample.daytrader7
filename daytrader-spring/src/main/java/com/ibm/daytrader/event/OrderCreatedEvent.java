package com.ibm.daytrader.event;

import org.springframework.context.ApplicationEvent;

public class OrderCreatedEvent extends ApplicationEvent {

    private final Integer orderID;
    private final String userID;
    private final boolean twoPhase;

    public OrderCreatedEvent(Object source, Integer orderID, String userID, boolean twoPhase) {
        super(source);
        this.orderID = orderID;
        this.userID = userID;
        this.twoPhase = twoPhase;
    }

    public Integer getOrderID() {
        return orderID;
    }

    public String getUserID() {
        return userID;
    }

    public boolean isTwoPhase() {
        return twoPhase;
    }
}
