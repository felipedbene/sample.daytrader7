package com.ibm.daytrader.service;

import java.math.BigDecimal;
import java.util.List;

import com.ibm.daytrader.dto.MarketSummaryDTO;
import com.ibm.daytrader.dto.RunStatsDTO;
import com.ibm.daytrader.entity.AccountDataBean;
import com.ibm.daytrader.entity.AccountProfileDataBean;
import com.ibm.daytrader.entity.HoldingDataBean;
import com.ibm.daytrader.entity.OrderDataBean;
import com.ibm.daytrader.entity.QuoteDataBean;

public interface TradeService {

    MarketSummaryDTO getMarketSummary();

    OrderDataBean buy(String userID, String symbol, double quantity);

    OrderDataBean sell(String userID, Integer holdingID);

    OrderDataBean completeOrder(Integer orderID);

    void cancelOrder(Integer orderID);

    List<OrderDataBean> getOrders(String userID);

    List<OrderDataBean> getClosedOrders(String userID);

    QuoteDataBean createQuote(String symbol, String companyName, BigDecimal price);

    QuoteDataBean getQuote(String symbol);

    List<QuoteDataBean> getAllQuotes();

    QuoteDataBean updateQuotePriceVolume(String symbol, BigDecimal changeFactor, double sharesTraded);

    List<HoldingDataBean> getHoldings(String userID);

    HoldingDataBean getHolding(Integer holdingID);

    AccountDataBean getAccountData(String userID);

    AccountProfileDataBean getAccountProfileData(String userID);

    AccountProfileDataBean updateAccountProfile(AccountProfileDataBean profileData);

    AccountDataBean login(String userID, String password);

    void logout(String userID);

    AccountDataBean register(String userID, String password, String fullname,
                             String address, String email, String creditcard,
                             BigDecimal openBalance);

    RunStatsDTO resetTrade(boolean deleteAll);
}
