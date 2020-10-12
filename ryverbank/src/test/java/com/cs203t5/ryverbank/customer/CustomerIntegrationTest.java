package com.cs203t5.ryverbank.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cs203t5.ryverbank.customer.CustomerRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;

import com.cs203t5.ryverbank.customer.Customer;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CustomerIntegrationTest{

    @LocalServerPort
    private int port;

    private final String baseURl = "http://localhost:";
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private CustomerRepository meinCustomers;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @AfterEach
    void tearDown(){
        meinCustomers.deleteAll();
    }


    @Test
    public void getCustomers_Success() throws Exception {
		URI uri = new URI(baseURl + port + "/customers");
        meinCustomers.save(new Customer("meinUser", encoder.encode("meinPassword"),"meinFullName", "S7982834C", "88888888", "meinAddress", "ROLE_MANAGER", true));
        HttpHeaders headers = new HttpHeaders();
        // Need to use array with a ReponseEntity here
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        ResponseEntity<String> result = restTemplate.withBasicAuth("meinUser", "meinPassword")
                                        .exchange(uri, HttpMethod.GET, entity, String.class);
        
        // Customer[] customers = result.getBody();
        System.out.println("\n\n\n" + result + "\n\n\n");
		
		assertEquals(200, result.getStatusCode().value());
		// assertEquals(1, customers.length);

    }




}
