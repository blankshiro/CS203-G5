package com.cs203t5.ryverbank.content;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.cs203t5.ryverbank.customer.CustomerUnauthorizedException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class ContentController {
    private ContentRepository meinContent;
    private ContentService contentService;

    public ContentController(ContentRepository meinContent, ContentService contentService) {
        this.meinContent = meinContent;
        this.contentService = contentService;
    }

    @ResponseStatus(HttpStatus.CREATED)
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
        // Testing code
        System.out.println("LOGGED IN AS: " + authenticatedUserRole);
        // Return all content that are approved/non-approved
        if (authenticatedUserRole.equals("ROLE_MANAGER") || authenticatedUserRole.equals("ROLE_ANALYST")) {
            if (meinContent.findAllByOrderByApprovedAsc().isEmpty()){
                throw new ContentNotFoundException("No content available for viewing");
            }
            return meinContent.findAllByOrderByApprovedAsc();

            // Return all content that are approved
        } else if (authenticatedUserRole.equals("ROLE_USER")) {
            if (meinContent.findByApproved(true).isEmpty()){
                throw new ContentNotFoundException("No content available for viewing");
            }
            return meinContent.findByApproved(true);
        } else {
            //Code will never reach here if the security config is functional
            throw new CustomerUnauthorizedException("You do not have permission to access the content");
        }
    }

    // This method should only be accessible to managers/analysts, with the
    // exception being approving the content
    /*
     * This method will be in charge of calling all the updating methods on content
     * 
     * Roles that can call these methods: Analyst, Manager 
     * updateTitle()
     * updateSummary() 
     * updateContent() 
     * updateLink()
     * 
     * This method approves the content so that it can be seen by users
     * 
     * Roles that can call these methods: Manager 
     * approveContent()
     */
    @PutMapping(value = "/contents/{id}")
    public Optional<Content> updateContentFields(@PathVariable Long id, @RequestBody Content aContent,
            Authentication auth) {

        String authenticatedUserRole = auth.getAuthorities().stream().findAny().get().getAuthority();
        /*
            A normal user should not be able to access this page based on the securityConfig
        */
        //If the content does not exist, throw error 404 handled by ContentNotFoundException
        //Code ends here if the book is not found
        if (!meinContent.existsById(id)){
            throw new ContentNotFoundException(id);
        }
        /*
            If the Json input passed in is not null for the fields, it means that someone wishes to edit the link
            This same process is repeated for every field that is available for updates.
        */
        //If the input passed into the Json is not null for the "link" field, it means that someone wishes to edit the link
        if (aContent.getTitle() != null){
                contentService.updateTitle(id, aContent.getTitle());
            }

        //If the input passed into the Json is not null for the "summary" field, it means that someone wishes to edit the summary
        if (aContent.getSummary() != null){
                contentService.updateSummary(id, aContent.getSummary());
            }      

        //If the input passed into the Json is not null for the "content" field, it means that someone wishes to edit the content
        if (aContent.getNewsContent() != null){
            contentService.updateContent(id, aContent.getNewsContent());
        }      

        //If the input passed into the Json is not null for the "link" field, it means that someone wishes to edit the link
        if (aContent.getLink() != null){
            contentService.updateLink(id, aContent.getLink());
        }
        
        //Only managers can access this function, so a role check needs to be performed
        if (aContent.isApproved()){
            //A nested-if is required here to throw an error if an analyst tries to approve content
            if(authenticatedUserRole.equals("ROLE_MANAGER")){
                contentService.approveContent(id);
            } else {
                throw new CustomerUnauthorizedException("Analysts cannot approve content");
            }
        }             
        return meinContent.findById(id);
    }

    
    @DeleteMapping(value = "/contents/{id}")
    public void deleteContent(@PathVariable Long id){
        try{
            meinContent.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ContentNotFoundException(id);
        }

    }


}
