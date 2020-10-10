package com.cs203t5.ryverbank.content;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        //If the content with the same title is found, then true value should be returned
        //This is basically line 22 of ContentServiceImpl
        when(meinContent.existsByTitle(any(String.class))).thenReturn(true);

        assertThrows(ContentExistsException.class, ()-> contentServices.createContent(newContent));

    }

    


}
