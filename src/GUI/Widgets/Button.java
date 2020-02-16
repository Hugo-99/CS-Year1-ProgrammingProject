package GUI.Widgets;

import GUI.Color;
import Main.Event;
import Main.Project;

import static GUI.Layout.*;

/**
 * Button Widget
 * Authors: Koh, Stefan
 */
public class Button extends Widget {
    public boolean isTab;
    private int event;
    private String label;
    private boolean selectedTab;

    /**
     * Normal constructor for a Button Widget
     *
     * @param main      Reference to the main class
     * @param x         x coordinate of the top-left corner of the button
     * @param y         y coordinate of the top-left corner of the button
     * @param width     Button width
     * @param height    Button height
     * @param label     Button Label
     * @param event     Event to trigger when the button is clicked
     */
    public Button(Project main, int x, int y, int width, int height, String label, int event) {
        super(main, x, y, width, height);

        this.event = event;
        this.label = label;
    }

    /**
     * Tab constructor for a Button Widget
     * Author: Stefan
     *
     * @param main          Reference to the main class
     * @param tabIndex      Index of this tab (first tab at index 1)
     * @param selectedTab   Whether this tab represents the currently displayed page
     * @param label         Tab label
     * @param event         Event to trigger when the button is clicked
     */
    Button(Project main, int tabIndex, boolean selectedTab, String label, int event) {
        super(main, (int) (WINDOW_WIDTH * (0.25 * tabIndex) - BUTTON_WIDTH / 2), 20, BUTTON_WIDTH, BUTTON_HEIGHT);
        this.isTab = true;

        this.event = event;
        this.label = label;
        this.hovering = false;
        this.selectedTab = selectedTab;
    }

    public void draw() {
        // Shape
        hovering = cursorInBounds();
        main.stroke(Color.GRAY_DARK);
        if(isTab)
            if(selectedTab)
                main.fill(hovering ? Color.SECONDARY : Color.PRIMARY);
            else
                main.fill(hovering ? Color.PRIMARY : Color.GRAY_LIGHT);
        else
            main.fill(hovering ? Color.SECONDARY : Color.PRIMARY);
        main.rect(x, y, width, height, BORDER_RADIUS);

        // Text
        main.textFont(main.FontBold);
        main.fill(isTab ? (hovering || selectedTab ? Color.WHITE : Color.PRIMARY) : Color.WHITE);
        main.textAlign(main.CENTER);
        main.text(label, x + width / 2, (float) (y + height * 0.65));
    }

    public void mousePressed() {
        if(cursorInBounds())
            Event.LAST = event;
    }
}
