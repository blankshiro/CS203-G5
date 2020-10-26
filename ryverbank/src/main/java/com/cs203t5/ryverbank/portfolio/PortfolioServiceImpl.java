package com.cs203t5.ryverbank.portfolio;

import java.util.*;
import java.util.stream.Collectors;

import com.cs203t5.ryverbank.trading.CustomStock;
import com.cs203t5.ryverbank.trading.Trade;
import com.cs203t5.ryverbank.trading.TradeRepository;


import org.springframework.stereotype.Service;

import javassist.compiler.ast.DoubleConst;

@Service
public class PortfolioServiceImpl implements PortfolioService {
    private PortfolioRepository portfolios;
    // private TradeRepository tradeRepo;
    // private AssetRepository assets;


    public PortfolioServiceImpl(PortfolioRepository portfolios,AssetRepository assets){
        this.portfolios = portfolios;
        // this.assets = assets;
    }

    //get portfolio using customer id
    public Portfolio getPortfolio(Long id){
        return portfolios.findByCustomerId(id).map(portfolio ->
            {
                calGainLoss(portfolio);
                return portfolios.save(portfolio);
            }).orElse(null);
            // .orElseGet(() -> 
            // {return portfolios.save(new Portfolio(id));});
    }

    public void calGainLoss(Portfolio portfolio){
        // Optional<Portfolio> p = portfolios.findByCustomerId(id);
        // Portfolio portfolio = p.get();

        List<Asset> list = new ArrayList<>();
        list = portfolio.getAssets();

        double unrealizedGainLoss = 0.0; 
        if (!list.isEmpty()){
            for(Asset asset : list){
                if(asset.isTraded == false)
                unrealizedGainLoss += asset.getGainLoss();
            }
        }
        portfolio.setUnrealizedGainLoss(unrealizedGainLoss);
    }


    public void updateRealizedGainLoss(Trade trade, CustomStock stock){
        if(trade.getStatus().equals("filled") || trade.getStatus().equals("partial-filled")){
            Long id = trade.getCustomerId();
            Optional<Portfolio> optional = portfolios.findByCustomerId(id);
            Portfolio portfolio = optional.get();
            double gain = 0.0;
            double avg = 0.0;
            double gainLoss = 0.0;

            if(trade.getAsk() == 0.0){
                gain = stock.getAsk() * trade.getFilledQuantity();
                avg = trade.getAvgPrice() * trade.getFilledQuantity();
                gainLoss = gain - avg;
            }
            else{
                gain = trade.getAsk() * trade.getFilledQuantity();
                avg = trade.getAvgPrice() * trade.getFilledQuantity();
                gainLoss = gain - avg;

            }
            portfolio.setTotalGainLoss(portfolio.getTotalGainLoss() + gainLoss);
            portfolios.save(portfolio);
        }
    }
   
}
