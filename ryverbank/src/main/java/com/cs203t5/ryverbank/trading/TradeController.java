package com.cs203t5.ryverbank.trading;

import com.cs203t5.ryverbank.customer.*;
import com.cs203t5.ryverbank.portfolio.AssetService;
import com.cs203t5.ryverbank.account_transaction.*;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.ToDoubleBiFunction;

import javax.validation.Valid;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;

/**
 * A TradeController that accepts and returns trade JSON data.
 */

@RestController
public class TradeController {
    /** The trade repository. */
    private TradeRepository trackRepository;
    /** The trade services. */
    private TradeServices tradeServices;
    /** The customer repository. */
    private CustomerRepository customerRepository;
    /** The account repository. */
    private AccountRepository accountRepository;
    /** The stock repository. */
    private StockRepository stockRepository;
    /** The asset services. */
    private AssetService assetService;

    /**
     * Constructs a TradeController with the following parameters.
     * 
     * @param trackRepository The trade repository.
     * @param tradeServices The trade services.
     * @param customerRepository The customer repository.
     * @param accountRepository The account repository.
     * @param stockRepository The stock repository.
     * @param assetService The asset services.
     */
    public TradeController(TradeRepository trackRepository, TradeServices tradeServices,
            CustomerRepository customerRepository, AccountRepository accountRepository, StockRepository stockRepository,
            AssetService assetService) {
        this.trackRepository = trackRepository;
        this.tradeServices = tradeServices;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.stockRepository = stockRepository;
        this.assetService = assetService;

    }

    /**
     * Create a new trade
     * 
     * @param trade
     * @return the trade
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/trades")
    public Trade createTrade(@Valid @RequestBody Trade trade, Authentication auth){
        //If customer submit a trade on weekend OR submit on weekday BUT before 9am and after 5pm (GMT+8) ,
        //Throw an error that shows market is close
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

        String authenticatedUsername = auth.getName();

        // retrieve optionalCustomer object from Customer repository
        Optional<Customer> optionalCustomer = customerRepository.findByUsername(authenticatedUsername);

        // If there customer does not exist, throw CustomerNotFoundException
        if (optionalCustomer.isEmpty()) {
            throw new CustomerNotFoundException("Could not find user " + authenticatedUsername);
        }

            
        Customer customer = optionalCustomer.get();
        

 
        // To do, check if account exists
        // Find the accounts that customer owns
        boolean accountExist = false;
        List<Account> accountList = accountRepository.findByCustomer(optionalCustomer);

        if(accountList.size() == 0){
            throw new AccountNotFoundException(trade.getAccountId());
        }else{
            for(Account account: accountList){
                if(account.getAccountID() == trade.getAccountId()){
                    accountExist = true;
                }

            }
        }

      //If there account does not exist, throw AccountNotFoundException 
       if(!accountExist){
            throw new AccountNotFoundException(trade.getAccountId());
        }


        Optional <Account> optionalAccount = accountRepository.findById(trade.getAccountId());


        //Checking quantity, if quantity is not multiple of 100
        //throw exception
        if(trade.getQuantity() % 100 != 0){
            throw new TradeInvalidException("Invalid Quantity");
        }


        if(trade.getQuantity() < 0){
            throw new TradeInvalidException("invalid quantity");
        }

        //Buy at market price
        if(trade.getAction().equals("buy") && trade.getBid() == 0.0){
            if(trade.getBid() < 0){
                throw new TradeInvalidException("invalid bid price");
            }
            
            Optional<CustomStock> optionalStock = stockRepository.findBySymbol(trade.getSymbol());

            if(optionalStock != null && optionalStock.isPresent()){
                CustomStock customStock = optionalStock.get();
                if(optionalAccount.get().getAvailableBalance() < (customStock.getAsk() * trade.getQuantity())){
                    throw new TradeInvalidException("Available Balance Not Enough");
                }else{
                    return tradeServices.createMarketBuyTrade(trade,customer,customStock);
                }
                
            }

        }


         //Sell at market order
        if(trade.getAction().equals("sell") && trade.getAsk() == 0.0){
            if(trade.getAsk() < 0){
                throw new TradeInvalidException("invalid ask price");
            }

            Optional<CustomStock> optionalStock = stockRepository.findBySymbol(trade.getSymbol());

            if (optionalStock != null && optionalStock.isPresent()) {
                CustomStock customStock = optionalStock.get();

                if(optionalAccount.get().getAvailableBalance() < (customStock.getAsk() * trade.getQuantity())){
                    throw new TradeInvalidException("Available Balance Not Enough");
                }else{
                    //make sure owner have this asset, and quantity do not exceed the amount of asset
                    //which the owner currently owns.
                    //if successful, quantity will be deducted and details of the asset will recompute again
                    assetService.sellAsset(trade.getSymbol(), trade.getQuantity(), customer.getCustomerId());
                    return tradeServices.createMarketSellTrade(trade,customer,customStock);
                }
                
       
            }

        }


        //Buy at limit order
        if(trade.getAction().equals("buy") && trade.getBid() != 0.0){
            if(trade.getBid() < 0){
                throw new TradeInvalidException("invalid bid price");
            }

            Optional<CustomStock> optionalStock = stockRepository.findBySymbol(trade.getSymbol());

            if(optionalStock != null && optionalStock.isPresent()){
                CustomStock customStock = optionalStock.get();
                if(optionalAccount.get().getAvailableBalance() < (trade.getBid() * trade.getQuantity())){
                    throw new TradeInvalidException("Available Balance Not Enough");
                }else{
                    return tradeServices.createLimitBuyTrade(trade, customer, customStock);
                }
                
            }
        }



         //Sell at limit order
         if(trade.getAction().equals("sell") && trade.getAsk() != 0.0){
            if(trade.getAsk() < 0){
                throw new TradeInvalidException("invalid ask price");
            }

            Optional<CustomStock> optionalStock = stockRepository.findBySymbol(trade.getSymbol());

            if(optionalStock != null && optionalStock.isPresent()){
                CustomStock customStock = optionalStock.get();
                if(optionalAccount.get().getAvailableBalance() < (trade.getAsk() * trade.getQuantity())){
                    throw new TradeInvalidException("Available Balance Not Enough");
                }else{
                    assetService.sellAsset(trade.getSymbol(), trade.getQuantity(), customer.getCustomerId());
                    return tradeServices.createLimitSellTrade(trade, customer, customStock);
                }

            }

        }

        return null;

    }

    /**
     * List all trades in the system
     * 
     * @return list of all trades
     */
    @GetMapping("/trades")
    public List<Trade> getAllTrades() {
        return trackRepository.findAll();
    }

    /**
     * Search for trade with the given id If there is not trade with the given "id",
     * throw a TradeNotFoundException
     * 
     * @param id
     * @return trade with the given id
     */

    @GetMapping("/trades/{id}")
    public Trade getTrade(@PathVariable Long id, Authentication auth) {
        String authenticatedUsername = auth.getName();

        // retrieve optionalCustomer object from Customer repository
        Optional<Customer> optionalCustomer = customerRepository.findByUsername(authenticatedUsername);

        // If there customer does not exist, throw CustomerNotFoundException
        if (optionalCustomer.isEmpty()) {
            throw new CustomerNotFoundException("Could not find user " + authenticatedUsername);
        }

        Customer customer = optionalCustomer.get();

        Trade trade = tradeServices.getTrade(id, customer);

        if (trade == null)
            throw new TradeNotFoundException(id);

        return tradeServices.getTrade(id, customer);

    }

    // This method should only be accessible to User
    /*
     * This method will be in charge of calling all the updating methods on Trade
     * 
     * Roles that can call these methods: User cancelTrade()
     */

    @PutMapping("/trades/{id}")
    public Optional<Trade> updateTrade(@PathVariable Long id, @Valid @RequestBody Trade newTradeInfo,
            Authentication auth) {
        String authenticatedUsername = auth.getName();

        // retrieve optionalCustomer object from Customer repository
        Optional<Customer> optionalCustomer = customerRepository.findByUsername(authenticatedUsername);

        // If there customer does not exist, throw CustomerNotFoundException
        if (optionalCustomer.isEmpty()) {
            throw new CustomerNotFoundException("Could not find user " + authenticatedUsername);
        }

        Customer customer = optionalCustomer.get();

        Trade trade = tradeServices.getTrade(id, customer);

        if (trade == null)
            throw new TradeNotFoundException(id);

        if (newTradeInfo.getAction().equals("cancel"))
            tradeServices.cancelTrade(id, customer);

        return trackRepository.findById(id);
    }

}
