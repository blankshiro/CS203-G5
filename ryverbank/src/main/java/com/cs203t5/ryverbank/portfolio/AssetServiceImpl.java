package com.cs203t5.ryverbank.portfolio;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.cs203t5.ryverbank.trading.Trade;

import org.springframework.stereotype.Service;



@Service
public class AssetServiceImpl implements AssetService {
    private AssetRepository assets;
    private PortfolioRepository portfolios;

    public AssetServiceImpl(AssetRepository assets, PortfolioRepository portfolios){
        this.assets = assets;
        this.portfolios = portfolios;
    }

    public void addAsset(Trade trade){
       
        if(trade.getStatus().equals("filled") || trade.getStatus().equals("partial-filled")){
            String code = trade.getSymbol();
            int quantity = trade.getFilledQuantity();
            double avg_price = trade.getAvgPrice();
            double current_price = trade.getAsk();
            Long id = trade.getCustomerId();

            // List<Asset> list = new ArrayList<>();
            Optional<Portfolio> p = portfolios.findByCustomerId(id);
            Portfolio portfolio = p.get();
            Asset asset = new Asset(code, quantity, avg_price, current_price, portfolio.getId(), false);

            //if bought at market price gainloss will be 0.0;
            if(current_price == 0.0){
                asset.setGainLoss(0.0);
            }
            
            assets.save(asset);
        }
      
    }

    
    public List<Asset> tradeAsset(String symbol, Long id){
        Optional<Portfolio> p = portfolios.findByCustomerId(id);
        Portfolio portfolio = p.get();
        Long Id = portfolio.getId();
        List<Asset> list = assets.findAllByCodeAndPortfolioIdAndIsTraded(symbol, Id, false);
        if(list.isEmpty()){

        }
        for(Asset asset : list){
            asset.setTraded(true);
            assets.save(asset);
        }

        return list;
    }
}
