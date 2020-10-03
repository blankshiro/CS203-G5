package com.cs203t5.ryverbank;

import java.io.IOException;
import java.math.BigDecimal;

import com.cs203t5.ryverbank.customer.*;
import com.cs203t5.ryverbank.trading.*;
import java.util.Optional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

@SpringBootApplication
public class RyverbankApplication {
	public static void main(String[] args) {

		ApplicationContext ctx = SpringApplication.run(RyverbankApplication.class, args);

		StockCrawler stc = ctx.getBean(StockCrawler.class);
		stc.crawl();
		StockRepository repo = ctx.getBean(StockRepository.class);

		Optional<CustomStock> stock = repo.findBySymbol("A17U");
		CustomStock stk = stock.get();

		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println(stk.getSymbol());
		System.out.println();
		System.out.println();
		
	}
}