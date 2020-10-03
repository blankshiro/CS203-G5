package com.cs203t5.ryverbank;

import java.util.Optional;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.cs203t5.ryverbank.trading.*;
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
		
		/*
		// JPA user repository init
		//create a manager account 
		//to be filled in with address, NRIC, full name etc
		CustomerRepository users = ctx.getBean(CustomerRepository.class);
		BCryptPasswordEncoder encoder = ctx.getBean(BCryptPasswordEncoder.class);
		System.out.println("[Add admin]: " + users.save(
			new Customer("manager", encoder.encode("goodpassword"),null, null, null, null, "ROLE_MANAGER", true)).getUsername());
		
		System.out.println("[Add analyst]: " + users.save(
			new Customer("analyst", encoder.encode("goodpassword"),null, null, null, null, "ROLE_ANALYST", true)).getUsername());

		System.out.println("[Add user]: " + users.save(
			new Customer("user1", encoder.encode("goodpassword"),null, null, null, null, "ROLE_USER", true)).getUsername());
	
		ContentRepository meinContent = ctx.getBean(ContentRepository.class);
		System.out.println("[Adding content]: " +  meinContent.save(
			new Content("The greatest show", "Invest in the greatest shows", "This is some news", "www.greatshows.com")).getTitle());

		System.out.println("[Adding content]: " +  meinContent.save(
			new Content("The lamest show", "Invest in the lamest shows", "This is bad news", "www.badshows.com")).getTitle());		

		System.out.println("[Adding content]: " +  meinContent.save(
			new Content("Konosuba Season 3", "Best shows", "Best girl is back", "www.konosuba.com")).getTitle());

		System.out.println("[Adding content]: " +  meinContent.save(
			new Content("Cats", "Cats are cool", "Need I say more?", "www.catsarekewl.com")).getTitle());
		*/

	}


}
