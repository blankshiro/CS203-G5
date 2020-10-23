package com.cs203t5.ryverbank;

import java.util.Optional;

import com.cs203t5.ryverbank.account_transaction.Account;
import com.cs203t5.ryverbank.account_transaction.AccountRepository;
import com.cs203t5.ryverbank.account_transaction.TransactionRepository;
import com.cs203t5.ryverbank.content.ContentRepository;
import com.cs203t5.ryverbank.customer.Customer;
import com.cs203t5.ryverbank.customer.CustomerRepository;
import com.cs203t5.ryverbank.portfolio.AssetRepository;
import com.cs203t5.ryverbank.portfolio.Portfolio;
import com.cs203t5.ryverbank.portfolio.PortfolioRepository;
import com.cs203t5.ryverbank.trading.StockCrawler;
import com.cs203t5.ryverbank.trading.StockRepository;
import com.cs203t5.ryverbank.trading.TradeRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GenericController {
    private ContentRepository meinContent;
    private CustomerRepository meinCustomers;
    private TradeRepository meinTrades;
    private AccountRepository meinAccounts;
    private StockRepository meinStocks;
    private TransactionRepository meinTransactions;

    public GenericController(ContentRepository meinContent, CustomerRepository meinCustomers,
            TradeRepository meinTrades, AccountRepository meinAccounts, StockRepository meinStocks
            , TransactionRepository meinTransactions) {
        this.meinContent = meinContent;
        this.meinCustomers = meinCustomers;
        this.meinTrades = meinTrades;
        this.meinAccounts = meinAccounts;
        this.meinStocks = meinStocks;
        this.meinTransactions = meinTransactions;
    }

    @GetMapping("/")
    public String welcome() {
        return "Welcome to Ryverbank - Connection successful";
    }

    @GetMapping("/reset")
    public void resetRepos() {
        // Delete all existing customers & content
        System.out.println("Deleting all customers");
        // Everyone but the marketmaker will be deleted
        meinCustomers.deleteAllButOne();

        System.out.println("Deleting all content");
        meinContent.deleteAll();

        System.out.println("Deleting all trades");
        meinTrades.deleteAll();

        System.out.println("Deleting all stock records");
        meinStocks.deleteAll();

        System.out.println("Deleting all transactions records");
        meinTransactions.deleteAll();
        
        //Reinitializing all the stock and trade information for the marketMaker
        Optional<Account> marketMakerAcc = meinAccounts.findById(1L);
        try {
            //Resetting the $ for the market maker
            Account foundAcc = marketMakerAcc.get();
            foundAcc.setAvailableBalance(100000.0);
            foundAcc.setBalance(100000.0);
            StockCrawler stc = new StockCrawler(meinStocks, meinTrades);
            stc.crawl();
            stc.marketMarker();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("[Add admin]: " + meinCustomers.save(
			new Customer("manager_1", encoder.encode("01_manager_01"),null, null, null, null, "ROLE_MANAGER", true)).getUsername());
		
		System.out.println("[Add analyst]: " + meinCustomers.save(
			new Customer("analyst_1", encoder.encode("01_analyst_01"),null, null, null, null, "ROLE_ANALYST", true)).getUsername());

		System.out.println("[Add analyst]: " + meinCustomers.save(
			new Customer("analyst_2", encoder.encode("02_analyst_02"),null, null, null, null, "ROLE_ANALYST", true)).getUsername());
        
    }



}
