package Main;

import Data.DBElement;
import Data.Query;
import Data.Story;
import Data.User;
import GUI.Animator;
import GUI.Color;
import GUI.Pages.*;
import GUI.Widgets.List;
import GUI.Widgets.ScrollBar;
import GUI.Widgets.StoryWidget;
import GUI.Widgets.Widget;

import java.util.ArrayList;

import static GUI.Layout.*;

/**
 * Event handler for the whole project
 * Authors: Adam, Koh, and Stefan
 */
public class Event {
    private static final int NULL = 0;
    public static final int DATABASE_UPDATED = 1;

    public static final int SWITCH_TO_STORIES = 2;
    public static final int SWITCH_TO_SEARCH = 3;
    public static final int SWITCH_TO_ANALYSIS = 4;
    public static final int SWITCH_TO_COMMENTS = 5;

    public static final int SEARCH = 6;
    public static final int HIDE_FILTERS = 7;
    public static final int SHOW_FILTERS = 8;
    public static final int GET_MORE_RESULTS = 9;

    public static final int RELOAD_DATA = 10;

    public static final int SWITCH_TO_USER_STORIES = 11;
    public static final int SWITCH_TO_USER_COMMENTS = 12;
    public static final int SWITCH_TO_USER = 13;

    public static int LAST = NULL;
    public static ArrayList<Object> extraData = new ArrayList<>();
    public static User user;

    static void handleEvents(Project main) {
        switch(Event.LAST) {
            case Event.NULL:
                break;
            case Event.DATABASE_UPDATED:
                new Thread(new Runnable() {
                    public void run() {
                        main.onDBUpdated();
                        main.loadingBar.doneLoading();
                    }
                }).start();
                break;
            case Event.SWITCH_TO_STORIES:
                if(main.currentPage instanceof SearchPage || main.currentPage instanceof AnalysisPage)
                    Animator.switchPages(main, main.currentPage, main.storyListingPage, Animator.TYPE_SWIPE_RIGHT);
                else if(!(main.currentPage instanceof StoryListingPage))
                    Animator.switchPages(main, main.currentPage, main.storyListingPage, Animator.TYPE_SWIPE_LEFT);
                break;
            case Event.SWITCH_TO_SEARCH:
                if(!(main.currentPage instanceof SearchPage))
                    if(!(main.currentPage instanceof AnalysisPage))
                        Animator.switchPages(main, main.currentPage, main.searchPage, Animator.TYPE_SWIPE_LEFT);
                    else
                        Animator.switchPages(main, main.currentPage, main.searchPage, Animator.TYPE_SWIPE_RIGHT);
                break;
            case Event.SWITCH_TO_ANALYSIS:
                if(!(main.currentPage instanceof AnalysisPage))
                    Animator.switchPages(main, main.currentPage, main.analysisPage, Animator.TYPE_SWIPE_LEFT);
                break;
            case Event.SEARCH:
                boolean[] primaryFiltersArray = ((SearchPage) main.searchPage).primaryCheckBox.getSelectedArray();
                int sortBy = ((SearchPage) main.searchPage).sortRadioButton.getSelectedInt();
                String input = ((SearchPage) main.searchPage).searchBar.getText();

                ArrayList<DBElement> results = new ArrayList<>();

                // Query for stories
                if(primaryFiltersArray[0]) {
                    boolean temp = primaryFiltersArray[1];
                    primaryFiltersArray[1] = false;
                    Query searchQuery = new Query(input, sortBy, primaryFiltersArray);
                    results = main.SQL.query(searchQuery);
                    primaryFiltersArray[1] = temp;
                }

                // Query for comments
                ArrayList<DBElement> comments = null;
                if(primaryFiltersArray[1]) {
                    boolean temp = primaryFiltersArray[0];
                    primaryFiltersArray[0] = false;
                    ((SearchPage) main.searchPage).query = new Query(input, sortBy, primaryFiltersArray);
                    comments = main.SQL.query(((SearchPage) main.searchPage).query);
                    primaryFiltersArray[0] = temp;
                }

                // Combine stories and comments
                if(results == null && comments != null)
                    results = comments;
                else if(comments != null) {
                    results.addAll(comments);
                }

                // Convert the stories from our query results to storyWidgets
                ArrayList<Widget> searchResult = main.searchPage.getWidgetList();
                boolean added = false;
                ArrayList<Widget> listWidget = new ArrayList<>(); // new list to store the results from the query
                if(results != null) {
                    for (int i = 0; i < results.size(); i++) {
                        DBElement currentDBElement = results.get(i);
                        if (currentDBElement instanceof Story) {
                            Story currentStory = (Story) results.get(i);
                            StoryWidget requestedStory = new StoryWidget(main, currentStory, MARGIN_LEFT, MARGIN_TOP + (i * (100 + LIST_SPACING)), WINDOW_WIDTH - MARGIN_LEFT - MARGIN_RIGHT, 100);
                            listWidget.add(requestedStory);
                        }
                    }
                }
                //
                Widget listToRemove = null;
                Widget listToAdd = null;
                for(Widget aWidget : searchResult) {
                    if(aWidget instanceof List) {
                        listToRemove = aWidget;
                        Widget newList = new List(main, MARGIN_LEFT, TOOLBAR_HEIGHT, LIST_SPACING, listWidget, true);
                        added = true;
                        for(Widget sb : searchResult)
                            if(sb instanceof ScrollBar)
                                ((ScrollBar) (sb)).setPercentage(0.0f);

                        ((List) (newList)).moveWidgets(0);
                        listToAdd = newList;
                    }
                }
                if(added) {
                    searchResult.remove(listToRemove);
                    searchResult.add(0, listToAdd);
                } else {
                    Widget toolbarCover = new Widget(main, 0, 0, WINDOW_WIDTH, TOOLBAR_HEIGHT);
                    toolbarCover.widgetColor = Color.BACKGROUND;
                    ScrollBar s = new ScrollBar(main, WINDOW_WIDTH - SB_WIDTH, 0, SB_WIDTH, WINDOW_HEIGHT, 30);
                    List scrollableStories = new List(main, MARGIN_LEFT, TOOLBAR_HEIGHT, LIST_SPACING, listWidget, true);
                    searchResult.add(0, toolbarCover);
                    searchResult.add(0, scrollableStories);
                    searchResult.add(s);
                }
                System.out.println(((SearchPage) main.searchPage).query.nextStart);
                main.searchPage.setWidgetList(searchResult);
                break;
            case Event.RELOAD_DATA:
                new Thread(new Runnable() {
                    public void run() {
                        main.loadingBar.startLoading();
                        main.SQL.reloadData();
                        main.loadingBar.doneLoading();
                    }
                }).start();
                break;
            case Event.HIDE_FILTERS:
                ((SearchPage) main.searchPage).hideFilters();
                break;
            case Event.SHOW_FILTERS:
                ((SearchPage) main.searchPage).showFilters();
                break;
            case Event.GET_MORE_RESULTS:
                main.currentPage.getMoreResults(Query.LastQuery);
                break;
            case Event.SWITCH_TO_COMMENTS:
                ArrayList<Widget> tempList = main.currentPage.getWidgetList();
                Story storyComments = null;
                int count0 = 0;
                for(int i = 0; i < tempList.size(); i++) {
                    if(tempList.get(i) instanceof List) {
                        count0 = i;
                        // exit the loop
                        i = tempList.size();
                    }
                }
                ArrayList<Widget> tempWidgets = ((List) tempList.get(count0)).widgetList;
                for(int j = 0; j < tempWidgets.size(); j++) {
                    Widget aWidget = tempWidgets.get(j);
                    if(aWidget != null && aWidget instanceof  StoryWidget && ((StoryWidget) aWidget).selected()) {
                        storyComments = ((StoryWidget) aWidget).getStory();
                        ((StoryWidget) aWidget).initializeChosenWidget();
                        j = tempWidgets.size();
                        ((StoryWidget) aWidget).deselect();
                    }
                }
                ((StoryPage) main.storyPage).setStory(storyComments);
                main.storyPage.onDBUpdated();
                Animator.switchPages(main, main.currentPage, main.storyPage, Animator.TYPE_SWIPE_RIGHT);
                break;
            case Event.SWITCH_TO_USER_STORIES:
                tempList = main.currentPage.getWidgetList();
                int count1 = 0;
                for(int i = 0; i < tempList.size(); i++) {
                    if(tempList.get(i) instanceof List) {
                        count1 = i;
                        i = tempList.size();
                    }
                }
                ((List) tempList.get(count1)).widgetList = user.getStories();
                break;
            case Event.SWITCH_TO_USER_COMMENTS:
                tempList = main.currentPage.getWidgetList();
                int count2 = 0;
                for(int i = 0; i < tempList.size(); i++) {
                    if(tempList.get(i) instanceof List) {
                        count2 = i;
                        i = tempList.size();
                    }
                }
                ((List) tempList.get(count2)).widgetList = user.getComments();
                break;
            case Event.SWITCH_TO_USER:
                String userName = (String) extraData.get(0);
                extraData = new ArrayList<>();
                Animator.switchPages(main, main.currentPage, new UserPage(main, userName), Animator.TYPE_SWIPE_RIGHT);
                break;
        }
        LAST = NULL;
        extraData = null;
    }
}
