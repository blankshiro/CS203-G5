package com.cs203t5.ryverbank.content;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ContentServiceImpl implements ContentService{

    //Repository of all the content
    private ContentRepository meinContent;

    //Performing injection
    public ContentServiceImpl(ContentRepository meinContent){
        this.meinContent = meinContent;
    }

    //Allows Analysts and Managers to create content
    //This method will be used exclusively by Analysts & Managers
    @Override
    public Content createContent(Content content){
        return meinContent.save(content);
    }

    //Returns a list of content that are approved
    //This method will be used by customers
    @Override
    public List<Content> getAppovedContent() {
        return meinContent.findByApproved(true);
    }

    //Returns a list of ALL content, regardless if they are approved or not
    //This method will be used exclusively by Analysts & Managers
    @Override
    public List<Content> getAllContent() {
        return meinContent.findAllByOrderByApprovedAsc();
    }

    //Updates the title of a particular content
    //This method will be used exclusively by Analysts & Managers
    @Override
    public Content updateTitle(Long contentId, String newTitle){
        if (newTitle != null && !newTitle.isEmpty()){
            return meinContent.findById(contentId).map(aContent -> {aContent.setTitle(newTitle);
                return meinContent.save(aContent);
            }).orElse(null);
        }
        return null;
    }

    //Updates the updateSummary of a particular content
    //This method will be used exclusively by Analysts & Managers
    @Override
    public Content updateSummary(Long contentId, String newSummary){
        if (newSummary != null && !newSummary.isEmpty()){
            return meinContent.findById(contentId).map(aContent -> {aContent.setSummary(newSummary);
                return meinContent.save(aContent);
            }).orElse(null);
        }
        return null;
    }

    
    //Updates the summary of a particular content
    //This method will be used exclusively by Analysts & Managers
    @Override
    public Content updateContent(Long contentId, String newContent){
        if (newContent != null && !newContent.isEmpty()){
            return meinContent.findById(contentId).map(aContent -> {aContent.setNewsContent(newContent);
                return meinContent.save(aContent);
            }).orElse(null);
        }
        return null;
    }

    //Updates the link of a particular content
    //This method will be used exclusively by Analysts & Managers
    @Override
    public Content updateLink(Long contentId, String newLink){
        if (newLink != null && !newLink.isEmpty()){
            return meinContent.findById(contentId).map(aContent -> {aContent.setLink(newLink);
                return meinContent.save(aContent);
            }).orElse(null);
        }
        return null;
    }

    //Deletes a particular content based on it's Id
    //This method will be used exclusively by Analysts & Managers
    @Override
    public void deleteContent(Long contentId){
        meinContent.deleteById(contentId);
    }

    //Updates the contentId's approval to true if found
    //This method will be used exclusively by the Managers
    @Override
    public Content approveContent(Long contentId){
            return meinContent.findById(contentId).map(aContent -> {aContent.setApproved(true);
                return meinContent.save(aContent);
            }).orElse(null);
    }

}
