package com.cs203t5.ryverbank;

import java.util.Optional;

import com.cs203t5.ryverbank.customer.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

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
			new Customer("manager", encoder.encode("goodpassword"),"Benedict Cumberbatch", "S1234567D", "69696969", "Hollywood", "ROLE_MANAGER", true)).getUsername());
		
		System.out.println("[Add analyst]: " + users.save(
			new Customer("analyst", encoder.encode("goodpassword"),"Tom Cruise", "S2345678H", "64206969", "Hollywood", "ROLE_ANALYST", true)).getUsername());

		System.out.println("[Adding account]: " + users.save(
			new Customer("user1", encoder.encode("goodpassword"),"Megumin", "S3456789A", "99999999", "Crimson Demon village", "ROLE_USER", true)).getUsername());
		
		System.out.println("[Adding account]: " + users.save(
			new Customer("user2", encoder.encode("goodpassword"),"Yun Yun", "S4567891A", "88888888", "Crimson Demon village", "ROLE_USER", true)).getUsername());

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
