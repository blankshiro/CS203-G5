package com.cs203t5.ryverbank.content;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cs203t5.ryverbank.customer.CustomerRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;

import com.cs203t5.ryverbank.customer.Customer;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ContentIntegrationTest {

    @LocalServerPort
    private int port;

    private final String baseURl = "http://localhost:";
    
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ContentRepository meinContent;
    
    @Autowired
    private CustomerRepository meinCustomers;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @AfterEach
    void tearDown(){
        meinContent.deleteAll();
        meinCustomers.deleteAll();
    }

    //Testing when ROLE_USERS get content - they should get 404 if no content is available
    @Test
    public void getContent_NoContentUSER_Failure() throws Exception {
        URI uri = new URI(baseURl + port + "/contents");

        meinCustomers.save(new Customer("meinUser", encoder.encode("meinPassword"),"meinFullName", "S7982834C", "88888888", "meinAddress", "ROLE_USER", true));
        //If there are no content, the Customer with ROLE_USER will be thrown an error 404
        ResponseEntity<Content> result = restTemplate.withBasicAuth("meinUser", "meinPassword")
                                        .getForEntity(uri, Content.class);
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void getContent_NoContentMANAGER_Failure() throws Exception {
        URI uri = new URI(baseURl + port + "/contents");

        meinCustomers.save(new Customer("meinUser", encoder.encode("meinPassword"),"meinFullName", "S7982834C", "88888888", "meinAddress", "ROLE_MANAGER", true));
        //If there are no content, the Customer with ROLE_USER will be thrown an error 404
        ResponseEntity<Content> result = restTemplate.withBasicAuth("meinUser", "meinPassword")
                                        .getForEntity(uri, Content.class);
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void getContent_MANAGER_Success() throws Exception {
        URI uri = new URI(baseURl + port + "/contents");

        meinCustomers.save(new Customer("meinUser", encoder.encode("meinPassword"),"meinFullName", "S7982834C", "88888888", "meinAddress", "ROLE_MANAGER", true));

        meinContent.save(new Content("Konosuba Season 3", "Best shows", "Best girl is back", "www.konosuba.com"));
        meinContent.save(new Content("Cats", "Cats are cool", "Need I say more?", "www.catsarekewl.com"));
        
        ResponseEntity<Content[]> result = restTemplate.withBasicAuth("meinUser", "meinPassword")
                                        .getForEntity(uri, Content[].class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(2, meinContent.count());
    }

    

    
}
