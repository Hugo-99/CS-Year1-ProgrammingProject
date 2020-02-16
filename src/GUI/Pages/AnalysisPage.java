package GUI.Pages;

import GUI.Color;
import GUI.Layout;
import GUI.Widgets.*;
import Main.Event;
import Main.Project;

import java.util.ArrayList;

import static GUI.Layout.*;

/**
 * Analysis Page
 * Author: Adam, Koh, and Stefan
 */
public class AnalysisPage extends Page {
    public AnalysisPage(Project main) {
        super(main);
        this.main = main;
        widgetList.add(new NavBar(main, 3));
    }

    /**
     * Updates the contents of the AnalysisPage with the new Database data
     * Authors: Adam, Koh, and Stefan
     */
    public void onDBUpdated() {
        Button ReloadDataButton = new Button(main, WINDOW_WIDTH - MARGIN_RIGHT - MARGIN_RIGHT - BUTTON_WIDTH, TOOLBAR_HEIGHT + PADDING, BUTTON_WIDTH, BUTTON_HEIGHT, "Reload Data", Event.RELOAD_DATA);
        widgetList.add(ReloadDataButton);

        // sample data sheet for bar chart
        ArrayList<Integer> data = main.SQL.getActivityPerDayOfWeek();
        // xAxisParameter
        char[] daysOfWeek = {'M', 'T', 'W', 'T', 'F', 'S', 'S'};

        OrderedList mostPopularWebsites = new OrderedList(main,0,0,0,0, main.SQL.getMostPopularWebsites(),main.SQL.getCountAllWebsites());
        TimeWidget mostActiveHours = new TimeWidget(main,0,0,0,0,main.SQL.getPostsPerHour());
        DWidget firstRow = new DWidget(main, MARGIN_LEFT * 2, TOOLBAR_HEIGHT + BAR_CHART_HEIGHT + LIST_SPACING, CONTENT_WIDTH, (int) (BAR_CHART_HEIGHT * 1.5), mostActiveHours, mostPopularWebsites);

        BarChart mostActiveDays = new BarChart(main, (CONTENT_WIDTH / 2), MARGIN_TOP + LIST_SPACING + BAR_CHART_HEIGHT, CONTENT_WIDTH, (int) (BAR_CHART_HEIGHT * 1.5), "Most Active Days", "Posts", "Day of the week", main.SQL.getActivityPerDayOfWeek(), daysOfWeek);

        ArrayList<Widget> listWidgets = new ArrayList<>();
        listWidgets.add(firstRow);
        listWidgets.add(mostActiveDays);

        List pageList = new List(main, MARGIN_LEFT, TOOLBAR_HEIGHT + BUTTON_HEIGHT, LIST_SPACING, listWidgets, false);
        widgetList.add(pageList);
    }
}