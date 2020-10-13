package com.cs203t5.ryverbank.content;

import org.junit.jupiter.api.AfterEach;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.URI;
import java.util.Optional;

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

    @Autowired
    private ContentService contentServices;


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

        meinCustomers.save(new Customer("meinManager", encoder.encode("meinPassword"),"meinFullName", "S7982834C", "88888888", "meinAddress", "ROLE_MANAGER", true));
        meinCustomers.save(new Customer("meinAnalyst", encoder.encode("meinPassword"),"meinFullName", "S1234567D", "99999999", "meinAddress", "ROLE_ANALYST", true));

        meinContent.save(new Content("meinTitle1", "meinSummary1", "meinContent1", "meinLink1"));
        meinContent.save(new Content("meinTitle2", "meinSummary2", "meinContent2", "meinLink2"));
        
        ResponseEntity<Content[]> result1 = restTemplate.withBasicAuth("meinManager", "meinPassword")
                                        .getForEntity(uri, Content[].class);
        ResponseEntity<Content[]> result2 = restTemplate.withBasicAuth("meinAnalyst", "meinPassword")
                                        .getForEntity(uri, Content[].class);

        assertEquals(200, result1.getStatusCode().value());
        assertEquals(200, result2.getStatusCode().value());
        assertEquals(2, meinContent.count());
        assertEquals(2, result1.getBody().length);
        assertEquals(2, result2.getBody().length);

    }
    @Test
    public void getContent_Customer_Success() throws Exception {
        URI uri = new URI(baseURl + port + "/contents");

        meinCustomers.save(new Customer("meinCustomer", encoder.encode("meinPassword"),"meinFullName", "S7982834C", "88888888", "meinAddress", "ROLE_USER", true));
        meinCustomers.save(new Customer("meinManager", encoder.encode("meinPassword"),"meinFullName", "S1234567D", "99999999", "meinAddress", "ROLE_MANAGER", true));

        Content c1 = meinContent.save(new Content("meinTitle1", "meinSummary1", "meinContent1", "meinLink1"));
        meinContent.save(new Content("meinTitle2", "meinSummary2", "meinContent2", "meinLink2"));
        contentServices.approveContent(c1.getId());

        ResponseEntity<Content[]> result1 = restTemplate.withBasicAuth("meinCustomer", "meinPassword")
                                        .getForEntity(uri, Content[].class);

        assertEquals(200, result1.getStatusCode().value());
        assertEquals(2, meinContent.count());
        assertEquals(1, result1.getBody().length);        
    }

    @Test
    public void getContent_unauthorized_Failure() throws Exception {
        URI uri = new URI(baseURl + port + "/contents");
        meinContent.save(new Content("meinTitle1", "meinSummary1", "meinContent1", "meinLink1"));


        ResponseEntity<Content> result = restTemplate.getForEntity(uri, Content.class);

        assertEquals(401, result.getStatusCode().value());
        
    }

    @Test

    public void deleteContent_validID_success() throws Exception{
        Content c1 = meinContent.save(new Content("meinTitle1", "meinSummary1", "meinContent1", "meinLink1"));
        URI uri = new URI(baseURl + port + "/contents/" + c1.getId());
        meinCustomers.save(new Customer("meinManager", encoder.encode("meinPassword"),"meinFullName", "S1234567D", "99999999", "meinAddress", "ROLE_MANAGER", true));

        ResponseEntity<Void> result = restTemplate.withBasicAuth("meinManager", "meinPassword")
        .exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(200, result.getStatusCode().value());
        Optional<Content> emptyValue = Optional.empty();
        assertEquals(emptyValue, meinContent.findById(c1.getId()));

    }
    
    @Test

    public void deleteContent_InvalidID_Failure() throws Exception{

        URI uri = new URI(baseURl + port + "/contents/1234");
        meinCustomers.save(new Customer("meinManager", encoder.encode("meinPassword"),"meinFullName", "S1234567D", "99999999", "meinAddress", "ROLE_MANAGER", true));

        ResponseEntity<Void> result = restTemplate.withBasicAuth("meinManager", "meinPassword")
        .exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void createContent_Success() throws Exception{
        URI uri = new URI(baseURl + port + "/contents");
        Content c1 = new Content("meinTitle1", "meinSummary1", "meinContent1", "meinLink1");
        Content c2 = new Content("meinTitle2", "meinSummary2", "meinContent2", "meinLink2");

        meinCustomers.save(new Customer("meinManager", encoder.encode("meinPassword"),"meinFullName", "S1234567D", "99999999", "meinAddress", "ROLE_MANAGER", true));
        meinCustomers.save(new Customer("meinAnalyst", encoder.encode("meinPassword"),"meinFullName", "S7982834C", "88888888", "meinAddress", "ROLE_ANALYST", true));
        
        ResponseEntity<Content> result1 = restTemplate.withBasicAuth("meinManager", "meinPassword")
        .postForEntity(uri, c1, Content.class);

        ResponseEntity<Content> result2 = restTemplate.withBasicAuth("meinAnalyst", "meinPassword")
        .postForEntity(uri, c2, Content.class);
        assertEquals(201, result1.getStatusCode().value());
        assertEquals(201, result2.getStatusCode().value());
        assertEquals(c1.getTitle(), result1.getBody().getTitle());
        assertEquals(c2.getTitle(), result2.getBody().getTitle());
        assertEquals(2, meinContent.count());

    }

    @Test
    public void createContent_Failure() throws Exception{
        URI uri = new URI(baseURl + port + "/contents");
        Content c1 = new Content("meinTitle1", "meinSummary1", "meinContent1", "meinLink1");
        meinCustomers.save(new Customer("meinUser", encoder.encode("meinPassword"),"meinFullName", "S1234567D", "99999999", "meinAddress", "ROLE_USER", true));

        ResponseEntity<Content> result = restTemplate.withBasicAuth("meinUser", "meinPassword")
        .postForEntity(uri, c1, Content.class);
        
        assertEquals(403, result.getStatusCode().value());
        assertEquals(0,meinContent.count());
    }

    @Test
    public void updateContent_Sucess() throws Exception{
        
        //For updates: We MUST create the HTTP headers with the new content as the body 
        Content c1 = meinContent.save(new Content("meinTitle1", "meinSummary1", "meinContent1", "meinLink1"));

        // - IMPORTANT: Import the correct Http package - import org.springframework.http.HttpEntity;
        HttpHeaders headers = new HttpHeaders();
        
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Content c2 = new Content("newTitle", "newSummary", "newContent", "newLink");
        HttpEntity<Content> requestEntity = new HttpEntity<>(c2,headers);

        URI uri = new URI(baseURl + port + "/contents/" + c1.getId());
        meinCustomers.save(new Customer("meinManager", encoder.encode("meinPassword"),"meinFullName", "S1234567D", "99999999", "meinAddress", "ROLE_MANAGER", true));

        ResponseEntity<Content> result = restTemplate.withBasicAuth("meinManager", "meinPassword")
        .exchange(uri, HttpMethod.PUT, requestEntity , Content.class);
        
        assertNotNull(result);
        assertEquals(c2.getTitle(), result.getBody().getTitle());
        assertEquals(c1.getId(), result.getBody().getId());
    }

    @Test
    public void updateContent_Failure() throws Exception{
        //Test if content is not found
        HttpHeaders headers = new HttpHeaders();
        
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Content c2 = new Content("newTitle", "newSummary", "newContent", "newLink");
        HttpEntity<Content> requestEntity = new HttpEntity<>(c2,headers);

        URI uri = new URI(baseURl + port + "/contents/100");
        meinCustomers.save(new Customer("meinManager", encoder.encode("meinPassword"),"meinFullName", "S1234567D", "99999999", "meinAddress", "ROLE_MANAGER", true));

        ResponseEntity<Content> result = restTemplate.withBasicAuth("meinManager", "meinPassword")
        .exchange(uri, HttpMethod.PUT, requestEntity , Content.class);
        
        assertEquals(404, result.getStatusCode().value());

    }

    @Test
    public void approveContent_Sucess() throws Exception{
        
        //For updates: We MUST create the HTTP headers with the new content as the body 
        Content c1 = meinContent.save(new Content("meinTitle1", "meinSummary1", "meinContent1", "meinLink1"));

        // - IMPORTANT: Import the correct Http package - import org.springframework.http.HttpEntity;
        HttpHeaders headers = new HttpHeaders();
        
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        //Simulate the approval
        Content c2 = new Content("meinTitle1", "meinSummary1", "meinContent1", "meinLink1", true);
        HttpEntity<Content> requestEntity = new HttpEntity<>(c2,headers);

        URI uri = new URI(baseURl + port + "/contents/" + c1.getId());
        meinCustomers.save(new Customer("meinManager", encoder.encode("meinPassword"),"meinFullName", "S1234567D", "99999999", "meinAddress", "ROLE_MANAGER", true));

        ResponseEntity<Content> result = restTemplate.withBasicAuth("meinManager", "meinPassword")
        .exchange(uri, HttpMethod.PUT, requestEntity , Content.class);
        
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());
        assertEquals(true, result.getBody().isApproved());
    }

    
    @Test
    //Analysts should not be allowed to approve 
    public void approveContent_Failure() throws Exception{
        
        //For updates: We MUST create the HTTP headers with the new content as the body 
        Content c1 = meinContent.save(new Content("meinTitle1", "meinSummary1", "meinContent1", "meinLink1"));

        // - IMPORTANT: Import the correct Http package - import org.springframework.http.HttpEntity;
        HttpHeaders headers = new HttpHeaders();
        
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        //Simulate the approval
        Content c2 = new Content("meinTitle1", "meinSummary1", "meinContent1", "meinLink1", true);
        HttpEntity<Content> requestEntity = new HttpEntity<>(c2,headers);

        URI uri = new URI(baseURl + port + "/contents/" + c1.getId());
        meinCustomers.save(new Customer("meinAnalyst", encoder.encode("meinPassword"),"meinFullName", "S1234567D", "99999999", "meinAddress", "ROLE_ANALYST", true));

        ResponseEntity<Content> result = restTemplate.withBasicAuth("meinAnalyst", "meinPassword")
        .exchange(uri, HttpMethod.PUT, requestEntity , Content.class);

        assertEquals(false, result.getBody().isApproved());
        assertEquals(403, result.getStatusCode().value());

    }



}
