package GUI.Widgets;

import Data.Story;
import GUI.Animator;
import GUI.Color;
import GUI.Layout;
import GUI.Pages.UserPage;
import Main.Project;
import Main.Utils;

import static GUI.Layout.*;
import static Main.Utils.getLargestStringInBounds;

/**
 * Information widget for story pages
 * Author: Stefan
 */
public class StoryHeader extends Widget {

    private final Story story;
    private final DropDownWidget sortBy;
    private boolean hoveringOverUser, hoveringOverURL;

    /**
     * Constructor for Story Header widgets
     * @param main      Reference to the main class
     * @param x         x coordinate of the top-left corner of the StoryHeader widget
     * @param y         y coordinate of the top-left corner of the StoryHeader widget
     * @param width     Widget width
     * @param height    Widget height
     * @param story     Story to display information of
     */
    public StoryHeader(Project main, int x, int y, int width, int height, Story story) {
        super(main, x, y, width, height);
        this.story = story;

        // DropDownWidget
        String[] choices = {"Recent", "Date", "Text"};
        this.sortBy = new DropDownWidget(main, x + width - DROPDOWN_WIDTH, y + height - Layout.DROPDOWN_HEIGHT, Layout.DROPDOWN_WIDTH, Layout.DROPDOWN_HEIGHT, "Sort by", choices);
    }

    /**
     * Draws the StoryHeader
     */
    public void draw() {
        sortBy.x = x + width - DROPDOWN_WIDTH;
        sortBy.y = y + height - Layout.DROPDOWN_HEIGHT;

        main.fill(Color.PRIMARY);
        main.textFont(main.FontHeading);
        main.text(Utils.getLargestStringInBounds(main, story.getTitle(), width), x + PADDING, y + PADDING);

        main.textFont(main.FontRegular);
        main.fill(Color.BLACK);
        main.text("By: " + story.getAuthor(), PADDING + x,
                APPROX_TEXT_HEIGHT * 2 + PADDING + PADDING + y,
                x + width - PADDING,
                APPROX_TEXT_HEIGHT * 2 + PADDING);
        if(hoveringOverUser) {
            main.stroke(Color.TEXT_DEFAULT);
            main.line(PADDING + x,
                    2 * (APPROX_TEXT_HEIGHT + PADDING) + PADDING + PADDING + y,
                    x + PADDING + main.textWidth("by: " + story.getAuthor()),
                    2 * (APPROX_TEXT_HEIGHT + PADDING) + PADDING + PADDING + y);
        }

        // URL
        main.fill(Color.SECONDARY);
        main.textAlign(main.LEFT);
        String URL = getLargestStringInBounds(main, story.getURL(), width - PADDING);
        main.text("URL: " + URL, PADDING + x, 3 * (APPROX_TEXT_HEIGHT + PADDING) + PADDING + y, width - PADDING, APPROX_TEXT_HEIGHT + PADDING);
        if(hoveringOverURL) {
            main.stroke(Color.SECONDARY);
            main.line(PADDING + x, 4 * (APPROX_TEXT_HEIGHT + PADDING) + PADDING + y, x + main.textWidth("URL: " + URL), 4 * (APPROX_TEXT_HEIGHT + PADDING) + PADDING + y);
        }

        sortBy.draw();
    }

    /**
     * Draws the StoryHeader with the specified offset
     * @param x X offset
     * @param y Y offset
     */
    public void drawWithOffset(int x, int y) {
        this.x += x;
        this.y += y;
        sortBy.x += x;
        sortBy.y += y;
        draw();
        this.x -= x;
        this.y -= y;
        sortBy.x -= x;
        sortBy.y -= y;
    }

    /**
     * Mouse click handler for StoryHeaders
     */
    public void mousePressed() {
        sortBy.mousePressed();
        if(cursorOverUser())
            Animator.switchPages(main, main.currentPage, new UserPage(main, story.getAuthor()), Animator.TYPE_SWIPE_RIGHT);
        if(cursorOverURL()) {
            main.link(story.getURL());
            System.out.println("Opening link: " + story.getURL());
        }
    }

    /**
     * @return Whether the cursor should switch to a link cursor
     */
    @Override
    public boolean cursorInBounds() {
        return cursorOverUser() || cursorOverDropdown() || cursorOverURL();
    }

    /**
     * @return Whether the cursor is over the URL
     */
    private boolean cursorOverURL() {
        if(main.mouseX > x + PADDING
                && main.mouseX < x + PADDING + main.textWidth("URL: " + getLargestStringInBounds(main, story.getURL(), width - PADDING))
                && main.mouseY > 3 * (APPROX_TEXT_HEIGHT + PADDING) + PADDING + y
                && main.mouseY < 3 * (APPROX_TEXT_HEIGHT + PADDING) + PADDING + y +APPROX_TEXT_HEIGHT + PADDING) {
            hoveringOverURL = true;
            return true;
        }
        hoveringOverURL = false;
        return false;
    }

    /**
     * @return Whether the cursor is over the Dropdown widget
     */
    private boolean cursorOverDropdown() {
        return main.mouseX > sortBy.x && main.mouseX < sortBy.x + sortBy.width
                && main.mouseY > sortBy.y && main.mouseY < sortBy.y + sortBy.height;
    }

    /**
     * @return Whether the cursor is over the User's name
     */
    private boolean cursorOverUser() {
        if(main.mouseX > x + PADDING
                && main.mouseX < x + PADDING + main.textWidth("by: " + story.getAuthor())
                && main.mouseY > y + APPROX_TEXT_HEIGHT * 2 + PADDING + PADDING
                && main.mouseY < 2 * (APPROX_TEXT_HEIGHT * 2 + PADDING) + PADDING + y) {
            hoveringOverUser = true;
            return true;
        }
        hoveringOverUser = false;
        return false;
    }
}
