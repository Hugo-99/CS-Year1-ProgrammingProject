package GUI.Widgets;

import GUI.Color;
import GUI.Layout;
import Main.Event;
import Main.Project;

import static GUI.Layout.DDI_HEIGHT;
import static GUI.Layout.DDI_WIDTH;

/**
 * Hide/collapse button for the search interface
 * Author: Stefan
 */
public class DropdownIndicatorWidget extends Widget {
    private double progress;

    /**
     * hide/collapse button Constructor
     * Author: Stefan
     *
     * @param main      Reference to the main class
     * @param x         x coordinate of the top-left corner of the hide/collapse button
     * @param y         y coordinate of the top-left corner of the hide/collapse button
     */
    public DropdownIndicatorWidget(Project main, int x, int y) {
        super(main, x, y, DDI_WIDTH, DDI_HEIGHT);
        progress = 0;
    }

    /**
     * Draws the hide/collapse button
     * Author: Stefan
     */
    public void draw() {
        main.stroke(hovering ? Color.PRIMARY : Color.GRAY_DARK);
        main.strokeWeight(Layout.DDI_THICKNESS);
        int midpointX = x + (width / 2);
        main.line(x, (float) (y + (height - (progress * height))), midpointX, (float) (y + (progress * height)));
        main.line(x + width, (float) (y + (height - (progress * height))), midpointX, (float) (y + (progress * height)));
    }

    /**
     * @return Animation progress of the button
     */
    public double getProgress() {
        return progress;
    }

    /**
     * @param progress Animation progress to set the button to
     */
    public void setProgress(double progress) {
        this.progress = progress;
    }

    /**
     * Mouse click handler
     * Author: Stefan
     */
    public void mousePressed() {
        if(!hovering)
            return;
        if(progress == 1)
            Event.LAST = Event.SHOW_FILTERS;
        else if(progress == 0)
            Event.LAST = Event.HIDE_FILTERS;
    }
}
