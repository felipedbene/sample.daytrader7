package com.ibm.daytrader.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.daytrader.config.TradeProperties;
import com.ibm.daytrader.dto.MarketSummaryDTO;
import com.ibm.daytrader.dto.RunStatsDTO;
import com.ibm.daytrader.entity.AccountDataBean;
import com.ibm.daytrader.entity.AccountProfileDataBean;
import com.ibm.daytrader.entity.HoldingDataBean;
import com.ibm.daytrader.entity.OrderDataBean;
import com.ibm.daytrader.entity.QuoteDataBean;
import com.ibm.daytrader.event.OrderCreatedEvent;
import com.ibm.daytrader.event.QuotePriceChangeEvent;
import com.ibm.daytrader.repository.AccountProfileRepository;
import com.ibm.daytrader.repository.AccountRepository;
import com.ibm.daytrader.repository.HoldingRepository;
import com.ibm.daytrader.repository.OrderRepository;
import com.ibm.daytrader.repository.QuoteRepository;
import com.ibm.daytrader.util.FinancialUtils;
import com.ibm.daytrader.util.TradeConfig;

@Service
@Transactional
public class TradeServiceImpl implements TradeService {

    private static final Logger log = LoggerFactory.getLogger(TradeServiceImpl.class);

    private final AccountRepository accountRepository;
    private final AccountProfileRepository profileRepository;
    private final QuoteRepository quoteRepository;
    private final HoldingRepository holdingRepository;
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final TradeProperties tradeProperties;
    private final TradeConfig tradeConfig;
    private final MarketSummaryService marketSummaryService;

    public TradeServiceImpl(AccountRepository accountRepository,
                            AccountProfileRepository profileRepository,
                            QuoteRepository quoteRepository,
                            HoldingRepository holdingRepository,
                            OrderRepository orderRepository,
                            ApplicationEventPublisher eventPublisher,
                            TradeProperties tradeProperties,
                            TradeConfig tradeConfig,
                            MarketSummaryService marketSummaryService) {
        this.accountRepository = accountRepository;
        this.profileRepository = profileRepository;
        this.quoteRepository = quoteRepository;
        this.holdingRepository = holdingRepository;
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.tradeProperties = tradeProperties;
        this.tradeConfig = tradeConfig;
        this.marketSummaryService = marketSummaryService;
    }

    @Override
    @Transactional(readOnly = true)
    public MarketSummaryDTO getMarketSummary() {
        return marketSummaryService.getMarketSummary();
    }

    @Override
    public OrderDataBean buy(String userID, String symbol, double quantity) {
        log.debug("buy: userID={}, symbol={}, quantity={}", userID, symbol, quantity);

        AccountProfileDataBean profile = profileRepository.findById(userID)
                .orElseThrow(() -> new RuntimeException("No such user: " + userID));
        AccountDataBean account = profile.getAccount();
        QuoteDataBean quote = quoteRepository.findById(symbol)
                .orElseThrow(() -> new RuntimeException("No such quote: " + symbol));

        OrderDataBean order = createOrder(account, quote, null, "buy", quantity);

        // Debit account balance
        BigDecimal price = quote.getPrice();
        BigDecimal orderFee = order.getOrderFee();
        BigDecimal total = new BigDecimal(quantity).multiply(price).add(orderFee);
        account.setBalance(account.getBalance().subtract(total));

        // Complete order synchronously
        completeOrder(order.getOrderID());

        return order;
    }

    @Override
    public OrderDataBean sell(String userID, Integer holdingID) {
        log.debug("sell: userID={}, holdingID={}", userID, holdingID);

        AccountProfileDataBean profile = profileRepository.findById(userID)
                .orElseThrow(() -> new RuntimeException("No such user: " + userID));
        AccountDataBean account = profile.getAccount();

        HoldingDataBean holding = holdingRepository.findById(holdingID).orElse(null);
        if (holding == null) {
            log.error("User {} attempted to sell holding {} which has already been sold", userID, holdingID);
            OrderDataBean orderData = new OrderDataBean();
            orderData.setOrderStatus("cancelled");
            return orderRepository.save(orderData);
        }

        QuoteDataBean quote = holding.getQuote();
        double quantity = holding.getQuantity();

        OrderDataBean order = createOrder(account, quote, holding, "sell", quantity);

        // Mark holding as in-flight
        holding.setPurchaseDate(LocalDateTime.of(1970, 1, 1, 0, 0));

        // Credit account balance
        BigDecimal price = quote.getPrice();
        BigDecimal orderFee = order.getOrderFee();
        BigDecimal total = new BigDecimal(quantity).multiply(price).subtract(orderFee);
        account.setBalance(account.getBalance().add(total));

        // Complete order synchronously
        completeOrder(order.getOrderID());

        return order;
    }

    @Override
    public OrderDataBean completeOrder(Integer orderID) {
        log.debug("completeOrder: orderID={}", orderID);

        OrderDataBean order = orderRepository.findById(orderID)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderID));

        if (order.isCompleted()) {
            throw new RuntimeException("Attempt to complete an already completed order: " + orderID);
        }

        AccountDataBean account = order.getAccount();
        QuoteDataBean quote = order.getQuote();
        HoldingDataBean holding = order.getHolding();

        if (order.isBuy()) {
            // Create a new holding for this buy order
            HoldingDataBean newHolding = new HoldingDataBean(
                    order.getQuantity(),
                    order.getPrice(),
                    LocalDateTime.now(),
                    account,
                    quote
            );
            newHolding = holdingRepository.save(newHolding);
            order.setHolding(newHolding);
        }

        if (order.isSell()) {
            if (holding == null) {
                order.cancel();
                throw new RuntimeException("Unable to sell order " + orderID + " - holding already sold");
            }
            // Clear FK references on all orders (buy + sell) before deleting
            orderRepository.clearHoldingReferences(holding.getHoldingID());
            holdingRepository.delete(holding);
        }

        order.setOrderStatus("closed");
        order.setCompletionDate(LocalDateTime.now());

        // Update quote price if configured
        if (tradeProperties.isUpdateQuotePrices()) {
            updateQuotePriceVolume(quote.getSymbol(),
                    tradeConfig.getRandomPriceChangeFactor(),
                    order.getQuantity());
        }

        return order;
    }

    @Override
    public void cancelOrder(Integer orderID) {
        log.debug("cancelOrder: orderID={}", orderID);
        OrderDataBean order = orderRepository.findById(orderID)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderID));
        order.cancel();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDataBean> getOrders(String userID) {
        return orderRepository.findByAccountProfileUserID(userID);
    }

    @Override
    public List<OrderDataBean> getClosedOrders(String userID) {
        List<OrderDataBean> closedOrders = orderRepository.findClosedOrdersByUserID(userID);

        if (tradeProperties.isLongRun()) {
            // Remove closed orders to prevent table growth during long runs
            for (OrderDataBean order : closedOrders) {
                orderRepository.delete(order);
            }
        } else {
            orderRepository.markClosedOrdersCompleted(userID);
        }

        return closedOrders;
    }

    @Override
    public QuoteDataBean createQuote(String symbol, String companyName, BigDecimal price) {
        QuoteDataBean quote = new QuoteDataBean(symbol, companyName, 0, price, price, price, price, 0);
        return quoteRepository.save(quote);
    }

    @Override
    @Transactional(readOnly = true)
    public QuoteDataBean getQuote(String symbol) {
        return quoteRepository.findById(symbol).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuoteDataBean> getAllQuotes() {
        return quoteRepository.findAll();
    }

    @Override
    public QuoteDataBean updateQuotePriceVolume(String symbol, BigDecimal changeFactor, double sharesTraded) {
        if (!tradeProperties.isUpdateQuotePrices()) {
            return new QuoteDataBean();
        }

        QuoteDataBean quote = quoteRepository.findBySymbolForUpdate(symbol)
                .orElseThrow(() -> new RuntimeException("Quote not found: " + symbol));

        BigDecimal oldPrice = quote.getPrice();
        BigDecimal openPrice = quote.getOpen();

        if (oldPrice.equals(TradeConfig.PENNY_STOCK_PRICE)) {
            changeFactor = TradeConfig.PENNY_STOCK_RECOVERY_MIRACLE_MULTIPLIER;
        } else if (oldPrice.compareTo(TradeConfig.MAXIMUM_STOCK_PRICE) > 0) {
            changeFactor = TradeConfig.MAXIMUM_STOCK_SPLIT_MULTIPLIER;
        }

        BigDecimal newPrice = changeFactor.multiply(oldPrice).setScale(2, RoundingMode.HALF_UP);

        quote.setPrice(newPrice);
        quote.setChange(newPrice.subtract(openPrice).doubleValue());
        quote.setVolume(quote.getVolume() + sharesTraded);

        // Publish price change event for WebSocket
        if (tradeProperties.isPublishQuotePriceChange()) {
            eventPublisher.publishEvent(new QuotePriceChangeEvent(this, symbol, oldPrice, newPrice));
        }

        return quote;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoldingDataBean> getHoldings(String userID) {
        return holdingRepository.findByAccountProfileUserID(userID);
    }

    @Override
    @Transactional(readOnly = true)
    public HoldingDataBean getHolding(Integer holdingID) {
        return holdingRepository.findById(holdingID).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDataBean getAccountData(String userID) {
        AccountProfileDataBean profile = profileRepository.findById(userID)
                .orElseThrow(() -> new RuntimeException("No such user: " + userID));
        AccountDataBean account = profile.getAccount();
        account.setProfileID(profile.getUserID());
        return account;
    }

    @Override
    @Transactional(readOnly = true)
    public AccountProfileDataBean getAccountProfileData(String userID) {
        return profileRepository.findById(userID).orElse(null);
    }

    @Override
    public AccountProfileDataBean updateAccountProfile(AccountProfileDataBean profileData) {
        AccountProfileDataBean existing = profileRepository.findById(profileData.getUserID())
                .orElseThrow(() -> new RuntimeException("No such user: " + profileData.getUserID()));

        existing.setAddress(profileData.getAddress());
        existing.setPassword(profileData.getPassword());
        existing.setFullName(profileData.getFullName());
        existing.setCreditCard(profileData.getCreditCard());
        existing.setEmail(profileData.getEmail());

        return profileRepository.save(existing);
    }

    @Override
    public AccountDataBean login(String userID, String password) {
        AccountProfileDataBean profile = profileRepository.findById(userID)
                .orElseThrow(() -> new RuntimeException("No such user: " + userID));

        if (!profile.getPassword().equals(password)) {
            throw new RuntimeException("Login failure for user: " + userID + " - incorrect password");
        }

        AccountDataBean account = profile.getAccount();
        account.login();

        return account;
    }

    @Override
    public void logout(String userID) {
        AccountProfileDataBean profile = profileRepository.findById(userID)
                .orElseThrow(() -> new RuntimeException("No such user: " + userID));
        AccountDataBean account = profile.getAccount();
        account.logout();
    }

    @Override
    public AccountDataBean register(String userID, String password, String fullname,
                                    String address, String email, String creditcard,
                                    BigDecimal openBalance) {
        log.debug("register: userID={}", userID);

        // Check if user already exists
        if (profileRepository.findById(userID).isPresent()) {
            log.error("Failed to register - user {} already exists", userID);
            return null;
        }

        AccountProfileDataBean profile = new AccountProfileDataBean(
                userID, password, fullname, address, email, creditcard);

        AccountDataBean account = new AccountDataBean(
                0, 0, null, LocalDateTime.now(), openBalance, openBalance, userID);

        profile = profileRepository.save(profile);

        account.setProfile(profile);
        account = accountRepository.save(account);

        profile.setAccount(account);

        return account;
    }

    @Override
    public RunStatsDTO resetTrade(boolean deleteAll) {
        RunStatsDTO stats = new RunStatsDTO();

        if (deleteAll) {
            orderRepository.deleteAll();
            holdingRepository.deleteAll();
            accountRepository.deleteAll();
            profileRepository.deleteAll();
            quoteRepository.deleteAll();
        }

        stats.setTradeUserCount((int) profileRepository.count());
        stats.setTradeStockCount((int) quoteRepository.count());
        stats.setHoldingCount((int) holdingRepository.count());
        stats.setOrderCount((int) orderRepository.count());

        return stats;
    }

    private OrderDataBean createOrder(AccountDataBean account, QuoteDataBean quote,
                                      HoldingDataBean holding, String orderType, double quantity) {
        OrderDataBean order = new OrderDataBean(
                orderType,
                "open",
                LocalDateTime.now(),
                null,
                quantity,
                quote.getPrice().setScale(FinancialUtils.SCALE, FinancialUtils.ROUND),
                tradeConfig.getOrderFee(orderType),
                account,
                quote,
                holding
        );
        return orderRepository.save(order);
    }
}
