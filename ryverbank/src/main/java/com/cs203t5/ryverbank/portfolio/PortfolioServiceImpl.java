package com.cs203t5.ryverbank.portfolio;

import java.util.*;
import java.util.stream.Collectors;

import com.cs203t5.ryverbank.trading.Trade;
import com.cs203t5.ryverbank.trading.TradeRepository;

import org.springframework.stereotype.Service;

@Service
public class PortfolioServiceImpl implements PortfolioService {
    private PortfolioRepository portfolios;
    // private TradeRepository tradeRepo;
    private AssetRepository assets;


    public PortfolioServiceImpl(PortfolioRepository portfolios,AssetRepository assets){
        this.portfolios = portfolios;
        this.assets = assets;
    }

    //get portfolio using customer id
    public Portfolio getPortfolio(Long id){
        return portfolios.findByCustomerId(id).map(portfolio ->
            {return portfolios.save(portfolio);})
            .orElseGet(() -> 
            {return portfolios.save(new Portfolio(id));});
    }

    
    //adding asset by converting buy trade into asset class
    // public void addAsset(Trade trade){
       
    //     if(trade.getStatus().equals("filled") && trade.getStatus().equals("partial-filled")){
    //         String code = trade.getSymbol();
    //         int quantity = trade.getFilledQuantity();
    //         double avg_price = trade.getAvgPrice();
    //         double current_price = trade.getAsk();
    //         Long id = trade.getCustomerId();

    //         Asset asset = new Asset(code, quantity, avg_price, current_price, id, false);
    //         assets.save(asset);
    //     }
        
    // }

    // public void deleteAsset(String symbol, Long id){

    // }

    
}
