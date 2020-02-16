package GUI.Widgets;

import Main.Event;
import Main.Project;

import java.util.ArrayList;

import static GUI.Layout.*;

/**
 * Widget wrapper for lists
 * Author: Adam
 */
public class List extends Widget {
    private final boolean includeGetMoreButton;
    private Project main;
    public ArrayList<Widget> widgetList;
    private int totalY;
    private int bufferSize;
    private boolean drawReverse;

    public List(Project main, int x, int y, int bufferSize, ArrayList<Widget> listOfWidgets, boolean includeGetMoreButton) {
        super(main, x, y, 0, 0);
        this.widgetList = listOfWidgets;
        this.bufferSize = bufferSize;
        this.includeGetMoreButton = false;

        this.drawReverse = false;
        totalY = MARGIN_TOP;
        for(Widget aWidget : listOfWidgets)
            totalY += aWidget.height + bufferSize;
        if(includeGetMoreButton) {
            Button getMoreButton = new Button(main, x, totalY + bufferSize, WINDOW_WIDTH - MARGIN_LEFT - MARGIN_RIGHT, 100, "Get more results", Event.GET_MORE_RESULTS);
            listOfWidgets.add(getMoreButton);
            totalY += bufferSize + getMoreButton.height;
        }
        moveWidgets(0);
    }

    public void setDrawReverse() {
        drawReverse = true;
    }

    /**
     * Moves all widgets to their correct position to enable scrolling through the list.
     * Needs a percentage passed in, this represents how far down the list we scrolled. The percentage should be taken from ScrollBar class.
     * @param percentage
     */
    public void moveWidgets(float percentage) {
        float startingY = ((totalY - WINDOW_HEIGHT + MARGIN_TOP) * percentage) - MARGIN_TOP;
        int aboveY = 0;
        for(Widget aWidget : widgetList) {
            aWidget.move((int) (aboveY - startingY));
            aboveY += aWidget.height + bufferSize;
        }
    }

    public void draw() {
        if(drawReverse)
            for(int i = widgetList.size() - 1; i >= 0; i--)
                widgetList.get(i).draw();
        else
            for(Widget aWidget : widgetList)
                aWidget.draw();
    }

    /**
     * Adds new widgets and returns the percentage to which the list should be set, so that the old stories stay on the screen and the new ones appear below them.
     * @param newWidgets
     * @return
     */
    public float addNew(ArrayList<Widget> newWidgets) {
        int oldY = totalY;
        Widget getMoreButton = widgetList.remove(widgetList.size() - 1);
        totalY -= (getMoreButton.height + bufferSize);
        for(Widget aWidget : newWidgets) {
            totalY += aWidget.height + bufferSize;
            widgetList.add(aWidget);
        }
        getMoreButton.move(totalY + bufferSize);
        totalY += getMoreButton.height + bufferSize;
        widgetList.add(getMoreButton);
        return ((float) oldY / totalY);
    }

    public void mousePressed() {
        for(Widget aWidget : widgetList)
            aWidget.mousePressed();
    }

    public ArrayList<Widget> getWidgetList() {
        return widgetList;
    }
}
