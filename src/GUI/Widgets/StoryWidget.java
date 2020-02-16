package GUI.Widgets;

import Data.Story;
import GUI.Color;
import GUI.Layout;
import Main.Event;
import Main.Project;
import processing.core.PApplet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static GUI.Layout.APPROX_TEXT_HEIGHT;
import static GUI.Layout.PADDING;
import static Main.Utils.getLargestStringInBounds;

/**
 * Widget for showing brief information about a story
 * Authors: Nixon, Stefan
 */
public class StoryWidget extends Widget {
    Project main;
    private Data.Story story;
    private boolean hoveringOverURL, hoveringOverUser, hoveringOverTitle, chosen;
    private float authorWidth, titleWidth;

    /**
     * StoryWidget constructor
     * @param main      Reference to the main class
     * @param story     Story to display
     * @param x         x coordinate of the top-left corner of the Story widget
     * @param y         y coordinate of the top-left corner of the Story widget
     * @param width     Widget width
     * @param height    Widget height
     */
    public StoryWidget(Project main, Story story, int x, int y, int width, int height) {
        super(main, x, y, width, height);
        this.main = main;
        this.story = story;

        authorWidth = main.textWidth("by: " + story.getAuthor());
        titleWidth = main.textWidth(story.getTitle());
        hoveringOverURL = false;
        hoveringOverTitle = false;
        chosen = false;
    }

    /**
     * Draws the StoryWidget
     * Author: Nixon and Stefan
     */
    public void draw() {
        // Background
        int alpha = (int) PApplet.map(y, Layout.MARGIN_TOP - height, Layout.MARGIN_TOP, 0, 255);
        main.fill(Color.BACKGROUND_DIMMED);
        main.stroke(Color.GRAY_DARK);
        main.rect(x, y, width, height, Layout.BORDER_RADIUS / 2);

        // Title and score
        main.fill(Color.PRIMARY, alpha);
        main.textAlign(main.LEFT);
        main.textFont(main.FontBold);
        main.text(story.getTitle(), PADDING + x, PADDING + y, width - PADDING - 70 - PADDING, APPROX_TEXT_HEIGHT + PADDING);
        if(hoveringOverTitle) {
            main.stroke(Color.PRIMARY);
            main.line(PADDING + x, APPROX_TEXT_HEIGHT + PADDING + PADDING + y, x + PADDING + main.textWidth(getLargestStringInBounds(main, story.getTitle(), width - 2 * PADDING)), APPROX_TEXT_HEIGHT + PADDING + PADDING + y);
        }

        main.textFont(main.FontRegular);
        main.textAlign(main.RIGHT);
        main.text(String.valueOf(story.getScore()), x + width - PADDING - 70, PADDING + y, 70, APPROX_TEXT_HEIGHT + PADDING);

        // Date
        main.fill(Color.TEXT_DEFAULT, alpha);
        main.textAlign(main.LEFT);
        String formattedDate = new SimpleDateFormat("dd/MM/yyyy").format(story.getDate());

        main.text("By: " + story.getAuthor() + "       Posted: " + formattedDate, PADDING + x, APPROX_TEXT_HEIGHT + PADDING + PADDING + y, width - PADDING, APPROX_TEXT_HEIGHT + PADDING);
        if(hoveringOverUser) {
            main.stroke(Color.TEXT_DEFAULT);
            main.line(PADDING + x, 2 * (APPROX_TEXT_HEIGHT + PADDING) + PADDING + y, x + PADDING + main.textWidth("by: " + story.getAuthor()), 2 * (APPROX_TEXT_HEIGHT + PADDING) + PADDING + y);
        }

        // URL
        main.fill(Color.SECONDARY, alpha);
        main.textAlign(main.LEFT);
        String URL = getLargestStringInBounds(main, story.getURL(), width - PADDING);
        main.text("URL: " + URL, PADDING + x, 2 * (APPROX_TEXT_HEIGHT + PADDING) + PADDING + y, width - PADDING, APPROX_TEXT_HEIGHT + PADDING);
        if(hoveringOverURL) {
            main.stroke(Color.SECONDARY);
            main.line(PADDING + x, 3 * (APPROX_TEXT_HEIGHT + PADDING) + PADDING + y, x + main.textWidth("URL: " + URL), 3 * (APPROX_TEXT_HEIGHT + PADDING) + PADDING + y);
        }
    }

    /**
     * @return Whether the cursor should switch to a link cursor
     */
    public boolean cursorInBounds() {
        return cursorOverURL() || cursorOverUser() || cursorOverTitle();
    }

    /**
     * @return Whether the cursor is over the title
     */
    private boolean cursorOverTitle() {
        if(main.mouseX > x + PADDING
                && main.mouseX < x + PADDING + titleWidth
                && main.mouseY > y + PADDING
                && main.mouseY < APPROX_TEXT_HEIGHT + PADDING + PADDING + y) {
            hoveringOverTitle = true;
            return true;
        }
        hoveringOverTitle = false;
        return false;
    }

    /**
     * @return Whether the cursor is over the user's name
     */
    private boolean cursorOverUser() {
        if(main.mouseX > x + PADDING
                && main.mouseX < x + PADDING + authorWidth
                && main.mouseY > y + APPROX_TEXT_HEIGHT + PADDING + PADDING
                && main.mouseY < 2 * (APPROX_TEXT_HEIGHT + PADDING) + PADDING + y) {
            hoveringOverUser = true;
            return true;
        }
        hoveringOverUser = false;
        return false;
    }

    /**
     * @return Whether the cursor is over the URL
     */
    private boolean cursorOverURL() {
        if(main.mouseX > x + PADDING
                && main.mouseX < x + width
                && main.mouseY > 2 * (APPROX_TEXT_HEIGHT + PADDING) + PADDING + y
                && main.mouseY < 2 * (APPROX_TEXT_HEIGHT + PADDING) + PADDING + y + APPROX_TEXT_HEIGHT + PADDING) {
            hoveringOverURL = true;
            return true;
        }
        hoveringOverURL = false;
        return false;
    }

    /**
     * Mouse click handler for the Story widget
     */
    public void mousePressed() {
        if(cursorOverTitle()) {
            chosen = true;
            Event.LAST = Event.SWITCH_TO_COMMENTS;
        } else if(cursorOverUser()) {
            String userName = story.getAuthor();
            Event.extraData = new ArrayList<>();
            Event.extraData.add(userName);
            Event.LAST = Event.SWITCH_TO_USER;
        } else if(cursorOverURL()) {
            main.link(story.getURL());
            System.out.println("Opening link: " + story.getURL());
        }
    }

    public boolean selected() {
        return chosen;
    }

    public void initializeChosenWidget() {
        chosen = false;
    }

    public Story getStory() {
        return story;
    }

    public void deselect() {
        chosen = false;
    }
}