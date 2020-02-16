package Data;

import Main.Project;

import java.util.ArrayList;
import java.util.Date;

import static GUI.Layout.PADDING;

/**
 * Data structure for comments
 * Authors: Adam, Stefan, Koh
 */
public class Comment extends DBElement {
    private Project main;

    private final Date date;
    private String text;
    private int extraHeight;
    private int[] replyIDs;
    private ArrayList<Comment> replies;
    private ArrayList<String> eachLine;

    Comment(Project main, String text, String author, Date date, int parent, int id, int[] replyIDs) {
        super(id, author);
        this.main = main;
        this.text = text.replace("<p>", "").replace("&#34;", "").replace("&#39;", "");
        this.date = date;
        this.replyIDs = replyIDs;

        extraHeight = 0;
        this.replies = new ArrayList<>();
        eachLine = new ArrayList<>();
        setupText();
        setExtraHeight();
    }

    Comment(Project main, String text, String author, Date date, int parent, int id, ArrayList<Comment> replies) {
        super(id, author);
        this.main = main;
        this.text = text;
        this.date = date;
        this.replies = replies;

        extraHeight = 0;
        eachLine = new ArrayList<>();
        setupText();
        setExtraHeight();
    }
    /**
     * setupText method
     * Returns none, resize the size of the String to fit in the comment widget
     * Parameter: none
     * Author: Koh
     */
    private void setupText() {
        String[] textData = getText().split(" ");
        String lines = "";

        for(int i = 0; i < textData.length; i++) {
            lines += (textData[i] + " ");

            try {
                if(i + 1 < textData.length)
                    if((main.textWidth(lines) + main.textWidth(textData[i + 1])) > (main.width - PADDING)) {
                        eachLine.add(lines);
                        lines = "";
                    }

                if(main.textWidth(lines) < (main.width - PADDING) && i == textData.length - 1) {
                    eachLine.add(lines);
                    lines = "";
                }
            } catch(Exception e) {
                System.out.println(lines);
            }
        }
    }

    public ArrayList<String> getLines() {
        return eachLine;
    }

    /**
     * setExtraHeight method
     * Returns none, set an extra height for the comment widget to able to fit in all of the comments
     * Parameter: none
     * Author: Koh
     */
    private void setExtraHeight() {
        if(eachLine.size() > 3)
            extraHeight = (eachLine.size() - 3) * 23;
    }

    public int getExtraHeight() {
        return extraHeight;
    }

    public String toString() {
        return text + " by: " + author + "\n" + "Posted: " + date
                + "\n" + (replyIDs != null ? replyIDs.length + " replies" : "");
    }

    public Date getDate() {
        return date;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public ArrayList<Comment> getReplies() {
        return replies;
    }
}