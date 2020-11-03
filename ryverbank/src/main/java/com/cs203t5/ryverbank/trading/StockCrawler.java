package com.cs203t5.ryverbank.trading;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.cs203t5.ryverbank.account_transaction.*;
import com.cs203t5.ryverbank.portfolio.*;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

/**
 * Stock crawler class that crawls stock info data from the Straits Times Index:
 * https://www.sgx.com/indices/products/sti using yahoo finance API.
 */
@Component
public class StockCrawler {
    /** The stock repository. */
    private StockRepository stockRepository;
    /** The trade repository. */
    private TradeRepository tradeRepository;
    /** The asset services. */
    private AssetService assetService;
    /** The transaction services. */
    private TransactionServices tranService;
    /** The account services. */
    private AccountServices accService;
    /** The portfolio services. */
    private PortfolioService portfolioService;
    /** Counter. */
    private int count = 0;

    /**
     * Constructs a StockCrawler with the following parameters.
     * 
     * @param stockRepository  The stock repository.
     * @param tradeRepository  The trade repository.
     * @param assetService     The asset services.
     * @param tranService      The transaction services.
     * @param accService       The account services.
     * @param portfolioService The Portfolio Services
     */
    public StockCrawler(StockRepository stockRepository, TradeRepository tradeRepository, AssetService assetService,
            TransactionServices tranService, AccountServices accService, PortfolioService portfolioService) {
        this.stockRepository = stockRepository;
        this.tradeRepository = tradeRepository;
        this.assetService = assetService;
        this.accService = accService;
        this.tranService = tranService;
        this.portfolioService = portfolioService;

    }

    // Open the market at 9am (GMT+8) every weekday
    // @Scheduled(cron = "0 00 09 ? * MON-FRI", zone = "GMT+8")
    /**
     * Simulates a web crawler by crawling the stock market from SGX and saving it
     * into the stock repository.
     */
    public void crawl() {

        try {
            String[] symbols = new String[] { "A17U", "C61U", "C31", "C38U", "C09", "C52", "D01", "D05", "G13", "H78",
                    "C07", "J36", "J37", "BN4", "N2IU", "ME8U", "M44U", "O39", "S58", "U96", "S68", "C6L", "Z74", "S63",
                    "Y92", "U11", "U14", "V03", "F34", "BS6" };

            for (String symbol : symbols) {
                Stock stock = YahooFinance.get(symbol + ".SI");
                // double bid = stock.getQuote().getBid().doubleValue();
                double bid = 0.0;
                double price = stock.getQuote().getPrice().doubleValue();
                // double ask = stock.getQuote().getAsk().doubleValue();
                double ask = 0.0;
                int bidVolume = 0;
                // stock.getQuote().getBidSize().intValue();
                int askVolume = 0;
                // stock.getQuote().getAskSize().intValue();
                stockRepository.save(new CustomStock(symbol, price, bidVolume, bid, askVolume, ask));
            }
        } catch (IOException e) {
            System.out.println("One of the stock is not found");
        }

    }

    /**
     * Simulates a stock market by crawling the stock data from SGX and saving it in
     * the trade repository. This market will only be open at 9am (GMT+8) every
     * weekday.
     */
    @Scheduled(cron = "0 00 09 ? * MON-FRI", zone = "GMT+8")
    public void marketMaker() {

        long currentTimestamp = Instant.now().getEpochSecond();

        try {
            String[] symbols = new String[] { "A17U", "C61U", "C31", "C38U", "C09", "C52", "D01", "D05", "G13", "H78",
                    "C07", "J36", "J37", "BN4", "N2IU", "ME8U", "M44U", "O39", "S58", "U96", "S68", "C6L", "Z74", "S63",
                    "Y92", "U11", "U14", "V03", "F34", "BS6" };

            // Market marker buy and sell trade for each symbol
            for (String symbol : symbols) {
                Stock stock = YahooFinance.get(symbol + ".SI");
                String buyAction = "buy";
                int quantity = 20000;
                double bid = stock.getQuote().getBid().doubleValue();
                long date = currentTimestamp;
                String status = "open";

                String sellAction = "sell";
                double ask = stock.getQuote().getAsk().doubleValue();

                // Customer_Id is the market maker account
                int account_Id = 1;
                int customer_Id = 4;
                Long accountId = Long.valueOf(account_Id);
                Long customerId = Long.valueOf(customer_Id);

                tradeRepository.save(new Trade(buyAction, symbol, quantity, bid, 0.0, 0.0, 0, date, accountId,
                        customerId, status, 0.0));
                tradeRepository.save(new Trade(sellAction, symbol, quantity, 0.0, ask, 0.0, 0, date, accountId,
                        customerId, status, 0.0));

                Optional<CustomStock> optionalStocks = stockRepository.findBySymbol(symbol);
                if (optionalStocks != null || optionalStocks.isPresent()) {
                    CustomStock customStock = optionalStocks.get();
                    customStock.setAsk(ask);
                    customStock.setBid(bid);
                    customStock.setBidVolume(quantity);
                    customStock.setAskVolume(quantity);

                    stockRepository.save(customStock);
                }

            }

        } catch (IOException e) {
            System.out.println("One of the stock is not found");
        }

        System.out.println("Market is open");

    }

    /**
     * Simulates the opening of a buy market by finding the list of open or
     * partially filled sell trades in the current repository and matching it to
     * them. This market will only be open at 9am (GMT+8) every weekday.
     */
    @Scheduled(cron = "30 00 09 ? * MON-FRI", zone = "GMT+8")
    public void openBuyMarket() {
        String[] symbols = new String[] { "A17U", "C61U", "C31", "C38U", "C09", "C52", "D01", "D05", "G13", "H78",
                "C07", "J36", "J37", "BN4", "N2IU", "ME8U", "M44U", "O39", "S58", "U96", "S68", "C6L", "Z74", "S63",
                "Y92", "U11", "U14", "V03", "F34", "BS6" };

        for (String symbol : symbols) {
            // Get the list of trades for {symbol}
            List<Trade> tradesList = tradeRepository.findAllBySymbol(symbol);
            List<Trade> listOfBuyTrades = new ArrayList<>();

            // Customer_Id is the market maker account
            int account_Id = 1;
            Long accountId = Long.valueOf(account_Id);

            // Gets all the buy trades that are open or partially filled and add to
            // listOfBuyTrades
            for (Trade trade : tradesList) {
                if (trade.getAction().equals("buy") && (trade.getStatus().equals("open"))) {
                    if (!(trade.getAccountId().equals(accountId))) {
                        listOfBuyTrades.add(trade);
                    }

                }
            }

            // For each buy trade in the list
            for (Trade trade : listOfBuyTrades) {
                Optional<CustomStock> optionalCustomStock = stockRepository.findBySymbol(symbol);
                CustomStock customStock = optionalCustomStock.get();
                List<Trade> listOfTrades = tradeRepository.findAllBySymbol(trade.getSymbol());
                List<Trade> listOfSellTrades = new ArrayList<>();

                // Set the newBidPrice, the best price will be recorded
                // best price is the higher bid
                // It must be better than the current stock's bid price and still lower than the
                // ask price
                double newBidPrice = customStock.getBid();
                int newBidVolume = customStock.getBidVolume();
                double tradeBidPrice = trade.getBid();
                if (tradeBidPrice == 0.0) {
                    tradeBidPrice = customStock.getBid();
                }

                if (tradeBidPrice > newBidPrice && tradeBidPrice < customStock.getAsk()) {
                    newBidPrice = tradeBidPrice;
                    newBidVolume = trade.getQuantity();

                }
                // Get the list of open & partial-filled of market sell trades for {symbol}
                if (trade.getBid() == 0.0 || trade.getBid() > customStock.getAsk()) {
                    for (Trade sellTradeList : listOfTrades) {
                        if (sellTradeList.getAction().equals("sell")) {
                            if (sellTradeList.getStatus().equals("open")
                                    || sellTradeList.getStatus().equals("partial-filled")) {
                                listOfSellTrades.add(sellTradeList);
                            }

                        }
                    }
                } else {
                    // Get the list of open & partial-filled sell trades that are equal to the
                    // bid_price or lower than the bid price for {symbol}
                    for (Trade sellTradeList : listOfTrades) {
                        if (sellTradeList.getAction().equals("sell")) {
                            if (sellTradeList.getAsk() == trade.getBid() || sellTradeList.getAsk() < trade.getBid()) {
                                if (sellTradeList.getStatus().equals("open")
                                        || sellTradeList.getStatus().equals("partial-filled")) {
                                    listOfSellTrades.add(sellTradeList);
                                }
                            }

                        }
                    }
                }

                // When there is not available sell trades on the market
                // Set the trade to it's original status
                // Add the subsequent volume
                try {
                    if (listOfSellTrades.size() == 0) {
                        if (trade.getStatus().equals("partial-filled")) {
                            trade.setStatus("partial-filled");
                        } else {
                            trade.setStatus("open");
                        }
                        customStock.setBid(newBidPrice);
                        customStock.setBidVolume(newBidVolume);
                        customStock.setAskVolume(customStock.getAskVolume() - trade.getFilledQuantity());
                        count = 0;

                        // trade successful then store into user's asset list
                        // assetService.addAsset(trade)
                        assetService.addAsset(trade, customStock);

                        tradeRepository.save(trade);
                        return;
                    }
                    // If is a new trade, meaning no status has been set yet, set the trade to open
                } catch (NullPointerException e) {
                    trade.setStatus("open");
                    customStock.setBid(newBidPrice);
                    customStock.setBidVolume(newBidVolume);
                    count = 0;
                    tradeRepository.save(trade);
                    return;
                }

                double lastPrice = 0.0;

                // This is set avg price for trade at the begining before there is any match
                try {
                    trade.setAvgPrice(trade.getAvgPrice());
                } catch (NullPointerException e) {
                    trade.setAvgPrice(0.0);
                }

                double avgPrice = trade.getAvgPrice();

                if (listOfSellTrades.size() != 0) {
                    Date date = new Date(listOfSellTrades.get(0).getDate());
                    Trade matchTrade = listOfSellTrades.get(0);

                    // Match to market sell price first
                    for (Trade sellTrade : listOfSellTrades) {
                        Date currentSellTradeDate = new Date(sellTrade.getDate());
                        if (matchTrade.getAsk() > sellTrade.getAsk()) {
                            matchTrade = sellTrade;
                        } else if (matchTrade.getAsk() == sellTrade.getAsk()) {
                            if (date.after(currentSellTradeDate)) {
                                matchTrade = sellTrade;
                            }
                        }
                    }

                    // add the number of matched trade by one
                    count++;

                    // When submitted trade has more quantity than match trade
                    if (matchTrade.getQuantity() - trade.getQuantity() < 0) {

                        int matchTradeFilledQuantity = matchTrade.getFilledQuantity() + matchTrade.getQuantity();
                        int tradeFilledQuantity = trade.getFilledQuantity() + matchTrade.getQuantity();
                        int tradeQuantity = trade.getQuantity() - matchTrade.getQuantity();

                        matchTrade.setFilledQuantity(matchTradeFilledQuantity);
                        trade.setFilledQuantity(tradeFilledQuantity);
                        trade.setQuantity(tradeQuantity);
                        matchTrade.setQuantity(0);

                    }

                    else {

                        int matchTradeFilledQuantity = matchTrade.getFilledQuantity() + trade.getQuantity();
                        int tradeFilledQuantity = trade.getQuantity() + trade.getFilledQuantity();
                        int matchTradeQuantity = matchTrade.getQuantity() - trade.getQuantity();

                        matchTrade.setFilledQuantity(matchTradeFilledQuantity);
                        trade.setFilledQuantity(tradeFilledQuantity);
                        trade.setQuantity(0);
                        matchTrade.setQuantity(matchTradeQuantity);

                    }

                    if (matchTrade.getQuantity() != 0) {
                        matchTrade.setStatus("partial-filled");
                    } else {
                        matchTrade.setStatus("filled");

                    }
                    if (trade.getQuantity() != 0) {
                        trade.setStatus("partial-filled");

                    } else {
                        trade.setStatus("filled");
                    }

                    // Set the avg_price for current trade
                    double matchTradeAskPrice;
                    if (matchTrade.getAsk() == 0.0) {
                        matchTradeAskPrice = customStock.getAsk();
                    } else {
                        matchTradeAskPrice = matchTrade.getAsk();
                    }

                    avgPrice = (avgPrice + matchTradeAskPrice) / count;
                    trade.setAvgPrice(avgPrice);

                    // Set the avg_price for match trade
                    matchTrade.setAvgPrice(tradeBidPrice);

                    lastPrice = matchTrade.getAsk();

                    tradeRepository.save(trade);
                    /* ACCOUNT MATCH TRADE CREATED HERE. GET THE SELLER ID HERE */
                    Long give = trade.getAccountId();
                    Long take = matchTrade.getAccountId();
                    double amt = trade.getFilledQuantity() * customStock.getAsk();
                    accService.accTradeOnHold(take, amt);
                    tranService.addTransaction(give, take, amt * -1);
                    tradeRepository.save(matchTrade);
                }

                // If trade is partial-filled after matching, find other available sell trade on
                // the market
                if (trade.getStatus().equals("partial-filled")) {
                    // Set stock last price
                    if (lastPrice == 0.0) {
                        customStock.setLastPrice(customStock.getAsk());
                    } else {
                        customStock.setLastPrice(lastPrice);
                    }

                    // Set stock bid price
                    customStock.setBid(customStock.getBid());

                    // createMarketBuyTrade(trade, customer, customStock);
                    openBuyMarket();
                }

                customStock.setAskVolume(customStock.getAskVolume() - trade.getFilledQuantity());

                // Set stock last price
                if (lastPrice == 0.0) {
                    customStock.setLastPrice(customStock.getAsk());
                } else {
                    customStock.setLastPrice(lastPrice);
                }
                // Set stock bid price
                customStock.setBid(customStock.getBid());

                count = 0;
                // if it reaches here, straight away count as success
                assetService.addAsset(trade, customStock);

                /*
                 * This is to set the askVolume for the stockInfo
                 */
                // Get the list of trades for {symbol}
                List<Trade> listOfAvaiTrades = tradeRepository.findAllBySymbol(symbol);
                List<Trade> listOfAvaiSellTrades = new ArrayList<>();

                if (customStock.getAskVolume() <= 0) {
                    // Get the list of open & partial-filled of market buy trades for {symbol}
                    for (Trade sellTradeList : listOfAvaiTrades) {
                        if (sellTradeList.getAction().equals("sell")) {
                            if (sellTradeList.getStatus().equals("open")
                                    || sellTradeList.getStatus().equals("partial-filled")) {
                                listOfAvaiSellTrades.add(sellTradeList);
                            }

                        }
                    }

                    if (listOfAvaiSellTrades.size() != 0) {
                        Date date = new Date(listOfAvaiSellTrades.get(0).getDate());
                        Trade matchTrade = listOfAvaiSellTrades.get(0);

                        for (Trade sellTrade : listOfAvaiSellTrades) {
                            Date currentSellTradeDate = new Date(sellTrade.getDate());
                            if (matchTrade.getAsk() > sellTrade.getAsk()) {
                                matchTrade = sellTrade;
                            } else if (matchTrade.getAsk() == sellTrade.getAsk()) {
                                if (date.after(currentSellTradeDate)) {
                                    matchTrade = sellTrade;
                                }
                            }
                        }

                        customStock.setAskVolume(matchTrade.getQuantity());
                        if (matchTrade.getAsk() != 0.0) {
                            customStock.setAsk(matchTrade.getAsk());
                        }

                    } else {
                        customStock.setAskVolume(0);
                    }

                }
                tradeRepository.save(trade);
                stockRepository.save(customStock);
            }

        }

    }

    /**
     * Simulates the opening of a sell market by finding the list of open or
     * partially filled buy trades in the current repository and matching it to
     * them. This market will only be open at 9am (GMT+8) every weekday.
     */
    @Scheduled(cron = "30 00 09 ? * MON-FRI", zone = "GMT+8")
    public void openSellMarket() {
        String[] symbols = new String[] { "A17U", "C61U", "C31", "C38U", "C09", "C52", "D01", "D05", "G13", "H78",
                "C07", "J36", "J37", "BN4", "N2IU", "ME8U", "M44U", "O39", "S58", "U96", "S68", "C6L", "Z74", "S63",
                "Y92", "U11", "U14", "V03", "F34", "BS6" };

        for (String symbol : symbols) {
            // Get the list of trades for {symbol}
            List<Trade> tradesList = tradeRepository.findAllBySymbol(symbol);
            List<Trade> listOfSellTrades = new ArrayList<>();

            // Customer_Id is the market maker account
            int account_Id = 1;
            Long accountId = Long.valueOf(account_Id);

            for (Trade trade : tradesList) {
                if (trade.getAction().equals("sell")
                        && (trade.getStatus().equals("open") || trade.getStatus().equals("partial-filled"))) {
                    if (!(trade.getAccountId().equals(accountId))) {
                        listOfSellTrades.add(trade);
                    }

                }
            }

            for (Trade trade : listOfSellTrades) {
                Optional<CustomStock> optionalCustomStock = stockRepository.findBySymbol(symbol);
                CustomStock customStock = optionalCustomStock.get();
                List<Trade> listOfTrades = tradeRepository.findAllBySymbol(trade.getSymbol());
                List<Trade> listOfBuyTrades = new ArrayList<>();

                // Market Sell
                if (trade.getAsk() == 0.0 || trade.getAsk() < customStock.getBid()) {
                    // Get list of open / partial-filled market buy trades
                    for (Trade buyTrade : listOfTrades) {
                        if (buyTrade.getAction().equals("buy")) {
                            if (buyTrade.getStatus().equals("open") || buyTrade.getStatus().equals("partial-filled")) {
                                listOfBuyTrades.add(buyTrade);
                            }

                        }
                    }
                } else { // Limit Sell
                    // Gte the list of open & partial-filled buy trades that are equal to the
                    // ask_price or higher than the ask_price
                    for (Trade buyTrade : listOfTrades) {
                        if (buyTrade.getAction().equals("buy")) {
                            if (buyTrade.getBid() == trade.getAsk() || buyTrade.getBid() > trade.getAsk()) {
                                if (buyTrade.getStatus().equals("open")
                                        || buyTrade.getStatus().equals("partial-filled")) {
                                    listOfBuyTrades.add(buyTrade);
                                }
                            }

                        }
                    }
                }

                try {
                    if (listOfBuyTrades.size() == 0) {
                        if (trade.getStatus().equals("partial-filled")) {
                            trade.setStatus("partial-filled");
                        } else {
                            trade.setStatus("open");
                        }

                        customStock.setBidVolume(customStock.getBidVolume() - trade.getFilledQuantity());
                        count = 0;
                        tradeRepository.save(trade);
                        portfolioService.updateRealizedGainLoss(trade, customStock);
                        return;
                    }
                } catch (NullPointerException e) {
                    trade.setStatus("open");
                    count = 0;
                    tradeRepository.save(trade);
                    return;
                }

                double lastPrice = 0.0;
                // This is set avg price for trade at the begining before there is any match
                try {
                    trade.setAvgPrice(trade.getAvgPrice());
                } catch (NullPointerException e) {
                    trade.setAvgPrice(0.0);
                }
                double avgPrice = trade.getAvgPrice();

                if (listOfBuyTrades.size() != 0) {
                    Date date = new Date(listOfBuyTrades.get(0).getDate());
                    Trade matchTrade = listOfBuyTrades.get(0);

                    // Match to market buy price (current highest buy price)
                    // then the highest buy price
                    for (Trade buyTrade : listOfBuyTrades) {
                        Date currentBuyTradeDate = new Date(buyTrade.getDate());
                        if (matchTrade.getBid() < buyTrade.getBid()) {
                            matchTrade = buyTrade;
                        } else if (matchTrade.getBid() == buyTrade.getBid()) {
                            if (date.after(currentBuyTradeDate)) {
                                matchTrade = buyTrade;
                            }
                        }
                    }

                    // add the number of matched trade by one
                    count++;

                    if (matchTrade.getQuantity() - trade.getQuantity() < 0) {

                        int matchTradeFilledQuantity = matchTrade.getFilledQuantity() + matchTrade.getQuantity();
                        int tradeFilledQuantity = trade.getFilledQuantity() + matchTrade.getQuantity();
                        int tradeQuantity = trade.getQuantity() - matchTrade.getQuantity();

                        matchTrade.setFilledQuantity(matchTradeFilledQuantity);
                        trade.setFilledQuantity(tradeFilledQuantity);
                        trade.setQuantity(tradeQuantity);
                        matchTrade.setQuantity(0);

                    } else {

                        int matchTradeFilledQuantity = matchTrade.getFilledQuantity() + trade.getQuantity();
                        int tradeFilledQuantity = trade.getQuantity() + trade.getFilledQuantity();
                        int matchTradeQuantity = matchTrade.getQuantity() - trade.getQuantity();

                        matchTrade.setFilledQuantity(matchTradeFilledQuantity);
                        trade.setFilledQuantity(tradeFilledQuantity);
                        trade.setQuantity(0);
                        matchTrade.setQuantity(matchTradeQuantity);

                    }

                    if (matchTrade.getQuantity() != 0) {
                        matchTrade.setStatus("partial-filled");
                    } else {
                        matchTrade.setStatus("filled");

                    }
                    if (trade.getQuantity() != 0) {
                        trade.setStatus("partial-filled");
                    } else {
                        trade.setStatus("filled");
                    }

                    // Set the avg_price for current trade
                    double matchTradeBidPrice;
                    if (matchTrade.getBid() == 0.0) {
                        matchTradeBidPrice = customStock.getBid();
                    } else {
                        matchTradeBidPrice = matchTrade.getBid();
                    }

                    avgPrice = (avgPrice + matchTradeBidPrice) / count;
                    trade.setAvgPrice(avgPrice);

                    // Set the avg_price for match trade
                    double tradeAskPrice;
                    if (trade.getAsk() == 0.0) {
                        tradeAskPrice = customStock.getAsk();
                    } else {
                        tradeAskPrice = trade.getAsk();
                    }
                    matchTrade.setAvgPrice(tradeAskPrice);

                    // Set the last price
                    lastPrice = matchTrade.getBid();

                    tradeRepository.save(trade);
                    /* ACCOUNT MATCH TRADE CREATED HERE. GET THE SELLER ID HERE */
                    Long take = trade.getAccountId();
                    Long give = matchTrade.getAccountId();
                    double amt = trade.getFilledQuantity() * customStock.getBid();
                    accService.accTradeOnHold(take, amt);
                    accService.accTradeOnHold(give, amt * -1);
                    tranService.addTransaction(take, give, amt);
                    tradeRepository.save(matchTrade);
                }

                if (trade.getStatus().equals("partial-filled")) {
                    // Set stock last price
                    if (lastPrice == 0.0) {
                        customStock.setLastPrice(customStock.getBid());
                    } else {
                        customStock.setLastPrice(lastPrice);

                    }
                    // Set stock ask price
                    customStock.setAsk(customStock.getAsk());

                    openSellMarket();
                }

                customStock.setBidVolume(customStock.getBidVolume() - trade.getFilledQuantity());
                // Set stock last price
                if (lastPrice == 0.0) {
                    customStock.setLastPrice(customStock.getBid());
                } else {
                    customStock.setLastPrice(lastPrice);
                }
                // Set stock ask price
                customStock.setAsk(customStock.getAsk());

                portfolioService.updateRealizedGainLoss(trade, customStock);

                // Set the bidVolume
                List<Trade> listOfAvaiTrades = tradeRepository.findAllBySymbol(symbol);
                List<Trade> listOfAvaiBuyTrades = new ArrayList<>();

                if (customStock.getBidVolume() <= 0) {
                    // Get the list of open & partial-filled of market buy trades for {symbol}
                    for (Trade buyTradeList : listOfAvaiTrades) {
                        if (buyTradeList.getAction().equals("buy")) {
                            if (buyTradeList.getStatus().equals("open")
                                    || buyTradeList.getStatus().equals("partial-filled")) {
                                listOfAvaiBuyTrades.add(buyTradeList);
                            }

                        }
                    }

                    if (listOfAvaiBuyTrades.size() != 0) {
                        Date date = new Date(listOfAvaiBuyTrades.get(0).getDate());
                        Trade matchTrade = listOfAvaiBuyTrades.get(0);

                        for (Trade buyTrade : listOfAvaiBuyTrades) {
                            Date currentBuyTradeDate = new Date(buyTrade.getDate());
                            if (matchTrade.getBid() < buyTrade.getBid()) {
                                matchTrade = buyTrade;
                            } else if (matchTrade.getBid() == buyTrade.getBid()) {
                                if (date.after(currentBuyTradeDate))
                                    matchTrade = buyTrade;
                            }

                        }

                        customStock.setBidVolume(matchTrade.getQuantity());
                        if (matchTrade.getBid() != 0.0) {
                            customStock.setAsk(matchTrade.getBid());
                        }
                    } else {
                        customStock.setBidVolume(0);
                    }

                }
                tradeRepository.save(trade);
                stockRepository.save(customStock);
            }

        }

    }

    /**
     * Simulates a closing of the stock market by finding all the open and partially
     * filled trades in the repository and setting them to expired. This market will
     * close at 5pm (GMT+8) every weekday
     */
    @Scheduled(cron = "0 00 17 ? * MON-FRI", zone = "GMT+8")
    public void closeMarket() {
        String[] symbols = new String[] { "A17U", "C61U", "C31", "C38U", "C09", "C52", "D01", "D05", "G13", "H78",
                "C07", "J36", "J37", "BN4", "N2IU", "ME8U", "M44U", "O39", "S58", "U96", "S68", "C6L", "Z74", "S63",
                "Y92", "U11", "U14", "V03", "F34", "BS6" };

        for (String symbol : symbols) {
            List<Trade> tradeList = tradeRepository.findAllBySymbol(symbol);
            for (Trade trade : tradeList) {
                if (trade.getStatus().equals("open") || trade.getStatus().equals("partial-filled")) {
                    trade.setStatus("expired");
                    tradeRepository.save(trade);
                }
            }
        }
        System.out.println("Market is close");
    }

}
