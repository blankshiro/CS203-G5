package com.cs203t5.ryverbank.trading;

import java.util.List;

import org.springframework.web.bind.annotation.*;

/**
 * A StockController that accepts and returns stock JSON data.
 */
@RestController
public class StockController {
    /** The stock repository. */
    private StockRepository stockRepository;
    /** The stock services. */
    private StockServices stockService;

    /**
     * Construcst a StockController with the following parameters.
     * 
     * @param stockRepository The stock repository.
     * @param stockService The stock services.
     */
    public StockController(StockRepository stockRepository, StockServices stockService) {
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
