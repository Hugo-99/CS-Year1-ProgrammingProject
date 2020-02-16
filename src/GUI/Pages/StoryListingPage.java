package GUI.Pages;

import Data.DBElement;
import Data.Query;
import Data.Story;
import GUI.Layout;
import GUI.Widgets.*;
import Main.Project;

import java.util.ArrayList;

import static Data.Query.SORT_BY_LATEST;
import static GUI.Layout.*;

/**
 * Story Listing Page - The landing page
 * Authors: Koh
 */
public class StoryListingPage extends Page {
    /**
     * Constructor for the Story Listing Page
     * @param main Reference to the main class
     */
    public StoryListingPage(Project main) {
        super(main);
        widgetList.add(new NavBar(main, 1));
    }

    /**
     * Refreshes the listed stories
     * Authors: Koh
     */
    @Override
    public void onDBUpdated() {
        System.out.println("story listing updated!");
        // Get the stories
        boolean[] filters = new boolean[4];
        filters[Query.TABLE_STORIES] = true;
        filters[Query.TABLE_COMMENTS] = false;
        filters[Query.TABLE_DATES] = false;
        filters[Query.TABLE_USERS] = false;
        Query initialQuery = new Query("", SORT_BY_LATEST, filters);
        ArrayList<DBElement> results = main.SQL.query(initialQuery);

        ArrayList<Widget> stories = new ArrayList<>();
        for(int i = 0; i < results.size(); i++) {
            Story currentStory = (Story) results.get(i);
            StoryWidget sampleStory = new StoryWidget(main, currentStory, MARGIN_LEFT, MARGIN_TOP + (i * (100 + LIST_SPACING)), WINDOW_WIDTH - MARGIN_LEFT - MARGIN_RIGHT, Layout.STORY_COMMENT_HEIGHT);
            stories.add(sampleStory);
        }

        ScrollBar sB = new ScrollBar(main, WINDOW_WIDTH - SB_WIDTH, 0, SB_WIDTH, WINDOW_HEIGHT, 30);
        List scrollableStories = new List(main, MARGIN_LEFT, TOOLBAR_HEIGHT, LIST_SPACING, stories, true);

        widgetList = new ArrayList<>();
        widgetList.add(scrollableStories);
        widgetList.add(new NavBar(main, 1));
        widgetList.add(sB);
    }
}
