package com.cs203t5.ryverbank.trading;

import com.cs203t5.ryverbank.customer.*;
import com.cs203t5.ryverbank.portfolio.AssetService;
import com.cs203t5.ryverbank.account_transaction.*;

import java.util.*;

import javax.validation.Valid;

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

    private AccountServices accService;

    /**
     * Constructs a TradeController with the following parameters.
     * 
     * @param trackRepository    The trade repository.
     * @param tradeServices      The trade services.
     * @param customerRepository The customer repository.
     * @param accountRepository  The account repository.
     * @param stockRepository    The stock repository.
     * @param assetService       The asset services.
     */
    public TradeController(TradeRepository trackRepository, TradeServices tradeServices,
            CustomerRepository customerRepository, AccountRepository accountRepository, StockRepository stockRepository,
            AssetService assetService,  AccountServices accService) {
        this.trackRepository = trackRepository;
        this.tradeServices = tradeServices;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.stockRepository = stockRepository;
        this.assetService = assetService;
        this.accService = accService;

    }

    /**
     * Create a new trade based on the trade information and user authentication. If
     * the user is not found, throw CustomerNotFoundException. If account is not
     * found, throw AccountNotFoundException. If the trade is invalid or the
     * account does not have enough available balance, throw TradeInvalidException.
     * 
     * @param trade The trade to be created.
     * @param auth Checks for authenticated username.
     * @return The trade created.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/trades")
    public Trade createTrade(@Valid @RequestBody Trade trade, Authentication auth) {

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

        if (accountList.size() == 0) {
            throw new AccountNotFoundException(trade.getAccountId());
        } else {
            for (Account account : accountList) {
                if (account.getAccountID() == trade.getAccountId()) {
                    accountExist = true;
                }

            }
        }

        // If there account does not exist, throw AccountNotFoundException
        if (!accountExist) {
            throw new AccountNotFoundException(trade.getAccountId());
        }

        Optional<Account> optionalAccount = accountRepository.findById(trade.getAccountId());

        // Checking quantity, if quantity is not multiple of 100
        // throw exception
        if (trade.getQuantity() % 100 != 0) {
            throw new TradeInvalidException("Invalid Quantity");
        }

        if (trade.getQuantity() < 0) {
            throw new TradeInvalidException("invalid quantity");
        }

        // Buy at market price
        if (trade.getAction().equals("buy") && trade.getBid() == 0.0) {
            if (trade.getBid() < 0) {
                throw new TradeInvalidException("invalid bid price");
            }

            if (trade.getAsk() != 0) {
                trade.setAsk(0);
            }

            Optional<CustomStock> optionalStock = stockRepository.findBySymbol(trade.getSymbol());

            if (optionalStock != null && optionalStock.isPresent()) {
                CustomStock customStock = optionalStock.get();
                if (optionalAccount.get().getAvailableBalance() < (customStock.getAsk() * trade.getQuantity())) {
                    throw new TradeInvalidException("Available Balance Not Enough");
                } else {
                    accService.accTradeOnHold(trade.getAccountId(), trade.getQuantity() * customStock.getAsk() * -1);
                    return tradeServices.createMarketBuyTrade(trade, customer, customStock);
                }

            }

        }

        // Sell at market order
        if (trade.getAction().equals("sell") && trade.getAsk() == 0.0) {
            if (trade.getAsk() < 0) {
                throw new TradeInvalidException("invalid ask price");
            }
            if (trade.getBid() < 0 || trade.getBid() > 0) {
                trade.setBid(0.0);
            }

            Optional<CustomStock> optionalStock = stockRepository.findBySymbol(trade.getSymbol());

            if (optionalStock != null && optionalStock.isPresent()) {
                CustomStock customStock = optionalStock.get();

                // make sure owner have this asset, and quantity do not exceed the amount of
                // asset
                // which the owner currently owns.
                // if successful, quantity will be deducted and details of the asset will
                // recompute again
                // assetService.sellAsset(trade.getSymbol(), trade.getFilledQuantity(), customer.getCustomerId(), trade.getStatus());
                // System.out.println(customer.getCustomerId() + "SELL: " + trade.getFilledQuantity());
                return tradeServices.createMarketSellTrade(trade, customer, customStock);

            }

        }

        // Buy at limit order
        if (trade.getAction().equals("buy") && trade.getBid() != 0.0) {
            if (trade.getBid() < 0) {
                throw new TradeInvalidException("invalid bid price");
            }
            if (trade.getAsk() != 0) {
                trade.setAsk(0);
            }

            Optional<CustomStock> optionalStock = stockRepository.findBySymbol(trade.getSymbol());

            if (optionalStock != null && optionalStock.isPresent()) {
                CustomStock customStock = optionalStock.get();
                if (optionalAccount.get().getAvailableBalance() < (trade.getBid() * trade.getQuantity())) {
                    throw new TradeInvalidException("Available Balance Not Enough");
                } else {
                     /* ACCOUNT GET THE BUYER ID FROM TRADE HERE */
                    accService.accTradeOnHold(trade.getAccountId(), trade.getQuantity() * trade.getBid() * -1);
                    return tradeServices.createLimitBuyTrade(trade, customer, customStock);
                }

            }
        }

        // Sell at limit order
        if (trade.getAction().equals("sell") && trade.getAsk() != 0.0) {
            if (trade.getAsk() < 0) {
                throw new TradeInvalidException("invalid ask price");
            }
            if (trade.getBid() < 0 || trade.getBid() > 0) {
                trade.setBid(0.0);
            }

            Optional<CustomStock> optionalStock = stockRepository.findBySymbol(trade.getSymbol());

            if (optionalStock != null && optionalStock.isPresent()) {
                CustomStock customStock = optionalStock.get();
                // assetService.sellAsset(trade.getSymbol(), trade.getFilledQuantity(), customer.getCustomerId(), trade.getStatus());
                // System.out.println(customer.getCustomerId() + "SELL: " + trade.getFilledQuantity());
                return tradeServices.createLimitSellTrade(trade, customer, customStock);

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
     * @param id The trade id to find.
     * @param auth Checks for authenticated username.
     * @return The trade found.
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

    /**
     * Updates the trade based on the new trade information and user authentication.
     * If no user is found, throw CustomerNotFoundException. If no trade is found,
     * throw TradeNotFoundException.
     *
     * @param id           The id of the trade.
     * @param newTradeInfo The new trade information.
     * @param auth         The user authentication.
     * @return The trade updated.
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

        
        if (newTradeInfo.getStatus().equals("cancelled")){
            tradeServices.cancelTrade(id, customer);
        }
         

        return trackRepository.findById(id);
    }

}
