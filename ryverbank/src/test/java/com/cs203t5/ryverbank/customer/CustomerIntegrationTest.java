package com.cs203t5.ryverbank.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CustomerIntegrationTest {
        @LocalServerPort
        private int port;

        private static final ObjectMapper om = new ObjectMapper();

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

        private static void printJSON(Object object) {
                String result;
                try {
                        result = om.writerWithDefaultPrettyPrinter().writeValueAsString(object);
                        System.out.println(result);
                } catch (JsonProcessingException e) {
                        e.printStackTrace();
                }
        }

        @Test
        public void createCustomer_Success() throws Exception {

        }

        @Test
        public void createCustomer_Failure() throws Exception {

        }

        @Test
        public void getCustomers_Success() throws Exception {
                URI uri = new URI(baseUrl + port + "/customers");

                Customer user = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "91251234",
                                "White House", "ROLE_USER", true);

                users.save(user);

                ResponseEntity<String> response = restTemplate.withBasicAuth("manager", "goodpassword")
                                .getForEntity(uri, String.class);

                printJSON(response);

                assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
                assertEquals(HttpStatus.OK, response.getStatusCode());

                // ResponseEntity<Customer[]> result = restTemplate.withBasicAuth("manager",
                // "goodpassword")
                // .getForEntity(uri, Customer[].class);

                // Customer[] users = result.getBody();

                // assertEquals(200, result.getStatusCode().value());
                // assertEquals(2, users.length);
        }

        @Test
        public void getCustomer_ValidCustomerId_Success() throws Exception {
                Customer user = new Customer("user1", "goodpassword1", "Hibiki", "S8529649C", "91251234", "White House",
                                "ROLE_USER", true);

                users.save(user);

                Long id = user.getId();

                URI uri = new URI(baseUrl + port + "/customers/" + id);

                ResponseEntity<String> response = restTemplate.withBasicAuth("manager", "goodpassword")
                                .getForEntity(uri, String.class);

                printJSON(response);

                assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
                assertEquals(HttpStatus.OK, response.getStatusCode());

                // ResponseEntity<Customer> result = restTemplate.withBasicAuth("manager1",
                // "goodpassword1")
                // .getForEntity(uri, Customer.class);

                // assertEquals(200, result.getStatusCode().value());
                // assertEquals(user.getUsername(), result.getBody().getUsername());
        }

        @Test
        public void updateUser_Valid_Success() throws Exception {

        }

        @Test
        public void updateUser_Invalid_Failure() throws Exception {

        }

}
