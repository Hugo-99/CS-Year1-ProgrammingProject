package GUI.Widgets;

import Data.User;
import GUI.Color;
import Main.Project;
import Main.Utils;

import static GUI.Layout.APPROX_TEXT_HEIGHT;
import static GUI.Layout.PADDING;

/**
 * Information widget for user pages
 * Author: Stefan
 */
public class UserHeader extends Widget {

    private final User user;
    private final int totalStories;

    /**
     * Constructor for User Header widgets
     * @param main          Reference to the main class
     * @param x             x coordinate of the top-left corner of the UserHeader widget
     * @param y             y coordinate of the top-left corner of the UserHeader widget
     * @param width         Widget width
     * @param height        Widget height
     * @param user          User to display information for
     * @param totalStories  This user's total stories
     */
    public UserHeader(Project main, int x, int y, int width, int height, User user, int totalStories) {
        super(main, x, y, width, height);
        this.user = user;
        this.totalStories = totalStories;
    }

    /**
     * Draws the UserWidget
     */
    public void draw() {
        main.textFont(main.FontHeading);
        main.fill(Color.PRIMARY);
        main.text(Utils.getLargestStringInBounds(main, user.getName(), width), x + PADDING, y + PADDING);

        main.textFont(main.FontRegular);
        main.fill(Color.BLACK);
        main.text("Total Score: " + user.getScore(), PADDING + x,
                APPROX_TEXT_HEIGHT * 2 + PADDING + PADDING + y,
                x + width - PADDING,
                APPROX_TEXT_HEIGHT * 2 + PADDING);
        main.text("Total Stories: " + totalStories, PADDING + x,
                APPROX_TEXT_HEIGHT * 6 + PADDING + PADDING + y,
                x + width - PADDING,
                APPROX_TEXT_HEIGHT * 6 + PADDING);
    }

    /**
     * @return Whether the cursor should switch to a link cursor
     */
    public boolean cursorInBounds() {
        return false;
    }
}
