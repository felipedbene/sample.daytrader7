package com.ibm.daytrader.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

import com.ibm.daytrader.entity.HoldingDataBean;

public final class FinancialUtils {

    public static final RoundingMode ROUND = RoundingMode.HALF_UP;
    public static final int SCALE = 2;
    public static final BigDecimal ZERO = new BigDecimal("0.00").setScale(SCALE);
    public static final BigDecimal ONE = new BigDecimal("1.00").setScale(SCALE);
    public static final BigDecimal HUNDRED = new BigDecimal("100.00").setScale(SCALE);

    private FinancialUtils() {
    }

    public static BigDecimal computeGain(BigDecimal currentBalance, BigDecimal openBalance) {
        return currentBalance.subtract(openBalance).setScale(SCALE);
    }

    public static BigDecimal computeGainPercent(BigDecimal currentBalance, BigDecimal openBalance) {
        if (openBalance.doubleValue() == 0.0) {
            return ZERO;
        }
        return currentBalance.divide(openBalance, SCALE, ROUND)
                .subtract(ONE)
                .multiply(HUNDRED);
    }

    public static BigDecimal computeHoldingsTotal(Collection<HoldingDataBean> holdingDataBeans) {
        BigDecimal holdingsTotal = new BigDecimal("0.00").setScale(SCALE);
        if (holdingDataBeans == null) {
            return holdingsTotal;
        }
        for (HoldingDataBean holdingData : holdingDataBeans) {
            BigDecimal total = holdingData.getPurchasePrice()
                    .multiply(new BigDecimal(holdingData.getQuantity()));
            holdingsTotal = holdingsTotal.add(total);
        }
        return holdingsTotal.setScale(SCALE, ROUND);
    }
}
