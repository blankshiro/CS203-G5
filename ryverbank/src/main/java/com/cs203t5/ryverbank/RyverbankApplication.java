package com.cs203t5.ryverbank;

import com.cs203t5.ryverbank.trading.*;
import com.cs203t5.ryverbank.customer.*;
import com.cs203t5.ryverbank.account_transaction.*;
import com.cs203t5.ryverbank.content.*;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main class.
 */
@SpringBootApplication
@EnableScheduling
public class RyverbankApplication {
	public static void main(String[] args) {

		ApplicationContext ctx = SpringApplication.run(RyverbankApplication.class, args);
		
		// JPA user repository init
		//create a manager account 
		//to be filled in with address, NRIC, full name etc
		CustomerRepository users = ctx.getBean(CustomerRepository.class);
		AccountRepository accounts = ctx.getBean(AccountRepository.class);
		BCryptPasswordEncoder encoder = ctx.getBean(BCryptPasswordEncoder.class);
		System.out.println("[Add admin]: " + users.save(
			new Customer("manager_1", encoder.encode("01_manager_01"),null, null, null, null, "ROLE_MANAGER", true)).getUsername());
		
		System.out.println("[Add analyst]: " + users.save(
			new Customer("analyst_1", encoder.encode("01_analyst_01"),null, null, null, null, "ROLE_ANALYST", true)).getUsername());

		System.out.println("[Add analyst]: " + users.save(
			new Customer("analyst_2", encoder.encode("02_analyst_02"),null, null, null, null, "ROLE_ANALYST", true)).getUsername());

		System.out.println("[Add user]: " + users.save(
			new Customer("user1", encoder.encode("goodpassword"),null, null, null, null, "ROLE_USER", true)).getUsername());
		
		int customerId = 4;
		long customer_id = customerId;
		System.out.println("[Add acccount for user1]" + accounts.save(
			new Account(customer_id,100000.0,100000.0)).getCustomer_id());
	
		ContentRepository meinContent = ctx.getBean(ContentRepository.class);
		System.out.println("[Adding content]: " +  meinContent.save(
			new Content("The greatest show", "Invest in the greatest shows", "This is some news", "www.greatshows.com")).getTitle());

		System.out.println("[Adding content]: " +  meinContent.save(
			new Content("The lamest show", "Invest in the lamest shows", "This is bad news", "www.badshows.com")).getTitle());		

		System.out.println("[Adding content]: " +  meinContent.save(
			new Content("Konosuba Season 3", "Best shows", "Best girl is back", "www.konosuba.com")).getTitle());

		System.out.println("[Adding content]: " +  meinContent.save(
			new Content("Cats", "Cats are cool", "Need I say more?", "www.catsarekewl.com")).getTitle());

			// StockCrawler crawler = ctx.getBean(StockCrawler.class);

			// crawler.crawl();
	
			// crawler.marketMaker();
	}
}
