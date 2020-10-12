package com.cs203t5.ryverbank.content;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ContentServiceTest {
    
    @Mock
    private ContentRepository meinContent;

    @InjectMocks
    private ContentServiceImpl contentServices;

    /*
        Run all tests in this java file: mvnw -Dtest=ContentServiceTest.java
        Run all tests in the project: mvnw test

        Naming convention for Mockito tests
        MethodName_StateUnderTest_ExpectedBehavior
        MethodName = The name of the method you are testing
        StateUnderTest = What is the condition that you are testing
        ExpectedBehavior = What do you expect based on the condition

        Understanding Mockito terminologies:
        when(...).thenReturn(...); translates to:
        when(callmethodsherewithparameters).thenReturn(result)
        This seems like rewriting the method for some reason

        Asserting:
        assertEquals(callmethodsherewithparameters, resultsYouAreExpecting)
        assertNotNull(variable)

    */

    @Test
    void createContent_newContent_returnNewContent(){
        //Arrange
        Content newContent = new Content("newTitle", 
        "newSummary", "newContent", "newLink");
        
        //Mocking the save operation
        when(meinContent.save(any(Content.class))).thenReturn(newContent);

        // Act: actually using the save option
        Content savedContent = contentServices.createContent(newContent);

        //Assertion: Check if what you want to test really happens
        //Check if the content is actually saved
        //Note: Might not be right since createContent will never return null
        //Rather, it will only create if the content title doesn't already exist
        //Throw ContentExistsException if otherwise, so the next test will be if the content already exists
        assertNotNull(savedContent);

        /*
            This verify statement means: 
            "check if the "save" function with the parameter newContent
            has been called in meinContent database"
        */
        verify(meinContent).save(newContent);
    }

    @Test
    void createContent_existingTitle_throwContentExistsException(){

        Content newContent = new Content("newTitle", 
        "newSummary", "newContent", "newLink");

        //This should be null if an exception is thrown
        Content addedContent = contentServices.createContent(newContent);
        
        //If the content with the same title is found, then true value should be returned
        //This is basically line 22 of ContentServiceImpl
        when(meinContent.existsByTitle(any(String.class))).thenReturn(true);
        System.out.println(addedContent);
        //Assert if null
        assertNull(addedContent);
        //Assert if the exception is thrown
        assertThrows(ContentExistsException.class, ()-> contentServices.createContent(newContent));
    }

    @Test
    void getAllContent_listOfAllContent(){
        Content content1 = contentServices.createContent(new Content("title1", "summary1", "content1", "link1"));
        Content content2 = contentServices.createContent(new Content("title2", "summary2", "content2", "link2"));
        
        //Stubbing
        when (meinContent.findAllByOrderByApprovedAsc()).thenReturn(Arrays.asList(content1, content2));

        //Act
        List<Content> allContent = contentServices.getAllContent();
        assertNotNull(allContent);
        assertEquals(2, allContent.size());

    }

    @Test
    void getApprovedContent_listOfApprovedContent(){
        Content content1 = contentServices.createContent(new Content("title1", "summary1", "content1", "link1"));
        Content content2 = contentServices.createContent(new Content("title2", "summary2", "content2", "link2"));

        //Because both content are not approved, it should return 0 content
        when(meinContent.findByApproved(true)).thenReturn(new ArrayList<Content>());
        //Act
        List<Content> allContent = contentServices.getAppovedContent();
        assertEquals(0, allContent.size());
    }

    @Test
    void updateTitle_contentFound_contentWithUpdatedTitle(){
        Content content1 = new Content("title1", "summary1", "content1", "link1");
        Long contentId = 1L;
        //Mock a successful search and get a title
        when(meinContent.findById(contentId)).thenReturn(Optional.of(content1));
        when(meinContent.save(any(Content.class))).thenReturn(content1);

        Content updatedContent = contentServices.updateTitle(contentId, "newTitle");
        assertEquals("newTitle", updatedContent.getTitle());
    }

    @Test
    void updateTitle_contentNotFound_ReturnNull(){

        Long contentId = 100L;
        //Mock a successful search and get a title
        when(meinContent.findById(contentId)).thenReturn(Optional.empty());
        //No need to mock the saving if the title is not saved
        Content updatedContent = contentServices.updateTitle(contentId, "newTitle");
        assertNull(updatedContent);
    }
    
    @Test
    void updateTitle_nullInput_ReturnNull(){
        Long contentId = 100L;
        Content updatedContent = contentServices.updateTitle(contentId, null);
        assertNull(updatedContent);
    }
    
    @Test
    void updateTitle_EmptyInput_ReturnNull(){
        Long contentId = 100L;
        Content updatedContent = contentServices.updateTitle(contentId, "");
        assertNull(updatedContent);
    }

    @Test
    void updateSummary_contentFound_contentWithUpdatedSummary(){
        Content content1 = new Content("title1", "summary1", "content1", "link1");
        Long contentId = 1L;
        //Mock a successful search and get the content
        when(meinContent.findById(contentId)).thenReturn(Optional.of(content1));
        when(meinContent.save(any(Content.class))).thenReturn(content1);

        Content updatedContent = contentServices.updateSummary(contentId, "newSummary");
        assertEquals("newSummary", updatedContent.getSummary());
    }
    
    @Test
    void updateSummary_contentNotFound_ReturnNull(){

        Long contentId = 100L;
        //Mock an unsuccessful search
        when(meinContent.findById(contentId)).thenReturn(Optional.empty());

        Content updatedContent = contentServices.updateSummary(contentId, "newSummary");
        assertNull(updatedContent);
    }

    @Test
    void updateSummary_nullInput_ReturnNull(){
        Long contentId = 100L;
        Content updatedContent = contentServices.updateSummary(contentId, null);
        assertNull(updatedContent);
    }

    @Test
    void updateSummary_EmptyInput_ReturnNull(){
        Long contentId = 100L;
        Content updatedContent = contentServices.updateSummary(contentId, "");
        assertNull(updatedContent);
    }

    @Test
    void updateContent_contentFound_contentWithUpdatedContent(){
        Content content1 = new Content("title1", "summary1", "content1", "link1");
        Long contentId = 1L;
        //Mock a successful search and get the content
        when(meinContent.findById(contentId)).thenReturn(Optional.of(content1));
        when(meinContent.save(any(Content.class))).thenReturn(content1);

        Content updatedContent = contentServices.updateContent(contentId, "newContent");
        assertEquals("newContent", updatedContent.getNewsContent());
    }

    @Test
    void updateContent_contentNotFound_ReturnNull(){
        Long contentId = 100L;
        //Mock an unsuccessful search
        when(meinContent.findById(contentId)).thenReturn(Optional.empty());

        Content updatedContent = contentServices.updateContent(contentId, "newContent");
        assertNull(updatedContent);
    }

    @Test
    void updateContent_nullInput_ReturnNull(){
        Long contentId = 100L;
        Content updatedContent = contentServices.updateContent(contentId, null);
        assertNull(updatedContent);
    }
    @Test
    void updateContent_EmptyInput_ReturnNull(){
        Long contentId = 100L;
        Content updatedContent = contentServices.updateContent(contentId, "");
        assertNull(updatedContent);
    }

    @Test
    void updateLink_contentFound_contentWithUpdatedLink(){
        Content content1 = new Content("title1", "summary1", "content1", "link1");
        Long contentId = 1L;
        //Mock a successful search and get the content
        when(meinContent.findById(contentId)).thenReturn(Optional.of(content1));
        when(meinContent.save(any(Content.class))).thenReturn(content1);

        Content updatedContent = contentServices.updateLink(contentId, "newLink");
        assertEquals("newLink", updatedContent.getLink());
    }
    
    @Test
    void updateLink_contentNotFound_ReturnNull(){
        Long contentId = 100L;
        //Mock an unsuccessful search
        when(meinContent.findById(contentId)).thenReturn(Optional.empty());

        Content updatedContent = contentServices.updateLink(contentId, "newLink");
        assertNull(updatedContent);

    }
    @Test
    void updateLink_NullInput_ReturnNull(){
        Long contentId = 100L;
        Content updatedContent = contentServices.updateLink(contentId, null);
        assertNull(updatedContent);
        
    }

    @Test
    void updateLink_EmptyInput_ReturnNull(){
        Long contentId = 100L;
        Content updatedContent = contentServices.updateLink(contentId, "");
        assertNull(updatedContent);
    }

    @Test
    void deleteContent_noReturns(){
        Long contentId = 100L;
        contentServices.deleteContent(contentId);

        verify(meinContent).deleteById(contentId);
    }

    @Test
    void approveContent_contentFound_returnApprovedContent(){
        Content content1 = new Content("title1", "summary1", "content1", "link1");
        Long contentId = 1L;
        //Mock a successful search and get the content
        when(meinContent.findById(contentId)).thenReturn(Optional.of(content1));
        when(meinContent.save(any(Content.class))).thenReturn(content1);

        Content updatedContent = contentServices.approveContent(contentId);
        assertEquals(true, updatedContent.isApproved());
    }

    @Test
    void approveContent_contentNotFound_returnNull(){
        Long contentId = 100L;
        //Mock an unsuccessful search
        when(meinContent.findById(contentId)).thenReturn(Optional.empty());        
        Content updatedContent = contentServices.approveContent(contentId);
        assertNull(updatedContent);
    }

}
