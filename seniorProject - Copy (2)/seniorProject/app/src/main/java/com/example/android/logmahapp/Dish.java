package com.example.android.logmahapp;

/**
 * An {@link Dish} object contains information related to a single dish.
 */
public class Dish {

    /** Title of the Article */
    private String articleTitle;

    /** Name of the section */
    private String sectionName;

    /** Name of the author */
    private String authorName;

    /** The date published*/
    private String webPublicationDate;

    /** Website URL of the earthquake */
    private String webUrl;

    /**
     * Constructs a new {@link Dish} object.
     *
     * @param articleTitle is the article title of the new
     * @param sectionName is the section name of the new
     * @param authorName is the the author name of the new
     * @param webPublicationDate is the publish date of the new
     * @param webURL is the website URL to find more details about the new
     */
    public Dish(String articleTitle, String sectionName, String authorName, String webPublicationDate, String webURL) {
        this.articleTitle = articleTitle;
        this.sectionName = sectionName;
        this.authorName = authorName;
        this.webPublicationDate = webPublicationDate;
        this.webUrl = webURL;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getWebPublicationDate() {
        return webPublicationDate;
    }

    public String getWebUrl() {
        return webUrl;
    }
}
