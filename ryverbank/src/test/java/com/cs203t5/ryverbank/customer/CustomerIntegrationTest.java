package com.cs203t5.ryverbank.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CustomerIntegrationTest {
        @LocalServerPort
        private int port;

        private final String baseUrl = "http://localhost:";

        @Autowired
        private TestRestTemplate restTemplate;

        @Autowired
        private CustomerRepository users;

        @Autowired
        private BCryptPasswordEncoder encoder;

        @BeforeEach
        void addManager() {
                users.save(new Customer("manager", encoder.encode("goodpassword"), "Hayashi", "S9839006E", "91251235",
                                "Isekai", "ROLE_MANAGER", true));
        }

        @AfterEach
        void tearDown() {
                users.deleteAll();
        }

        @Test
        public void getCustomers_Success() throws Exception {
                URI uri = new URI(baseUrl + port + "/customers");

                Customer user = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "91251234",
                                "White House", "ROLE_USER", true);

                users.save(user);
                
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setContentType(MediaType.APPLICATION_JSON);
                requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                HttpEntity<Customer[]> requestEntity = new HttpEntity<Customer[]>(null, requestHeaders);

                

                // ResponseEntity<Customer[]> result = restTemplate.withBasicAuth("manager", "goodpassword")
                //                 .getForEntity(uri, Customer[].class);

                ResponseEntity<Customer[]> result = restTemplate.exchange(uri, HttpMethod.GET,
                                requestEntity, Customer[].class);

                Customer[] users = result.getBody();

                assertEquals(200, result.getStatusCode().value());
                assertEquals(2, users.length);
        }

        @Test
        public void getCustomer_ValidCustomerId_Success() throws Exception {
                Customer user = new Customer("user1", "goodpassword1", "Hibiki", "S8529649C", "91251234", "White House",
                                "ROLE_USER", true);

                users.save(user);

                Long id = user.getId();

                URI uri = new URI(baseUrl + port + "/customers/" + id);

                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setContentType(MediaType.APPLICATION_JSON);
                requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

                HttpEntity<Customer> requestEntity = new HttpEntity<Customer>(null, requestHeaders);
                
                ResponseEntity<Customer> result = restTemplate.exchange(uri, HttpMethod.GET, requestEntity,
                                Customer.class);

               // ResponseEntity<Customer> result = restTemplate.withBasicAuth("manager1", "goodpassword1")
                               // .getForEntity(uri, Customer.class);

                assertEquals(200, result.getStatusCode().value());
                assertEquals(user.getUsername(), result.getBody().getUsername());
        }

        @Test
        public void updateStatus_Valid_Success() throws Exception {
                Customer user = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "91251234",
                                "White House", "ROLE_USER", true);
                Long id = users.save(user).getId();

                Customer newUserInfo = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "91251234",
                                "White House", "ROLE_USER", false);

                URI uri = new URI(baseUrl + port + "/customers/" + id);

                ResponseEntity<Customer> result = restTemplate.withBasicAuth("admin", "goodpassword").exchange(uri,
                                HttpMethod.PUT, new HttpEntity<>(newUserInfo), Customer.class);

                assertEquals(200, result.getStatusCode().value());
                assertEquals(newUserInfo.getUsername(), result.getBody().getUsername());
        }
}
