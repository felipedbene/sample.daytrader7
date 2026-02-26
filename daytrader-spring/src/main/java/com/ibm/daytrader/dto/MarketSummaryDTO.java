package com.ibm.daytrader.dto;

import java.math.BigDecimal;
import java.util.List;

import com.ibm.daytrader.entity.QuoteDataBean;

public class MarketSummaryDTO {

    private BigDecimal TSIA;
    private BigDecimal openTSIA;
    private double totalVolume;
    private List<QuoteDataBean> topGainers;
    private List<QuoteDataBean> topLosers;

    public MarketSummaryDTO() {
    }

    public MarketSummaryDTO(BigDecimal tsia, BigDecimal openTSIA, double totalVolume,
                            List<QuoteDataBean> topGainers, List<QuoteDataBean> topLosers) {
        this.TSIA = tsia;
        this.openTSIA = openTSIA;
        this.totalVolume = totalVolume;
        this.topGainers = topGainers;
        this.topLosers = topLosers;
    }

    public BigDecimal getTSIA() {
        return TSIA;
    }

    public void setTSIA(BigDecimal tsia) {
        this.TSIA = tsia;
    }

    public BigDecimal getOpenTSIA() {
        return openTSIA;
    }

    public void setOpenTSIA(BigDecimal openTSIA) {
        this.openTSIA = openTSIA;
    }

    public double getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(double totalVolume) {
        this.totalVolume = totalVolume;
    }

    public List<QuoteDataBean> getTopGainers() {
        return topGainers;
    }

    public void setTopGainers(List<QuoteDataBean> topGainers) {
        this.topGainers = topGainers;
    }

    public List<QuoteDataBean> getTopLosers() {
        return topLosers;
    }

    public void setTopLosers(List<QuoteDataBean> topLosers) {
        this.topLosers = topLosers;
    }
}
