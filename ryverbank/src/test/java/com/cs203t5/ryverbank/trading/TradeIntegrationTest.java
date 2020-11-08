package com.cs203t5.ryverbank.trading;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import antlr.collections.List;
import net.minidev.json.JSONObject;

import com.cs203t5.ryverbank.account_transaction.Account;
import com.cs203t5.ryverbank.account_transaction.AccountRepository;
import com.cs203t5.ryverbank.account_transaction.AccountServices;
import com.cs203t5.ryverbank.customer.Customer;
import com.cs203t5.ryverbank.customer.CustomerRepository;
import com.cs203t5.ryverbank.customer.CustomerService;
import com.cs203t5.ryverbank.portfolio.AssetService;
import com.cs203t5.ryverbank.portfolio.Portfolio;
import com.cs203t5.ryverbank.portfolio.PortfolioRepository;
import com.cs203t5.ryverbank.portfolio.PortfolioService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class TradeIntegrationTest {

    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TradeRepository tradeRepository;
    
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountServices accountServices;

    @Autowired
    private PortfolioService portfolioServices;

    @Autowired
    private AssetService assetServices;

    @Autowired
    private BCryptPasswordEncoder encoder;



    @AfterEach
    void tearDown(){
        tradeRepository.deleteAll();
        customerRepository.deleteAll();
        portfolioRepository.deleteAll();
        
    }


     

    @Test
    public void createMarketBuyTrade_Success() throws Exception{

        URI uri = new URI(baseUrl + port + "/trades");
        Customer customer = customerRepository.save(new Customer("user1", encoder.encode("user1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));
   
        Account acc = accountRepository.save(new Account(null, customer, customer.getCustomerId(), 80000.0, 80000.0));

        CustomStock customStock = stockRepository.save(new CustomStock("A17U", 2.5, 20000, 2.5, 20000, 2.7));


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "buy");
        jsonObject.put("symbol", customStock.getSymbol());
        jsonObject.put("quantity", 100);
        jsonObject.put("bid", 0.0);
        jsonObject.put("account_id", acc.getAccountID());

        ResponseEntity<Trade> response = restTemplate.withBasicAuth("user1", "user1")
        .postForEntity(uri, jsonObject, Trade.class);
        assertEquals(201, response.getStatusCode().value());
       
    }


    @Test
    public void createMarketBuyTrade_InvalidQuantity() throws Exception{

        URI uri = new URI(baseUrl + port + "/trades");
        Customer customer = customerRepository.save(new Customer("user1", encoder.encode("user1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));
   
        Account acc = accountRepository.save(new Account(null, customer, customer.getCustomerId(), 80000.0, 80000.0));

        CustomStock customStock = stockRepository.save(new CustomStock("A17U", 2.5, 20000, 2.5, 20000, 2.7));


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "buy");
        jsonObject.put("symbol", customStock.getSymbol());
        jsonObject.put("quantity", 179);
        jsonObject.put("bid", 0.0);
        jsonObject.put("account_id", acc.getAccountID());

        ResponseEntity<Trade> response = restTemplate.withBasicAuth("user1", "user1")
        .postForEntity(uri, jsonObject, Trade.class);


        assertEquals(400, response.getStatusCode().value());
       
    }


    @Test
    public void createMarketBuyTrade_InsufficientBalance() throws Exception{

        URI uri = new URI(baseUrl + port + "/trades");
        Customer customer = customerRepository.save(new Customer("user1", encoder.encode("user1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));
   
        Account acc = accountRepository.save(new Account(null, customer, customer.getCustomerId(), 800.0, 800.0));

        CustomStock customStock = stockRepository.save(new CustomStock("A17U", 2.5, 20000, 2.5, 20000, 2.7));


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "buy");
        jsonObject.put("symbol", customStock.getSymbol());
        jsonObject.put("quantity", 20000);
        jsonObject.put("bid", 0.0);
        jsonObject.put("account_id", acc.getAccountID());

        ResponseEntity<Trade> response = restTemplate.withBasicAuth("user1", "user1")
        .postForEntity(uri, jsonObject, Trade.class);


        assertEquals(400, response.getStatusCode().value());
       
    }

    @Test
    public void createMarketBuyTrade_InvalidStockSymbol() throws Exception{

        URI uri = new URI(baseUrl + port + "/trades");
        Customer customer = customerRepository.save(new Customer("user1", encoder.encode("user1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));
   
        Account acc = accountRepository.save(new Account(null, customer, customer.getCustomerId(), 800.0, 800.0));


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "buy");
        jsonObject.put("symbol", "123");
        jsonObject.put("quantity", 20000);
        jsonObject.put("bid", 0.0);
        jsonObject.put("account_id", acc.getAccountID());

        ResponseEntity<Trade> response = restTemplate.withBasicAuth("user1", "user1")
        .postForEntity(uri, jsonObject, Trade.class);


        assertEquals(404, response.getStatusCode().value());
       
    }

    @Test
    public void createMarketBuyTrade_InvalidBidPrice() throws Exception{

        URI uri = new URI(baseUrl + port + "/trades");
        Customer customer = customerRepository.save(new Customer("user1", encoder.encode("user1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));
   
        Account acc = accountRepository.save(new Account(null, customer, customer.getCustomerId(), 80000.0, 80000.0));

        CustomStock customStock = stockRepository.save(new CustomStock("A17U", 2.5, 20000, 2.5, 20000, 2.7));


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "buy");
        jsonObject.put("symbol", customStock.getSymbol());
        jsonObject.put("quantity", 1000);
        jsonObject.put("bid", -10.0);
        jsonObject.put("account_id", acc.getAccountID());

        ResponseEntity<Trade> response = restTemplate.withBasicAuth("user1", "user1")
        .postForEntity(uri, jsonObject, Trade.class);

        assertEquals(400, response.getStatusCode().value());
       
    }
 

    @Test
    public void createMarketBuyTrade_InvalidCustomer() throws Exception{

        URI uri = new URI(baseUrl + port + "/trades");
        long currentTimestamp = Instant.now().getEpochSecond();
        Customer customer = customerRepository.save(new Customer("user1", encoder.encode("user1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));
   
        Account acc = accountRepository.save(new Account(customer.getCustomerId(), 80000.0, 80000.0));

        Trade trade = tradeRepository.save(new Trade("buy", "A17U", 2000, 0.0,0.0, 0.0, 0, currentTimestamp, acc.getAccountID(), customer.getCustomerId(), "open", 0.0));

        ResponseEntity<Trade> result = restTemplate.withBasicAuth("user2", "user1")
        .postForEntity(uri, trade, Trade.class);


        assertEquals(401, result.getStatusCode().value());
       
    }

    @Test
    public void createMarketBuyTrade_AccountNotFound() throws Exception{
        URI uri = new URI(baseUrl + port + "/trades");
        long currentTimestamp = Instant.now().getEpochSecond();
        Customer customer = customerRepository.save(new Customer("user1", encoder.encode("user1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));
        Customer customer2 = customerRepository.save(new Customer("user2", encoder.encode("goodpassword2"), "customer 2", "S9839006E",
        "91251235", "Dog House", "ROLE_USER", true));
   

        Account acc2 = accountRepository.save(new Account(customer2.getCustomerId(), 80000.0, 80000.0));

        Trade trade = tradeRepository.save(new Trade("buy", "A17U", 2000, 0.0,0.0, 0.0, 0, currentTimestamp, acc2.getAccountID(), customer.getCustomerId(), "open", 0.0));

        ResponseEntity<Trade> result = restTemplate.withBasicAuth("user1", "user1")
        .postForEntity(uri, trade, Trade.class);

        assertEquals(404, result.getStatusCode().value());
       
    }



    @Test
    public void createMarketSellTrade_InvalidQuantity() throws Exception{
        URI uri = new URI(baseUrl + port + "/trades");
        Customer customer = customerRepository.save(new Customer("user1", encoder.encode("user1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));
   
        Account acc = accountRepository.save(new Account(null, customer, customer.getCustomerId(), 80000.0, 80000.0));

        CustomStock customStock = stockRepository.save(new CustomStock("A17U", 2.5, 20000, 2.5, 20000, 2.7));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "buy");
        jsonObject.put("symbol", customStock.getSymbol());
        jsonObject.put("quantity", 1000);
        jsonObject.put("bid", 0.0);
        jsonObject.put("account_id", acc.getAccountID());

        ResponseEntity<Trade> response = restTemplate.withBasicAuth("user1", "user1")
        .postForEntity(uri, jsonObject, Trade.class);

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("action", "sell");
        jsonObject2.put("symbol", customStock.getSymbol());
        jsonObject2.put("quantity", 179);
        jsonObject2.put("ask", 0.0);
        jsonObject2.put("account_id", acc.getAccountID());

        ResponseEntity<Trade> response2 = restTemplate.withBasicAuth("user1", "user1")
        .postForEntity(uri, jsonObject2, Trade.class);

        assertEquals(400, response2.getStatusCode().value());
    }

    @Test
    public void createMarketSellTrade_InvalidAskPrice() throws Exception{
        URI uri = new URI(baseUrl + port + "/trades");
        Customer customer = customerRepository.save(new Customer("user1", encoder.encode("user1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));
   
        Account acc = accountRepository.save(new Account(null, customer, customer.getCustomerId(), 80000.0, 80000.0));

        CustomStock customStock = stockRepository.save(new CustomStock("A17U", 2.5, 20000, 2.5, 20000, 2.7));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "buy");
        jsonObject.put("symbol", customStock.getSymbol());
        jsonObject.put("quantity", 1000);
        jsonObject.put("bid", 0.0);
        jsonObject.put("account_id", acc.getAccountID());

        ResponseEntity<Trade> response = restTemplate.withBasicAuth("user1", "user1")
        .postForEntity(uri, jsonObject, Trade.class);

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("action", "sell");
        jsonObject2.put("symbol", customStock.getSymbol());
        jsonObject2.put("quantity", 1000);
        jsonObject2.put("ask", -1);
        jsonObject2.put("account_id", acc.getAccountID());

        ResponseEntity<Trade> response2 = restTemplate.withBasicAuth("user1", "user1")
        .postForEntity(uri, jsonObject2, Trade.class);

        assertEquals(400, response2.getStatusCode().value());
    }

    @Test
    public void createMarketSellTrade_InvalidStockSymbol() throws Exception{
        URI uri = new URI(baseUrl + port + "/trades");
        Customer customer = customerRepository.save(new Customer("user1", encoder.encode("user1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));
   
        Account acc = accountRepository.save(new Account(null, customer, customer.getCustomerId(), 80000.0, 80000.0));

        CustomStock customStock = stockRepository.save(new CustomStock("A17U", 2.5, 20000, 2.5, 20000, 2.7));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "buy");
        jsonObject.put("symbol", customStock.getSymbol());
        jsonObject.put("quantity", 1000);
        jsonObject.put("bid", 0.0);
        jsonObject.put("account_id", acc.getAccountID());

        ResponseEntity<Trade> response = restTemplate.withBasicAuth("user1", "user1")
        .postForEntity(uri, jsonObject, Trade.class);

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("action", "sell");
        jsonObject2.put("symbol", "123");
        jsonObject2.put("quantity", 100);
        jsonObject2.put("ask", 0.0);
        jsonObject2.put("account_id", acc.getAccountID());

        ResponseEntity<Trade> response2 = restTemplate.withBasicAuth("user1", "user1")
        .postForEntity(uri, jsonObject2, Trade.class);

        assertEquals(404, response2.getStatusCode().value());
    }

    @Test
    public void createLimitBuyTrade_Success() throws Exception{

        URI uri = new URI(baseUrl + port + "/trades");
        Customer customer = customerRepository.save(new Customer("user1", encoder.encode("user1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));
   
        Account acc = accountRepository.save(new Account(null, customer, customer.getCustomerId(), 80000.0, 80000.0));

        CustomStock customStock = stockRepository.save(new CustomStock("A17U", 2.5, 20000, 2.5, 20000, 2.7));


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "buy");
        jsonObject.put("symbol", customStock.getSymbol());
        jsonObject.put("quantity", 100);
        jsonObject.put("bid", 2.6);
        jsonObject.put("account_id", acc.getAccountID());

        ResponseEntity<Trade> response = restTemplate.withBasicAuth("user1", "user1")
        .postForEntity(uri, jsonObject, Trade.class);
        assertEquals(201, response.getStatusCode().value());
       
    }

    @Test
    public void createLimitBuyTrade_InvalidCustomer() throws Exception{

        URI uri = new URI(baseUrl + port + "/trades");
        Customer customer = customerRepository.save(new Customer("user1", encoder.encode("user1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));

   
        Account acc = accountRepository.save(new Account(null, customer, customer.getCustomerId(), 80000.0, 80000.0));


        CustomStock customStock = stockRepository.save(new CustomStock("A17U", 2.5, 20000, 2.5, 20000, 2.7));


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "buy");
        jsonObject.put("symbol", customStock.getSymbol());
        jsonObject.put("quantity", 100);
        jsonObject.put("bid", 2.6);
        jsonObject.put("account_id", acc.getAccountID());

        ResponseEntity<Trade> response = restTemplate.withBasicAuth("user2", "user1")
        .postForEntity(uri, jsonObject, Trade.class);
        assertEquals(401, response.getStatusCode().value());
       
    }

    @Test
    public void createLimitBuyTrade_InsufficientQuantity() throws Exception{

        URI uri = new URI(baseUrl + port + "/trades");
        Customer customer = customerRepository.save(new Customer("user1", encoder.encode("user1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));
   
        Account acc = accountRepository.save(new Account(null, customer, customer.getCustomerId(), 800.0, 800.0));

        CustomStock customStock = stockRepository.save(new CustomStock("A17U", 2.5, 20000, 2.5, 20000, 2.7));


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "buy");
        jsonObject.put("symbol", customStock.getSymbol());
        jsonObject.put("quantity", 10000);
        jsonObject.put("bid", 2.6);
        jsonObject.put("account_id", acc.getAccountID());

        ResponseEntity<Trade> response = restTemplate.withBasicAuth("user1", "user1")
        .postForEntity(uri, jsonObject, Trade.class);
        assertEquals(400, response.getStatusCode().value());
       
    }

    @Test
    public void createLimitBuyTrade_InvalidQuantity() throws Exception{

        URI uri = new URI(baseUrl + port + "/trades");
        Customer customer = customerRepository.save(new Customer("user1", encoder.encode("user1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));
   
        Account acc = accountRepository.save(new Account(null, customer, customer.getCustomerId(), 80000.0, 80000.0));

        CustomStock customStock = stockRepository.save(new CustomStock("A17U", 2.5, 20000, 2.5, 20000, 2.7));


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "buy");
        jsonObject.put("symbol", customStock.getSymbol());
        jsonObject.put("quantity", -10);
        jsonObject.put("bid", 2.6);
        jsonObject.put("account_id", acc.getAccountID());

        ResponseEntity<Trade> response = restTemplate.withBasicAuth("user1", "user1")
        .postForEntity(uri, jsonObject, Trade.class);
        assertEquals(400, response.getStatusCode().value());
       
    }

    @Test
    public void createLimitBuyTrade_Open() throws Exception{

        URI uri = new URI(baseUrl + port + "/trades");
        Customer customer = customerRepository.save(new Customer("user1", encoder.encode("user1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));
   
        Account acc = accountRepository.save(new Account(null, customer, customer.getCustomerId(), 80000.0, 80000.0));

        CustomStock customStock = stockRepository.save(new CustomStock("A17U", 2.5, 20000, 2.5, 20000, 2.7));
        


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "buy");
        jsonObject.put("symbol", customStock.getSymbol());
        jsonObject.put("quantity", 1000);
        jsonObject.put("bid", (customStock.getAsk() - 1));
        jsonObject.put("account_id", acc.getAccountID());

        ResponseEntity<Trade> response = restTemplate.withBasicAuth("user1", "user1")
        .postForEntity(uri, jsonObject, Trade.class);
        assertEquals(201, response.getStatusCode().value());
        assertEquals("open", response.getBody().getStatus());
       
    }
  


    @Test
    public void getTrade_Success() throws Exception {
        Customer customer = customerRepository.save(new Customer("user1", encoder.encode("goodpassword1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));

        Account acc = accountRepository.save(new Account(customer.getCustomerId(), 80000.0, 80000.0));

        long currentTimestamp = Instant.now().getEpochSecond();
        Trade trade = tradeRepository.save(new Trade("buy", "A17U", 2000, 0.0,0.0, 0.0, 0, currentTimestamp, acc.getAccountID(), customer.getCustomerId(), "open", 0.0));

        URI uri = new URI(baseUrl + port + "/trades/" + trade.getId());

        ResponseEntity<Object> response = restTemplate.withBasicAuth("user1", "goodpassword1")
        .getForEntity(uri, Object.class);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void getTrade_NotFound() throws Exception {
        customerRepository.save(new Customer("user1", encoder.encode("goodpassword1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));

        URI uri = new URI(baseUrl + port + "/trades/2");

        ResponseEntity<Object> response = restTemplate.withBasicAuth("user1", "goodpassword1")
        .getForEntity(uri, Object.class);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void getTrade_UnauthorizeCustomer() throws Exception {
        Customer customer = customerRepository.save(new Customer("user1", encoder.encode("goodpassword1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));

        Customer customer2 = customerRepository.save(new Customer("user2", encoder.encode("goodpassword2"), "customer 2", "S9839006E",
        "91251235", "Dog House", "ROLE_USER", true));

        Account acc = accountRepository.save(new Account(customer.getCustomerId(), 80000.0, 80000.0));

        long currentTimestamp = Instant.now().getEpochSecond();
        Trade trade = tradeRepository.save(new Trade("buy", "A17U", 2000, 0.0,0.0, 0.0, 0, currentTimestamp, acc.getAccountID(), customer.getCustomerId(), "open", 0.0));

        URI uri = new URI(baseUrl + port + "/trades/" + trade.getId());

        ResponseEntity<Object> response = restTemplate.withBasicAuth("user2", "goodpassword2")
        .getForEntity(uri, Object.class);

        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    public void getTrade_invalidCustomer() throws Exception {
        Customer customer = customerRepository.save(new Customer("user1", encoder.encode("goodpassword1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));

        Account acc = accountRepository.save(new Account(customer.getCustomerId(), 80000.0, 80000.0));

        long currentTimestamp = Instant.now().getEpochSecond();
        Trade trade = tradeRepository.save(new Trade("buy", "A17U", 2000, 0.0,0.0, 0.0, 0, currentTimestamp, acc.getAccountID(), customer.getCustomerId(), "open", 0.0));

        URI uri = new URI(baseUrl + port + "/trades/" + trade.getId());

        ResponseEntity<Object> response = restTemplate.withBasicAuth("user2", "goodpassword2")
        .getForEntity(uri, Object.class);

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    public void cancelledTrade_Successful() throws Exception {
        //For updates: We MUST create the HTTP headers with the new trade as the body 

        Customer customer = customerRepository.save(new Customer("user1", encoder.encode("goodpassword1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));


        Account acc = accountRepository.save(new Account(customer.getCustomerId(), 80000.0, 80000.0));

        long currentTimestamp = Instant.now().getEpochSecond();

        Trade trade1 = tradeRepository.save(new Trade ("buy", "A17U", 2000, 0.0,0.0, 0.0, 0, currentTimestamp, acc.getAccountID(), customer.getCustomerId(), "open", 0.0));


        // - IMPORTANT: Import the correct Http package - import org.springframework.http.HttpEntity;
        HttpHeaders headers = new HttpHeaders();
        
        headers.setContentType(MediaType.APPLICATION_JSON);
        

        Trade trade2 = tradeRepository.save(new Trade ("buy", "A17U", 2000, 0.0,0.0, 0.0, 0, currentTimestamp, acc.getAccountID(), customer.getCustomerId(), "cancelled", 0.0));
        HttpEntity<Trade> requestEntity = new HttpEntity<>(trade2,headers);

        URI uri = new URI(baseUrl + port + "/trades/" + trade1.getId());

        ResponseEntity<Trade> result = restTemplate.withBasicAuth("user1", "goodpassword1")
        .exchange(uri, HttpMethod.PUT, requestEntity , Trade.class);

        
        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result);
        assertEquals(trade2.getStatus(), result.getBody().getStatus());
        assertEquals(trade1.getId(), result.getBody().getId());

    }

    @Test
    public void cancelledTrade_unSuccessful() throws Exception {
        //For updates: We MUST create the HTTP headers with the new trade as the body 

        Customer customer = customerRepository.save(new Customer("user1", encoder.encode("goodpassword1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));

        Customer customer2 = customerRepository.save(new Customer("user2", encoder.encode("goodpassword2"), "customer 2", "S9839006E",
        "91251235", "Dog House", "ROLE_USER", true));


        Account acc = accountRepository.save(new Account(customer.getCustomerId(), 80000.0, 80000.0));

        long currentTimestamp = Instant.now().getEpochSecond();

        Trade trade1 = tradeRepository.save(new Trade ("buy", "A17U", 1000, 0.0, 0.0, 3.0, 1000, currentTimestamp, acc.getAccountID(), customer.getCustomerId(), "partial-filled", 3.0));


        // - IMPORTANT: Import the correct Http package - import org.springframework.http.HttpEntity;
        HttpHeaders headers = new HttpHeaders();
        
        headers.setContentType(MediaType.APPLICATION_JSON);
        

        Trade trade2 = tradeRepository.save(new Trade ("buy", "A17U", 1000, 0.0, 0.0, 3.0, 1000, currentTimestamp, acc.getAccountID(), customer.getCustomerId(), "cancelled", 3.0));
        HttpEntity<Trade> requestEntity = new HttpEntity<>(trade2,headers);

        URI uri = new URI(baseUrl + port + "/trades/" + trade1.getId());
  

        ResponseEntity<Trade> result = restTemplate.withBasicAuth("user1", "goodpassword1")
        .exchange(uri, HttpMethod.PUT, requestEntity , Trade.class);
        

        assertEquals(400, result.getStatusCode().value());

    }


    @Test
    public void cancelledTrade_UnauthorizeCustomer() throws Exception {
        //For updates: We MUST create the HTTP headers with the new trade as the body 

        Customer customer = customerRepository.save(new Customer("user1", encoder.encode("goodpassword1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));

        Customer customer2 = customerRepository.save(new Customer("user2", encoder.encode("goodpassword2"), "customer 2", "S9839006E",
        "91251235", "Dog House", "ROLE_USER", true));

        Account acc = accountRepository.save(new Account(customer.getCustomerId(), 80000.0, 80000.0));

        long currentTimestamp = Instant.now().getEpochSecond();

        Trade trade = tradeRepository.save(new Trade("buy", "A17U", 2000, 0.0,0.0, 0.0, 0, currentTimestamp, acc.getAccountID(), customer.getCustomerId(), "open", 0.0));
    
        // - IMPORTANT: Import the correct Http package - import org.springframework.http.HttpEntity;
        HttpHeaders headers = new HttpHeaders();
        
        headers.setContentType(MediaType.APPLICATION_JSON);
        

        Trade trade2 = tradeRepository.save(new Trade("buy", "A17U", 2000, 0.0,0.0, 0.0, 0, currentTimestamp, acc.getAccountID(), customer.getCustomerId(), "cancelled", 0.0));
        HttpEntity<Trade> requestEntity = new HttpEntity<>(trade2,headers);


        URI uri = new URI(baseUrl + port + "/trades/" + trade.getId());

        ResponseEntity<Trade> result = restTemplate.withBasicAuth("user2", "goodpassword2")
        .exchange(uri, HttpMethod.PUT, requestEntity , Trade.class);
        
        assertEquals(403, result.getStatusCode().value());

    }

    @Test
    public void cancelledTrade_NotFound() throws Exception {
            //For updates: We MUST create the HTTP headers with the new trade as the body 

            Customer customer = customerRepository.save(new Customer("user1", encoder.encode("goodpassword1"), "Woofy Dog", "S8529649C",
            "91251234", "Dog House", "ROLE_USER", true));
    
            Account acc = accountRepository.save(new Account(customer.getCustomerId(), 80000.0, 80000.0));
    
            long currentTimestamp = Instant.now().getEpochSecond();
    
            Trade trade = tradeRepository.save(new Trade("buy", "A17U", 2000, 0.0,0.0, 0.0, 0, currentTimestamp, acc.getAccountID(), customer.getCustomerId(), "open", 0.0));
        
            // - IMPORTANT: Import the correct Http package - import org.springframework.http.HttpEntity;
            HttpHeaders headers = new HttpHeaders();
            
            headers.setContentType(MediaType.APPLICATION_JSON);
            
    
            Trade trade2 = tradeRepository.save(new Trade("buy", "A17U", 2000, 0.0,0.0, 0.0, 0, currentTimestamp, acc.getAccountID(), customer.getCustomerId(), "cancelled", 0.0));
            HttpEntity<Trade> requestEntity = new HttpEntity<>(trade2,headers);
    
    
            URI uri = new URI(baseUrl + port + "/trades/100");
    
            ResponseEntity<Trade> result = restTemplate.withBasicAuth("user1", "goodpassword1")
            .exchange(uri, HttpMethod.PUT, requestEntity , Trade.class);
            
            assertEquals(404, result.getStatusCode().value());

    }

    @Test
    public void cancelledTrade_InvalidCustomer() throws Exception {
        //For updates: We MUST create the HTTP headers with the new trade as the body 

        Customer customer = customerRepository.save(new Customer("user1", encoder.encode("goodpassword1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));

        Account acc = accountRepository.save(new Account(customer.getCustomerId(), 80000.0, 80000.0));

        long currentTimestamp = Instant.now().getEpochSecond();

        Trade trade = tradeRepository.save(new Trade("buy", "A17U", 2000, 0.0,0.0, 0.0, 0, currentTimestamp, acc.getAccountID(), customer.getCustomerId(), "open", 0.0));
    
        // - IMPORTANT: Import the correct Http package - import org.springframework.http.HttpEntity;
        HttpHeaders headers = new HttpHeaders();
        
        headers.setContentType(MediaType.APPLICATION_JSON);
        

        Trade trade2 = tradeRepository.save(new Trade("buy", "A17U", 2000, 0.0,0.0, 0.0, 0, currentTimestamp, acc.getAccountID(), customer.getCustomerId(), "cancelled", 0.0));
        HttpEntity<Trade> requestEntity = new HttpEntity<>(trade2,headers);


        URI uri = new URI(baseUrl + port + "/trades/" + trade.getId());

        ResponseEntity<Trade> result = restTemplate.withBasicAuth("user2", "goodpassword2")
        .exchange(uri, HttpMethod.PUT, requestEntity , Trade.class);
        
        assertEquals(401, result.getStatusCode().value());

    }


    
}
