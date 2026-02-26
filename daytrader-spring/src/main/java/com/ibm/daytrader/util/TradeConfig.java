package com.ibm.daytrader.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.ibm.daytrader.config.TradeProperties;

@Component
public class TradeConfig {

    public static final BigDecimal PENNY_STOCK_PRICE = new BigDecimal("0.01").setScale(2, RoundingMode.HALF_UP);
    public static final BigDecimal PENNY_STOCK_RECOVERY_MIRACLE_MULTIPLIER = new BigDecimal("600.00").setScale(2, RoundingMode.HALF_UP);
    public static final BigDecimal MAXIMUM_STOCK_PRICE = new BigDecimal("400.00").setScale(2, RoundingMode.HALF_UP);
    public static final BigDecimal MAXIMUM_STOCK_SPLIT_MULTIPLIER = new BigDecimal("0.50").setScale(2, RoundingMode.HALF_UP);

    private static final BigDecimal ORDER_FEE = new BigDecimal("24.95");
    private static final BigDecimal ONE = new BigDecimal("1.0");

    private final TradeProperties properties;
    private final Random random = new Random(System.currentTimeMillis());

    private ArrayList<Integer> deck = null;
    private int card = 0;

    public TradeConfig(TradeProperties properties) {
        this.properties = properties;
    }

    public TradeProperties getProperties() {
        return properties;
    }

    public BigDecimal getOrderFee(String orderType) {
        if ("buy".equalsIgnoreCase(orderType) || "sell".equalsIgnoreCase(orderType)) {
            return properties.getOrderFee();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getRandomPriceChangeFactor() {
        double percentGain = rndFloat(1) * 0.1;
        if (random() < 0.5) {
            percentGain *= -1;
        }
        percentGain += 1;

        BigDecimal percentGainBD = new BigDecimal(percentGain).setScale(2, RoundingMode.HALF_UP);
        if (percentGainBD.doubleValue() <= 0.0) {
            percentGainBD = ONE;
        }
        return percentGainBD;
    }

    public synchronized String rndUserID() {
        int numUsers = properties.getMaxUsers();
        if (deck == null || deck.size() != numUsers) {
            deck = new ArrayList<>(numUsers);
            for (int i = 0; i < numUsers; i++) {
                deck.add(i);
            }
            Collections.shuffle(deck, random);
        }
        if (card >= numUsers) {
            card = 0;
        }
        return "uid:" + deck.get(card++);
    }

    public String rndSymbol() {
        return "s:" + rndInt(properties.getMaxQuotes() - 1);
    }

    public double random() {
        return random.nextDouble();
    }

    public int rndInt(int i) {
        return (int) (random() * i);
    }

    public float rndFloat(int i) {
        return (float) (random() * i);
    }

    public BigDecimal rndBigDecimal(float f) {
        return new BigDecimal(random() * f).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean rndBoolean() {
        return random.nextBoolean();
    }

    public float rndPrice() {
        return rndInt(200) + 1.0f;
    }

    public float rndQuantity() {
        return rndInt(200) + 1.0f;
    }

    public String rndAddress() {
        return rndInt(1000) + " Oak St.";
    }

    public String rndCreditCard() {
        return rndInt(100) + "-" + rndInt(1000) + "-" + rndInt(1000) + "-" + rndInt(1000);
    }

    public String rndEmail(String userID) {
        return userID + "@" + rndInt(100) + ".com";
    }

    public String rndFullName() {
        return "first:" + rndInt(1000) + " last:" + rndInt(5000);
    }
}
