package com.cs203t5.ryverbank.trading;

import org.springframework.stereotype.Service;

import com.cs203t5.ryverbank.customer.*;
import com.cs203t5.ryverbank.portfolio.AssetService;
import com.cs203t5.ryverbank.account_transaction.*;
import com.cs203t5.ryverbank.portfolio.PortfolioService;

import java.time.Instant;
import java.time.*;

import java.util.*;

@Service
public class TradeServiceImpl implements TradeServices {
    private TradeRepository tradeRepository;

    // addded asset service impl here by junan
    private AssetService assetService;
    private TransactionServices tranService;
    private AccountServices accService;
    private PortfolioService portfolioService;
    private int count = 0;

    public TradeServiceImpl(TradeRepository tradeRepository, AssetService assetService, TransactionServices tranService,
            AccountServices accService, PortfolioService portfolioService) {
        this.tradeRepository = tradeRepository;
        this.assetService = assetService;
        this.tranService = tranService;
        this.accService = accService;
        this.portfolioService = portfolioService;
    }

    // Get All trades on the market
    // This method will be used exclusively by Manager / Market maker
    // @Override
    // public List<Trade> getAllTrades(){
    // return tradeRepository.findAll();
    // }

    // Get Specific trades on the market
    // This method will be used exclusively by Customer
    // Only customer who created the trade can view the trade
    @Override
    public Trade getTrade(Long tradeId, Customer customer) {
        return tradeRepository.findById(tradeId).map(trade -> {
            if (trade.getCustomerId() == customer.getCustomerId()) {
                return tradeRepository.save(trade);
            } else {
                throw new CustomerUnauthorizedException(customer.getCustomerId());
            }
        }).orElse(null);

    }

    // Cancel Specific trades on the market
    // This method will be used exclusively by Customer
    // Only customer who created the trade can cancel the trade
    // Only "Open" trade can be cancelled

    @Override
    public Trade cancelTrade(Long tradeId, Customer customer) {
        return tradeRepository.findById(tradeId).map(trade -> {
            if (trade.getCustomerId() == customer.getCustomerId()) {
                if (trade.getStatus().equals("open")) {
                    trade.setStatus("cancelled");
                    // if it is sell then asset quantity will be put back into portfolio
                    if (trade.getAction().equals("sell")) {
                        assetService.retrieveAsset(trade.getSymbol(), trade.getQuantity(), customer.getCustomerId());
                    }
                    return tradeRepository.save(trade);
                } else {
                    throw new TradeInvalidException("Invalid action");
                }
            } else {
                throw new CustomerUnauthorizedException(customer.getCustomerId());
            }
        }).orElse(null);
    }

    // Create a Market Buy Trade
    // This method will be used exclusively by Customer
    @Override
    public Trade createMarketBuyTrade(Trade trade, Customer customer, CustomStock customStock) {

        long currentTimestamp = Instant.now().getEpochSecond();

        // Set the customer_id for the trade
        trade.setCustomerId(customer.getCustomerId());

        // Set the time when trade is submitted
        trade.setDate(currentTimestamp);

        /* ACCOUNT GET THE BUYER ID FROM TRADE HERE */
        accService.accTradeOnHold(trade.getAccountId(), trade.getQuantity() * customStock.getAsk() * -1);

        // If customer submit a trade on weekend OR submit on weekday BUT before 9am and
        // after 5pm (GMT+8) ,
        // Throw an error that shows market is close
        TimeZone timeZone = TimeZone.getTimeZone("GMT+8");

        Calendar startDateTime = Calendar.getInstance(timeZone);
        startDateTime.set(Calendar.HOUR_OF_DAY, 9);
        startDateTime.set(Calendar.MINUTE, 0);
        startDateTime.set(Calendar.SECOND, 0);

        Calendar endDateTime = Calendar.getInstance(timeZone);
        endDateTime.set(Calendar.HOUR_OF_DAY, 24);
        endDateTime.set(Calendar.MINUTE, 0);
        endDateTime.set(Calendar.SECOND, 0);

        Calendar saturday = Calendar.getInstance(timeZone);
        saturday.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

        Calendar sunday = Calendar.getInstance(timeZone);
        sunday.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        Calendar today = Calendar.getInstance(timeZone);

        if (!(today.after(startDateTime) && today.before(endDateTime)) || today.equals(saturday)
            ||  today.equals(sunday)  ) {
            trade.setStatus("open");
         
        } else {
            // Get the list of trades for {symbol}
            List<Trade> listOfTrades = tradeRepository.findAllBySymbol(trade.getSymbol());
            List<Trade> listOfSellTrades = new ArrayList<>();

            // Get the list of open & partial-filled of market sell trades for {symbol}
            for (Trade sellTradeList : listOfTrades) {
                if (sellTradeList.getAction().equals("sell")) {
                    if (sellTradeList.getStatus().equals("open")
                            || sellTradeList.getStatus().equals("partial-filled")) {
                        listOfSellTrades.add(sellTradeList);
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
                    if(customStock.getBidVolume() == 0){
                        customStock.setBidVolume(trade.getQuantity());
                    }else{
                        customStock.setBidVolume(trade.getQuantity());
                    }
                  
                    customStock.setAskVolume(customStock.getAskVolume() - trade.getFilledQuantity());
                    count = 0;

                    // trade successful then store into user's asset list
                    // assetService.addAsset(trade)
                    assetService.addAsset(trade, customStock);

                    return tradeRepository.save(trade);
                }
                // If is a new trade, meaning no status has been set yet, set the trade to open
            } catch (NullPointerException e) {
                trade.setStatus("open");
                if(customStock.getBidVolume() == 0){
                    customStock.setBidVolume(trade.getQuantity());
                }else{
                    customStock.setBidVolume(trade.getQuantity());
                }
                // customStock.setBidVolume(customStock.getBidVolume() + trade.getQuantity());
                count = 0;
                return tradeRepository.save(trade);
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
                double tradeBidPrice;
                if (trade.getBid() == 0.0) {
                    tradeBidPrice = customStock.getBid();
                } else {
                    tradeBidPrice = trade.getBid();
                }
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
               
            

                return createMarketBuyTrade(trade, customer, customStock);
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
            // portfolioService.addAsset(trade, trade.getCustomerId());
            assetService.addAsset(trade, customStock);
        }

        /*
        This is to set the askVolume for the stockInfo
        */
        // Get the list of trades for {symbol}
        List<Trade> listOfTrades = tradeRepository.findAllBySymbol(trade.getSymbol());
        List<Trade> listOfSellTrades = new ArrayList<>();


        if(customStock.getAskVolume() <= 0){
         // Get the list of open & partial-filled of market buy trades for {symbol}
            for (Trade sellTradeList : listOfTrades) {
                if (sellTradeList.getAction().equals("sell")) {
                    if (sellTradeList.getStatus().equals("open")
                            || sellTradeList.getStatus().equals("partial-filled")) {
                                listOfSellTrades.add(sellTradeList);
                    }

                }
            }
          
            if(listOfSellTrades.size() != 0){
                Date date = new Date(listOfSellTrades.get(0).getDate());
                Trade matchTrade = listOfSellTrades.get(0);

                for(Trade sellTrade: listOfSellTrades){
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
                if(matchTrade.getAsk() != 0.0){
                    customStock.setAsk(matchTrade.getAsk());
                }

            }else{
                customStock.setAskVolume(0);
            } 
            
        }

          
        return tradeRepository.save(trade);

    }

    // Create a Market Sell Trade
    // This method will be used exclusively by Customer
    @Override
    public Trade createMarketSellTrade(Trade trade, Customer customer, CustomStock customStock) {
        long currentTimestamp = Instant.now().getEpochSecond();

        // Set the customer_id for the trade
        trade.setCustomerId(customer.getCustomerId());

        // Set the time when trade is submitted
        trade.setDate(currentTimestamp);

        // If customer submit a trade on weekend OR submit on weekday BUT before 9am and
        // after 5pm (GMT+8) ,
        // Throw an error that shows market is close
        TimeZone timeZone = TimeZone.getTimeZone("GMT+8");

        Calendar startDateTime = Calendar.getInstance(timeZone);
        startDateTime.set(Calendar.HOUR_OF_DAY, 9);
        startDateTime.set(Calendar.MINUTE, 0);
        startDateTime.set(Calendar.SECOND, 0);

        Calendar endDateTime = Calendar.getInstance(timeZone);
        endDateTime.set(Calendar.HOUR_OF_DAY, 24);
        endDateTime.set(Calendar.MINUTE, 0);
        endDateTime.set(Calendar.SECOND, 0);

        Calendar saturday = Calendar.getInstance(timeZone);
        saturday.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

        Calendar sunday = Calendar.getInstance(timeZone);
        sunday.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        Calendar today = Calendar.getInstance(timeZone);

        if (!(today.after(startDateTime) && today.before(endDateTime)) || today.equals(saturday)
        ||  today.equals(sunday)   ) {
            trade.setStatus("open");

        } else {
            List<Trade> listOfTrades = tradeRepository.findAllBySymbol(trade.getSymbol());
            List<Trade> listOfBuyTrades = new ArrayList<>();

            // Get list of open / partial-filled market buy trades
            for (Trade buyTrade : listOfTrades) {
                if (buyTrade.getAction().equals("buy")) {
                    if (buyTrade.getStatus().equals("open") || buyTrade.getStatus().equals("partial-filled")) {
                        listOfBuyTrades.add(buyTrade);
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

                    portfolioService.updateRealizedGainLoss(trade, customStock);
                    return tradeRepository.save(trade);
                }
            } catch (NullPointerException e) {
                trade.setStatus("open");
                count = 0;
                return tradeRepository.save(trade);
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

                return createMarketSellTrade(trade, customer, customStock);
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

        }

        portfolioService.updateRealizedGainLoss(trade, customStock);

          // Set the bidVolume
          List<Trade> listOfTrades = tradeRepository.findAllBySymbol(trade.getSymbol());
          List<Trade> listOfBuyTrades = new ArrayList<>();
  
  
          if(customStock.getBidVolume() <= 0){
           // Get the list of open & partial-filled of market buy trades for {symbol}
              for (Trade buyTradeList : listOfTrades) {
                  if (buyTradeList.getAction().equals("buy")) {
                      if (buyTradeList.getStatus().equals("open")
                              || buyTradeList.getStatus().equals("partial-filled")) {
                                listOfBuyTrades.add(buyTradeList);
                      }
  
                  }
              }
            
              if(listOfBuyTrades.size() != 0){
                  Date date = new Date(listOfBuyTrades.get(0).getDate());
                  Trade matchTrade = listOfBuyTrades.get(0);
  
                  for (Trade buyTrade : listOfBuyTrades) {
                    Date currentBuyTradeDate = new Date(buyTrade.getDate());
                    if (matchTrade.getBid() < buyTrade.getBid()) {
                        matchTrade = buyTrade;
                    } else if (matchTrade.getBid() == buyTrade.getBid()) {
                        if (date.after(currentBuyTradeDate))
                            matchTrade = buyTrade;
                    }

                }
  
                  customStock.setBidVolume(matchTrade.getQuantity());
                  if(matchTrade.getBid() != 0.0){
                    customStock.setAsk(matchTrade.getBid());
                }
              }else{
                  customStock.setBidVolume(0);
              } 
              
          }

        return tradeRepository.save(trade);

    }

    // Create a Limit Buy Trade
    // This method will be used exclusively by Customer
    @Override
    public Trade createLimitBuyTrade(Trade trade, Customer customer, CustomStock customStock) {
        long currentTimestamp = Instant.now().getEpochSecond();

        // Set the customer_id for the trade
        trade.setCustomerId(customer.getCustomerId());

        // Set the time when trade is submitted
        trade.setDate(currentTimestamp);

        /* ACCOUNT GET THE BUYER ID FROM TRADE HERE */
        accService.accTradeOnHold(trade.getAccountId(), trade.getQuantity() * trade.getBid() * -1);

        // If customer submit a trade on weekend OR submit on weekday BUT before 9am and
        // after 5pm (GMT+8) ,
        // Throw an error that shows market is close
        TimeZone timeZone = TimeZone.getTimeZone("GMT+8");

        Calendar startDateTime = Calendar.getInstance(timeZone);
        startDateTime.set(Calendar.HOUR_OF_DAY, 9);
        startDateTime.set(Calendar.MINUTE, 0);
        startDateTime.set(Calendar.SECOND, 0);

        Calendar endDateTime = Calendar.getInstance(timeZone);
        endDateTime.set(Calendar.HOUR_OF_DAY, 24);
        endDateTime.set(Calendar.MINUTE, 0);
        endDateTime.set(Calendar.SECOND, 0);

        Calendar saturday = Calendar.getInstance(timeZone);
        saturday.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

        Calendar sunday = Calendar.getInstance(timeZone);
        sunday.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        Calendar today = Calendar.getInstance(timeZone);

        if (!(today.after(startDateTime) && today.before(endDateTime)) || today.equals(saturday)
        ||  today.equals(sunday)    ) {
            trade.setStatus("open");

        } else {
            // Get the list of trades for {symbol}
            List<Trade> listOfTrades = tradeRepository.findAllBySymbol(trade.getSymbol());
            List<Trade> listOfSellTrades = new ArrayList<>();

            // Set the newBidPrice, the best price will be recorded
            // best price is the higher bid
            // It must be better than the current stock's bid price and still lower than the
            // ask price
            double newBidPrice = customStock.getBid();
            int newBidVolume = customStock.getBidVolume();
            
    
            if (trade.getBid() > newBidPrice && trade.getBid() < customStock.getAsk()) {
                newBidPrice = trade.getBid();
                newBidVolume = trade.getQuantity();
          
            }

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

            // When there no sell trades for the {symbol} stock
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

                    // save the trade as an asset here
                    assetService.addAsset(trade, customStock);

                    return tradeRepository.save(trade);
                } // when it is a new trade so there is no status
            } catch (NullPointerException e) {
                trade.setStatus("open");
                customStock.setBid(newBidPrice);
                customStock.setBidVolume(newBidVolume);
                // customStock.setBidVolume(customStock.getBidVolume() + trade.getQuantity());
                count = 0;
                return tradeRepository.save(trade);
            }

            // This is set avg_price for trade
            // If is a open trade, then avg_price will be set to 0.0
            // If is a partial filled trade, then avg_pirce will be the same
            try {
                trade.setAvgPrice(trade.getAvgPrice());
            } catch (NullPointerException e) {
                trade.setAvgPrice(0.0);
            }

            double avgPrice = trade.getAvgPrice();

            double lastPrice = 0.0;
            if (listOfSellTrades.size() != 0) {
                Date date = new Date(listOfSellTrades.get(0).getDate());
                Trade matchTrade = listOfSellTrades.get(0);

                // Get the lowest price trade, if is same price then
                // Get the earlist submitted sell trade
                // Buy @ low price
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

                // Add number of match trade
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
                double tradeBidPrice;
                if (trade.getBid() == 0.0) {
                    tradeBidPrice = customStock.getBid();
                } else {
                    tradeBidPrice = trade.getBid();
                }
                matchTrade.setAvgPrice(tradeBidPrice);

                lastPrice = matchTrade.getAsk();
                tradeRepository.save(trade);
                /* ACCOUNT MATCH TRADE CREATED HERE. GET THE SELLER ID HERE */
                Long give = trade.getAccountId();
                Long take = matchTrade.getAccountId();
                double amt = trade.getFilledQuantity() * matchTrade.getAsk();
                // System.out.println(matchTrade.getFilledQuantity());
                // seller available balance will increase
                accService.accTradeOnHold(take, amt);
                // add in transaction
                tranService.addTransaction(give, take, amt * -1);
                tradeRepository.save(matchTrade);
            }

            // If current trade is only partial-filled after being matched, find other
            // available sell trade on the market
            if (trade.getStatus().equals("partial-filled")) {
                // Set stock last price
                customStock.setLastPrice(lastPrice);
                // Set Stock bid price
                customStock.setBid(newBidPrice);

                return createLimitBuyTrade(trade, customer, customStock);
            }

            // Update stock's last price, bid price and ask volume
            customStock.setLastPrice(lastPrice);
            customStock.setBid(newBidPrice);
            customStock.setAskVolume(customStock.getAskVolume() - trade.getFilledQuantity());

            count = 0;

            // will add trade into the portfolio here
            // portfolioService.addAsset(trade, trade.getCustomerId());
            assetService.addAsset(trade, customStock);

        }


        /*
        This is to set the askVolume for the stockInfo
        */
        // Get the list of trades for {symbol}
        List<Trade> listOfTrades = tradeRepository.findAllBySymbol(trade.getSymbol());
        List<Trade> listOfSellTrades = new ArrayList<>();


        if(customStock.getAskVolume() <= 0){
         // Get the list of open & partial-filled of market buy trades for {symbol}
            for (Trade sellTradeList : listOfTrades) {
                if (sellTradeList.getAction().equals("sell")) {
                    if (sellTradeList.getStatus().equals("open")
                            || sellTradeList.getStatus().equals("partial-filled")) {
                                listOfSellTrades.add(sellTradeList);
                    }

                }
            }
          
            if(listOfSellTrades.size() != 0){
                Date date = new Date(listOfSellTrades.get(0).getDate());
                Trade matchTrade = listOfSellTrades.get(0);

                for(Trade sellTrade: listOfSellTrades){
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
                if(matchTrade.getAsk() != 0.0){
                    customStock.setAsk(matchTrade.getAsk());
                }
            }else{
                customStock.setAskVolume(0);
            } 
            
        }

        
        return tradeRepository.save(trade);

    }

    @Override
    public Trade createLimitSellTrade(Trade trade, Customer customer, CustomStock customStock) {
        long currentTimestamp = Instant.now().getEpochSecond();

        // Set the customer_id for the trade
        trade.setCustomerId(customer.getCustomerId());

        // Set the time when trade is submitted
        trade.setDate(currentTimestamp);

        // If customer submit a trade on weekend OR submit on weekday BUT before 9am and
        // after 5pm (GMT+8) ,
        // Throw an error that shows market is close
        TimeZone timeZone = TimeZone.getTimeZone("GMT+8");

        Calendar startDateTime = Calendar.getInstance(timeZone);
        startDateTime.set(Calendar.HOUR_OF_DAY, 9);
        startDateTime.set(Calendar.MINUTE, 0);
        startDateTime.set(Calendar.SECOND, 0);

        Calendar endDateTime = Calendar.getInstance(timeZone);
        endDateTime.set(Calendar.HOUR_OF_DAY, 24);
        endDateTime.set(Calendar.MINUTE, 0);
        endDateTime.set(Calendar.SECOND, 0);

        Calendar saturday = Calendar.getInstance(timeZone);
        saturday.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

        Calendar sunday = Calendar.getInstance(timeZone);
        sunday.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        Calendar today = Calendar.getInstance(timeZone);

        if (!(today.after(startDateTime) && today.before(endDateTime)) || today.equals(saturday)
        ||  today.equals(sunday)   ) {
            trade.setStatus("open");
            // customStock.setAskVolume(customStock.getAskVolume() + trade.getQuantity());

        } else {
            List<Trade> listOfTrades = tradeRepository.findAllBySymbol(trade.getSymbol());
            List<Trade> listOfBuyTrades = new ArrayList<>();


        
            // Set the newAskPrice, the best price will be recorded
            // It must be better than the current stock's ask price and still higher /equal
            // than the bid price
            // best price is lower ask
            double newAskPrice = customStock.getAsk();
            int newAskVolume = customStock.getAskVolume();
            if (trade.getAsk() < newAskPrice && trade.getAsk() > customStock.getBid()
                    || trade.getAsk() == customStock.getBid()) {
                newAskPrice = trade.getAsk();
                newAskVolume = trade.getQuantity();
            }

            // Gte the list of open & partial-filled buy trades that are equal to the
            // ask_price or higher than the ask_price
            for (Trade buyTrade : listOfTrades) {
                if (buyTrade.getAction().equals("buy")) {
                    if (buyTrade.getBid() == trade.getAsk() || buyTrade.getBid() > trade.getAsk()) {
                        if (buyTrade.getStatus().equals("open") || buyTrade.getStatus().equals("partial-filled")) {
                            listOfBuyTrades.add(buyTrade);
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
                    customStock.setAsk(newAskPrice);
                    customStock.setAskVolume(newAskVolume);
                    // customStock.setAskVolume(customStock.getAskVolume() + trade.getQuantity());
                    customStock.setBidVolume(customStock.getBidVolume() - trade.getFilledQuantity());
                    count = 0;

                    portfolioService.updateRealizedGainLoss(trade, customStock);
                    return tradeRepository.save(trade);
                }
            } catch (NullPointerException e) {
                trade.setStatus("open");
                customStock.setAsk(newAskPrice);
                customStock.setAskVolume(newAskVolume);
                // customStock.setAskVolume(customStock.getAskVolume() + trade.getQuantity());
                count = 0;
                return tradeRepository.save(trade);
            }

            double lastPrice = 0.0;

            // This is set avg price for trade at the begining before there is any match
            try {
                trade.setAvgPrice(trade.getAvgPrice());
            } catch (NullPointerException e) {
                trade.setAvgPrice(0.0);
            }
            double avgPrice = trade.getAvgPrice();

            // Get the best price trade, if is same price then
            // Get the earliest submitted buy trade
            // best price trade = highest bid
            // Sell @ High price

            if (listOfBuyTrades.size() != 0) {
                Date date = new Date(listOfBuyTrades.get(0).getDate());
                Trade matchTrade = listOfBuyTrades.get(0);

                for (Trade buyTrade : listOfBuyTrades) {
                    Date currentBuyTradeDate = new Date(buyTrade.getDate());
                    if (matchTrade.getBid() < buyTrade.getBid()) {
                        matchTrade = buyTrade;
                    } else if (matchTrade.getBid() == buyTrade.getBid()) {
                        if (date.after(currentBuyTradeDate))
                            matchTrade = buyTrade;
                    }

                }

                // Add the number of match trade
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

                lastPrice = matchTrade.getBid();

                tradeRepository.save(trade);
                /* ACCOUNT MATCH TRADE CREATED HERE. GET THE SELLER ID HERE */
                Long take = trade.getAccountId();
                Long give = matchTrade.getAccountId();
                double amt = trade.getFilledQuantity() * matchTrade.getBid();
                accService.accTradeOnHold(take, amt);
                accService.accTradeOnHold(give, amt * -1);
                tranService.addTransaction(take, give, amt);
                tradeRepository.save(matchTrade);
            }

            if (trade.getStatus().equals("partial-filled")) {
                // Set stock last price
                customStock.setLastPrice(lastPrice);
                // Set Stock ask price
                customStock.setAsk(newAskPrice);
                return createLimitSellTrade(trade, customer, customStock);
            }

            // Set Stock Bid volume
            customStock.setBidVolume(customStock.getBidVolume() - trade.getFilledQuantity());
            // Set stock last price
            customStock.setLastPrice(lastPrice);

            // Set Stock ask price
            customStock.setAsk(newAskPrice);

            count = 0;
        }

        portfolioService.updateRealizedGainLoss(trade, customStock);

            // Set the bidVolume
            List<Trade> listOfTrades = tradeRepository.findAllBySymbol(trade.getSymbol());
            List<Trade> listOfBuyTrades = new ArrayList<>();
    
    
            if(customStock.getBidVolume() <= 0){
             // Get the list of open & partial-filled of market buy trades for {symbol}
                for (Trade buyTradeList : listOfTrades) {
                    if (buyTradeList.getAction().equals("buy")) {
                        if (buyTradeList.getStatus().equals("open")
                                || buyTradeList.getStatus().equals("partial-filled")) {
                                  listOfBuyTrades.add(buyTradeList);
                        }
    
                    }
                }
              
                if(listOfBuyTrades.size() != 0){
                    Date date = new Date(listOfBuyTrades.get(0).getDate());
                    Trade matchTrade = listOfBuyTrades.get(0);
    
                    for (Trade buyTrade : listOfBuyTrades) {
                      Date currentBuyTradeDate = new Date(buyTrade.getDate());
                      if (matchTrade.getBid() < buyTrade.getBid()) {
                          matchTrade = buyTrade;
                      } else if (matchTrade.getBid() == buyTrade.getBid()) {
                          if (date.after(currentBuyTradeDate))
                              matchTrade = buyTrade;
                      }
  
                  }
    
                    customStock.setBidVolume(matchTrade.getQuantity());
                    if(matchTrade.getBid() != 0.0){
                        customStock.setAsk(matchTrade.getBid());
                    }
                }else{
                    customStock.setBidVolume(0);
                } 
                
            }
        return tradeRepository.save(trade);
    }
}
