package GUI.Widgets;

import Main.Event;
import Main.Project;

import java.util.ArrayList;

import static GUI.Layout.TOOLBAR_HEIGHT;
import static GUI.Layout.WINDOW_WIDTH;

/**
 * Nav bar widget
 * Displays the three main tabs with a widget to cover all widgets being drawn underneath
 * Author: Stefan
 */
public class NavBar extends Widget {
    private ArrayList<Widget> widgetList;

    /**
     * NavBar Widget constructor
     * @param main          Reference to the main class
     * @param selectedTab   Index of the currently selected class (starts at 1)
     */
    public NavBar(Project main, int selectedTab) {
        super(main, 0, 0, WINDOW_WIDTH, TOOLBAR_HEIGHT);

        widgetList = new ArrayList<>();
        widgetList.add(new Widget(main, 0, 0, WINDOW_WIDTH, TOOLBAR_HEIGHT));
        widgetList.add(new Button(main, 1, selectedTab == 1, "Stories", Event.SWITCH_TO_STORIES));
        widgetList.add(new Button(main, 2, selectedTab == 2, "Search", Event.SWITCH_TO_SEARCH));
        widgetList.add(new Button(main, 3, selectedTab == 3, "Analysis", Event.SWITCH_TO_ANALYSIS));
    }

    /**
     * Draws the NavBar Widget
     */
    public void draw() {
        main.strokeWeight(1);
        for(Widget widget : widgetList)
            widget.draw();
    }

    /**
     * Mouse moved handler for the NavBar widget
     */
    public void mouseMoved() {
        for(Widget widget : widgetList)
            widget.mouseMoved();
    }

    /**
     * Mouse click handler for the NavBar widget
     */
    public void mousePressed() {
        for(Widget widget : widgetList)
            widget.mousePressed();
    }

    /**
     * @return Whether the mouse is over any of the tabs
     */
    public boolean cursorInBounds() {
        boolean inBounds = false;
        for(int i = 1; i < widgetList.size(); i++)
            if(widgetList.get(i).cursorInBounds())
                inBounds = true;

        return  inBounds;
    }
}
