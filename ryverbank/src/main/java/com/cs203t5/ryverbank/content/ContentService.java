package com.cs203t5.ryverbank.content;

import java.util.List;

/**
 * An interface for various content services.
 */
public interface ContentService {

    /**
     * Creates a content based on the specified content information. If there is an
     * existing content with the same title, throw ContentExistsException.
     * 
     * @param content The content to be created.
     * @return The content created.
     */
    Content createContent(Content content);

    /**
     * Finds all the approved content in the content repository.
     * 
     * @return The list of approved contents.
     */
    List<Content> getAppovedContent();

    /**
     * Finds all the content in the content repository.
     * 
     * @return The list of all contents.
     */
    List<Content> getAllContent();

    /**
     * Updates the content title with the specified content id. If no content is
     * found, return null.
     * 
     * @param contentId The content id.
     * @param newTitle  The content's new title.
     * @return The content with the updated title.
     */
    Content updateTitle(Long contentId, String newTitle);

    /**
     * Updates the content summary with the specified content id. If no content is
     * found, return null.
     * 
     * @param contentId  The content id.
     * @param newSummary The content's new summary.
     * @return The content with the updated summary.
     */
    Content updateSummary(Long contentId, String newSummary);

    /**
     * Updates the content news with the specified content id. If no content is
     * found, return null.
     * 
     * @param contentId  The content id.
     * @param newContent The content's new news.
     * @return The content with the updated news.
     */
    Content updateContent(Long contentId, String newContent);

    /**
     * Updates the content link with the specified content id. If no content is
     * found, return null.
     * 
     * @param contentId  The content id.
     * @param newLink The content's new link.
     * @return The content with the updated link.
     */
    Content updateLink(Long contentId, String newLink);

    /**
     * Deletes the content with the specified content id.
     * 
     * @param contentId The content id.
     */
    void deleteContent(Long contentId);

    /**
     * Allows the manager to approve content with the specified content id. If no
     * content is found, return null.
     * 
     * @param contentId The content id.
     * @return The approved content.
     */
    Content approveContent(Long contentId);

}
