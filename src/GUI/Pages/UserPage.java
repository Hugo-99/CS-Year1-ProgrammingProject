package GUI.Pages;

import Data.User;
import GUI.Widgets.*;
import Main.Project;

import java.util.ArrayList;
import java.util.Collections;

import static GUI.Layout.*;

/**
 * Page displaying all the info for a user
 * Authors: Koh, Stefan
 */
public class UserPage extends Page {
    private User user;

    /**
     * Constructor of the User Page
     * @param main          Reference to the main class
     * @param userString    Name of the user
     */
    public UserPage(Project main, String userString) {
        super(main);

        int index = -1;
        for(int i = 0; i < main.SQL.users.size(); i++)
            if(main.SQL.users.get(i).name.equalsIgnoreCase(userString))
                index = i;

        if(index == -1)
            System.err.println("Couldn't find user " + userString + " in database");
        else {
            this.user = main.SQL.users.get(index);

            // Initialize the currentList
            ArrayList<Widget> currentList = user.getStories();

            currentList.add(new UserHeader(main, MARGIN_LEFT, TOOLBAR_HEIGHT, CONTENT_WIDTH, STORY_HEADER_HEIGHT, user, currentList.size()));
            Collections.reverse(currentList);
            ScrollBar sB = new ScrollBar(main, WINDOW_WIDTH - SB_WIDTH, 0, SB_WIDTH, WINDOW_HEIGHT, 30);
            List scrollableStories = new List(main, MARGIN_LEFT, TOOLBAR_HEIGHT, LIST_SPACING, currentList, false);

            widgetList.add(scrollableStories);
            widgetList.add(new NavBar(main, 1));
            widgetList.add(sB);
        }
    }
}
