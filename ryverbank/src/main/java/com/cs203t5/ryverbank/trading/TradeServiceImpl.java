package com.cs203t5.ryverbank.trading;

import org.springframework.stereotype.Service;



import com.cs203t5.ryverbank.customer.*;
import com.cs203t5.ryverbank.portfolio.AssetNotFoundException;
import com.cs203t5.ryverbank.portfolio.AssetService;
import com.cs203t5.ryverbank.account_transaction.*;
import com.cs203t5.ryverbank.portfolio.PortfolioService;

import java.time.Instant;
import java.time.*;

import java.util.*;

/**
 * Implementation of the TradeServices class.
 * 
 * @see TradeServices
 */
@Service
public class TradeServiceImpl implements TradeServices {
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
     * Constructs a TradeServiceImpl with the following parameters.
     * 
     * @param tradeRepository The trade repository.
     * @param assetService The asset services.
     * @param tranService The transaction services.
     * @param accService The account services.
     * @param portfolioService The portfolio services.
     */
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
                    }else if(trade.getAction().equals("buy")){
                        accService.accTradeOnHold(trade.getAccountId(), trade.getQuantity() * trade.getTradedPrice());

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
        // accService.accTradeOnHold(trade.getAccountId(), trade.getQuantity() * customStock.getAsk() * -1);

   
        // If customer submit a trade on weekend OR submit on weekday BUT before 9am and
        // after 5pm (GMT+8) ,
        // Throw an error that shows market is close
        TimeZone timeZone = TimeZone.getTimeZone("GMT+8");

        Calendar startDateTime = Calendar.getInstance(timeZone);
        startDateTime.set(Calendar.HOUR_OF_DAY, 00);
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

        trade.setTradedPrice(customStock.getAsk());
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
                    System.out.println(trade.getCustomerId() + "BUY: " + trade.getFilledQuantity());
                    if(trade.getFilledQuantity() != 0.0){
                        trade.setAvgPrice(trade.getAvgPrice() / trade.getFilledQuantity());
                    }
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
                if(accService.getAccount(trade.getAccountId()).getBalance() == 0){
                    trade.setAvgPrice(trade.getAvgPrice() / trade.getFilledQuantity());
                    return tradeRepository.save(trade);
                }
                
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
                int originalFilledQuantity = trade.getFilledQuantity();
                int originalQuantity = trade.getQuantity();
                int originalMatchFilledQuantity = matchTrade.getFilledQuantity();
                int originalMatchQuantity = matchTrade.getQuantity();

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
                // Set the avg_price for match trade
                double tradeBidPrice;
                if (trade.getBid() == 0.0) {
                    tradeBidPrice = customStock.getBid();
                } else {
                    tradeBidPrice = trade.getBid();
                }
          
         
                /* ACCOUNT MATCH TRADE CREATED HERE. GET THE SELLER ID HERE */
                Long give = trade.getAccountId();
                Long take = matchTrade.getAccountId();
                double amt = trade.getFilledQuantity() * customStock.getAsk();

              
                if(accService.getAccount(trade.getAccountId()).getBalance() < amt ){
                
                    double askPrice = matchTrade.getAsk();
                    if(matchTrade.getAsk() == 0.0){
                        askPrice = tradeBidPrice;
                    }
                    int newAmount = (int) Math.round( accService.getAccount(trade.getAccountId()).getBalance() / askPrice);
                    if(newAmount % 100 != 0){
                        newAmount = (int)(Math.round( newAmount / 100.0) * 100);
                    }
                    // if(newAmount == 0){
                    //     throw new InsufficientBalanceException("Not enough funds");
                    // }
                    avgPrice += (newAmount * askPrice);
                    trade.setAvgPrice(avgPrice);
                    trade.setFilledQuantity(originalFilledQuantity + newAmount);
                    trade.setQuantity(originalQuantity - newAmount);
                    matchTrade.setFilledQuantity(originalMatchFilledQuantity + newAmount);
                    matchTrade.setQuantity(originalMatchQuantity - newAmount);
                    if(trade.getQuantity() != 0){
                        trade.setStatus("partial-filled");
                    }
                    if(matchTrade.getQuantity() != 0){
                        matchTrade.setStatus("partial-filled");
                    }

                    tradeRepository.save(trade);
                    tradeRepository.save(matchTrade);
                   
                    amt = newAmount * askPrice;
                    accService.accTradeOnHold(take, amt);
                    tranService.addTransaction(give, take, amt * -1);
                }else{
                    // Set the avg_price for current trade
                    double matchTradeAskPrice;
                    if (matchTrade.getAsk() == 0.0) {
                        matchTradeAskPrice = customStock.getAsk();
                    } else {
                        matchTradeAskPrice = matchTrade.getAsk();
                    }
           
                    avgPrice += (trade.getFilledQuantity() * matchTradeAskPrice) ;
                    trade.setAvgPrice(avgPrice);
                 
                    matchTrade.setAvgPrice(tradeBidPrice);

                    lastPrice = matchTrade.getAsk();

                    tradeRepository.save(trade);
                    accService.accTradeOnHold(take, amt);
                    tranService.addTransaction(give, take, amt * -1);
                }

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
            System.out.println(trade.getCustomerId() + "BUY: " + trade.getFilledQuantity());
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

        trade.setAvgPrice(trade.getAvgPrice() / trade.getFilledQuantity());
          
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
        startDateTime.set(Calendar.HOUR_OF_DAY, 00);
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
                    System.out.println(trade.getSymbol() + "was sold");
                    customStock.setBidVolume(customStock.getBidVolume() - trade.getFilledQuantity());
                    count = 0;
                    if(trade.getFilledQuantity() != 0){
                        trade.setAvgPrice(trade.getAvgPrice() / trade.getFilledQuantity());
                    }
                    
                    // assetService.sellAsset(trade.getSymbol(), trade.getFilledQuantity(), trade.getCustomerId());
                    assetService.sellAsset(trade.getSymbol(), trade.getFilledQuantity(), customer.getCustomerId(), trade.getStatus());
                    System.out.println(customer.getCustomerId() + "SELL: " + trade.getFilledQuantity());
                    portfolioService.updateRealizedGainLoss(trade, customStock);
                    return tradeRepository.save(trade);
                }
            } catch (NullPointerException e) {
                trade.setStatus("open");
                count = 0;
                return tradeRepository.save(trade);
            }
            catch(AssetNotFoundException e){
                System.out.println(e);
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

                //assetService.sellAsset(trade.getSymbol(), trade.getFilledQuantity(), customer.getCustomerId(), trade.getStatus());
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

                avgPrice += trade.getFilledQuantity() * matchTradeBidPrice;
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
                // assetService.sellAsset(trade.getSymbol(), trade.getFilledQuantity(), customer.getCustomerId(), trade.getStatus());
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
            count = 0;

        }
        //assetService.sellAsset(trade.getSymbol(), trade.getFilledQuantity(), trade.getCustomerId());
        assetService.sellAsset(trade.getSymbol(), trade.getFilledQuantity(), customer.getCustomerId(), trade.getStatus());
        System.out.println(customer.getCustomerId() + "SELL: " + trade.getFilledQuantity());
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

        trade.setAvgPrice(trade.getAvgPrice() / trade.getFilledQuantity());
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

        // If customer submit a trade on weekend OR submit on weekday BUT before 9am and
        // after 5pm (GMT+8) ,
        // Throw an error that shows market is close
        TimeZone timeZone = TimeZone.getTimeZone("GMT+8");

        Calendar startDateTime = Calendar.getInstance(timeZone);
        startDateTime.set(Calendar.HOUR_OF_DAY, 00);
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

        trade.setTradedPrice(trade.getBid());

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

           
            // This is set avg_price for trade
            // If is a open trade, then avg_price will be set to 0.0
            // If is a partial filled trade, then avg_pirce will be the same
            try {
                trade.setAvgPrice(trade.getAvgPrice());
            } catch (NullPointerException e) {
                trade.setAvgPrice(0.0);
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
                    System.out.println(trade.getCustomerId() + "BUY: " + trade.getFilledQuantity());
                    if(trade.getFilledQuantity() != 0.0){
                        trade.setAvgPrice(trade.getAvgPrice() / trade.getFilledQuantity());
                    }
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

          

            double avgPrice = trade.getAvgPrice();
            

            double lastPrice = 0.0;
            if (listOfSellTrades.size() != 0) {

                if(accService.getAccount(trade.getAccountId()).getBalance() == 0){
                    trade.setAvgPrice(trade.getAvgPrice() / trade.getFilledQuantity());
                    return tradeRepository.save(trade);
                }

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
                int originalFilledQuantity = trade.getFilledQuantity();
                int originalQuantity = trade.getQuantity();
                int originalMatchFilledQuantity = matchTrade.getFilledQuantity();
                int originalMatchQuantity = matchTrade.getQuantity();

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

                // // Set the avg_price for current trade
                // double matchTradeAskPrice;
                // if (matchTrade.getAsk() == 0.0) {
                //     matchTradeAskPrice = customStock.getAsk();
                // } else {
                //     matchTradeAskPrice = matchTrade.getAsk();
                // }

                // avgPrice = (avgPrice + matchTradeAskPrice) / count;
                // trade.setAvgPrice(avgPrice);

                // Set the avg_price for match trade
                double tradeBidPrice;
                if (trade.getBid() == 0.0) {
                    tradeBidPrice = customStock.getBid();
                } else {
                    tradeBidPrice = trade.getBid();
                }
          

                lastPrice = matchTrade.getAsk();
                tradeRepository.save(trade);

                /* ACCOUNT MATCH TRADE CREATED HERE. GET THE SELLER ID HERE */
                Long give = trade.getAccountId();
                Long take = matchTrade.getAccountId();
                double amt = trade.getFilledQuantity() * matchTrade.getAsk();

                if(accService.getAccount(trade.getAccountId()).getBalance() < amt ){
                
                    double askPrice = matchTrade.getAsk();
                    if(matchTrade.getAsk() == 0.0){
                        askPrice = tradeBidPrice;
                    }
                    int newAmount = (int) Math.round( accService.getAccount(trade.getAccountId()).getBalance() / askPrice);
                    if(newAmount % 100 != 0){
                        newAmount = (int)(Math.round( newAmount / 100.0) * 100);
                    }
                    // if(newAmount == 0){
                    //     throw new InsufficientBalanceException("Not enough funds");
                    // }
                    avgPrice += (newAmount * askPrice);
                    trade.setAvgPrice(avgPrice);
                    trade.setFilledQuantity(originalFilledQuantity + newAmount);
                    trade.setQuantity(originalQuantity - newAmount);
                    matchTrade.setFilledQuantity(originalMatchFilledQuantity + newAmount);
                    matchTrade.setQuantity(originalMatchQuantity - newAmount);
                    if(trade.getQuantity() != 0){
                        trade.setStatus("partial-filled");
                    }
                    if(matchTrade.getQuantity() != 0){
                        matchTrade.setStatus("partial-filled");
                    }

                    tradeRepository.save(trade);
                    tradeRepository.save(matchTrade);
                   
                    amt = newAmount * askPrice;
                    accService.accTradeOnHold(take, amt);
                    tranService.addTransaction(give, take, amt * -1);
                }else{
                    // Set the avg_price for current trade
                    double matchTradeAskPrice;
                    if (matchTrade.getAsk() == 0.0) {
                        matchTradeAskPrice = customStock.getAsk();
                    } else {
                        matchTradeAskPrice = matchTrade.getAsk();
                    }
           
                    avgPrice += (trade.getFilledQuantity() * matchTradeAskPrice) ;
                    trade.setAvgPrice(avgPrice);
                 
                    matchTrade.setAvgPrice(tradeBidPrice);

                    lastPrice = matchTrade.getAsk();

                    tradeRepository.save(trade);
                    accService.accTradeOnHold(take, amt);
                    tranService.addTransaction(give, take, amt * -1);
                }
              
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
            System.out.println(trade.getCustomerId() + "BUY: " + trade.getFilledQuantity());

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

        trade.setAvgPrice(trade.getAvgPrice() / trade.getFilledQuantity());
    
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
        startDateTime.set(Calendar.HOUR_OF_DAY, 00);
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
            if(customStock.getAskVolume() == 0){
                newAskPrice = trade.getAsk();
                newAskVolume = trade.getQuantity();
            }else{
                if (trade.getAsk() < newAskPrice && trade.getAsk() > customStock.getBid()
                || trade.getAsk() == customStock.getBid()) {
                    newAskPrice = trade.getAsk();
                    newAskVolume = trade.getQuantity();
            }
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
                    System.out.println(trade.getSymbol() + "was sold");
                    count = 0;
                    if(trade.getFilledQuantity() != 0){
                        trade.setAvgPrice(trade.getAvgPrice() / trade.getFilledQuantity());
                    }
                    // assetService.sellAsset(trade.getSymbol(), trade.getFilledQuantity(), trade.getCustomerId());
                    portfolioService.updateRealizedGainLoss(trade, customStock);
                    assetService.sellAsset(trade.getSymbol(), trade.getFilledQuantity(), customer.getCustomerId(), trade.getStatus());
                    System.out.println(customer.getCustomerId() + "SELL: " + trade.getFilledQuantity());
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

                assetService.sellAsset(trade.getSymbol(), trade.getFilledQuantity(), customer.getCustomerId(), trade.getStatus());
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

                avgPrice += trade.getFilledQuantity() * matchTradeBidPrice;
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
                // assetService.sellAsset(trade.getSymbol(), trade.getFilledQuantity(), customer.getCustomerId(), trade.getStatus());
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
        // assetService.sellAsset(trade.getSymbol(), trade.getFilledQuantity(), trade.getCustomerId());
        // assetService.sellAsset(trade.getSymbol(), trade.getFilledQuantity(), customer.getCustomerId(), trade.getStatus());
        System.out.println(customer.getCustomerId() + "SELL: " + trade.getFilledQuantity());
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
        trade.setAvgPrice(trade.getAvgPrice() / trade.getFilledQuantity());
        return tradeRepository.save(trade);
    }
}
