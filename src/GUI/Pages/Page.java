package GUI.Pages;

import Data.DBElement;
import Data.Query;
import Data.Story;
import GUI.Widgets.*;
import Main.Project;

import java.util.ArrayList;

import static GUI.Layout.*;
import static processing.core.PConstants.*;

/**
 * Superclass of all pages
 * Authors: Adam, Koh, and Stefan
 */
public class Page {
    public Project main;
    protected ArrayList<Widget> widgetList;

    /**
     * Generic page constructor
     * @param main Reference to the main class
     */
    Page(Project main) {
        this.main = main;
        widgetList = new ArrayList<>();
    }

    public void setWidgetList(ArrayList<Widget> widgetList) {
        this.widgetList = widgetList;
    }

    public ArrayList<Widget> getWidgetList() {
        return widgetList;
    }

    /**
     * Draws all the widgets on the page
     */
    public void draw() {
        main.strokeWeight(1);
        widgetList.forEach(Widget::draw);
    }

    /**
     * Handles special activity for mouse movements
     * Authors: Adam and Stefan
     */
    public void mouseMoved() {
        int cursorType = ARROW;
        for(Widget widget : widgetList) {
            widget.mouseMoved();
            if((widget instanceof Button ||
                widget instanceof NavBar ||
                widget instanceof CheckBoxWidget ||
                widget instanceof DropdownIndicatorWidget)
                && widget.cursorInBounds())
                cursorType = HAND;
            else if(widget instanceof List) {
                List widgetAsList = (List) widget;
                for(Widget listWidget : widgetAsList.widgetList) {
                    if(listWidget.cursorInBounds()) {
                        cursorType = HAND;
                        break;
                    }
                }
            }
            else if(widget instanceof SearchBar && widget.cursorInBounds())
                cursorType = TEXT;
        }
        main.cursor(cursorType);
    }

    /**
     * Handles special activity for mouse clicks
     * Authors: Adam and Stefan
     */
    public void mousePressed() {
        float y = 0;
        for(Widget widget : widgetList) {
            widget.mousePressed();
            if(widget instanceof ScrollBar)
                y = ((ScrollBar) (widget)).getPercentage();
            else if(widget instanceof List)
                ((List) (widget)).moveWidgets(y);
        }
    }

    /**
     * Handles special activity for scrolling
     * Authors: Adam
     */
    public void mouseScrolled(float scrollValue) {
        float y = 0;
        for(Widget widget : widgetList)
            if(widget instanceof ScrollBar) {
                ((ScrollBar) (widget)).mouseScrolled(scrollValue);
                y = ((ScrollBar) (widget)).getPercentage();
            }

        for(Widget widget : widgetList)
            if(widget instanceof List)
                ((List) (widget)).moveWidgets(y);
    }

    /**
     * Handles special activity for mouse drags
     * Authors: Adam
     */
    public void mouseDragged() {
        float y = 0;
        for(Widget widget : widgetList)
            if(widget instanceof ScrollBar) {
                ((ScrollBar) (widget)).drag();
                y = ((ScrollBar) (widget)).getPercentage();
            }

        for(Widget widget : widgetList)
            if(widget instanceof List)
                ((List) (widget)).moveWidgets(y);
    }

    public void mouseReleased() {
        for(Widget widget : widgetList) {
            if(widget instanceof ScrollBar) {
                ((ScrollBar) (widget)).deactivate();
            }
        }
    }

    public void getMoreResults(Query query) {
        ArrayList<DBElement> results = main.SQL.query(query);
        query.queryComplete();

        if(results == null)
            return;

        System.out.println(results.size());
        ArrayList<Widget> stories = new ArrayList<>();
        for(int i = 0; i < results.size(); i++) {
            Story currentStory = (Story) results.get(i);
            // StoryWidget storyWidget = new Story(currentStory);
            StoryWidget sampleStory = new StoryWidget(main, currentStory, MARGIN_LEFT, MARGIN_TOP + (i * (100 + LIST_SPACING)), WINDOW_WIDTH - MARGIN_LEFT - MARGIN_RIGHT, 100);
            stories.add(sampleStory);
        }
        float newPercentage = 0;
        for(Widget aWidget : widgetList) {
            if(aWidget instanceof List) {
                newPercentage = ((List) (aWidget)).addNew(stories);
            }
        }
        for(Widget aWidget : widgetList) {
            if(aWidget instanceof ScrollBar) {
                ((ScrollBar) (aWidget)).setPercentage(newPercentage);
            }
        }
    }

    public void keyPressed() {

    }

    public void onDBUpdated() {

    }
}
