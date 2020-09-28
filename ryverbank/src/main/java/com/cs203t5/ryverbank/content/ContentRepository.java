package com.cs203t5.ryverbank.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    // This method is used to search for approved list of content for the customer
    List<Content> findByApproved(boolean approved);

    // This method is used to search for ALL content, ordered by False
    List<Content> findAllByOrderByApprovedAsc();

    // This method is used to search for a particular content, based on its Id
    Optional<Content> findById(Long Id);

}

