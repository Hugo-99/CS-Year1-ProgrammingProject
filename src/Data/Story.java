package Data;

import java.util.ArrayList;
import java.util.Date;

/**
 * Data structure for storing stories
 * Authors: Adam, Stefan
 */
public class Story extends DBElement {
    private final Date date;
    private final int[] commentIDs;
    private String title, author, url;
    private int score;
    private ArrayList<Comment> comments;
    private int descendantCount;

    /**
     * Story Constructor
     * Authors: Adam and Stefan
     *
     * @param id                Story ID
     * @param title             Story Title
     * @param author            Story's author
     * @param date              Date the story was posted
     * @param url               URL to the article
     * @param score             Story's score
     * @param comments          Comments for this story
     * @param descendantCount   Number of comments for this story
     */
    Story(int id, String title, String author, Date date, String url, int score, int[] comments, int descendantCount) {
        super(id, author);
        this.title = title;
        this.author = author;
        this.date = date;
        this.url = url;
        this.score = score;
        this.commentIDs = comments;
        this.descendantCount = descendantCount;
        this.comments = new ArrayList<>();
    }

    /**
     * Returns all the information of this story in an easy-to-read manner
     * Author: Stefan
     *
     * @return Story as a string
     */
    public String toString() {
        return title + " by: " + author + " (url: " + url + ")\n" + score + " Points - Published: " + date
                + "\n" + descendantCount + " comments";
    }

    void addComment(Comment commentToAdd) {
        comments.add(commentToAdd);
    }

    void printComments() {
        comments.forEach(System.out::println);
    }

    public String getAuthor() {
        return author;
    }

    public Date getDate() {
        return date;
    }

    int getDescendantCount() {
        return descendantCount;
    }

    public String getURL() {
        return url;
    }

    public int getScore() {
        return score;
    }

    public int[] getCommentIDs() {
        return commentIDs;
    }

    public String getTitle() {
        return title;
    }

    int getID() {
        return id;
    }
}
