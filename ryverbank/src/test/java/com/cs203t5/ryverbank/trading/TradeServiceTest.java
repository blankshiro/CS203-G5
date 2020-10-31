package com.cs203t5.ryverbank.trading;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.cs203t5.ryverbank.account_transaction.Account;
import com.cs203t5.ryverbank.account_transaction.AccountRepository;
import com.cs203t5.ryverbank.account_transaction.AccountServiceImpl;
import com.cs203t5.ryverbank.account_transaction.AccountServices;
import com.cs203t5.ryverbank.customer.Customer;
import com.cs203t5.ryverbank.customer.CustomerUnauthorizedException;
import com.cs203t5.ryverbank.trading.TradeRepository;
import com.cs203t5.ryverbank.trading.TradeServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;

@ExtendWith(MockitoExtension.class)
public class TradeServiceTest{
    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TradeServiceImpl tradeServiceImpl;

    @InjectMocks
    private AccountServiceImpl accountServiceImpl;

    @Mock
    private AccountServices accountService;

   

       /*
        Run all tests in this java file: mvnw -Dtest=ContentServiceTest.java
        Run all tests in the project: mvnw test

        Naming convention for Mockito tests
        MethodName_StateUnderTest_ExpectedBehavior
        MethodName = The name of the method you are testing
        StateUnderTest = What is the condition that you are testing
        ExpectedBehavior = What do you expect based on the condition

        Understanding Mockito terminologies:
        when(...).thenReturn(...); translates to:
        when(callmethodsherewithparameters).thenReturn(result)
        This seems like rewriting the method for some reason

        Asserting:
        assertEquals(callmethodsherewithparameters, resultsYouAreExpecting)
        assertNotNull(variable)

    */
        
    @Test
    void createTrade_returnTrade(){
   
        int account_Id = 1;
        int customer_Id = 4;
        Long accountId = Long.valueOf(account_Id);
        Long customerId = Long.valueOf(customer_Id);

       //Arrange 
       Trade trade = new Trade("buy", "A17U", 2000, 0.0, 0.0, 0.0, 0,Instant.now().getEpochSecond(),  accountId, customerId, null);
       Customer customer = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "91251234","White House", "ROLE_USER", true);
       accountServiceImpl.addAccount(new Account(customer.getCustomerId(), 500000, 500000));

       CustomStock customStock = new CustomStock("A17U", 3.25, 20000, 3.30, 20000,3.30) ;

       //Mocking the save
       when(tradeRepository.save(any(Trade.class))).thenReturn(trade);

       // Act: actually using the save option
       Trade savedTrade = tradeServiceImpl.createMarketBuyTrade(trade, customer, customStock );

        //Assertion: Check if what you want to test really happens
        //Check if the trade is actually saved
        //Note: Might not be right since createContent will never return null
        //Rather, it will only create if the content title doesn't already exist

        assertNotNull(savedTrade);
        /*
         This verify statement means: 
        "check if the "save" function with the parameter newTrade has been called in trade database"
        */
        verify(tradeRepository).save(trade);
      
    }

    @Test
    void createTrade_InvalidQuantityException(){
        int account_Id = 1;
        int customer_Id = 4;
        Long accountId = Long.valueOf(account_Id);
        Long customerId = Long.valueOf(customer_Id);

       //Arrange 
       Trade trade = new Trade("buy", "A17U", 429, 0.0, 0.0, 0.0, 0,Instant.now().getEpochSecond(),  accountId, customerId, null);
       Customer customer = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "91251234","White House", "ROLE_USER", true);
       Account account = new Account(customer.getCustomerId(), 500000, 500000);
       accountServiceImpl.addAccount(account);

       CustomStock customStock = new CustomStock("A17U", 3.25, 20000, 3.30, 20000,3.30) ;

        //This should be null if an exception is thrown
        Trade savedTrade = tradeServiceImpl.createMarketBuyTrade(trade, customer, customStock );
 
         //Assert if null
         assertNull(savedTrade); 

    }

    @Test
    void getAllTrades(){
        int account_Id = 1;
        int customer_Id = 4;
        Long accountId = Long.valueOf(account_Id);
        Long customerId = Long.valueOf(customer_Id);

       //Arrange 
       Trade trade1 = new Trade("buy", "A17U", 2000, 0.0, 0.0, 0.0, 0,Instant.now().getEpochSecond(),  accountId, customerId, null);
       Trade trade2 = new Trade("buy", "C61U", 20001, 0.0, 0.0, 0.0, 0,Instant.now().getEpochSecond(),  accountId, customerId, null);
       Customer customer = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "91251234","White House", "ROLE_USER", true);
       Account account = new Account(customer.getCustomerId(), 500000, 500000);
       accountServiceImpl.addAccount(account);

       CustomStock customStock1 = new CustomStock("A17U", 3.25, 20000, 3.30, 20000,3.30) ;
       CustomStock customStock2 = new CustomStock("C61U", 3.25, 20000, 3.30, 20000,3.30) ;
       
      Trade savedTrade1 = tradeServiceImpl.createMarketBuyTrade(trade1, customer, customStock1);
         
      Trade savedTrade2 = tradeServiceImpl.createMarketBuyTrade(trade2, customer, customStock2);


        //Stubbing
        when (tradeRepository.findAll()).thenReturn(Arrays.asList(savedTrade1, savedTrade2));


        //Act
        List<Trade> allTrade = tradeServiceImpl.getAllTrades();
        assertNotNull(allTrade);
        assertEquals(2, allTrade.size());
        
    }

    @Test
     void cancelTrade_Success(){
        int account_Id = 1;
        int customer_Id = 4;
        Long accountId = Long.valueOf(account_Id);
        Long customerId = Long.valueOf(customer_Id);

       //Arrange 
       Trade trade = new Trade("buy", "A17U", 2000, 0.0, 0.0, 0.0, 0, Instant.now().getEpochSecond(),  accountId, customerId, null);
       Customer customer = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "91251234","White House", "ROLE_USER", true);
       Account account = new Account(customer.getCustomerId(), 500000, 500000);
       accountServiceImpl.addAccount(account);

       CustomStock customStock = new CustomStock("A17U", 3.25, 20000, 3.30, 20000,3.30) ;

         //Mocking the save
         when(tradeRepository.save(any(Trade.class))).thenReturn(trade);

        //This should be null if an exception is thrown
        Trade savedTrade = tradeServiceImpl.createMarketBuyTrade(trade, customer, customStock );
       

        //Mock a successful search and get a title
        when(tradeRepository.findById(savedTrade.getId())).thenReturn(Optional.of(savedTrade));
        when(tradeRepository.save(any(Trade.class))).thenReturn(savedTrade);

        Trade updatedTrade = tradeServiceImpl.cancelTrade(savedTrade.getId(), customer);
        assertEquals("cancelled", updatedTrade.getStatus());
     }

     @Test
     void cancelTrade_InvalidTrade(){
        int account_Id = 1;
        int customer_Id = 4;
        Long accountId = Long.valueOf(account_Id);
        Long customerId = Long.valueOf(customer_Id);

       //Arrange 
       Trade trade = new Trade("buy", "A17U", 2000, 0.0, 0.0, 0.0, 0, Instant.now().getEpochSecond(),  accountId, customerId, null);
       Customer customer = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "91251234","White House", "ROLE_USER", true);
       Account account = new Account(customer.getCustomerId(), 500000, 500000);
       accountServiceImpl.addAccount(account);

       CustomStock customStock = new CustomStock("A17U", 3.25, 20000, 3.30, 20000,3.30) ;

         //Mocking the save
         when(tradeRepository.save(any(Trade.class))).thenReturn(trade);

        //Make the trade partial-filled
        Trade savedTrade = tradeServiceImpl.createMarketBuyTrade(trade, customer, customStock);
        savedTrade.setStatus("partial-filled");
        
        
        //Mock a successful search 
        when(tradeRepository.findById(savedTrade.getId())).thenReturn(Optional.of(savedTrade));
        assertThrows(TradeInvalidException.class, ()-> tradeServiceImpl.cancelTrade(savedTrade.getId(), customer));

     }


     @Test
     void cancelTrade_UnauthorizedCustomer(){

        int account_Id = 1;
        int customer_Id = 4;
        Long accountId = Long.valueOf(account_Id);
        Long customerId = Long.valueOf(customer_Id);

   

       //Arrange 
       Trade trade = new Trade("buy", "A17U", 2000, 0.0, 0.0, 0.0, 0, Instant.now().getEpochSecond(),  accountId, customerId, null);
       Customer customer = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "91251234","White House", "ROLE_USER", true);
       Customer newCustomer = new Customer("user2", "goodpassword2", "Mark Tan", "S7982834C", "91431234","White House", "ROLE_USER", true);
       Account account = new Account(customer.getCustomerId(), 500000, 500000);
       accountServiceImpl.addAccount(account);

       CustomStock customStock = new CustomStock("A17U", 3.25, 20000, 3.30, 20000,3.30) ;

        //Mocking the save
         when(tradeRepository.save(any(Trade.class))).thenReturn(trade);

        //Make the trade partial-filled
        Trade savedTrade = tradeServiceImpl.createMarketBuyTrade(trade, customer, customStock);
     
        //Return a null
        Trade updatedTrade = tradeServiceImpl.cancelTrade(savedTrade.getId(), newCustomer);

        assertNull(updatedTrade);
        //Mock a successful search 
        // when(tradeRepository.findById(savedTrade.getId())).thenReturn(Optional.of(savedTrade));
        // assertThrows(CustomerUnauthorizedException.class, ()-> tradeServiceImpl.cancelTrade(savedTrade.getId(), newCustomer));

     }
}