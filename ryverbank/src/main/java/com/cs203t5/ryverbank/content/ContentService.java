package com.cs203t5.ryverbank.content;

import java.util.List;

/**
 * An interface for content services.
 */
public interface ContentService {
    
    //Creating new content (C of C.R.U.D)
    //Roles: Analyst & Manager
    Content createContent(Content content);
    
    //Reading content (R of C.R.U.D)
    //Roles: Customers only approved content
    List<Content> getAppovedContent();

    //Roles: Analyst & Manager
    List<Content> getAllContent();

    //Updating content (U of C.R.U.D)
    //Roles: Analyst & Manager
    Content updateTitle(Long contentId, String newTitle);
    Content updateSummary(Long contentId, String newSummary);
    Content updateContent(Long contentId, String newContent);
    Content updateLink(Long contentId, String newLink);
    
    //Deleting content (D of C.R.U.D)
    //Roles: Analyst & Manager
    void deleteContent(Long contentId);


    //Approving content
    //Roles: Manager
    Content approveContent(Long contentId);
    
}
