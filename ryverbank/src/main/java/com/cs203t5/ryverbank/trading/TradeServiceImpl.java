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
     

     

        //Get the list of open & partial-filled sell trades for {symbol}
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
    
        if(listOfSellTrades.size() != 0){
            Date date = new Date(listOfSellTrades.get(0).getDate());
            Trade matchTrade = listOfSellTrades.get(0);

            //Get the earlist submitted sell trade
            for(Trade sellTrade: listOfSellTrades){
                Date currentSellTradeDate = new Date(sellTrade.getDate());
                // System.out.println(date.after(currentSellTradeDate));
                if(date.after(currentSellTradeDate)){
                    matchTrade = sellTrade;
                }
                    
            }


            
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
        
            tradeRepository.save(trade);
            tradeRepository.save(matchTrade);


        }
            
        

      
        if(trade.getStatus().equals("partial-filled")){
            return createMarketBuyTrade(trade, customer, customStock);
        }

        customStock.setAskVolume(customStock.getAskVolume() - trade.getFilledQuantity());

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
            return tradeRepository.save(trade);
            }
        }catch(NullPointerException e){
            trade.setStatus("open");
            customStock.setAskVolume(customStock.getAskVolume() + trade.getQuantity());
            return tradeRepository.save(trade);
        }

        if(listOfBuyTrades.size() != 0){
            Date date = new Date(listOfBuyTrades.get(0).getDate());
            Trade matchTrade = listOfBuyTrades.get(0);

            for(Trade buyTrade: listOfBuyTrades){
                Date currentBuyTradeDate = new Date(buyTrade.getDate());
                if(date.after(currentBuyTradeDate))
                    matchTrade = buyTrade;
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
            
            tradeRepository.save(trade);
            tradeRepository.save(matchTrade);

        }
        
    
        if(trade.getStatus().equals("partial-filled")){
                return createMarketSellTrade(trade, customer, customStock);
        }

        customStock.setBidVolume(customStock.getBidVolume() - trade.getFilledQuantity());

        return tradeRepository.save(trade);

    }

   
}
