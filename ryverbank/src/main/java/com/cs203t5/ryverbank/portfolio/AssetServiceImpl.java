package com.cs203t5.ryverbank.portfolio;

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
            double average = 0.0;
            int count = 1;

            //parse recorded past current prices into double
            for(String amnt : avglist){
                average += Double.parseDouble(amnt);
                count++;
            }
            //update the quantity
            asset.setQuantity(asset.getQuantity() + trade.getFilledQuantity());
             //update the record of average prices
            asset.setAvgPrice((average + current_price) / count);
           
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


    //for selling asset, deduct quantity from asset, make changes to the asset details
    public void sellAsset(String symbol, int quantity, Long customerId){
        Optional<Portfolio> opPortfolio = portfolios.findByCustomerId(customerId);
        Portfolio portfolio = opPortfolio.get();

        Optional<Asset> opAsset = assets.findByCodeAndPortfolioIdAndIsTraded(symbol, portfolio.getId(), false);
        if(opAsset.isEmpty()){
            throw new AssetNotFoundException("You do not have " + symbol + " asset in your portfolio.");
        }
        else if(opAsset.isPresent()){
            Asset asset = opAsset.get();

            //check quantity not to exceed the quantity that the customer currently owned
            if(asset.getQuantity() < quantity){
                throw new SellQuantityExceedException("amount owned:" + asset.getQuantity() + ", quantity:" + quantity);
            }
            
            asset.setQuantity(asset.getQuantity() - quantity);
            if(asset.getQuantity() == 0){
                asset.setTraded(true);
            }
            asset.setValue(asset.getCurrentPrice() * asset.getQuantity());
            asset.setGainLoss(asset.getValue() - (asset.getAvgPrice() * asset.getQuantity()));

            // portfolio.setTotalGainLoss(portfolio.getTotalGainLoss() + asset.getGainLoss());
            assets.save(asset);
        }
    }

    //when owner cancels trade, this will be called to retrieve back the asset
    public void retrieveAsset(String symbol, int quantity, Long customerId){
        Optional<Portfolio> opPortfolio = portfolios.findByCustomerId(customerId);
        Portfolio portfolio = opPortfolio.get();

        //check if the owner has a new or current asset to put back in
        Optional<Asset> opCurrentAsset = assets.findByCodeAndPortfolioIdAndIsTraded(symbol, portfolio.getId(), false);

        //if the owner have sold all of the asset, retrieve back the old asset class and store back in.
        Optional<Asset> opOldAsset = assets.findByCodeAndPortfolioIdAndIsTraded(symbol, portfolio.getId(), true);

        if(opCurrentAsset.isPresent() && opOldAsset.isPresent()){
            Asset currentAsset = opCurrentAsset.get();
            Asset oldAsset = opOldAsset.get();

            //transfer old info to new asset, will have to recompute the avg
            currentAsset.setRecord(currentAsset.getRecord() + "," + oldAsset.getRecord());

            //added retrieve quantity into current quantity
            currentAsset.setQuantity(currentAsset.getQuantity() + quantity);

            //recalculate value and gain loss
            currentAsset.setValue(currentAsset.getCurrentPrice() * currentAsset.getQuantity());
            
            String record = currentAsset.getRecord();
            //get list of average price collected in string format
            String[] avglist = record.split(",");
            //compute average price here
            double average = 0.0;
            int count = 1;

            //parse recorded past current prices into double
            for(String amnt : avglist){
                average += Double.parseDouble(amnt);
                count++;
            }
            currentAsset.setAvgPrice(average / count);
            currentAsset.setGainLoss(currentAsset.getValue() - (currentAsset.getAvgPrice() * currentAsset.getQuantity()));

            //save newly changes of current asset and sold asset
            assets.save(currentAsset);
            assets.save(oldAsset);

        }
        //if the owner have the current asset, just store back the quantity and recompute the gain loss
        else if(opCurrentAsset.isPresent()){
            Asset currentAsset = opCurrentAsset.get();
            currentAsset.setQuantity(currentAsset.getQuantity() + quantity);
            currentAsset.setValue(currentAsset.getCurrentPrice() * currentAsset.getQuantity());
            currentAsset.setGainLoss(currentAsset.getValue() - (currentAsset.getAvgPrice() * currentAsset.getQuantity()));

            assets.save(currentAsset);
        }
        else{
            Asset oldAsset = opOldAsset.get();
            oldAsset.setTraded(false);
            oldAsset.setQuantity(quantity);
            String record = oldAsset.getRecord();

            String[] avglist = record.split(",");
            
            double average = 0.0;
            int count = 1;

            for(String amnt : avglist){
                average += Double.parseDouble(amnt);
                count++;
            }
            oldAsset.setAvgPrice(average / count);
            double currentPrice = Double.parseDouble(avglist[avglist.length - 1]);
            oldAsset.setValue(currentPrice * quantity);
            oldAsset.setGainLoss(oldAsset.getValue() - (oldAsset.getAvgPrice() * quantity));

            assets.save(oldAsset);
        }

    }
    
}
