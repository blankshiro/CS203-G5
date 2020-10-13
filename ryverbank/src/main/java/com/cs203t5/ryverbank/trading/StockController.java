package com.cs203t5.ryverbank.trading;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
public class StockController {
    private StockRepository stockRepository;
    private StockServices stockService;
 

    public StockController( StockRepository stockRepository, StockServices stockService) {
        this.stockRepository = stockRepository;
        this.stockService = stockService;
     
    }


    /**
     * List all stocks in the system
     * 
     * @return list of all stocls
     */
    @GetMapping("/stocks")
    public List<CustomStock> listStocks() {
        return stockRepository.findAll();
    }

        /**
     * Get specific stocks in the system
     * 
     * @return Specific stocls
     */
    @GetMapping("/stocks/{symbol}")
    public CustomStock getStocks(@PathVariable String symbol) {
       CustomStock stock = stockService.getStock(symbol);

       if (stock == null)
            throw new StockSymbolNotFoundException("No stock information found for " + symbol);
       return stock;
    }

    

    
   

 

 

   

 
}
