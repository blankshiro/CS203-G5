package com.cs203t5.ryverbank;

import java.util.Calendar;
import java.util.Optional;
import java.util.TimeZone;

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
import com.cs203t5.ryverbank.trading.TradeInvalidException;
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
    private PortfolioRepository meinPortfolios;

    public GenericController(ContentRepository meinContent, CustomerRepository meinCustomers,
            TradeRepository meinTrades, AccountRepository meinAccounts, StockRepository meinStocks
            , TransactionRepository meinTransactions, PortfolioRepository meinPortfolios) {
        this.meinContent = meinContent;
        this.meinCustomers = meinCustomers;
        this.meinTrades = meinTrades;
        this.meinAccounts = meinAccounts;
        this.meinStocks = meinStocks;
        this.meinTransactions = meinTransactions;
        this.meinPortfolios = meinPortfolios;
    }

    @GetMapping("/")
    public String welcome() {
        return "Welcome to Ryverbank - Connection successful";
    }

    @GetMapping("/reset")
    public void resetRepos() {
        //Delete all portfolios 
        System.out.println("Deleting all Portfolios");
        meinPortfolios.deleteImmediate();
        System.out.println(meinPortfolios.count());
        
        System.out.println("Deleting all accounts");
        meinAccounts.deleteImmediate();
        System.out.println(meinAccounts.count());
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
        
        // Reinitializing all the stock and trade information for the marketMaker
        Optional<Account> marketMakerAcc = meinAccounts.findById(1L);
        try {
            TimeZone timeZone = TimeZone.getTimeZone("GMT+8");

            Calendar startDateTime=Calendar.getInstance(timeZone);
            startDateTime.set(Calendar.HOUR_OF_DAY,9);
            startDateTime.set(Calendar.MINUTE,0);
            startDateTime.set(Calendar.SECOND,0);
        
            Calendar endDateTime=Calendar.getInstance(timeZone);
            endDateTime.set(Calendar.HOUR_OF_DAY,17);
            endDateTime.set(Calendar.MINUTE,0);
            endDateTime.set(Calendar.SECOND,0);
        
        
            Calendar saturday = Calendar.getInstance(timeZone);
            saturday.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);
    
            Calendar sunday = Calendar.getInstance(timeZone);
            sunday.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        
            Calendar today = Calendar.getInstance(timeZone);
    
           if(!(today.after(startDateTime) && today.before(endDateTime)) || today.equals(saturday) || today.equals(sunday))
           {
               throw new TradeInvalidException("Market is close");
           }
            //Resetting the $ for the market maker
            Account foundAcc = marketMakerAcc.get();
            foundAcc.setAvailableBalance(100000.0);
            foundAcc.setBalance(100000.0);
            StockCrawler stc = new StockCrawler(meinStocks, meinTrades);
            stc.crawl();
            stc.marketMaker();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("[Creating an Admin]: " + meinCustomers.save(
			new Customer("manager_1", encoder.encode("01_manager_01"),null, null, null, null, "ROLE_MANAGER", true)).getUsername());
		
		System.out.println("[Add analyst]: " + meinCustomers.save(
			new Customer("analyst_1", encoder.encode("01_analyst_01"),null, null, null, null, "ROLE_ANALYST", true)).getUsername());

		System.out.println("[Add analyst]: " + meinCustomers.save(
			new Customer("analyst_2", encoder.encode("02_analyst_02"),null, null, null, null, "ROLE_ANALYST", true)).getUsername());
        
    }


}
