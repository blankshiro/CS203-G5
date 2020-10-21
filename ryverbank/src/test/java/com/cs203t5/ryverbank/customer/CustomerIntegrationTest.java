package com.cs203t5.ryverbank.customer;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import net.minidev.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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

        @Test
        public void createCustomer_Success() throws Exception {
                URI uri = new URI(baseUrl + port + "/customers");

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", "user1");
                jsonObject.put("password", encoder.encode("goodpassword1"));
                jsonObject.put("full_name", "Woofy Dog");
                jsonObject.put("nric", "S8529649C");
                jsonObject.put("phone", "91251234");
                jsonObject.put("address", "Dog House");
                jsonObject.put("authorities", "ROLE_USER");
                jsonObject.put("active", "true");

                ResponseEntity<Object> result = restTemplate.withBasicAuth("manager", "goodpassword").postForEntity(uri,
                                jsonObject, Object.class);

                System.out.println(result.getBody());

                assertEquals(201, result.getStatusCode().value());
                assertEquals(2, users.count());
        }

        @Test
        public void createCustomer_Failure() throws Exception {
                URI uri = new URI(baseUrl + port + "/customers");

                Customer user1 = new Customer("user1", encoder.encode("goodpassword1"), "Woofy Dog", "S8529649C",
                                "91251234", "Dog House", "ROLE_USER", true);

                users.save(user1);

                Customer user2 = new Customer("user2", "goodpassword2", "Kitty Cat", "S9889644I", "91251236",
                                "Cat House", "ROLE_USER", true);

                ResponseEntity<Object> result = restTemplate.withBasicAuth("user1", "goodpassword1").postForEntity(uri,
                                user2, Object.class);

                assertEquals(403, result.getStatusCode().value());
                assertEquals(2, users.count());
        }

        @Test
        public void getCustomers_Success() throws Exception {
                URI uri = new URI(baseUrl + port + "/customers");

                Customer user = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "91251234",
                                "White House", "ROLE_USER", true);

                users.save(user);

                ResponseEntity<Object[]> result = restTemplate.withBasicAuth("manager", "goodpassword")
                                .getForEntity(uri, Object[].class);

                Object[] users = result.getBody();

                assertEquals(200, result.getStatusCode().value());
                assertEquals(2, users.length);
        }

        @Test
        public void getCustomer_ValidCustomerId_Success() throws Exception {
                Customer user = new Customer("user1", "goodpassword1", "Hibiki", "S8529649C", "91251234", "White House",
                                "ROLE_USER", true);

                users.save(user);

                Long id = user.getCustomerId();

                URI uri = new URI(baseUrl + port + "/customers/" + id);

                ResponseEntity<Object> result = restTemplate.withBasicAuth("manager", "goodpassword").getForEntity(uri,
                                Object.class);

                String jsonString = om.writeValueAsString(result.getBody());
                System.out.println("JSONSTRING = " + jsonString);

                JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);

                assertEquals(200, result.getStatusCode().value());
                assertEquals(user.getUsername(), jsonObject.get("username").getAsString());
        }

        @Test
        public void getCustomer_InvalidCustomerId_Failure() throws Exception {
                URI uri = new URI(baseUrl + port + "/customers/999");

                ResponseEntity<Object> result = restTemplate.withBasicAuth("manager", "goodpassword").getForEntity(uri,
                                Object.class);

                assertEquals(404, result.getStatusCode().value());
        }

        @Test
        public void updateUser_Valid_Success() throws Exception {
                Customer user = new Customer("user1", "goodpassword1", "Hibiki", "S8529649C", "91251234", "White House",
                                "ROLE_USER", true);

                users.save(user);

                Long id = user.getCustomerId();

                URI uri = new URI(baseUrl + port + "/customers/" + id);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", "user1");
                jsonObject.put("password", encoder.encode("goodpassword1"));
                jsonObject.put("full_name", "Hibiki");
                jsonObject.put("nric", "S8529649C");
                jsonObject.put("phone", "91251234");
                jsonObject.put("address", "Condo");
                jsonObject.put("authorities", "ROLE_USER");
                jsonObject.put("active", "true");

                HttpHeaders headers = new HttpHeaders();

                HttpEntity<Object> requestEntity = new HttpEntity<>(jsonObject, headers);

                ResponseEntity<Object> result = restTemplate.withBasicAuth("manager", "goodpassword").exchange(uri,
                                HttpMethod.PUT, requestEntity, Object.class);

                System.out.println("result = " + result);

                String jsonString = om.writeValueAsString(result.getBody());
                JsonObject obj = new Gson().fromJson(jsonString, JsonObject.class);

                assertNotNull(result);
                assertEquals("Condo", obj.get("address").getAsString());
        }

        /*
        @Test
        public void updateUserSelf_Valid_Success() throws Exception {
                Customer user = new Customer("user1", encoder.encode("goodpassword1"), "Hibiki", "S8529649C",
                                "91251234", "White House", "ROLE_USER", true);

                users.save(user);

                Long id = user.getCustomerId();

                URI uri = new URI(baseUrl + port + "/customers/" + id);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", "user1");
                jsonObject.put("password", encoder.encode("goodpassword1"));
                jsonObject.put("full_name", "Hibiki");
                jsonObject.put("nric", "S8529649C");
                jsonObject.put("phone", "91251234");
                jsonObject.put("address", "Condo");
                jsonObject.put("authorities", "ROLE_USER");
                jsonObject.put("active", "true");

                HttpHeaders headers = new HttpHeaders();

                HttpEntity<Object> requestEntity = new HttpEntity<>(jsonObject, headers);

                ResponseEntity<Object> result = restTemplate.withBasicAuth("user1", "goodpassword1").exchange(uri,
                                HttpMethod.PUT, requestEntity, Object.class);

                System.out.println("result = " + result);

                String jsonString = om.writeValueAsString(result.getBody());
                JsonObject obj = new Gson().fromJson(jsonString, JsonObject.class);

                assertNotNull(result);
                assertEquals("Condo", obj.get("address").getAsString());
        } */

        @Test
        public void updateUser_Invalid_Failure() throws Exception {
                URI uri = new URI(baseUrl + port + "/customers/999");

                HttpHeaders headers = new HttpHeaders();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", "user1");
                jsonObject.put("password", encoder.encode("goodpassword1"));
                jsonObject.put("full_name", "Hibiki");
                jsonObject.put("nric", "S8529649C");
                jsonObject.put("phone", "91251234");
                jsonObject.put("address", "Condo");
                jsonObject.put("authorities", "ROLE_USER");
                jsonObject.put("active", "true");

                HttpEntity<Object> requestEntity = new HttpEntity<>(jsonObject, headers);

                ResponseEntity<Object> result = restTemplate.withBasicAuth("manager", "goodpassword").exchange(uri,
                                HttpMethod.PUT, requestEntity, Object.class);

                assertEquals(404, result.getStatusCode().value());
        }

}
