package com.cs203t5.ryverbank.trading;

import java.util.*;

import org.springframework.stereotype.Component;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import java.io.IOException;
import java.math.BigDecimal;


@Component
public class StockCrawler {

    private StockRepository stocks;

    public StockCrawler(StockRepository stocks){
        this.stocks = stocks;
    }

    public void crawl() {
        
        try{
            String[] symbols  = new String[] {"A17U", "C61U", "C31", "C38U", "C09", "C52","D01","D05","G13","H78",
            "C07","J36","J37","BN4","N2IU","ME8U","M44U"};

            for(String symbol : symbols){
                Stock stock = YahooFinance.get(symbol + ".SI");
                double bid = stock.getQuote().getBid().doubleValue();
                double price = stock.getQuote().getPrice().doubleValue();
                double ask = stock.getQuote().getAsk().doubleValue();
                int bidVolume = stock.getQuote().getBidSize().intValue();
                int askVolume = stock.getQuote().getAskSize().intValue();

                stocks.save(new CustomStock(symbol, price, bidVolume, bid, askVolume, ask));
            }
        }
        catch(IOException e){
            System.out.println("One of the stock is not found");
        }
        
    }
}
