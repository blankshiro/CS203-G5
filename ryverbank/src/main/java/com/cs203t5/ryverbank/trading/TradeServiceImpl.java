package com.cs203t5.ryverbank.trading;
import org.springframework.stereotype.Service;


import com.cs203t5.ryverbank.customer.*;


import java.security.Timestamp;
import java.time.Instant;



import java.util.*;



@Service
public class TradeServiceImpl implements TradeServices {
    private TradeRepository tradeRepository;
 
   


    public TradeServiceImpl(TradeRepository tradeRepository ) {
        this.tradeRepository = tradeRepository;
        
    }

    //Get All trades on the market
    //This method will be used exclusively by Manager / Market maker
    @Override
    public List<Trade> getAllTrades(){
        return tradeRepository.findAll();
    }

    //Get Specific trades on the market
    //This method will be used exclusively by Customer
    //Only customer who created the trade can view the trade
    @Override
    public Trade getTrade(Long tradeId, Customer customer){
        return tradeRepository.findById(tradeId).map(trade -> {
            if(trade.getCustomerId() == customer.getId()){
                return tradeRepository.save(trade);
            }
            else{
                throw new CustomerUnauthorizedException(customer.getId());
            }
        }).orElse(null);      
     
    }

    //Cancel Specific trades on the market
    //This method will be used exclusively by Customer
    //Only customer who created the trade can cancel the trade
    //Only "Open" trade can be cancelled

    @Override
    public Trade cancelTrade(Long tradeId, Customer customer){
        return tradeRepository.findById(tradeId).map(trade ->{
            if(trade.getCustomerId() == customer.getId()){
                if(trade.getStatus().equals("open")){
                    trade.setStatus("cancelled");
                    return tradeRepository.save(trade);
                }else{
                    throw new TradeInvalidException("Invalid action");
                }
            }else{
                throw new CustomerUnauthorizedException(customer.getId());
            }
        }).orElse(null);
    }

    //Create a Market Buy Trade
    //This method will be used exclusively by Customer
    @Override
    public Trade createMarketBuyTrade(Trade trade, Customer customer, CustomStock customStock){
        
        long currentTimestamp = Instant.now().getEpochSecond();
    
        //Set the customer_id for the trade
        trade.setCustomerId(customer.getId());

        //Set the time when trade is submitted
        trade.setDate(currentTimestamp);


        //Get the list of trades for {symbol}
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
                return tradeRepository.save(trade);
            }
        }catch(NullPointerException e){
            trade.setStatus("open");
            customStock.setBidVolume(customStock.getBidVolume() + trade.getQuantity());
            return tradeRepository.save(trade);
        }
    
        double lastPrice = 0.0;
        if(listOfSellTrades.size() != 0){
            Date date = new Date(listOfSellTrades.get(0).getDate());
            Trade matchTrade = listOfSellTrades.get(0);

           //Match to market sell price firt
          for(Trade sellTrade: listOfSellTrades){
                Date currentSellTradeDate = new Date(sellTrade.getDate());
                if(sellTrade.getAsk() == customStock.getAsk()){
                    matchTrade = sellTrade;
                }else if(matchTrade.getAsk() == sellTrade.getAsk()){
                    if(date.after(currentSellTradeDate)){
                        matchTrade = sellTrade;
                    }
                }
                else if(matchTrade.getAsk() > sellTrade.getAsk()){
                    matchTrade = sellTrade;
                }
                 
        }
            // for(Trade sellTrade: listOfSellTrades){
            //     Date currentSellTradeDate = new Date(sellTrade.getDate());
            //     if(matchTrade.getAsk() > sellTrade.getAsk()){
            //         matchTrade = sellTrade;
            //     }
            //     else if(matchTrade.getAsk() == sellTrade.getAsk()){
            //         if(date.after(currentSellTradeDate)){
            //             matchTrade = sellTrade;
            //         }
            //     }        
            // }


            
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
        
            //Set the avg_price to current ask price for current Trade
            trade.setAvgPrice(customStock.getAsk());

            //Set the avg_price to current ask price for match trade
            matchTrade.setAvgPrice(customStock.getAsk());
            lastPrice = matchTrade.getAsk();

            tradeRepository.save(trade);
            tradeRepository.save(matchTrade);


        }
            
        

      
        if(trade.getStatus().equals("partial-filled")){
             //Set stock last price
            if(lastPrice == 0.0){
                customStock.setLastPrice(customStock.getAsk());
            }else{
                customStock.setLastPrice(lastPrice);
            }

            //Set stock bid price
            customStock.setBid(customStock.getBid());
          
            
            return createMarketBuyTrade(trade, customer, customStock);
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
       
     

    return tradeRepository.save(trade);

    }

    //Create a Market Sell Trade
    //This method will be used exclusively by Customer
    @Override
    public Trade createMarketSellTrade(Trade trade, Customer customer, CustomStock customStock){
        
        long currentTimestamp = Instant.now().getEpochSecond();
    
        //Set the customer_id for the trade
        trade.setCustomerId(customer.getId());

        //Set the time when trade is submitted
        trade.setDate(currentTimestamp);


        List <Trade> listOfTrades = tradeRepository.findAllBySymbol(trade.getSymbol());
        List<Trade> listOfBuyTrades = new ArrayList<>();
     


      //Get list of open / partial-filled market buy trades
        for(Trade buyTrade: listOfTrades){
            if(buyTrade.getAction().equals("buy")){
                if(buyTrade.getStatus().equals("open") || buyTrade.getStatus().equals("partial-filled")){
                    if(buyTrade.getBid() == customStock.getBid()){
                        listOfBuyTrades.add(buyTrade);
                    }
                    
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
            return tradeRepository.save(trade);
            }
        }catch(NullPointerException e){
            trade.setStatus("open");
            customStock.setAskVolume(customStock.getAskVolume() + trade.getQuantity());
            return tradeRepository.save(trade);
        }


        double lastPrice = 0.0;
        if(listOfBuyTrades.size() != 0){
            Date date = new Date(listOfBuyTrades.get(0).getDate());
            Trade matchTrade = listOfBuyTrades.get(0);


            //Match to market buy price
            for(Trade buyTrade: listOfBuyTrades){
                Date currentBuyTradeDate = new Date(buyTrade.getDate());
               if(matchTrade.getBid() == buyTrade.getBid()){
                    if(date.after(currentBuyTradeDate))
                        matchTrade = buyTrade;
                }
              
            }
       
          
            // for(Trade buyTrade: listOfBuyTrades){
            //     Date currentBuyTradeDate = new Date(buyTrade.getDate());
            //     if(matchTrade.getBid() < buyTrade.getBid()){
            //         matchTrade = buyTrade;
            //     }else if(matchTrade.getBid() == buyTrade.getBid()){
            //         if(date.after(currentBuyTradeDate))
            //             matchTrade = buyTrade;
            //     }
              
            // }
            
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

            //Set the avg_price to current bid price for current Trade
            trade.setAvgPrice(customStock.getBid());

            //Set the avg_price to current bid price for match trade
            matchTrade.setAvgPrice(customStock.getBid());
            lastPrice = matchTrade.getBid();
                      
            
            tradeRepository.save(trade);
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
           
            return createMarketSellTrade(trade, customer, customStock);
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

        return tradeRepository.save(trade);

    }

    //Create a Limit Buy Trade
    //This method will be used exclusively by Customer
    @Override
    public Trade createLimitBuyTrade(Trade trade, Customer  customer, CustomStock customStock){
        long currentTimestamp = Instant.now().getEpochSecond();
    
        //Set the customer_id for the trade
        trade.setCustomerId(customer.getId());

        //Set the time when trade is submitted
        trade.setDate(currentTimestamp);


        //Get the list of trades for {symbol}
        List <Trade> listOfTrades = tradeRepository.findAllBySymbol(trade.getSymbol());
        List<Trade> listOfSellTrades = new ArrayList<>();

        //Set the newBidPrice, the best price will be recorded
        //It must be better than the current stock's bid price and still lower than the ask price
        double newBidPrice = customStock.getBid();
        if(trade.getBid() > newBidPrice && trade.getBid()< customStock.getAsk()){
            newBidPrice = trade.getBid();
        }
     

     

        //Get the list of open & partial-filled sell trades that are equal to the bid_price or lower than the bid price for {symbol}
        for(Trade sellTradeList: listOfTrades){
            if(sellTradeList.getAction().equals("sell")){
                if(sellTradeList.getAsk() == trade.getBid() || sellTradeList.getAsk() < trade.getBid()){
                    if(sellTradeList.getStatus().equals("open") || sellTradeList.getStatus().equals("partial-filled")){
                        listOfSellTrades.add(sellTradeList);
                    }
                }
                
                    
            }
        }

        //When there no sell trades for the {symbol} stock
        try{
            if(listOfSellTrades.size() == 0){
                if(trade.getStatus().equals("partial-filled")){
                    trade.setStatus("partial-filled");
                }
                else{
                    trade.setStatus("open");
                }
                customStock.setBid(newBidPrice);
                customStock.setBidVolume(customStock.getBidVolume() + trade.getQuantity());
                customStock.setAskVolume(customStock.getAskVolume() - trade.getFilledQuantity());
                return tradeRepository.save(trade);
            } //when it is a new trade so there is no status
        }catch(NullPointerException e){
            trade.setStatus("open");
            customStock.setBid(newBidPrice);
            customStock.setBidVolume(customStock.getBidVolume() + trade.getQuantity());
            return tradeRepository.save(trade);
        }
    
        double lastPrice = 0.0;
        if(listOfSellTrades.size() != 0){
            Date date = new Date(listOfSellTrades.get(0).getDate());
            Trade matchTrade = listOfSellTrades.get(0);

            //Get the highest price trade, if is same price then
            //Get the earlist submitted sell trade
            //Buy @ low price
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
            System.out.println(matchTrade.getId() + " matchTrade ID");


            
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
        
            lastPrice = matchTrade.getAsk();
            tradeRepository.save(trade);
            tradeRepository.save(matchTrade);


        }
            
        

      
        if(trade.getStatus().equals("partial-filled")){
              //Set stock last price
              customStock.setLastPrice(lastPrice);
                //Set Stock bid price
              customStock.setBid(newBidPrice);

            return createMarketBuyTrade(trade, customer, customStock);
        }

        //Set stock last price
        customStock.setLastPrice(lastPrice);
        customStock.setAskVolume(customStock.getAskVolume() - trade.getFilledQuantity());
        //Set Stock bid price
        customStock.setBid(newBidPrice);

    return tradeRepository.save(trade);

        
    }

    @Override
    public Trade createLimitSellTrade(Trade trade, Customer  customer, CustomStock customStock){
        long currentTimestamp = Instant.now().getEpochSecond();
    
        //Set the customer_id for the trade
        trade.setCustomerId(customer.getId());

        //Set the time when trade is submitted
        trade.setDate(currentTimestamp);


        List <Trade> listOfTrades = tradeRepository.findAllBySymbol(trade.getSymbol());
        List<Trade> listOfBuyTrades = new ArrayList<>();
     
        //Set the newAskPrice, the best price will be recorded
        //It must be better than the current stock's ask price and still higher /equal than the bid price
        double newAskPrice = customStock.getAsk();
        if(trade.getAsk() > newAskPrice && trade.getAsk() > customStock.getBid() || trade.getAsk() == customStock.getBid() ){
            newAskPrice = trade.getAsk();
        }

        //Gte the list of open & partial-filled buy trades that are equal to the ask_price or higher than the ask_price
        for(Trade buyTrade: listOfTrades){
            if(buyTrade.getAction().equals("buy")){
                if(buyTrade.getBid() == trade.getAsk() || buyTrade.getBid() > trade.getAsk()){
                    if(buyTrade.getStatus().equals("open") || buyTrade.getStatus().equals("partial-filled")){
                        listOfBuyTrades.add(buyTrade);
                    }
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
            customStock.setAsk(newAskPrice);
            customStock.setAskVolume(customStock.getAskVolume() + trade.getQuantity());
            customStock.setBidVolume(customStock.getBidVolume() - trade.getFilledQuantity());
            return tradeRepository.save(trade);
            }
        }catch(NullPointerException e){
            trade.setStatus("open");
            customStock.setAsk(newAskPrice);
            customStock.setAskVolume(customStock.getAskVolume() + trade.getQuantity());
            return tradeRepository.save(trade);
        }

        //Get the highest price trade, if is same price then
        //Get the earliest submitted buy trade
        //Sell @ High price

        double lastPrice = 0.0;

        if(listOfBuyTrades.size() != 0){
            Date date = new Date(listOfBuyTrades.get(0).getDate());
            Trade matchTrade = listOfBuyTrades.get(0);

            for(Trade buyTrade: listOfBuyTrades){
                Date currentBuyTradeDate = new Date(buyTrade.getDate());
                if(matchTrade.getBid() < buyTrade.getBid()){
                    matchTrade = buyTrade;
                }else if(matchTrade.getBid() == buyTrade.getBid()){
                    if(date.after(currentBuyTradeDate))
                        matchTrade = buyTrade;
                }
              
            }

            
            
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

            //Set the avg_price to current bid price for current Trade
            trade.setAvgPrice(customStock.getBid());

            //Set the avg_price to current bid price for match trade
            matchTrade.setAvgPrice(customStock.getBid());

            lastPrice = matchTrade.getBid();
                      
            
            tradeRepository.save(trade);
            tradeRepository.save(matchTrade);

        }
        
    
        if(trade.getStatus().equals("partial-filled")){
                //Set stock last price
                customStock.setLastPrice(lastPrice);
                 //Set Stock ask price
                customStock.setAsk(newAskPrice);
                return createMarketSellTrade(trade, customer, customStock);
        }

        //Set Stock Bid volume
        customStock.setBidVolume(customStock.getBidVolume() - trade.getFilledQuantity());
        //Set stock last price
        customStock.setLastPrice(lastPrice);

        //Set Stock ask price
        customStock.setAsk(newAskPrice);

        return tradeRepository.save(trade);
    }

   
}
