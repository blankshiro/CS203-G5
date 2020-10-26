package com.cs203t5.ryverbank.trading;

import java.util.*;

import org.springframework.stereotype.Component;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.scheduling.annotation.Scheduled;

@Component
public class StockCrawler {

    private StockRepository stockRepository;
    private TradeRepository tradeRepository;

    public StockCrawler(StockRepository stockRepository, TradeRepository tradeRepository) {
        this.stockRepository = stockRepository;
        this.tradeRepository = tradeRepository;
    }

    //Open the market at 9am (GMT+8) every weekday
    @Scheduled(cron = "0 00 09 ? * MON-FRI", zone = "GMT+8")
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

      //Open the market at 9am (GMT+8) every weekday
    @Scheduled(cron = "0 00 09 ? * MON-FRI",zone = "GMT+8")
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

                tradeRepository.save(
                        new Trade(buyAction, symbol, quantity, bid, 0.0, 0.0, 0, date, accountId, customerId, status));
                tradeRepository.save(
                        new Trade(sellAction, symbol, quantity, 0.0, ask, 0.0, 0, date, accountId, customerId, status));

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

       //Close the market at 5pm (GMT+8) every weekday
       @Scheduled(cron = "0 00 17 ? * MON-FRI",zone = "GMT+8")
       public void closeMarket(){
           String[] symbols  = new String[] {"A17U", "C61U", "C31", "C38U", "C09", "C52","D01","D05","G13","H78",
           "C07","J36","J37","BN4","N2IU","ME8U","M44U", "O39", "S58", "U96","S68","C6L", "Z74","S63","Y92","U11","U14","V03","F34","BS6"};
   
           for(String symbol: symbols){
               List<Trade> tradeList = tradeRepository.findAllBySymbol(symbol);
               for(Trade trade: tradeList){
                   if(trade.getStatus().equals("open") || trade.getStatus().equals("partial-filled")){
                       trade.setStatus("expired");
                       tradeRepository.save(trade);
                   }
               }
           }
           System.out.println("Market is close");
       }

}
