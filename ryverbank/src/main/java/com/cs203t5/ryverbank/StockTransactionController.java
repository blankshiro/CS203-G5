package com.cs203t5.ryverbank.controller;

import com.cs203t5.ryverbank.service.*;
import com.cs203t5.ryverbank.entity.*;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;


@RestController
public class StockTransactionController {
    private StockTransactionServices stockTransactionServices;

    public StockTransactionController(StockTransactionServices stockTransactionServices){
        this.stockTransactionServices = stockTransactionServices;
    }

    @RequestMapping("/stockMarket/myCartList")
    public List<StockTransaction> displayCartlist(){
        return this.stockTransactionServices.listStockCart();
    }

    @PostMapping("/stockMarket")
    public StockTransaction purchaseStock(@RequestBody Stock stock){
        return this.stockTransactionServices.createStockTransaction(stock);
    }
}
