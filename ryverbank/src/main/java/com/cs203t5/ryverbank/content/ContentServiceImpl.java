package com.cs203t5.ryverbank.content;

import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Implementation of the ContentService class.
 * 
 * @see ContentService
 */
@Service
public class ContentServiceImpl implements ContentService {

    /** The content repository. */
    private ContentRepository meinContent;

    /**
     * Constructs a ContentServiceImpl with the following parameter.
     * 
     * @param meinContent The content repository.
     */
    public ContentServiceImpl(ContentRepository meinContent) {
        this.meinContent = meinContent;
    }

    @Override
    public Content createContent(Content content) {
        if (meinContent.existsByTitle(content.getTitle())) {
            throw new ContentExistsException(content.getTitle());
        }
        return meinContent.save(content);
    }

    @Override
    public List<Content> getAppovedContent() {
        return meinContent.findByApproved(true);
    }

    @Override
    public List<Content> getAllContent() {
        return meinContent.findAllByOrderByApprovedAsc();
    }

    @Override
    public Content updateTitle(Long contentId, String newTitle) {
        if (newTitle != null && !newTitle.isEmpty()) {
            return meinContent.findById(contentId).map(aContent -> {
                aContent.setTitle(newTitle);
                return meinContent.save(aContent);
            }).orElse(null);
        }
        return null;
    }

    @Override
    public Content updateSummary(Long contentId, String newSummary) {
        if (newSummary != null && !newSummary.isEmpty()) {
            return meinContent.findById(contentId).map(aContent -> {
                aContent.setSummary(newSummary);
                return meinContent.save(aContent);
            }).orElse(null);
        }
        return null;
    }

    @Override
    public Content updateContent(Long contentId, String newContent) {
        if (newContent != null && !newContent.isEmpty()) {
            return meinContent.findById(contentId).map(aContent -> {
                aContent.setNewsContent(newContent);
                return meinContent.save(aContent);
            }).orElse(null);
        }
        return null;
    }

    @Override
    public Content updateLink(Long contentId, String newLink) {
        if (newLink != null && !newLink.isEmpty()) {
            return meinContent.findById(contentId).map(aContent -> {
                aContent.setLink(newLink);
                return meinContent.save(aContent);
            }).orElse(null);
        }
        return null;
    }

    @Override
    public void deleteContent(Long contentId) {
        meinContent.deleteById(contentId);
    }

    @Override
    public Content approveContent(Long contentId) {
        return meinContent.findById(contentId).map(aContent -> {
            aContent.setApproved(true);
            return meinContent.save(aContent);
        }).orElse(null);
    }

}
