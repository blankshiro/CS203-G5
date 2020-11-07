package com.cs203t5.ryverbank.account_transaction;

import org.assertj.core.api.ObjectAssert;
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

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;



import com.cs203t5.ryverbank.customer.CustomerRepository;


import net.minidev.json.JSONObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.ArrayList;
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
    public void getUserAccount_Success() throws Exception{
        Customer customer = customers.save(new Customer("user1", encoder.encode("goodpassword1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));

        Account acc = accounts.save(new Account(customer.getCustomerId(), 8000.0, 1000.0));
        URI uri = new URI(baseURl + port + "/accounts/" + acc.getAccountID());

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

        assertEquals(201, response.getStatusCode().value());
        assertEquals(1, accounts.count());
    }

    //when available balance > balance, should be autoadjusted to equal to balance
    @Test
    public void createAccountWrongAvailBalance_CorrectedBalance() throws Exception{
        Customer customer = customers.save(new Customer("user1", encoder.encode("goodpassword1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));

        customers.save(new Customer("manager", encoder.encode("goodpassword"), "Hayashi", "S9839006E", "91251235",
        "Isekai", "ROLE_MANAGER", true));

        URI uri = new URI(baseURl + port + "/accounts");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("customer_id", customer.getCustomerId());
        jsonObject.put("balance", 6000.0);
        jsonObject.put("available_balance", 9000.0);

        ResponseEntity<Account> response = restTemplate.withBasicAuth("manager", "goodpassword")
        .postForEntity(uri, jsonObject, Account.class);

        Account account = response.getBody();

        assertEquals(201, response.getStatusCode().value());
        assertTrue(account.getBalance() >= account.getAvailableBalance());

    }

    @Test
    public void createAccount_CustomerNotFound()throws Exception {
        customers.save(new Customer("manager", encoder.encode("goodpassword"), "Hayashi", "S9839006E", "91251235",
        "Isekai", "ROLE_MANAGER", true));

        URI uri = new URI(baseURl + port + "/accounts");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("customer_id", 15L);
        jsonObject.put("balance", 6000.0);
        jsonObject.put("available_balance", 9000.0);

        ResponseEntity<Object> response = restTemplate.withBasicAuth("manager", "goodpassword")
        .postForEntity(uri, jsonObject, Object.class);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    public void getAccount_AccountNotFound() throws Exception {
        customers.save(new Customer("user1", encoder.encode("goodpassword1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));

        URI uri = new URI(baseURl + port + "/accounts/2");

        ResponseEntity<Object> response = restTemplate.withBasicAuth("user1", "goodpassword1")
        .getForEntity(uri, Object.class);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void getAccount_Unauthorized()throws Exception {
        Customer customer1 = customers.save(new Customer("user1", encoder.encode("goodpassword1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));

        customers.save(new Customer("user2", encoder.encode("goodpassword1"), "Woofy Dog", "S1539649C",
        "95451234", "Dog House", "ROLE_USER", true));

        Account acc = accounts.save(new Account(customer1.getCustomerId(), 8000.0, 1000.0));

        URI uri = new URI(baseURl + port + "/accounts/" + acc.getAccountID());

        ResponseEntity<Object> response = restTemplate.withBasicAuth("user2", "goodpassword1")
        .getForEntity(uri, Object.class);

        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    public void createAccount_Forbidden() throws Exception{
        Customer customer = customers.save(new Customer("user1", encoder.encode("goodpassword1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("customer_id", customer.getCustomerId());
        jsonObject.put("balance", 6000.0);
        jsonObject.put("available_balance", 9000.0);

        URI uri = new URI(baseURl + port + "/accounts");

        ResponseEntity<Object> response = restTemplate.withBasicAuth("user1", "goodpassword1")
        .postForEntity(uri, jsonObject, Object.class);

        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    public void getTransaction_Success() throws Exception {
        Customer customer1 = customers.save(new Customer("user1", encoder.encode("goodpassword1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));

        Customer customer2 = customers.save(new Customer("user2", encoder.encode("goodpassword1"), "Woofy Dog", "S1539649C",
        "95451234", "Dog House", "ROLE_USER", true));

        Account acc1 = accounts.save(new Account(customer1.getCustomerId(), 8000.0, 1000.0));
        Account acc2 = accounts.save(new Account(customer2.getCustomerId(), 8000.0, 1000.0));

        transactions.save(new Transaction(acc1.getAccountID(), acc2.getAccountID(), 500.0));
        transactions.save(new Transaction(acc2.getAccountID(), acc1.getAccountID(), 600.0));

        URI uri = new URI(baseURl + port + "/accounts/" + acc1.getAccountID() + "/transactions");

        ResponseEntity<Transaction[]> response = restTemplate.withBasicAuth("user1", "goodpassword1")
        .getForEntity(uri, Transaction[].class);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, transactions.count());
        assertEquals(2, response.getBody().length);
    }

    @Test
    public void getTransaction_AccountNotFound() throws Exception {

        Customer customer1 = customers.save(new Customer("user1", encoder.encode("goodpassword1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));

        Customer customer2 = customers.save(new Customer("user2", encoder.encode("goodpassword1"), "Woofy Dog", "S1539649C",
        "95451234", "Dog House", "ROLE_USER", true));

        accounts.save(new Account(customer1.getCustomerId(), 8000.0, 1000.0));
        accounts.save(new Account(customer2.getCustomerId(), 8000.0, 1000.0));

        URI uri = new URI(baseURl + port + "/accounts/" + 3 + "/transactions");

        ResponseEntity<Object> response = restTemplate.withBasicAuth("user1", "goodpassword1")
        .getForEntity(uri, Object.class);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void getTransaction_CustomerUnauthorized() throws Exception{
        
        Customer customer1 = customers.save(new Customer("user1", encoder.encode("goodpassword1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));

        Customer customer2 = customers.save(new Customer("user2", encoder.encode("goodpassword1"), "Woofy Dog", "S1539649C",
        "95451234", "Dog House", "ROLE_USER", true));

        accounts.save(new Account(customer1.getCustomerId(), 8000.0, 1000.0));
        Account acc = accounts.save(new Account(customer2.getCustomerId(), 8000.0, 1000.0));

        URI uri = new URI(baseURl + port + "/accounts/" + acc.getAccountID() + "/transactions");

        ResponseEntity<Object> response = restTemplate.withBasicAuth("user1", "goodpassword1")
        .getForEntity(uri, Object.class);

        assertEquals(403, response.getStatusCode().value());
    }


    @Test
    public void addTransaction_Success() throws Exception {
        Customer customer1 = customers.save(new Customer("user1", encoder.encode("goodpassword1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));

        Customer customer2 = customers.save(new Customer("user2", encoder.encode("goodpassword1"), "Woofy Dog", "S1539649C",
        "95451234", "Dog House", "ROLE_USER", true));

        Account acc1 = accounts.save(new Account(customer1.getCustomerId(), 8000.0, 1000.0));
        Account acc2 = accounts.save(new Account(customer2.getCustomerId(), 8000.0, 1000.0));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from", acc1.getAccountID());
        jsonObject.put("to", acc2.getAccountID());
        jsonObject.put("amount", 300.0);

        URI uri = new URI(baseURl + port + "/accounts/" + acc1.getAccountID() + "/transactions");
        ResponseEntity<Transaction> response = restTemplate.withBasicAuth("user1", "goodpassword1")
        .postForEntity(uri, jsonObject, Transaction.class);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(1, transactions.count());
    }

    @Test
    public void addTransaction_CustomerUnauthorized() throws Exception {
        Customer customer1 = customers.save(new Customer("user1", encoder.encode("goodpassword1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));

        Customer customer2 = customers.save(new Customer("user2", encoder.encode("goodpassword1"), "Woofy Dog", "S1539649C",
        "95451234", "Dog House", "ROLE_USER", true));

        Account acc1 = accounts.save(new Account(customer1.getCustomerId(), 8000.0, 1000.0));
        Account acc2 = accounts.save(new Account(customer2.getCustomerId(), 8000.0, 1000.0));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from", acc1.getAccountID());
        jsonObject.put("to", acc2.getAccountID());
        jsonObject.put("amount", 300.0);

        URI uri = new URI(baseURl + port + "/accounts/" + acc1.getAccountID() + "/transactions");

        ResponseEntity<Object> response = restTemplate.withBasicAuth("user2", "goodpassword1")
        .postForEntity(uri, jsonObject, Object.class);

        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    public void addTransaction_AccountNotFound() throws Exception {
        Customer customer1 = customers.save(new Customer("user1", encoder.encode("goodpassword1"), "Woofy Dog", "S8529649C",
        "91251234", "Dog House", "ROLE_USER", true));

        customers.save(new Customer("user2", encoder.encode("goodpassword1"), "Woofy Dog", "S1539649C",
        "95451234", "Dog House", "ROLE_USER", true));

        Account acc1 = accounts.save(new Account(customer1.getCustomerId(), 8000.0, 1000.0));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from", acc1.getAccountID());
        jsonObject.put("to", 2);
        jsonObject.put("amount", 300.0);

        URI uri = new URI(baseURl + port + "/accounts/" + acc1.getAccountID() + "/transactions");
        ResponseEntity<Object> response = restTemplate.withBasicAuth("user1", "goodpassword1")
        .postForEntity(uri, jsonObject, Object.class);

        assertEquals(404, response.getStatusCode().value());
    }
}
