package com.cs203t5.ryverbank.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.cs203t5.ryverbank.user.*;

@Component
public class RestTemplateClient {
    private final RestTemplate template;
    
    public RestTemplateClient(RestTemplateBuilder restTemplateBuilder) {
        this.template = restTemplateBuilder
                .basicAuthentication("admin", "goodpassword")
                .build();
    }

    public User getUser(final String URI, final Long id) {
        final User user = template.getForObject(URI + "/" + id, User.class);
        return user;
    }

    public User addUser(final String URI, final User user) {
        final User returned = template.postForObject(URI, user, User.class);
        
        return returned;
    }

    public ResponseEntity<User> getUserEntity(final String URI, final Long id){
        return template.getForEntity(URI + "/{id}", User.class, Long.toString(id));
    }
}
