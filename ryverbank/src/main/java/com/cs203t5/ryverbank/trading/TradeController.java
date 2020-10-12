package com.cs203t5.ryverbank.trading;
import com.cs203t5.ryverbank.customer.*;
import com.cs203t5.ryverbank.account_transaction.*;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;

@RestController
public class TradeController {
    private TradeRepository trackRepository;
    private TradeServices tradeServices;
    private CustomerRepository customerRepository;
    private AccountRepository accountRepository;
    private StockRepository stockRepository;

 

    public TradeController( TradeRepository trackRepository, TradeServices tradeServices, CustomerRepository customerRepository,AccountRepository accountRepository,StockRepository stockRepository ) {
        this.trackRepository = trackRepository;
        this.tradeServices = tradeServices;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.stockRepository = stockRepository;
     
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
        String authenticatedUsername = auth.getName();

        //retrieve optionalCustomer object from Customer repository
       Optional <Customer> optionalCustomer = customerRepository.findByUsername(authenticatedUsername);

        //If there customer does not exist, throw CustomerNotFoundException 
        if(optionalCustomer.isEmpty()){
            throw new CustomerNotFoundException("Could not find user " + authenticatedUsername);
        }
            
        Customer customer = optionalCustomer.get();
        

        //To do, check if account exists
        //Find the accounts that customer owns
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

        


        //Buy at market price
        if(trade.getAction().equals("buy") && trade.getBid() == 0.0){
            Optional<CustomStock> optionalStock = stockRepository.findBySymbol(trade.getSymbol());
            if(optionalStock != null && optionalStock.isPresent()){
                CustomStock customStock = optionalStock.get();
                return tradeServices.createMarketBuyTrade(trade,customer,customStock);
            }
       
        }

         //Sell at market order
        if(trade.getAction().equals("sell") && trade.getAsk() == 0.0){
            Optional<CustomStock> optionalStock = stockRepository.findBySymbol(trade.getSymbol());
            if(optionalStock != null && optionalStock.isPresent()){
                CustomStock customStock = optionalStock.get();
                return tradeServices.createMarketSellTrade(trade,customer,customStock);
            }
       
        }

        //Buy at limit order
        if(trade.getAction().equals("buy") && trade.getBid() != 0.0){
            Optional<CustomStock> optionalStock = stockRepository.findBySymbol(trade.getSymbol());
            if(optionalStock != null && optionalStock.isPresent()){
                CustomStock customStock = optionalStock.get();
                if(trade.getBid() > customStock.getAsk() || trade.getBid() == customStock.getAsk()){
                    return tradeServices.createMarketBuyTrade(trade,customer,customStock);
                }else if(trade.getBid() < customStock.getAsk()){
                    return tradeServices.createLimitBuyTrade(trade, customer, customStock);
                }
             
            }
        }



         //Sell at limit order
         if(trade.getAction().equals("sell") && trade.getAsk() != 0.0){
            Optional<CustomStock> optionalStock = stockRepository.findBySymbol(trade.getSymbol());
            if(optionalStock != null && optionalStock.isPresent()){
                CustomStock customStock = optionalStock.get();
                if(trade.getAsk() < customStock.getBid() || trade.getAsk() == customStock.getBid()){
                    return tradeServices.createMarketSellTrade(trade,customer,customStock);
                }else if(trade.getAsk() > customStock.getBid()){
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
    public List<Trade> getAllTrades(){
        return trackRepository.findAll();
    }

    /**
     * Search for trade with the given id 
     * If there is not trade with the given "id",
     * throw a TradeNotFoundException
     * 
     * @param id
     * @return trade with the given id
     */

    @GetMapping("/trades/{id}")
    public Trade getTrade(@PathVariable Long id, Authentication auth){
        String authenticatedUsername = auth.getName();

        //retrieve optionalCustomer object from Customer repository
        Optional <Customer> optionalCustomer = customerRepository.findByUsername(authenticatedUsername);
   
        //If there customer does not exist, throw CustomerNotFoundException 
        if(optionalCustomer.isEmpty()){
            throw new CustomerNotFoundException("Could not find user " + authenticatedUsername);
        }
            
        Customer customer = optionalCustomer.get();

        Trade trade = tradeServices.getTrade(id, customer);

        if(trade == null)
            throw new TradeNotFoundException(id);
        
        return tradeServices.getTrade(id, customer);

    }

    // This method should only be accessible to User
    /*
     * This method will be in charge of calling all the updating methods on Trade
     * 
     * Roles that can call these methods: User
     * cancelTrade()
     */

    @PutMapping("/trades/{id}")
    public Optional<Trade> updateTrade(@PathVariable Long id, @Valid @RequestBody Trade newTradeInfo,Authentication auth){
        String authenticatedUsername = auth.getName();

        //retrieve optionalCustomer object from Customer repository
        Optional <Customer> optionalCustomer = customerRepository.findByUsername(authenticatedUsername);
   
        //If there customer does not exist, throw CustomerNotFoundException 
        if(optionalCustomer.isEmpty()){
            throw new CustomerNotFoundException("Could not find user " + authenticatedUsername);
        }
            
        Customer customer = optionalCustomer.get();

        Trade trade = tradeServices.getTrade(id, customer);

        if(trade == null)
            throw new TradeNotFoundException(id);
        
        if(newTradeInfo.getAction().equals("cancel"))
             tradeServices.cancelTrade(id, customer);
        
        return trackRepository.findById(id);
    }
    




 

    

    
   

 

 

   

 
}
