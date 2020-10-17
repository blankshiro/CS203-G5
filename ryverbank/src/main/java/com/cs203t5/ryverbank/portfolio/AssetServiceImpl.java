package com.cs203t5.ryverbank.portfolio;

import java.util.List;

import com.cs203t5.ryverbank.trading.Trade;

import org.springframework.stereotype.Service;

@Service
public class AssetServiceImpl implements AssetService {
    private AssetRepository assets;

    public AssetServiceImpl(AssetRepository assets){
        this.assets = assets;
    }

    public void addAsset(Trade trade){
       
        if(trade.getStatus().equals("filled") || trade.getStatus().equals("partial-filled")){
            String code = trade.getSymbol();
            int quantity = trade.getFilledQuantity();
            double avg_price = trade.getAvgPrice();
            double current_price = trade.getAsk();
            Long id = trade.getCustomerId();

            Asset asset = new Asset(code, quantity, avg_price, current_price, id, "false");
            assets.save(asset);
        }
      
    }

    public List<Asset> tradeAsset(String symbol, Long id){
        List<Asset> list = assets.findAllByCodeAndCustomerIdAndIsTraded(symbol, id, "false");
        if(list.isEmpty()){

        }
        for(Asset asset : list){
            asset.setIsTraded("true");
            assets.save(asset);
        }

        return list;
    }
}
