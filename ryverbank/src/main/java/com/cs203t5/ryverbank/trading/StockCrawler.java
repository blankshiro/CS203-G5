package com.cs203t5.ryverbank.trading;

import java.util.*;
import com.cs203t5.ryverbank.portfolio.*;
import com.cs203t5.ryverbank.portfolio.AssetService;
import com.cs203t5.ryverbank.account_transaction.*;

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
    private AssetService assetService;
    private TransactionServices tranService;
    private AccountServices accService;
    private int count = 0;

    public StockCrawler(StockRepository stockRepository, TradeRepository tradeRepository,  AssetService assetService, TransactionServices tranService, AccountServices accService) {
        this.stockRepository = stockRepository;
        this.tradeRepository = tradeRepository;
        this.assetService = assetService;
        this.accService = accService;
        this.tranService = tranService;
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
    @Scheduled(cron = "30 00 09 ? * MON-FRI", zone = "GMT+8")
    public void openBuyMarket(){
        String[] symbols = new String[] { "A17U", "C61U", "C31", "C38U", "C09", "C52", "D01", "D05", "G13", "H78",
        "C07", "J36", "J37", "BN4", "N2IU", "ME8U", "M44U", "O39", "S58", "U96", "S68", "C6L", "Z74", "S63",
        "Y92", "U11", "U14", "V03", "F34", "BS6" };

        for(String symbol: symbols){
             //Get the list of trades for {symbol}
         List <Trade> tradesList = tradeRepository.findAllBySymbol(symbol);
         List<Trade> listOfBuyTrades = new ArrayList<>();
         

            // Customer_Id is the market maker account
            int account_Id = 1;
            Long accountId = Long.valueOf(account_Id);

         for(Trade trade: tradesList){
             if(trade.getAction().equals("buy") && (trade.getStatus().equals("open") ||  trade.getStatus().equals("partial-filled"))){
                 if(!(trade.getAccountId().equals(accountId))){
                    listOfBuyTrades.add(trade);
                 }
               
             }
         }

         for(Trade trade: listOfBuyTrades){
            Optional <CustomStock> optionalCustomStock = stockRepository.findBySymbol(symbol);
            CustomStock customStock = optionalCustomStock.get();
            List <Trade> listOfTrades = tradeRepository.findAllBySymbol(trade.getSymbol());
            List<Trade> listOfSellTrades = new ArrayList<>();
            
            //Get the list of open & partial-filled of market sell trades for {symbol}
                for(Trade sellTradeList: listOfTrades){
                    if(sellTradeList.getAction().equals("sell")){
                        if(sellTradeList.getStatus().equals("open") || sellTradeList.getStatus().equals("partial-filled")){
                                listOfSellTrades.add(sellTradeList);
                        }
                            
                    }
                }
    
                
                //When there is not available sell trades on the market
                //Set the trade to it's original status
                //Add the subsequent volume
                try{
                    if(listOfSellTrades.size() == 0){
                        if(trade.getStatus().equals("partial-filled")){
                            trade.setStatus("partial-filled");
                        }
                        else{
                            trade.setStatus("open");
                        }
                        customStock.setBidVolume(customStock.getBidVolume() + trade.getQuantity());
                        customStock.setAskVolume(customStock.getAskVolume() - trade.getFilledQuantity());
                        count = 0;
    
                        //trade successful then store into user's asset list
                        // assetService.addAsset(trade)
                        assetService.addAsset(trade, customStock);
                    
                        tradeRepository.save(trade);
                        return;
                    }
                    //If is a new trade, meaning no status has been set yet, set the trade to open
                }catch(NullPointerException e){
                    trade.setStatus("open");
                    customStock.setBidVolume(customStock.getBidVolume() + trade.getQuantity());
                    count = 0;
                    tradeRepository.save(trade);
                    return;
                }
            
                double lastPrice = 0.0;
    
                //This is set avg price for trade at the begining before there is any match
                try{
                    trade.setAvgPrice(trade.getAvgPrice());
                }catch(NullPointerException e ){
                    trade.setAvgPrice(0.0);
                }
            
                double avgPrice = trade.getAvgPrice();
            
        
                if(listOfSellTrades.size() != 0){
                    Date date = new Date(listOfSellTrades.get(0).getDate());
                    Trade matchTrade = listOfSellTrades.get(0);
    
                //Match to market sell price first
                    for(Trade sellTrade: listOfSellTrades){
                        Date currentSellTradeDate = new Date(sellTrade.getDate());
                        if(matchTrade.getAsk() > sellTrade.getAsk()){
                            matchTrade = sellTrade;
                        }
                        else if(matchTrade.getAsk() == sellTrade.getAsk()){
                            if(date.after(currentSellTradeDate)){
                                matchTrade = sellTrade;
                            }
                        }        
                    }
    
                    //add the number of matched trade by one
                    count++;
                    
    
                    //When submitted trade has more quantity than match trade
                    if(matchTrade.getQuantity() - trade.getQuantity() < 0){
    
                        int matchTradeFilledQuantity  = matchTrade.getFilledQuantity() + matchTrade.getQuantity();
                        int tradeFilledQuantity = trade.getFilledQuantity() + matchTrade.getQuantity();
                        int tradeQuantity = trade.getQuantity() - matchTrade.getQuantity();
    
                        matchTrade.setFilledQuantity(matchTradeFilledQuantity);
                        trade.setFilledQuantity(tradeFilledQuantity);
                        trade.setQuantity(tradeQuantity);
                        matchTrade.setQuantity(0);
                        
                    }
                    
                    else{
    
                        int matchTradeFilledQuantity  = matchTrade.getFilledQuantity() + trade.getQuantity();
                        int tradeFilledQuantity = trade.getQuantity() + trade.getFilledQuantity();
                        int matchTradeQuantity = matchTrade.getQuantity() - trade.getQuantity();
    
                        matchTrade.setFilledQuantity(matchTradeFilledQuantity);
                        trade.setFilledQuantity(tradeFilledQuantity);
                        trade.setQuantity(0);
                        matchTrade.setQuantity(matchTradeQuantity);
                        
                    }
    
    
                    if(matchTrade.getQuantity()  != 0){
                        matchTrade.setStatus("partial-filled");
                    }else{
                        matchTrade.setStatus("filled");
                        
                    }
                    if(trade.getQuantity()  != 0){
                        trade.setStatus("partial-filled");
    
                    }else{
                        trade.setStatus("filled");
                    }
                
                
                    //Set the avg_price for current trade
                    double matchTradeAskPrice;
                    if(matchTrade.getAsk() == 0.0){
                        matchTradeAskPrice = customStock.getAsk();
                    }else{
                        matchTradeAskPrice = matchTrade.getAsk();
                    }
            
                    avgPrice = (avgPrice + matchTradeAskPrice) / count;
                    trade.setAvgPrice(avgPrice);
    
                    //Set the avg_price for match trade
                    double tradeBidPrice;
                    if(trade.getBid() == 0.0){
                        tradeBidPrice  = customStock.getBid();
                    }else{
                        tradeBidPrice = trade.getBid();
                    }
                    matchTrade.setAvgPrice(tradeBidPrice);
    
                    lastPrice = matchTrade.getAsk();
    
                    
                    tradeRepository.save(trade);
                    /* ACCOUNT MATCH TRADE CREATED HERE. GET THE SELLER ID HERE*/
                    Long give = trade.getAccountId();
                    Long take = matchTrade.getAccountId();
                    double amt = trade.getFilledQuantity()*customStock.getAsk();
                    accService.accTradeOnHold(take, amt);
                    tranService.addTransaction(give, take, amt*-1);
                    tradeRepository.save(matchTrade);
                }
                    
                
    
            //If trade is partial-filled after matching, find other available sell trade on the market
                if(trade.getStatus().equals("partial-filled")){
                    //Set stock last price
                    if(lastPrice == 0.0){
                        customStock.setLastPrice(customStock.getAsk());
                    }else{
                        customStock.setLastPrice(lastPrice);
                    }
    
                    //Set stock bid price
                    customStock.setBid(customStock.getBid());
                    
                    //  createMarketBuyTrade(trade, customer, customStock);
                    openBuyMarket();
                }
    
                customStock.setAskVolume(customStock.getAskVolume() - trade.getFilledQuantity());
                
                //Set stock last price
                if(lastPrice == 0.0){
                    customStock.setLastPrice(customStock.getAsk());
                }else{
                    customStock.setLastPrice(lastPrice);
                }
                //Set stock bid price
                customStock.setBid(customStock.getBid());
            
            
    
            count = 0;
            //if it reaches here, straight away count as success
            // portfolioService.addAsset(trade, trade.getCustomerId());
            assetService.addAsset(trade, customStock);
            tradeRepository.save(trade);
         }
         
     

        }
        
    }


 @Scheduled(cron = "30 00 09 ? * MON-FRI", zone = "GMT+8")
    public void openSellMarket(){
        String[] symbols = new String[] { "A17U", "C61U", "C31", "C38U", "C09", "C52", "D01", "D05", "G13", "H78",
        "C07", "J36", "J37", "BN4", "N2IU", "ME8U", "M44U", "O39", "S58", "U96", "S68", "C6L", "Z74", "S63",
        "Y92", "U11", "U14", "V03", "F34", "BS6" };

        for(String symbol: symbols){
             //Get the list of trades for {symbol}
         List <Trade> tradesList = tradeRepository.findAllBySymbol(symbol);
         List<Trade> listOfSellTrades = new ArrayList<>();
         

            // Customer_Id is the market maker account
            int account_Id = 1;
            Long accountId = Long.valueOf(account_Id);

         for(Trade trade: tradesList){
             if(trade.getAction().equals("sell") && (trade.getStatus().equals("open") ||  trade.getStatus().equals("partial-filled"))){
                 if(!(trade.getAccountId().equals(accountId))){
                    listOfSellTrades.add(trade);
                 }
               
             }
         }

         for(Trade trade: listOfSellTrades){
            Optional <CustomStock> optionalCustomStock = stockRepository.findBySymbol(symbol);
            CustomStock customStock = optionalCustomStock.get();
            List <Trade> listOfTrades = tradeRepository.findAllBySymbol(trade.getSymbol());
            List<Trade> listOfBuyTrades = new ArrayList<>();
        


        //Get list of open / partial-filled market buy trades
            for(Trade buyTrade: listOfTrades){
                if(buyTrade.getAction().equals("buy")){
                    if(buyTrade.getStatus().equals("open") || buyTrade.getStatus().equals("partial-filled")){
                            listOfBuyTrades.add(buyTrade);
                    }
                    
                }
            }



            try{          
                if(listOfBuyTrades.size() == 0){
                    if(trade.getStatus().equals("partial-filled")){
                        trade.setStatus("partial-filled");
                    }
                else{
                    trade.setStatus("open");
                }
                customStock.setAskVolume(customStock.getAskVolume() + trade.getQuantity());
                customStock.setBidVolume(customStock.getBidVolume() - trade.getFilledQuantity());
                count = 0;
                 tradeRepository.save(trade);
                 //ADD PORTFOLIO HERE
                 return;
                }
            }catch(NullPointerException e){
                trade.setStatus("open");
                customStock.setAskVolume(customStock.getAskVolume() + trade.getQuantity());
                count = 0;
                 tradeRepository.save(trade);
                 return;
            }


            double lastPrice = 0.0;
            //This is set avg price for trade at the begining before there is any match
            try{
                trade.setAvgPrice(trade.getAvgPrice());
            }catch(NullPointerException e ){
                trade.setAvgPrice(0.0);
            }
            double avgPrice = trade.getAvgPrice();

    
            if(listOfBuyTrades.size() != 0){
                Date date = new Date(listOfBuyTrades.get(0).getDate());
                Trade matchTrade = listOfBuyTrades.get(0);


                //Match to market buy price (current highest buy price)
                //then the highest buy price
                for(Trade buyTrade: listOfBuyTrades){
                    Date currentBuyTradeDate = new Date(buyTrade.getDate());
                if(matchTrade.getBid() < buyTrade.getBid()  ){
                            matchTrade = buyTrade;
                    }
                    else if(matchTrade.getBid() == buyTrade.getBid()){
                        if(date.after(currentBuyTradeDate)){
                            matchTrade = buyTrade;
                        }
                    } 
                }
        
                //add the number of matched trade by one
                count++;
            
                
                if(matchTrade.getQuantity() - trade.getQuantity() < 0){

                    int matchTradeFilledQuantity  = matchTrade.getFilledQuantity() + matchTrade.getQuantity();
                    int tradeFilledQuantity = trade.getFilledQuantity() + matchTrade.getQuantity();
                    int tradeQuantity = trade.getQuantity() - matchTrade.getQuantity();

                    matchTrade.setFilledQuantity(matchTradeFilledQuantity);
                    trade.setFilledQuantity(tradeFilledQuantity);
                    trade.setQuantity(tradeQuantity);
                    matchTrade.setQuantity(0);
                    
                }else{

                    int matchTradeFilledQuantity  = matchTrade.getFilledQuantity() + trade.getQuantity();
                    int tradeFilledQuantity = trade.getQuantity() + trade.getFilledQuantity();
                    int matchTradeQuantity = matchTrade.getQuantity() - trade.getQuantity();

                    matchTrade.setFilledQuantity(matchTradeFilledQuantity);
                    trade.setFilledQuantity(tradeFilledQuantity);
                    trade.setQuantity(0);
                    matchTrade.setQuantity(matchTradeQuantity);
                
                }

                if(matchTrade.getQuantity()!= 0){
                    matchTrade.setStatus("partial-filled");
                }else{
                    matchTrade.setStatus("filled");
                    
                }
                if(trade.getQuantity() != 0){
                    trade.setStatus("partial-filled");
                }else{
                    trade.setStatus("filled");
                }

                //Set the avg_price for current trade
                double matchTradeBidPrice ;
                if(matchTrade.getBid() == 0.0){
                    matchTradeBidPrice  = customStock.getBid();
                }else{
                    matchTradeBidPrice = matchTrade.getBid();
                }
            
                avgPrice = (avgPrice + matchTradeBidPrice) / count;
                trade.setAvgPrice(avgPrice);
            
                //Set the avg_price for match trade
                double tradeAskPrice;
                if(trade.getAsk() == 0.0){
                    tradeAskPrice = customStock.getAsk();
                }else{
                    tradeAskPrice = trade.getAsk();
                }
                matchTrade.setAvgPrice(tradeAskPrice);


        
                //Set the last price
                lastPrice = matchTrade.getBid();
                        
                tradeRepository.save(trade);
                /* ACCOUNT MATCH TRADE CREATED HERE. GET THE SELLER ID HERE*/
                Long take = trade.getAccountId();
                Long give = matchTrade.getAccountId();
                double amt = trade.getFilledQuantity()*customStock.getBid();
                accService.accTradeOnHold(take, amt);
                accService.accTradeOnHold(give, amt*-1);
                tranService.addTransaction(take, give, amt);
                tradeRepository.save(matchTrade);
            }
            
        
            if(trade.getStatus().equals("partial-filled")){
                //Set stock last price
                if(lastPrice == 0.0){
                    customStock.setLastPrice(customStock.getBid());
                }else {
                    customStock.setLastPrice(lastPrice);

                }
                //Set stock ask price
                customStock.setAsk(customStock.getAsk());
            
                 openSellMarket();
            }

            customStock.setBidVolume(customStock.getBidVolume() - trade.getFilledQuantity());
            //Set stock last price
            if(lastPrice == 0.0){
                customStock.setLastPrice(customStock.getBid());
            }else {
                customStock.setLastPrice(lastPrice);
            }
            //Set stock ask price
            customStock.setAsk(customStock.getAsk());
            tradeRepository.save(trade);
         }
         
     

        }
        
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
