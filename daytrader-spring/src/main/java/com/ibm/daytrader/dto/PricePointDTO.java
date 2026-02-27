package com.ibm.daytrader.dto;

import java.math.BigDecimal;

public record PricePointDTO(long time, BigDecimal value) {}
