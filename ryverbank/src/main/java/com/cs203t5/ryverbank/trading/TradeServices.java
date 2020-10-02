package com.cs203t5.ryverbank.trading;

import java.util.List;

public interface TradeServices {
    
    Trade buyAtMarket(String symbol);
    Trade buyAtLimit(String symbol);

    Trade sellAtMarket(String symbol);
    Trade sellAtLimit(String symbol);
    
}
