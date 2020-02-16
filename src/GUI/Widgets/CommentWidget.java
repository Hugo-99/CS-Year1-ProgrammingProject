package GUI.Widgets;

import Data.Comment;
import GUI.Color;
import GUI.Layout;
import Main.Project;
import processing.core.PApplet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static GUI.Layout.*;
/**
 * CommentWidget
 * Author: Koh, Stefan
 */
public class CommentWidget extends Widget {
    private Comment comment;
    private Project main;
    private ArrayList<String> lines;
    private boolean indicateNoComments;

    /**
     * Constructor for Comment Widgets
     * Authors: Koh and Stefan
     *
     * @param main      Reference to the main class
     * @param comment   Comment to display
     * @param x         x coordinate of the top-left corner of the CommentWidget
     * @param y         y coordinate of the top-left corner of the CommentWidget
     * @param width     Comment width
     * @param height    Comment height
     */
    public CommentWidget(Project main, Comment comment, int x, int y, int width, int height) {
        super(main, x, y, width, height);
        this.main = main;
        this.comment = comment;
        lines = comment.getLines();
        this.indicateNoComments = false;
    }

    /**
     * CommentWidget constructor for empty stories
     * Author: Stefan
     *
     * @param main                  Reference to the main class
     * @param x                     x coordinate of the top-left corner of the CommentWidget
     * @param y                     y coordinate of the top-left corner of the CommentWidget
     * @param width                 Widget width
     * @param height                Widget height
     */
    public CommentWidget(Project main, int x, int y, int width, int height) {
        super(main, x, y, width, height);
        this.main = main;
        this.indicateNoComments = true;
    }

    /**
     * Draws the comment widget
     * Authors: Koh and Stefan
     */
    public void draw() {
        int alpha = (int) (PApplet.map(y, MARGIN_TOP - height, MARGIN_TOP, 0, 255));
        if(this.indicateNoComments) {
            main.fill(Color.TEXT_DEFAULT, alpha);
            main.textAlign(main.LEFT);
            main.text("No comments were found for this story", PADDING + x, APPROX_TEXT_HEIGHT + PADDING + PADDING + y, width - PADDING, APPROX_TEXT_HEIGHT + PADDING);
        } else {
            main.fill(Color.BACKGROUND_DIMMED);
            main.stroke(Color.GRAY_DARK, alpha);
            main.rect(x, y, width, height, BORDER_RADIUS / 2);

            main.fill(Color.PRIMARY, alpha);
            main.textAlign(main.LEFT);
            String formattedDate = new SimpleDateFormat("EE DD/MM/YYYY").format(comment.getDate());
            main.text("By: " + comment.getAuthor() + "       Posted: " + formattedDate, PADDING + x, PADDING + y, width - PADDING, APPROX_TEXT_HEIGHT + PADDING);

            main.fill(Color.TEXT_DEFAULT, alpha);
            main.textAlign(main.LEFT);
            if(!lines.isEmpty())
                for(int j = 0; j < lines.size(); j++)
                    main.text(lines.get(j), PADDING + x, (1 + j) * (APPROX_TEXT_HEIGHT + PADDING) + PADDING + y, width - PADDING, APPROX_TEXT_HEIGHT + PADDING);
        }
    }

    /**
     * @return Returns true when the cursor should change to a link cursor
     */
    @Override
    public boolean cursorInBounds() {
        return false;
    }
}
