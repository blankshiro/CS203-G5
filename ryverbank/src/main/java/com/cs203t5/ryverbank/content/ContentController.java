package com.cs203t5.ryverbank.content;

import java.util.List;

import javax.validation.Valid;

import com.cs203t5.ryverbank.customer.CustomerRepository;
import com.cs203t5.ryverbank.customer.CustomerService;
import com.cs203t5.ryverbank.customer.CustomerUnauthorizedException;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContentController {
    private ContentRepository meinContent;
    private ContentService contentService;

    public ContentController(ContentRepository meinContent, ContentService contentService) {
        this.meinContent = meinContent;
        this.contentService = contentService;
    }

    // This mapped URL is wrong - temporarily placed in content
    @PostMapping("/contents")
    public Content createContent(@Valid @RequestBody Content aContent, Authentication auth) {
        // Find out the authority of the person logged in
        // Note to self: Find more elegant way to get the authorities
        String authenticatedUserRole = auth.getAuthorities().stream().findAny().get().getAuthority();

        // Only allow them to create content if they are either a manager or an analyst
        if (authenticatedUserRole.equals("ROLE_MANAGER") || authenticatedUserRole.equals("ROLE_ANALYST")) {
            return contentService.createContent(aContent);
        } else {
            // If the user is not authorized to post, then direct them to HTTP403
            throw new CustomerUnauthorizedException("You do not the permission to create content");
        }

    }

    @GetMapping("/contents")
    public List<Content> getContents(Authentication auth) {
        String authenticatedUserRole = auth.getAuthorities().stream().findAny().get().getAuthority();
        //Testing code
        System.out.println("LOGGED IN AS: " + authenticatedUserRole);
        //Return all content that are approved/non-approved
        if (authenticatedUserRole.equals("ROLE_MANAGER") || authenticatedUserRole.equals("ROLE_ANALYST")) {
            return meinContent.findAllByOrderByApprovedAsc();

            //Return all content that are approved
        } else if (authenticatedUserRole.equals("ROLE_USER")) {
            return meinContent.findByApproved(true);
        } else {
            throw new CustomerUnauthorizedException("You do not have permission to access the content");
        }
    }

}
