package GUI.Pages;

import GUI.Animator;
import GUI.Layout;
import GUI.Widgets.*;
import Main.Project;

import java.util.ArrayList;

import static GUI.Animator.*;

/**
 * Intermediate page drawing the swipe between two pages
 * Author: Stefan
 */
public class AnimationPage extends Page {
    private final int type;
    private int direction;
    private Page firstPage;
    private Page secondPage;
    private Project main;

    private Animator animator;
    private double progress;

    /**
     * Constructor for Animation Pages
     * Author: Stefan
     *
     * @param main          Reference to the main class
     * @param firstPage     Reference to the starting page
     * @param secondPage    Reference to the ending page
     * @param animDuration  Length of the animation in seconds
     * @param type          Type of animation (see constants)
     */
    public AnimationPage(Project main, Page firstPage, Page secondPage, double animDuration, int type) {
        super(main);
        this.main = main;
        this.firstPage = firstPage;
        this.secondPage = secondPage;

        this.widgetList = firstPage.widgetList;
        this.type = type;

        if(type == TYPE_SWIPE_RIGHT)
            direction = 1;
        else if(type == TYPE_SWIPE_LEFT)
            direction = -1;

        animator = new Animator(animDuration);
    }

    /**
     * Draws the transition according to the type of animation required
     * Author: Stefan
     */
    public void draw() {
        progress = animator.getProgress();
        if(progress == 1) {
            if(firstPage instanceof SearchPage && secondPage instanceof SearchPage) {
                ArrayList<Widget> widgetList = secondPage.widgetList;
                for(int i = 0; i < widgetList.size() - 1; i++) {
                    Widget widget = widgetList.get(i);
                    widget.y += Layout.SEARCH_FILTER_HEIGHT * (type == TYPE_SHOW_FILTERS ? 1 : -1);
                    if(widget instanceof DropdownIndicatorWidget) {
                        DropdownIndicatorWidget ddi = (DropdownIndicatorWidget) widget;
                        ddi.setProgress(Math.round(ddi.getProgress()));
                    }
                }
            }
            secondPage.draw();
            main.currentPage = secondPage;
            return;
        }

        if(type == TYPE_SWIPE_LEFT || type == TYPE_SWIPE_RIGHT)
            drawSwipe();
        else if(type == TYPE_EXPAND_STORY)
            drawShowComments();
        else if(type == TYPE_HIDE_FILTERS)
            drawDockHide();
        else if(type == TYPE_SHOW_FILTERS)
            drawDockShow();
    }

    /**
     * Animates the search interface being revealed
     * Author: Stefan
     */
    private void drawDockShow() {
        for(int i = 0; i < widgetList.size() - 1; i++) {
            if(widgetList.get(i) instanceof DropdownIndicatorWidget)
                ((DropdownIndicatorWidget) widgetList.get(i)).setProgress(1 - progress);
            widgetList.get(i).drawWithOffset(0, (int) (progress * Layout.SEARCH_FILTER_HEIGHT));
        }
        widgetList.get(widgetList.size() - 1).draw();
    }

    /**
     * Animates the search interface being hidden
     * Author: Stefan
     */
    private void drawDockHide() {
        for(int i = 0; i < widgetList.size() - 1; i++) {
            if(widgetList.get(i) instanceof DropdownIndicatorWidget)
                ((DropdownIndicatorWidget) widgetList.get(i)).setProgress(progress);
            widgetList.get(i).drawWithOffset(0, (int) (progress * Layout.SEARCH_FILTER_HEIGHT * -1));
        }
        widgetList.get(widgetList.size() - 1).draw();
    }

    /**
     * Animates a swipe of widgets between two pages
     * Author: Stefan
     */
    private void drawSwipe() {
        // The most a widget will be offset for this progression
        int fullHorizontalOffset = (int) (progress * Layout.WINDOW_WIDTH * direction);

        for(Widget widget : firstPage.widgetList) {
            // If the button's a tab or scroll bar, draw it normally
            if(!(widget instanceof Button && ((Button) widget).isTab)) {
                // Special code for Lists
                if(widget instanceof List) {
                    List listWidget = (List) widget;
                    for(int i = 0; i < listWidget.widgetList.size(); i++) {
                        int totalXOffset = fullHorizontalOffset * (Layout.WINDOW_HEIGHT / listWidget.widgetList.get(i).y);
                        listWidget.widgetList.get(i).drawWithOffset(totalXOffset, 0);
                    }
                } else {
                    // Normal offset for other widgets
                    int totalXOffset = fullHorizontalOffset * (Layout.WINDOW_HEIGHT / (widget.y != 0 ? widget.y : 1));
                    widget.drawWithOffset(totalXOffset, 0);
                }
            } else {
                widget.draw();
            }
        }

        fullHorizontalOffset += Layout.WINDOW_WIDTH * direction * -1;
        for(Widget widget : secondPage.widgetList) {
            // If the button's a tab or scroll bar, draw it normally
            if(!((widget instanceof Button && ((Button) widget).isTab) || (widget instanceof ScrollBar))) {
                // Special code for Lists
                // Normal offset for other widgets
                if(widget instanceof List) {
                    List listWidget = (List) widget;
                    for(int i = 0; i < listWidget.widgetList.size(); i++) {
                        int totalXOffset = (fullHorizontalOffset * (Layout.WINDOW_HEIGHT / listWidget.widgetList.get(i).y));
                        listWidget.widgetList.get(i).drawWithOffset(totalXOffset, 0);
                    }
                } else
                    widget.drawWithOffset(fullHorizontalOffset, 0);
            } else
                widget.draw();
        }
    }

    /**
     * Dropped animation for comments being swiped from the bottom of the screen when switching to a StoryPage
     * Author: Stefan
     */
    private void drawShowComments() {

    }
}
