package com.cs203t5.ryverbank.portfolio;

import java.util.*;

import com.cs203t5.ryverbank.trading.Trade;

import org.springframework.stereotype.Service;

@Service
public class PortfolioServiceImpl implements PortfolioService {
    private PortfolioRepository portfolios;

    public PortfolioServiceImpl(PortfolioRepository portfolios){
        this.portfolios = portfolios;
    }

    public Portfolio getPortfolio(Long id){
        return portfolios.findByCustomerId(id).map(portfolio ->
            {return portfolios.save(portfolio);}).orElse(null);
    }

    public void addAsset(Trade trade, Long id){
        String code = trade.getSymbol();
        int quantity = trade.getFilledQuantity();
        double avg_price = trade.getAvgPrice();
        double current_price = trade.getAsk();

        Asset asset = new Asset(code, quantity, avg_price, current_price);

        Optional<Portfolio> optionalPortfolio = portfolios.findByCustomerId(id);

        Portfolio portfolio = optionalPortfolio.get();

        List<Asset> assets = portfolio.getAssets();
        assets.add(asset);

        portfolio.setAssets(assets);
        portfolio.setGainLoss();
        portfolios.save(portfolio);
    }

    public void deleteAsset(Trade trade, Long id){

    }
}
