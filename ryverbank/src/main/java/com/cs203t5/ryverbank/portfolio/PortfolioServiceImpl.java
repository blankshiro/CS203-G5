package com.cs203t5.ryverbank.portfolio;

import java.util.*;
import java.util.stream.Collectors;

import com.cs203t5.ryverbank.trading.Trade;
import com.cs203t5.ryverbank.trading.TradeRepository;

import org.springframework.stereotype.Service;

@Service
public class PortfolioServiceImpl implements PortfolioService {
    /** The portfolio repository. */
    private PortfolioRepository portfolios;
    // private TradeRepository tradeRepo;
    // private AssetRepository assets;

    public PortfolioServiceImpl(PortfolioRepository portfolios, AssetRepository assets) {
        this.portfolios = portfolios;
        // this.assets = assets;
    }

    /**
     * Finds the portfolio based on the customer id. If the portfolio is not found,
     * this method will return null.
     * 
     * @param id The id of the customer.
     * @return The portfolio found.
     */
    public Portfolio getPortfolio(Long id) {
        return portfolios.findByCustomerId(id).map(portfolio -> {
            calGainLoss(portfolio);
            return portfolios.save(portfolio);
        }).orElse(null);
        // .orElseGet(() ->
        // {return portfolios.save(new Portfolio(id));});
    }

    /**
     * Calculates the total profit and loss for all the trades made.
     * 
     * @param portfolio The specified portfolio.
     */
    public void calGainLoss(Portfolio portfolio) {
        // Optional<Portfolio> p = portfolios.findByCustomerId(id);
        // Portfolio portfolio = p.get();

        List<Asset> list = new ArrayList<>();
        list = portfolio.getAssets();

        double unrealizedGainLoss = 0.0;
        if (!list.isEmpty()) {
            for (Asset asset : list) {
                if (asset.isTraded == false)
                    unrealizedGainLoss += asset.getGainLoss();
            }
        }
        portfolio.setUnrealizedGainLoss(unrealizedGainLoss);
    }

    // public void calTotalGainLoss(double gainLoss, Portfolio portfolio){
    // portfolio.setTotalGainLoss(portfolio.getTotalGainLoss() + gainLoss);
    // portfolios.save(portfolio);
    // }
}
