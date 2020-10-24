package com.cs203t5.ryverbank.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * A ContentRepository that provides the mechanism for storage, retrieval,
 * search, update and delete operation on content objects
 */
@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    /**
     * Derived query to search for approved list of content for the customer
     * 
     * @param approved The contents that are approved.
     * @return The approved list of content.
     */
    List<Content> findByApproved(boolean approved);

    /**
     * Derived query to search for all content, ordered by False
     * 
     * @return The list of all content, ordered by False.
     */
    List<Content> findAllByOrderByApprovedAsc();

    /**
     * Derived query to search for a particular content, based on its Id
     * 
     * @param id The id of the content.
     * @return The content found.
     */
    Optional<Content> findById(Long Id);

    /**
     * Derived query to check if the title of the content already exists.
     * 
     * @param aTitle The title of the content.
     * @return True if the content exists, otherwise return False.
     */
    Boolean existsByTitle(String aTitle);
}

