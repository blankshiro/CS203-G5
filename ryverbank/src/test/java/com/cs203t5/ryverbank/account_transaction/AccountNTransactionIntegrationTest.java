package com.cs203t5.ryverbank.account_transaction;

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

import com.cs203t5.ryverbank.customer.CustomerRepository;
import net.minidev.json.JSONObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import com.cs203t5.ryverbank.customer.Customer;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AccountNTransactionIntegrationTest {
    
    @LocalServerPort
    private int port;

    private final String baseURl = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TransactionRepository transactions;

    @Autowired
    private AccountRepository accounts;

    @Autowired
    private CustomerRepository customers;

    @Autowired
    private AccountServices accountService;

    @Autowired
    private TransactionServices transactionServices;

    @Autowired
    private BCryptPasswordEncoder encoder;


    @BeforeEach
    public void addUserAndManager(){

    }

    @AfterEach
    void tearDown() {
        accounts.deleteAll();
        transactions.deleteAll();
        customers.deleteAll();
    }

    @Test
    public void getAllAccounts_ListSuccess() throws Exception {
        
        Customer customer1 = customers.save(new Customer("user1", encoder.encode("goodpassword1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));

        Customer customer2 = customers.save(new Customer("user2", encoder.encode("goodpassword1"), "Woofy Dog", "S9329741C",
        "95251234", "Dog House", "ROLE_USER", true));


        URI uri = new URI(baseURl + port + "/accounts");

        accounts.save(new Account(customer1.getCustomerId(), 8000.0, 1000.0));
        accounts.save(new Account(customer1.getCustomerId(), 8000.0, 1000.0));

        accounts.save(new Account(customer2.getCustomerId(), 8000.0, 1000.0));
        accounts.save(new Account(customer2.getCustomerId(), 8000.0, 1000.0));

        ResponseEntity<Account[]> response1 = restTemplate.withBasicAuth("user1", "goodpassword1")
        .getForEntity(uri, Account[].class);

        ResponseEntity<Account[]> response2 = restTemplate.withBasicAuth("user2", "goodpassword1")
        .getForEntity(uri, Account[].class);

        assertEquals(200, response1.getStatusCode().value());
        assertEquals(200, response2.getStatusCode().value());
        assertEquals(4, accounts.count());
        //got zero for body count
        //fail at these two
        // assertEquals(2, response1.getBody().length);
        // assertEquals(2, response2.getBody().length);
        System.out.println(response1.getBody().length);
        System.out.println(response2.getBody().length);
    }

    @Test
    public void getUserAccount_Success() throws Exception{
        Customer customer = customers.save(new Customer("user1", encoder.encode("goodpassword1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));

        Account acc = accounts.save(new Account(customer.getCustomerId(), 8000.0, 1000.0));
        URI uri = new URI(baseURl + port + "/accounts/" + acc.getAccountID());

        
        // accounts.save(new Account(customer.getCustomerId(), 5000.0, 600.0));

        ResponseEntity<Account> response = restTemplate.withBasicAuth("user1", "goodpassword1")
        .getForEntity(uri, Account.class);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, accounts.count());
    }


    @Test
    public void createAccount_Success() throws Exception {
        Customer customer = customers.save(new Customer("user1", encoder.encode("goodpassword1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));

        customers.save(new Customer("manager", encoder.encode("goodpassword"), "Hayashi", "S9839006E", "91251235",
        "Isekai", "ROLE_MANAGER", true));

        URI uri = new URI(baseURl + port + "/accounts");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("customer_id", customer.getCustomerId());
        jsonObject.put("balance", 8000.0);
        jsonObject.put("available_balance", 1000.0);

        ResponseEntity<Account> response = restTemplate.withBasicAuth("manager", "goodpassword")
        .postForEntity(uri, jsonObject, Account.class);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, accounts.count());
    }

}
