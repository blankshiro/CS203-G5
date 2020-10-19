package com.cs203t5.ryverbank.portfolio;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.cs203t5.ryverbank.trading.CustomStock;
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

    public void addAsset(Trade trade, CustomStock stock){
        String symbol = trade.getSymbol();
        Long customerId = trade.getCustomerId();
        Optional<Portfolio> p = portfolios.findByCustomerId(customerId);
        Portfolio portfolio = p.get();
        
       //make sure it is filled and partial-filled for buy trade!!!
        if(trade.getStatus().equals("filled") || trade.getStatus().equals("partial-filled")){
            Optional<Asset> optional = assets.findByCodeAndPortfolioIdAndIsTraded(symbol, portfolio.getId(), false);

            //if owner does not have this stock, then create a new asset
            if(optional.isEmpty()){
                String code = trade.getSymbol();
                int quantity = trade.getFilledQuantity();
                // double avg_price = trade.getAvgPrice();
                double current_price = trade.getBid();
                if(current_price == 0.0){
                    current_price = stock.getAsk();
                }
                //new asset created
                Asset asset = new Asset(code, quantity, current_price, current_price, portfolio.getId(), false, Double.toString(current_price));
                
                //update on the totalgainloss
                portfolio.setTotalGainLoss(portfolio.getTotalGainLoss() + asset.getGainLoss());

                assets.save(asset);
                portfolios.save(portfolio);


            }

            //else overwrite or update the current asset
            else{
                Asset asset = optional.get();
                updateAsset(trade, asset, stock, portfolio);
            }
            
        }
      
    }

    //update current asset
    public void updateAsset(Trade trade, Asset asset, CustomStock stock, Portfolio portfolio){
                
            double current_price = 0.0;
            //if its market buy, ask price should be 0
            if(trade.getBid() == 0.0){
                //get stock ask price since trade ask is 0
                asset.setCurrentPrice(stock.getAsk());
                current_price = stock.getAsk();
            }
            else{
                //else if it is limit buy, get trade ask price
                asset.setCurrentPrice(trade.getBid());
                current_price = trade.getBid();
            }

            String record = asset.getRecord();
            //get list of average price collected in string format
            String[] avglist = record.split(",");
            //compute average price here
            double total = 0.0;
            int count = 1;

            //parse recorded past current prices into double
            for(String amnt : avglist){
                total += Double.parseDouble(amnt);
                count++;
            }
            //update the quantity
            asset.setQuantity(asset.getQuantity() + trade.getFilledQuantity());
             //update the record of average prices
            asset.setAvgPrice((total + current_price) / count);
           
            asset.setValue(current_price * asset.getQuantity());

            //update the current gainloss of asset
            asset.setGainLoss(asset.getValue() - (asset.getAvgPrice()*asset.getQuantity()));

            //record down the current price into the asset for future reference as string
            asset.setRecord(asset.getRecord() + "," + Double.toString(current_price));

            //update the totalgainloss
            portfolio.setTotalGainLoss(portfolio.getTotalGainLoss() + asset.getGainLoss());
            
            assets.save(asset);
            portfolios.save(portfolio);

    }

    
    // public List<Asset> tradeAsset(String symbol, Long id){
        
    // }
}
